package org.mentpeak.parent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.AllArgsConstructor;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.parent.dto.ParentPaperDTO;
import org.mentpeak.parent.dto.ResponseQuestionDto;
import org.mentpeak.parent.dto.ResultDTO;
import org.mentpeak.parent.entity.*;
import org.mentpeak.parent.mapper.ParentOptionMapper;
import org.mentpeak.parent.mapper.ParentPaperMapper;
import org.mentpeak.parent.mapper.ParentPaperQuestionMapper;
import org.mentpeak.parent.mapper.ParentQuestionMapper;
import org.mentpeak.parent.service.IParentQuestionService;
import org.mentpeak.test.LogicOption;
import org.mentpeak.test.LogicQuestion;
import org.mentpeak.test.enums.QuestionEnum;
import org.mentpeak.test.feign.IReportClient;
import org.mentpeak.test.jumplogic.IdsJumpLogic;
import org.mentpeak.user.feign.IUserClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


/**
 * 家长他评问卷题目信息表 服务实现类
 *
 * @author lxp
 * @since 2022-06-17
 */
@Service
@AllArgsConstructor
public class ParentQuestionServiceImpl extends BaseServiceImpl<ParentQuestionMapper, ParentQuestion> implements IParentQuestionService {

    private final ParentOptionMapper parentOptionMapper;

    private final ParentPaperMapper paperMapper;

    private final ParentPaperQuestionMapper paperQuestionMapper;

    private final IUserClient userClient;

    private final IReportClient reportClient;

    /**
     * 自定义分页
     *
     * @return 题干列表
     */
    @Override
    public List<ParentQuestion> selectAllQuestions() {
        ParentQuestion parentQuestion = new ParentQuestion();
        parentQuestion.setQuestionnaireId(1L);
        return baseMapper.selectList(Condition.getQueryWrapper(parentQuestion));
    }

