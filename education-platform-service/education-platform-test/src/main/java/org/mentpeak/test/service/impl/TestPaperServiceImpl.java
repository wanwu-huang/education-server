package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mentpeak.core.auth.PlatformUser;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.test.dto.NextQuestionDTO;
import org.mentpeak.test.dto.QuestionDTO;
import org.mentpeak.test.entity.*;
import org.mentpeak.test.mapper.*;
import org.mentpeak.test.service.ITestPaperService;
import org.mentpeak.test.service.ReportUserService;
import org.mentpeak.test.strategy.ModuleStrategy;
import org.mentpeak.test.strategy.ModuleStrategyFactory;
import org.mentpeak.test.vo.*;
import org.mentpeak.user.entity.User;
import org.mentpeak.user.feign.IUserClient;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * 用户试卷信息表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestPaperServiceImpl extends BaseServiceImpl<TestPaperMapper, TestPaper> implements ITestPaperService {

    private final ModuleStrategyFactory moduleStrategyFactory;

    private final TestPaperRecordMapper paperRecordMapper;

    private final TestQuestionnaireMapper questionnaireMapper;

    private final TestPaperQuestionMapper paperQuestionMapper;

    private final TestOptionMapper optionMapper;

    private final IUserClient userClient;

    private final TestTaskUserMapper taskUserMapper;

    private final ReportUserService reportUserService;

    @Override
    public IPage<TestPaperVO> selectTestPaperPage(IPage<TestPaperVO> page, TestPaperVO testPaper) {
        return page.setRecords(baseMapper.selectTestPaperPage(page, testPaper));
    }

    @Override
    public InstructionVO getReminder(Long moduleId) {
        InstructionVO instruction = baseMapper.getInstruction(moduleId);
        try {
            Result<User> result = userClient.userInfoById(SecureUtil.getUserId());
            User data = result.getData();
            instruction.setName(data.getRealName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instruction;
    }

    @Override
    public QuestionVO generatePaper(GenerateVO generateVO) {
        // 只有第一模块需要生成试卷id,其余模块进来使用第一模块试卷id
        Long pid = 0L;
        if (ObjectUtils.isNotEmpty(generateVO.getPaperId())) {
            pid = generateVO.getPaperId();
        }
        if (generateVO.getModuleId() == 1 || pid == 0) {
            // 生成试卷
            TestPaper paper = new TestPaper();
            paper.setStartTime(LocalDateTime.now());
            paper.setUserId(SecureUtil.getUserId());
            paper.setTenantCode(SecureUtil.getTenantCode());
            paper.setQuestionnaireId(generateVO.getQuestionnaireId());
            paper.setTaskId(generateVO.getTaskId());
            baseMapper.insert(paper);
            pid = paper.getId();
        }
        // 用户问卷测试记录
//        TestPaperRecord record = new TestPaperRecord();
//        record.setStartTime(paper.getStartTime());
//        record.setTaskId(taskId);
//        record.setQuestionnaireId(questionnaireId);
//        record.setUserId(paper.getUserId());
//        record.setUserNo(SecureUtil.getUserAccount());
//        record.setUserName(SecureUtil.getUserName());
//        record.setTenantCode(SecureUtil.getTenantCode());
//        TestQuestionnaire testQuestionnaire = questionnaireMapper.selectById(questionnaireId);
//        record.setQuestionnaireName(testQuestionnaire.getName());
//        paperRecordMapper.insert(record);
        //开始推题，从模块1开始
        ModuleStrategy strategy = moduleStrategyFactory.getStrategy(generateVO.getModuleId().intValue());
        QuestionVO nextQuestion = strategy.getNextQuestion(generateVO.getModuleId(), 0, 1);
        nextQuestion.setPaperId(pid);
        nextQuestion.setQuestionnaireId(generateVO.getQuestionnaireId());
        return nextQuestion;
    }

    @Override
    public QuestionVO getNextQuestion(NextQuestionDTO nextQuestionDTO) {
        ModuleStrategy strategy = moduleStrategyFactory.getStrategy(nextQuestionDTO.getModuleId().intValue());
        int index = nextQuestionDTO.getModuleIndex() + 1;
        QuestionVO nextQuestion = strategy.getNextQuestion(nextQuestionDTO.getModuleId(), index, nextQuestionDTO.getModuleCount());
        nextQuestion.setPaperId(nextQuestionDTO.getPaperId());
        nextQuestion.setQuestionnaireId(nextQuestionDTO.getQuestionnaireId());
        return nextQuestion;
    }

    @Override
    public AnswerVo saveResult(QuestionDTO questionDTO) {
        AnswerVo vo = new AnswerVo();
        vo.setModuleId(questionDTO.getModuleId());
        if (questionDTO.getModuleCount() >= 3) {
            // 第九模块两道题出完，开始跳下模块的指导语
            vo.setIsFinished(1);
            // 跳回原来的模块  1-1,2   2-3,4     3-5,6    4-7,8
            if (questionDTO.getModuleIndex() == 1) {
                // 做完第一模块，跳第二模块
                vo.setModuleId(2L);
            } else if (questionDTO.getModuleIndex() == 3) {
                // 做完第二模块，跳第三模块
                vo.setModuleId(3L);
            } else if (questionDTO.getModuleIndex() == 5) {
                // 做完第三模块，跳第四模块
                vo.setModuleId(4L);
            } else if (questionDTO.getModuleIndex() == 7) {
                // 做完第四模块，跳第五模块
                vo.setModuleId(5L);
            }
        }
        if (questionDTO.getQtype() == 1) {
            // 单选
            // 判断是否返回上一题
            TestPaperQuestion paperQuestion = paperQuestionMapper.selectOne(Wrappers.<TestPaperQuestion>lambdaQuery()
                    .eq(TestPaperQuestion::getPaperId, questionDTO.getPaperId())
                    .eq(TestPaperQuestion::getQuestionId, questionDTO.getQid()));
            if (ObjectUtils.isNotEmpty(paperQuestion)) {
                // 返回上一题，重新保存答案
                paperQuestion.setOptionId(Func.toStr(questionDTO.getOid()));
                paperQuestionMapper.updateById(paperQuestion);
            } else {
                // 第一次作答
                TestPaperQuestion testPaperQuestion = new TestPaperQuestion();
                testPaperQuestion.setTenantCode(SecureUtil.getTenantCode());
                testPaperQuestion.setPaperId(questionDTO.getPaperId());
                testPaperQuestion.setQuestionId(questionDTO.getQid());
                testPaperQuestion.setOptionId(Func.toStr(questionDTO.getOid()));
                testPaperQuestion.setQuestionType(questionDTO.getQtype());
                testPaperQuestion.setCreateUser(SecureUtil.getUserId());
                paperQuestionMapper.insert(testPaperQuestion);
            }
            // 跳题
            if (questionDTO.getModuleId() == 6 && questionDTO.getIsNextId() == 0) {
                TestOption testOption = optionMapper.selectOne(Wrappers.<TestOption>lambdaQuery()
                        .eq(TestOption::getId, questionDTO.getOid()));
                // 先跳指导语
                vo.setIsFinished(1);
                if (testOption.getScore() == 0) {
                    // 跳到第八模块
                    vo.setModuleId(8L);
                } else {
                    // 跳到第七模块
                    vo.setModuleId(7L);
                }
            }
            // 第五模块跳第六模块
            if (questionDTO.getModuleId() == 5 && questionDTO.getIsNextId() == 0) {
                vo.setIsFinished(1);
                vo.setModuleId(6L);
            }
            // 第七模块跳第八模块
            if (questionDTO.getModuleId() == 7 && questionDTO.getIsNextId() == 0) {
                vo.setIsFinished(1);
                vo.setModuleId(8L);
            }
            // 结束
            if (questionDTO.getModuleId() == 8 && questionDTO.getIsNextId() == 0) {
                vo.setIsFinished(2);
                // TODO:出结果
                Long userId = SecureUtil.getUserId();
                saveTaskUser(questionDTO.getPaperId());

                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                CompletableFuture.runAsync(() -> {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                    updateRepore(questionDTO.getPaperId(), userId);
                });
            }
        }
        return vo;
    }

    void updateRepore(Long paperId, Long userId) {
        reportUserService.generateReport(paperId, userId);
    }

    private void saveTaskUser(Long paperId) {
        PlatformUser user = SecureUtil.getUser();
        // 更新试卷表
        TestPaper testPaper = baseMapper.selectById(paperId);
        testPaper.setIsFinish(1);
        testPaper.setFinishTime(LocalDateTime.now());
        baseMapper.updateById(testPaper);
        // 添加任务用户表数据
        TestTaskUser taskUser = new TestTaskUser();
        taskUser.setTenantCode(user.getTenantCode());
        taskUser.setUserId(user.getUserId());
        taskUser.setTestTaskId(testPaper.getTaskId());
        taskUser.setCompletionStatus(1);
        taskUserMapper.insert(taskUser);

        // 问卷记录表存取一份
        TestPaperRecord paperRecord = new TestPaperRecord();
        paperRecord.setUserId(user.getUserId());
        paperRecord.setTenantCode(user.getTenantCode());

        paperRecord.setStartTime(testPaper.getStartTime());
        paperRecord.setFinishTime(testPaper.getFinishTime());
        paperRecord.setTaskId(testPaper.getTaskId());
        paperRecord.setQuestionnaireId(testPaper.getQuestionnaireId());
        TestQuestionnaire testQuestionnaire = questionnaireMapper.selectById(testPaper.getQuestionnaireId());
        if (ObjectUtils.isNotEmpty(testQuestionnaire)) {
            if (ObjectUtils.isNotEmpty(testQuestionnaire.getName())) {
                paperRecord.setQuestionnaireName(testQuestionnaire.getName());
            }
        }
        paperRecord.setUserNo(user.getAccount());
        paperRecord.setUserName(user.getUserName());
        paperRecord.setPaperId(paperId);

        paperRecordMapper.insert(paperRecord);
    }

}
