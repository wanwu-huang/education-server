package org.mentpeak.test.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mentpeak.common.util.IntervalUtil;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.core.redis.core.RedisService;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.test.dto.TeacherPaperDTO;
import org.mentpeak.test.entity.*;
import org.mentpeak.test.mapper.*;
import org.mentpeak.test.service.ITestTeacherPaperService;
import org.mentpeak.test.entity.mongo.TeacherRating;
import org.mentpeak.test.service.ReportUserService;
import org.mentpeak.test.vo.QuestionOptionVO;
import org.mentpeak.test.vo.TeacherOption;
import org.mentpeak.test.vo.TeacherPaperVO;
import org.mentpeak.test.vo.TeacherStudentVO;
import org.mentpeak.test.vo.TestTeacherPaperVO;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 教师评定试卷信息表 服务实现类
 *
 * @author lxp
 * @since 2022-08-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TestTeacherPaperServiceImpl extends BaseServiceImpl<TestTeacherPaperMapper, TestTeacherPaper> implements ITestTeacherPaperService {

    private final MongoTemplate mongoTemplate;
    private final TestTeacherQuestionMapper questionMapper;
    private final TestTeacherOptionMapper optionMapper;

    private final RedisService redisService;
    private final TestTeacherConclusionMapper testTeacherConclusionMapper;

    private final TestTeacherPaperRecordMapper testTeacherPaperRecordMapper;

    private final TestTeacherPaperMapper testTeacherPaperMapper;

    private final ReportUserService reportUserService;

    @Override
    public IPage<TestTeacherPaperVO> selectTestTeacherPaperPage(IPage<TestTeacherPaperVO> page, TestTeacherPaperVO testTeacherPaper) {
        return page.setRecords(baseMapper.selectTestTeacherPaperPage(page, testTeacherPaper));
    }


    @Override
    public boolean dataToCache() {

        List<TestTeacherQuestion> questionList = questionMapper.selectList(Wrappers.<TestTeacherQuestion>lambdaQuery()
                .orderByAsc(TestTeacherQuestion::getSort));

        questionList.forEach(question -> {

            QuestionOptionVO<TeacherOption> questionOption = new QuestionOptionVO<>();

            List<TestTeacherOption> optionList = optionMapper.selectList(Wrappers.<TestTeacherOption>lambdaQuery()
                    .eq(TestTeacherOption::getQuestionId, question.getId()).orderByAsc(TestTeacherOption::getSort));

            List<TeacherOption> teacherOptionList = getTeacherOptionList(optionList);

            questionOption.setOptions(teacherOptionList);
            questionOption.setQId(question.getId().toString());
            questionOption.setQTitle(question.getTitle());
            questionOption.setQType(question.getQuestionType());

            // 题目信息
            String key = "education" + ":" + "teacher" + ":" + "question";
            key += ":" + questionOption.getQId();
            String qt = JSONObject.toJSON(questionOption).toString();
            redisService.set(key, qt);

        });
        log.info("=== 缓存教师评定数据 ===");
        return true;
    }

    /**
     * 题支数据处理
     *
     * @param optionList
     * @return
     */
    static List<TeacherOption> getTeacherOptionList(List<TestTeacherOption> optionList) {

        List<TeacherOption> teacherOptionList = new ArrayList<>();

        optionList.forEach(option -> {

            TeacherOption teacherOption = new TeacherOption();
            teacherOption.setOId(option.getId().toString());
            teacherOption.setOTitle(option.getTitle());
            teacherOption.setScore(option.getScore());
            // 单选
            teacherOption.setOType(1);
            teacherOptionList.add(teacherOption);
        });

        return teacherOptionList;
    }

    @Override
    public TeacherRating generateTeacherRating(Long gradeId, Long classId) {

        /**
         * 生成问卷
         *
         * 没有数据
         * 		生成问卷	-> 返回数据 【学生 + 问卷数据】
         * 	    老师 -> 班级 -> 对应学生
         *
         * 有数据
         * 		找到问卷  -> 返回数据 【根据评价记录ID查找数据】
         */

        Long userId = SecureUtil.getUserId();

        // 是否存在保存未提交数据
        List<TestTeacherPaper> teacherPaperList = baseMapper.selectList(Wrappers.<TestTeacherPaper>lambdaQuery()
                .eq(TestTeacherPaper::getGradeId, gradeId)
                .eq(TestTeacherPaper::getClassId, classId)
                .eq(TestTeacherPaper::getUserId, SecureUtil.getUserId())
                .eq(TestTeacherPaper::getIsFinish, 2));

        if (teacherPaperList.size() > 0) {
            // 直接返回 记录数据
            Query query = new Query();
            String evaluationRecordsId = teacherPaperList.get(0).getEvaluationRecordsId();
            query.addCriteria(Criteria.where("id").is(evaluationRecordsId));
            TeacherRating teacherRating = mongoTemplate.findOne(query, TeacherRating.class);
            return teacherRating;
        }

        // 生成问卷
        TestTeacherPaper teacherPaper = new TestTeacherPaper();
        teacherPaper.setTenantCode(SecureUtil.getTenantCode());
        teacherPaper.setUserId(userId);
        teacherPaper.setStartTime(LocalDateTime.now());
        teacherPaper.setQuestionnaireId(2L);
        teacherPaper.setGradeId(gradeId);
        teacherPaper.setClassId(classId);
        baseMapper.insert(teacherPaper);


        // 查询年级班级对应的学生信息
        List<TeacherStudentVO> studentVOList = baseMapper.stuListByGradeClassId(gradeId,
            classId);

        ArrayList<TeacherRating.StuQuestionData> stuQuestionDataList = new ArrayList<>();

        // 题目数据
        TeacherRating.StuQuestionData questionData = getQuestionData();

        studentVOList.forEach(teacherStudentVO -> {
            TeacherRating.StuQuestionData stuQuestionData = Func.copy(questionData, TeacherRating.StuQuestionData.class);
            stuQuestionData.setUserId(teacherStudentVO.getUserId());
            stuQuestionData.setRealName(teacherStudentVO.getRealName());
            stuQuestionDataList.add(stuQuestionData);
        });

        TeacherRating teacherRating = TeacherRating.builder()
                .paperId(teacherPaper.getId())
                .stuQuestionDataList(stuQuestionDataList)
                .build();

        return teacherRating;
    }

    /**
     * 获取题目数据
     * @return
     */
    TeacherRating.StuQuestionData getQuestionData(){

        TeacherRating.StuQuestionData questionData = new TeacherRating.StuQuestionData();

        String key = "education" + ":" + "teacher" + ":" + "question";

        Object stu = redisService.get(key + ":" + 10001);
        Object emotion = redisService.get(key + ":" + 10002);
        Object interpersonal = redisService.get(key + ":" + 10003);
        Object bully = redisService.get(key + ":" + 10004);
        Object be = redisService.get(key + ":" + 10005);
        Object discip = redisService.get(key + ":" + 10006);

        QuestionOptionVO<TeacherOption> stuQuestionOption = JSONObject.parseObject(stu.toString(),new TypeReference<QuestionOptionVO<TeacherOption>>(){});
        List<TeacherOption> stuOptionList = stuQuestionOption.getOptions();

        QuestionOptionVO<TeacherOption> emotionQuestionOption = JSONObject.parseObject(emotion.toString(),new TypeReference<QuestionOptionVO<TeacherOption>>(){});
        List<TeacherOption> emotionOptionList = emotionQuestionOption.getOptions();

        QuestionOptionVO<TeacherOption> interpersonalQuestionOption = JSONObject.parseObject(interpersonal.toString(),new TypeReference<QuestionOptionVO<TeacherOption>>(){});
        List<TeacherOption> interpersonalOptionList = interpersonalQuestionOption.getOptions();

        QuestionOptionVO<TeacherOption> bullyQuestionOption = JSONObject.parseObject(bully.toString(),new TypeReference<QuestionOptionVO<TeacherOption>>(){});
        List<TeacherOption> bullyOptionList = bullyQuestionOption.getOptions();

        QuestionOptionVO<TeacherOption> beQuestionOption = JSONObject.parseObject(be.toString(),new TypeReference<QuestionOptionVO<TeacherOption>>(){});
        List<TeacherOption> beOptionList = beQuestionOption.getOptions();

        QuestionOptionVO<TeacherOption> discipQuestionOption = JSONObject.parseObject(discip.toString(),new TypeReference<QuestionOptionVO<TeacherOption>>(){});
        List<TeacherOption> discipOptionList = discipQuestionOption.getOptions();

        // 学习成绩显著下滑
        questionData.setStudentAchievement(TeacherRating.QuestionData.builder()
                .questionId(stuQuestionOption.getQId())
                .optionId(stuOptionList.stream().map(TeacherOption::getOId).toArray(String[]::new))
                .score(stuOptionList.stream().map(TeacherOption::getScore).toArray(Integer[]::new))
                .select(1)
                .build());
        // 明显的情绪问题
        questionData.setEmotionalProblems(TeacherRating.QuestionData.builder()
                .questionId(emotionQuestionOption.getQId())
                .optionId(emotionOptionList.stream().map(TeacherOption::getOId).toArray(String[]::new))
                .score(emotionOptionList.stream().map(TeacherOption::getScore).toArray(Integer[]::new))
                .select(1)
                .build());
        // 严重的人际冲突
        questionData.setInterpersonalConflict(TeacherRating.QuestionData.builder()
                .questionId(interpersonalQuestionOption.getQId())
                .optionId(interpersonalOptionList.stream().map(TeacherOption::getOId).toArray(String[]::new))
                .score(interpersonalOptionList.stream().map(TeacherOption::getScore).toArray(Integer[]::new))
                .select(1)
                .build());
        // 欺负霸凌同学
        questionData.setBullyingClassmates(TeacherRating.QuestionData.builder()
                .questionId(bullyQuestionOption.getQId())
                .optionId(bullyOptionList.stream().map(TeacherOption::getOId).toArray(String[]::new))
                .score(bullyOptionList.stream().map(TeacherOption::getScore).toArray(Integer[]::new))
                .select(1)
                .build());
        // 被霸凌或被孤立
        questionData.setBeIsolated(TeacherRating.QuestionData.builder()
                .questionId(beQuestionOption.getQId())
                .optionId(beOptionList.stream().map(TeacherOption::getOId).toArray(String[]::new))
                .score(beOptionList.stream().map(TeacherOption::getScore).toArray(Integer[]::new))
                .select(1)
                .build());
        // 严重的违纪行为
        questionData.setDisciplinaryOffence(TeacherRating.QuestionData.builder()
                .questionId(discipQuestionOption.getQId())
                .optionId(discipOptionList.stream().map(TeacherOption::getOId).toArray(String[]::new))
                .score(discipOptionList.stream().map(TeacherOption::getScore).toArray(Integer[]::new))
                .select(1)
                .build());

        return questionData;

    }

    @Override
    public boolean saveNotCommit(TeacherRating teacherRating) {
        // 保存评价记录 更新数据
        teacherRating.setId(null);
        TeacherRating rat = mongoTemplate.save(teacherRating);
        TestTeacherPaper teacherPaper = new TestTeacherPaper();
        teacherPaper.setId(teacherRating.getPaperId());
        teacherPaper.setEvaluationRecordsId(rat.getId());
        teacherPaper.setIsFinish(2);
        baseMapper.updateById(teacherPaper);
        return true;
    }

    @Override
    public boolean saveCommit(TeacherRating teacherRating) {

        // 保存评价记录
        teacherRating.setId(null);
        TeacherRating rat = mongoTemplate.save(teacherRating);
        TestTeacherPaper teacherPaper = new TestTeacherPaper();
        teacherPaper.setId(teacherRating.getPaperId());
        teacherPaper.setEvaluationRecordsId(rat.getId());
        teacherPaper.setIsFinish(1);
        baseMapper.updateById(teacherPaper);

        // 处理数据 计算分数 以及对应结论
        Long paperId = teacherRating.getPaperId();
        Long userId = SecureUtil.getUserId();
        String tenantCode = SecureUtil.getTenantCode();

        // 数据
        List<TeacherRating.StuQuestionData> stuQuestionDataList = teacherRating.getStuQuestionDataList();

        // 获取结论
        List<TestTeacherConclusion> conclusionList = testTeacherConclusionMapper.selectList(null);

        List<Long> userList = new ArrayList<>();

      stuQuestionDataList.forEach(stuQuestionData -> {

            // 处理每个学生数据
            Long stuId = stuQuestionData.getUserId();

            // 学习成绩显著下滑
            Integer stuSelectValue = stuQuestionData.getStudentAchievement().getSelect();
            Integer stuScore = stuQuestionData.getStudentAchievement().getScore()[stuSelectValue];

            // 明显的情绪问题
            Integer emotionSelectValue = stuQuestionData.getEmotionalProblems().getSelect();
            Integer emotionScore = stuQuestionData.getEmotionalProblems().getScore()[emotionSelectValue];

            // 严重的人际冲突
            Integer interSelectValue = stuQuestionData.getInterpersonalConflict().getSelect();
            Integer interScore = stuQuestionData.getInterpersonalConflict().getScore()[interSelectValue];

            // 欺负霸凌同学
            Integer bullySelectValue = stuQuestionData.getBullyingClassmates().getSelect();
            Integer bullyScore = stuQuestionData.getBullyingClassmates().getScore()[bullySelectValue];

            // 被霸凌或被孤立
            Integer beSelectValue = stuQuestionData.getBeIsolated().getSelect();
            Integer beScore = stuQuestionData.getBeIsolated().getScore()[beSelectValue];

            // 严重的违纪行为
            Integer disSelectValue = stuQuestionData.getDisciplinaryOffence().getSelect();
            Integer disScore = stuQuestionData.getDisciplinaryOffence().getScore()[disSelectValue];

            Integer totalScore = stuScore + emotionScore + interScore + bullyScore + beScore + disScore;

            conclusionList.forEach(testTeacherConclusion -> {
                boolean result = IntervalUtil.isInTheInterval(totalScore.toString(), testTeacherConclusion.getScope());
                if(result){
                    // 插入问卷测试记录
                    TestTeacherPaperRecord testTeacherPaperRecord = new TestTeacherPaperRecord();
                    testTeacherPaperRecord.setTenantCode(tenantCode);
                    testTeacherPaperRecord.setPaperId(paperId);
                    testTeacherPaperRecord.setUserId(userId);
                    testTeacherPaperRecord.setStuId(stuId);
                    testTeacherPaperRecord.setScore(totalScore);
                    testTeacherPaperRecord.setRiskLevel(testTeacherConclusion.getRiskIndex());
                    testTeacherPaperRecord.setRiskResult(testTeacherConclusion.getRiskResult());
                    testTeacherPaperRecordMapper.insert(testTeacherPaperRecord);
                }
            });

          userList.add(stuId);
        });

        //  更新个人报告教师评定数据
        reportUserService.updatePersonalReportsTeacherRating(userList);

        return true;
    }


    @Override
    public Page<TeacherPaperVO> teacherRatingList(IPage<TeacherPaperVO> page,
        TeacherPaperDTO teacherPaperDTO) {
        Long userId = SecureUtil.getUserId();
        Page<TeacherPaperVO> teacherPaperVOPage = testTeacherPaperMapper.teacherRatingList(page, userId,teacherPaperDTO.getGradeId(), teacherPaperDTO.getClassId(),teacherPaperDTO.getStatus());
        List<TeacherPaperVO> teacherPaperVOList = teacherPaperVOPage.getRecords();
        // 上一页最大值
        long count = (page.getCurrent() - 1) * page.getSize();
        if(count < 0){
            count = 0;
        }
        AtomicInteger num = new AtomicInteger();
        Integer finalCount = Func.toInt(count);
        teacherPaperVOList.forEach(teacherPaperVO -> {
            teacherPaperVO
                .setFinishStatus(teacherPaperVO.getIsFinish() == 1 ? "已提交"
                    : (teacherPaperVO.getIsFinish() == 2 ? "暂时保存"
                        : (teacherPaperVO.getIsFinish() == 3 ? "未评定" : "" ) ));
            // 自动生成序号
            num.getAndIncrement();
            teacherPaperVO.setId(finalCount + num.get());
        });
        return teacherPaperVOPage;
    }
}