    @Override
    public ParentPaperDTO selectDetail(Long studentId) {

        ParentPaperDTO parentPaperDTO = new ParentPaperDTO();
        List<ResponseQuestionDto<LogicOption>> responseQuestionDtos = new ArrayList<>();

        List<ParentQuestion> parentQuestions = selectAllQuestions();
        parentQuestions.stream().map(
                parentQuestion -> {
                    ResponseQuestionDto<LogicOption> responseQuestionDto = new ResponseQuestionDto<>();
                    Long id = parentQuestion.getId();
                    //父子题切换
                    String questionTypeKey = Func.toStr(parentQuestion.getQuestionType());
                    if (QuestionEnum.MATRIX.getKey().equals(questionTypeKey) || QuestionEnum.PARENT_CHILD_DROP_DOWN.getKey().equals(questionTypeKey)) {
                        //直接查子题
                        List<ParentQuestion> subQ = baseMapper.selectList(new LambdaQueryWrapper<ParentQuestion>().eq(ParentQuestion::getParentId, id));

                        List<LogicQuestion<LogicOption>> subQuestions = new ArrayList<>();
                        subQ.stream().map(sub -> {
                            LogicQuestion<LogicOption> logicOptionLogicQuestion = new LogicQuestion<>();
                            logicOptionLogicQuestion.setExplain(sub.getRemarks());
                            logicOptionLogicQuestion.setIsJump("1");

                            logicOptionLogicQuestion.setQId(Func.toStr(sub.getId()));
                            logicOptionLogicQuestion.setQTitle(sub.getTitle());
                            logicOptionLogicQuestion.setSort(sub.getSort());
                            logicOptionLogicQuestion.setQType(Func.toInt(sub.getQuestionType()));
                            List<LogicOption> options = this.selectOptionList(sub.getId());
                            // 其他选项，id为0
//                            options.stream().forEach(logicOption -> {
//                                if (logicOption.getOType() == 1) {
//                                    logicOption.setOId("0");
//                                }
//                            });
                            logicOptionLogicQuestion.setOptions(options);
                            subQuestions.add(logicOptionLogicQuestion);
                            return sub;
                        }).collect(Collectors.toList());
                        //TODO：跳题逻辑，放在ParentOption的属性扩展中
//						responseQuestionDto.setExplain();
//						responseQuestionDto.setIsJump();
                        responseQuestionDto.setQId(Func.toStr(parentQuestion.getId()));
                        responseQuestionDto.setQTitle(parentQuestion.getTitle());
                        responseQuestionDto.setSort(parentQuestion.getSort());
                        responseQuestionDto.setQType(Func.toInt(parentQuestion.getQuestionType()));
                        responseQuestionDto.setSubQuestions(subQuestions);
                        responseQuestionDtos.add(responseQuestionDto);
                    } else {
                        //过滤父子题，仅保留一个题干类型题目，排除矩阵、父子下拉类型的子题干
                        if (parentQuestion.getParentId() == 0L) {
                            //赋值题干
                            responseQuestionDto.setQId(Func.toStr(parentQuestion.getId()));
                            responseQuestionDto.setQTitle(parentQuestion.getTitle());
                            responseQuestionDto.setSort(parentQuestion.getSort());
                            responseQuestionDto.setQType(parentQuestion.getQuestionType().intValue());
                            responseQuestionDto.setExplain(parentQuestion.getRemarks());
                            //获取选项
                            //赋值选项
                            List<LogicOption> options = this.selectOptionList(parentQuestion.getId());
//                            options.stream().forEach(logicOption -> {
//                                if (logicOption.getOType() == 1) {
//                                    logicOption.setOId("0");
//                                }
//                            });
                            //聚合
                            responseQuestionDto.setOptions(options);
                            responseQuestionDtos.add(responseQuestionDto);
                        }
                    }
                    return parentQuestion;
                }
        ).collect(Collectors.toList());
        // 新增试卷
        ParentPaper paper = new ParentPaper();
        Long userId = SecureUtil.getUserId();
        paper.setCreateUser(userId.longValue());
        paper.setStartTime(LocalDateTime.now());
        paper.setIsFinish(0);
        paper.setTenantCode(SecureUtil.getTenantCode());
        paper.setQuestionnaireId(1L);
        paper.setChildId(studentId);
        paperMapper.insert(paper);
        parentPaperDTO.setResponseQuestionDtos(responseQuestionDtos);
        parentPaperDTO.setPaperId(paper.getId());
        try {
            String studentName = paperMapper.getStudentName(studentId);
            parentPaperDTO.setName(studentName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parentPaperDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveResult(ResultDTO resultDTO) {
        // 保存答题
        List<QResult> result = resultDTO.getResult();
        result.stream().forEach(qResult -> {
            //1	single_choice	单选
            //2	multiple_choice	多选
            //3	drop_down	下拉
            //4	matrix	矩阵
            //5	fill_in_the_blanks	填空
            //6	parent_child_drop_down	父子题（下拉类型）
            ParentPaperQuestion question = new ParentPaperQuestion();
            question.setQuestionId(Long.valueOf(qResult.getQId()));
            question.setQuestionType(qResult.getQType());
            question.setPaperId(resultDTO.getPaperId());
            question.setTenantCode(SecureUtil.getTenantCode());
            if (qResult.getQType() == 1) {
                // 单选
                SingleChoice singleChoice = qResult.getSingleChoice();
                question.setOptionId(singleChoice.getOId());
                if (singleChoice.getOType() == 0) {
                    // 选项

                } else if (singleChoice.getOType() == 1) {
                    // 填空
                    question.setOtherContent(singleChoice.getExtResult().getInputContent());
                }
                paperQuestionMapper.insert(question);
            } else if (qResult.getQType() == 2) {
                // 多选
                List<SingleChoice> multipleChoice = qResult.getMultipleChoice();
                List<String> collect = multipleChoice.stream().map(SingleChoice::getOId).collect(Collectors.toList());
                question.setOptionId(Func.toStr(collect));
                paperQuestionMapper.insert(question);
            } else if (qResult.getQType() == 5) {
                // 填空
                question.setOtherContent(qResult.getInputContent());
                paperQuestionMapper.insert(question);
            } else if (qResult.getQType() == 6 || qResult.getQType() == 4) {
                // 父子 | 矩阵
                List<Answer> answers = qResult.getAnswers();
                question.setPQuestionId(Long.valueOf(qResult.getQId()));
                // 子题
                answers.stream().forEach(answer -> {
                    ParentPaperQuestion question1 = BeanUtil.copy(question, ParentPaperQuestion.class);
                    question1.setQuestionId(Long.valueOf(answer.getQId()));
                    if (answer.getQType() == 1 || answer.getQType() == 3) {
                        // 单选/下拉
                        SingleChoice singleChoice = answer.getSingleChoice();
                        question1.setOptionId(singleChoice.getOId());
                        if (singleChoice.getOType() == 0 || singleChoice.getOType() == 2) {
                            // 选项|单选框

                        } else if (singleChoice.getOType() == 1) {
                            // 填空
                            question1.setOtherContent(singleChoice.getExtResult().getInputContent());
                        }
                    } else if (answer.getQType() == 2) {
                        // 多选
                        List<SingleChoice> multipleChoice = answer.getMultipleChoice();
                        List<String> collect = multipleChoice.stream().map(SingleChoice::getOId).collect(Collectors.toList());
                        question1.setOptionId(Func.toStr(collect));
                    } else if (answer.getQType() == 5) {
                        // 填空
                        if (ObjectUtils.isNotEmpty(answer.getSingleChoice().getExtResult().getInputContent())) {
                            question1.setOtherContent(answer.getSingleChoice().getExtResult().getInputContent());
                        }
                    }
                    // 多条插入
                    paperQuestionMapper.insert(question1);
                });


            }
        });
        // 修改试卷完成状态
        ParentPaper paper = paperMapper.selectById(resultDTO.getPaperId());
        paper.setIsFinish(1);
        paperMapper.updateById(paper);

        // 更新家长他评
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            reportClient.updatePersonalReportsParentRating(paper.getChildId());
        });

        return true;
    }

    private List<LogicOption> selectOptionList(Long id) {
        List<ParentOption> parentOptions = parentOptionMapper.selectList(new LambdaQueryWrapper<ParentOption>().eq(ParentOption::getQuestionId, id));

        //赋值选项
        List<LogicOption> options = new ArrayList<>();
        parentOptions.stream().map(parentOption -> {
            LogicOption logicOption = new LogicOption();
            logicOption.setOId(Func.toStr(parentOption.getId()));
            logicOption.setOTitle(parentOption.getTitle());
            logicOption.setOType(Func.toInt(parentOption.getOptionType()));
            if (Func.isNotEmpty(parentOption.getExtParam())) {
                logicOption.setJumpLogic(Func.readJson(parentOption.getExtParam(), IdsJumpLogic.class));
            }
            options.add(logicOption);
            return parentOption;
        }).collect(Collectors.toList());
        return options;
    }
}

