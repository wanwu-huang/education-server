package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mentpeak.common.util.DateUtil;
import org.mentpeak.common.util.IntervalUtil;
import org.mentpeak.common.util.MathUtil;
import org.mentpeak.core.auth.PlatformUser;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.log.exception.PlatformApiException;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.parent.vo.ParentOptionVO;
import org.mentpeak.test.dto.ReportUserDTO;
import org.mentpeak.test.entity.*;
import org.mentpeak.test.entity.mongo.PersonalReport;
import org.mentpeak.test.entity.mongo.PersonalReport.UserInfo;
import org.mentpeak.test.entity.mongo.PersonalReportData;
import org.mentpeak.test.mapper.*;
import org.mentpeak.test.service.IReportTeacherClassService;
import org.mentpeak.test.service.ReportUserService;
import org.mentpeak.test.strategy.scoring.ColorEnum;
import org.mentpeak.test.strategy.scoring.DimensionStrategy;
import org.mentpeak.test.strategy.scoring.ScoreStrategyFactory;
import org.mentpeak.test.vo.DictVO;
import org.mentpeak.test.vo.DimensionIndexData;
import org.mentpeak.test.vo.ParentPaperQuestionVO;
import org.mentpeak.test.vo.ReportUserVO;
import org.mentpeak.user.entity.User;
import org.mentpeak.user.entity.UserExt;
import org.mentpeak.user.feign.IUserClient;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 个人报告 服务实现
 *
 * @author demain_lee
 * @since 2022-08-09
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReportUserServiceImpl implements ReportUserService {

    private final MongoTemplate mongoTemplate;

    private final TestPaperRecordMapper paperRecordMapper;

    private final TestPaperMapper paperMapper;


    private final ScoreStrategyFactory scoreStrategyFactory;

    private final TestPaperQuestionMapper paperQuestionMapper;

    private final TestDimensionIndexConclusionMapper dimensionIndexConclusionMapper;

    private final IUserClient userClient;

    private final ClassUserMapper classUserMapper;

    private final TestTaskMapper taskMapper;

    private final TestTeacherPaperRecordMapper testTeacherPaperRecordMapper;

    private final RedisTemplate redisTemplate;

    private final TestTaskUserMapper taskUserMapper;

    private final TestOptionMapper optionMapper;

    private final TestIndexMapper indexMapper;

    private final TestQuestionMapper questionMapper;

    private final IReportTeacherClassService reportTeacherClassService;


    private final ReportTeacherClassMapper reportTeacherClassMapper;

    @Override
    public PersonalReport reportInfo(Long taskId, Long userId) {

        AtomicReference<PersonalReport> report = new AtomicReference<>(
                PersonalReport.builder().build());
        report.get().setIsPermission(0);

        // 判断当前用户是否有权限访问
        String roleId = SecureUtil.getUser().getRoleId();
        Long teacherId = SecureUtil.getUserId();
        if (roleId.equals("5")) {
            // 老师
            List<ReportTeacherClass> reportTeacherClassesList = reportTeacherClassMapper.selectList(
                    Wrappers.<ReportTeacherClass>lambdaQuery()
                            .eq(ReportTeacherClass::getTeacherId, teacherId)
                            .eq(ReportTeacherClass::getTaskId, taskId)
                            .eq(ReportTeacherClass::getIsDeleted, 0));
            UserExt userExt = userClient.userExtInfoById(userId).getData();
            reportTeacherClassesList.forEach(reportTeacherClass -> {
                if (Func.toStr(reportTeacherClass.getGradeId()).equals(userExt.getGrade()) && reportTeacherClass.getClassId().equals(userExt.getClassId())) {
                    report.set(getReport(report.get(), taskId, userId));
                }
            });

        } else {
            report.set(getReport(report.get(), taskId, userId));
        }

        return report.get();

    }

    /**
     * 获取报告信息
     *
     * @param report
     * @param taskId
     * @param userId
     * @return
     */
    PersonalReport getReport(PersonalReport report, Long taskId, Long userId) {

        List<TestPaperRecord> testPaperRecordList = paperRecordMapper.paperRecordList(taskId, userId);
        String reportId = "";

        if (testPaperRecordList.size() > 0) {
            reportId = testPaperRecordList.get(0).getReportId();
        } else {
            throw new PlatformApiException("暂无数据");
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(reportId));
        report = mongoTemplate.findOne(query, PersonalReport.class);

        // 替换数据 带 X 的内容
        User user = userClient.allUserInfoById(userId).getData();
        String realName = user.getRealName();

        report = replacePersonalReport(report, realName);
        report.setIsPermission(1);
        return report;
    }


    /**
     * 替换报告数据
     *
     * @param personalReport
     * @param realName
     * @return
     */
    static PersonalReport replacePersonalReport(PersonalReport personalReport, String realName) {

        personalReport.getLearningStatus().getTotalResult().setResultDes(replaceContent(personalReport.getLearningStatus().getTotalResult().getResultDes(), realName));
        personalReport.getLearningStatus().getChartData().forEach(chartData -> {
            chartData.setResultDes(replaceContent(chartData.getResultDes(), realName));
        });

        personalReport.getBehavior().getTotalResult().setResultDes(replaceContent(personalReport.getBehavior().getTotalResult().getResultDes(), realName));
        personalReport.getBehavior().getChartData().forEach(chartData -> {
            chartData.setResultDes(replaceContent(chartData.getResultDes(), realName));
        });

        personalReport.getMentalToughness().getTotalResult().setResultDes(replaceContent(personalReport.getMentalToughness().getTotalResult().getResultDes(), realName));
        personalReport.getMentalToughness().getChartData().forEach(chartData -> {
            chartData.setResultDes(replaceContent(chartData.getResultDes(), realName));
        });

        personalReport.getStressIndex().getTotalResult().setResultDes(replaceContent(personalReport.getStressIndex().getTotalResult().getResultDes(), realName));
        personalReport.getStressIndex().getChartData().forEach(chartData -> {
            chartData.setResultDes(replaceContent(chartData.getResultDes(), realName));
        });

        personalReport.getEmotionalIndex().getTotalResult().setResultDes(replaceContent(personalReport.getEmotionalIndex().getTotalResult().getResultDes(), realName));
        personalReport.getEmotionalIndex().getChartData().forEach(chartData -> {
            chartData.setResultDes(replaceContent(chartData.getResultDes(), realName));
        });

        personalReport.getSleepIndex().getTotalResult().setResultDes(replaceContent(personalReport.getSleepIndex().getTotalResult().getResultDes(), realName));

        return personalReport;
    }

    /**
     * 替换内容
     *
     * @param oldStr     旧内容
     * @param replaceStr 替换内容
     * @return 替换后内容
     */
    static String replaceContent(String oldStr, String replaceStr) {

        String newStr = oldStr;

        if (Func.isNotEmpty(oldStr.trim())) {
            String regEx = "[X]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(oldStr);
            newStr = m.replaceAll(replaceStr);
        }
        return newStr;
    }

    @Override
    public boolean generateReport(Long paperId, Long userId) {

        log.info("===== 开始生成用户{}个人报告 ==== {}", userId, paperId);
        // 试卷是否完成
        TestPaper paper = paperMapper.selectById(paperId);
        if (paper.getIsFinish() == 0) {
            throw new PlatformApiException("试卷未完成");
        }

        /**
         * 维度
         *
         * 1546788164255932417   学习状态
         * 1546788937710755842   品行表现
         * 1546789344491134978   心理韧性
         * 1546789419250409474   综合压力
         * 1546789515505491970   情绪指数
         *
         * 1546789617431273473  自杀意念
         * 1546789785358622721  睡眠指数
         * 1546789869571858434  数据有效性
         */

        // 获取当前试卷各维度分数 以及对应结论

        // 个人报告基础数据
        PersonalReportData personalReportData = PersonalReportData.builder().build();

        // 个人报告数据
        PersonalReport personalReport = PersonalReport.builder().build();

        // 学习状态
        getStuDimensionIndexData(personalReportData, personalReport, paperId);

        // 品行表现
        getBehaviorDimensionIndexData(personalReportData, personalReport, paperId);

        // 心理韧性
        getMentalDimensionIndexData(personalReportData, personalReport, paperId);

        // 综合压力
        getOverallDimensionIndexData(personalReportData, personalReport, paperId);

        // 情绪指数
        getEmotionalDimensionIndexData(personalReportData, personalReport, paperId);

        // 睡眠指数
        getSleepIndexData(personalReportData, personalReport, paperId);

        // 测评概况
        getTestOverview(personalReport);

        // 作答有效性
        getValidityAnswersData(personalReportData, personalReport, paperId);

        // 自杀意念 (注：心理健康评级 部分字段 已赋值 )
        getSuicidalIdeationData(personalReportData, personalReport, paperId);

        // 学生评定等级
        getStuRatingLevel(personalReportData, personalReport, paperId);

        // 家长他评
        getParentRatingLevel(personalReportData, personalReport, userId);

        // 教师他评
        getTeacherRatingLevel(personalReportData, personalReport, userId);

        // 建议关注等级
        getRecommendedAttentionLevel(personalReport);

        // 个人信息
        getUserInfo(personalReportData, personalReport, paperId, userId, paper.getTaskId());

        personalReport.setTestTime(DateUtil.LocalDateTimeToStringTime(paper.getStartTime()));

        personalReportData.setPaperId(paperId.toString());

        personalReport.setCreateTime(DateUtil.LocalDateTimeToStringTime(LocalDateTime.now()));
        personalReportData.setCreateTime(DateUtil.LocalDateTimeToStringTime(LocalDateTime.now()));

        mongoTemplate.save(personalReportData);
        mongoTemplate.save(personalReport);

        // 更新 测评任务用户数据
        updateTaskUserData(personalReport, paper, userId);

        // 统计每个指标下 数据
        statisticData(paper.getTaskId(), paperId, userId);

        // 统计压力事件数据
        if (personalReport.getAnswerIsValidity() == 1) {
            // 只统计有效人员数据
            statisticStressData(paper.getTaskId(), paperId, userId);
        }

        // 更新报告记录数据
        updatePaperRecordData(paper.getTaskId(), userId, personalReport.getId());

        // 更新班级报告
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String tenantCode = SecureUtil.getTenantCode();
        CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            CompletableFuture.runAsync(() -> updateClassReportData(paper.getTaskId(), userId, tenantCode));
        });

        log.info("===== 用户{}个人报告生成结束 ==== {}", userId, paperId);
        return true;
    }


    /**
     * 更新报告记录数据
     */
    void updatePaperRecordData(Long taskId, Long userId, String reportId) {

        List<TestPaperRecord> testPaperRecordList = paperRecordMapper.paperRecordList(taskId, userId);
        if (testPaperRecordList.size() > 0) {
            TestPaperRecord testPaperRecord = testPaperRecordList.get(0);
            testPaperRecord.setReportId(reportId);
            paperRecordMapper.updateById(testPaperRecord);
        }

    }


    /**
     * 更新班级报告
     *
     * @param taskId
     * @param userId
     */
    void updateClassReportData(Long taskId, Long userId, String tenantCode) {
        UserExt userExt = taskUserMapper.getUserExtById(userId);
        if (Func.isNotEmpty(userExt.getGrade()) && Func.isNotEmpty(userExt.getClassId())) {
            reportTeacherClassService.addClassReport(taskId, Func.toLong(userExt.getGrade()), userExt.getClassId(), tenantCode);
        }
        log.info("更新班级报告数据");
    }


    /**
     * 学习状态
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getStuDimensionIndexData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        DimensionStrategy stuStrategy = scoreStrategyFactory.getStrategy(1546788164255932417L);
        // 维度指标数据
        DimensionIndexData stuDimensionIndexData = stuStrategy.dimensionScore(paperId);

        Map<Long, PersonalReport.ChartData> indexData = stuDimensionIndexData.getIndexData();

        // 个人报告基础数据
        // 学习状态 维度计分
        personalReportData.setStudyStatusScore(Func.toDouble(stuDimensionIndexData.getTotalData().getScore()));

        /**
         *  1546788164788609025 学习态度
         *  1546788174330650626 时间管理
         *  1546788189962821634 学习倦怠
         */
        PersonalReport.ChartData learningChartData = indexData.get(1546788164788609025L);
        PersonalReport.ChartData timeManagementChartData = indexData.get(1546788174330650626L);
        PersonalReport.ChartData learningBurnoutChartData = indexData.get(1546788189962821634L);

        // 学习态度 指标计分
        personalReportData.setLearningAttitudeScore(Func.toDouble(learningChartData.getScore()));
        // 时间管理 指标计分
        personalReportData.setTimeManagementScore(Func.toDouble(timeManagementChartData.getScore()));
        // 学习倦怠 指标计分
        personalReportData.setLearningBurnoutScore(Func.toDouble(learningBurnoutChartData.getScore()));

        // 个人报告数据
        PersonalReport.ResultData resultData = new PersonalReport.ResultData();

        resultData.setTotalResult(stuDimensionIndexData.getTotalData());

        List<PersonalReport.ChartData> chartDataList = indexData.values().stream().collect(Collectors.toList());
        resultData.setChartData(chartDataList);

        personalReport.setLearningStatus(resultData);
    }


    /**
     * 品行表现
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getBehaviorDimensionIndexData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        DimensionStrategy behaviorStrategy = scoreStrategyFactory.getStrategy(1546788937710755842L);
        // 维度指标数据
        DimensionIndexData behaviorDimensionIndexData = behaviorStrategy.dimensionScore(paperId);


        Map<Long, PersonalReport.ChartData> indexData = behaviorDimensionIndexData.getIndexData();

        // 个人报告基础数据
        // 品行表现 维度计分
        personalReportData.setBehaviorScore(Func.toDouble(behaviorDimensionIndexData.getTotalData().getScore()));

        /**
         *  1546788938151157761	道德性
         *  1546788944375504898	稳定性
         *  1546788949077319682	纪律性
         *  1546788953720414210	其他表现
         */
        PersonalReport.ChartData moralChartData = indexData.get(1546788938151157761L);
        PersonalReport.ChartData stabilityChartData = indexData.get(1546788944375504898L);
        PersonalReport.ChartData disciplineChartData = indexData.get(1546788949077319682L);
        PersonalReport.ChartData otherPerformanceChartData = indexData.get(1546788953720414210L);

        // 道德性 指标计分
        personalReportData.setMoralScore(Func.toDouble(moralChartData.getScore()));
        // 稳定性 指标计分
        personalReportData.setStabilityScore(Func.toDouble(stabilityChartData.getScore()));
        // 纪律性 指标计分
        personalReportData.setDisciplineScore(Func.toDouble(disciplineChartData.getScore()));
        // 其他表现 指标计分
        personalReportData.setOtherPerformanceScore(Func.toDouble(otherPerformanceChartData.getScore()));

        // 个人报告数据
        PersonalReport.ResultData resultData = new PersonalReport.ResultData();

        resultData.setTotalResult(behaviorDimensionIndexData.getTotalData());

        List<PersonalReport.ChartData> chartDataList = indexData.values().stream().collect(Collectors.toList());
        resultData.setChartData(chartDataList);

        personalReport.setBehavior(resultData);
    }


    /**
     * 心理韧性
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getMentalDimensionIndexData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        DimensionStrategy mentalStrategy = scoreStrategyFactory.getStrategy(1546789344491134978L);
        // 维度指标数据
        DimensionIndexData mentalDimensionIndexData = mentalStrategy.dimensionScore(paperId);


        Map<Long, PersonalReport.ChartData> indexData = mentalDimensionIndexData.getIndexData();

        // 个人报告基础数据
        // 心理韧性 维度计分
        personalReportData.setMentalToughnessScore(Func.toDouble(mentalDimensionIndexData.getTotalData().getScore()));

        /**
         *  1546789344952508417	情绪管理
         *  1546789349885009922	目标激励
         *  1546789354800734210	积极关注
         *  1546789359766790145	学校支持
         *  1546789364607016961	人际支持
         *  1546789369476603906	家庭支持
         */
        PersonalReport.ChartData emotionManagementChartData = indexData.get(1546789344952508417L);
        PersonalReport.ChartData goalMotivationChartData = indexData.get(1546789349885009922L);
        PersonalReport.ChartData positiveAttentionChartData = indexData.get(1546789354800734210L);
        PersonalReport.ChartData schoolSupportChartData = indexData.get(1546789359766790145L);
        PersonalReport.ChartData interpersonalSupportData = indexData.get(1546789364607016961L);
        PersonalReport.ChartData familySupportChartData = indexData.get(1546789369476603906L);


        // 情绪管理 指标计分
        personalReportData.setEmotionManagementScore(Func.toDouble(emotionManagementChartData.getScore()));
        // 目标激励 指标计分
        personalReportData.setGoalMotivationScore(Func.toDouble(goalMotivationChartData.getScore()));
        // 积极关注 指标计分
        personalReportData.setPositiveAttentionScore(Func.toDouble(positiveAttentionChartData.getScore()));
        // 学校支持 指标计分
        personalReportData.setSchoolSupportScore(Func.toDouble(schoolSupportChartData.getScore()));
        // 人际支持 指标计分
        personalReportData.setInterpersonalSupportScore(Func.toDouble(interpersonalSupportData.getScore()));
        // 学校支持 指标计分
        personalReportData.setFamilySupportScore(Func.toDouble(familySupportChartData.getScore()));


        // 个人报告数据
        PersonalReport.ResultData resultData = new PersonalReport.ResultData();

        resultData.setTotalResult(mentalDimensionIndexData.getTotalData());

        List<PersonalReport.ChartData> chartDataList = indexData.values().stream().collect(Collectors.toList());
        resultData.setChartData(chartDataList);

        personalReport.setMentalToughness(resultData);
    }


    /**
     * 综合压力
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getOverallDimensionIndexData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        DimensionStrategy overallStrategy = scoreStrategyFactory.getStrategy(1546789419250409474L);
        // 维度指标数据
        DimensionIndexData overallDimensionIndexData = overallStrategy.dimensionScore(paperId);

        Map<Long, PersonalReport.ChartData> indexData = overallDimensionIndexData.getIndexData();

        // 个人报告基础数据
        // 综合压力 维度计分
        personalReportData.setOverallStressScore(Func.toDouble(overallDimensionIndexData.getTotalData().getScore()));

        /**
         *  1546789419690811394L	学习压力
         *  1546789426871459841L	人际压力
         *  1546789434203103233L	受惩罚压力
         *  1546789443141165057L	丧失压力
         *  1546789455623413761L	适应压力
         */
        PersonalReport.ChartData studyStressChartData = indexData.get(1546789419690811394L);
        PersonalReport.ChartData interpersonalChartData = indexData.get(1546789426871459841L);
        PersonalReport.ChartData punishmentChartData = indexData.get(1546789434203103233L);
        PersonalReport.ChartData lossStressChartData = indexData.get(1546789443141165057L);
        PersonalReport.ChartData adaptationStressChartData = indexData.get(1546789455623413761L);


        // 学习压力 指标计分
        personalReportData.setStudyStressScore(Func.toDouble(studyStressChartData.getScore()));
        // 人际压力 指标计分
        personalReportData.setInterpersonalStressScore(Func.toDouble(interpersonalChartData.getScore()));
        // 受惩罚压力 指标计分
        personalReportData.setPunishmentStressScore(Func.toDouble(punishmentChartData.getScore()));
        // 丧失压力 指标计分
        personalReportData.setLossStressScore(Func.toDouble(lossStressChartData.getScore()));
        // 适应压力 指标计分
        personalReportData.setAdaptationStressScore(Func.toDouble(adaptationStressChartData.getScore()));


        // 个人报告数据
        PersonalReport.ResultData resultData = new PersonalReport.ResultData();

        resultData.setTotalResult(overallDimensionIndexData.getTotalData());

        List<PersonalReport.ChartData> chartDataList = indexData.values().stream().collect(Collectors.toList());
        resultData.setChartData(chartDataList);

        personalReport.setStressIndex(resultData);
    }

    /**
     * 情绪指数
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getEmotionalDimensionIndexData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        DimensionStrategy emotionalStrategy = scoreStrategyFactory.getStrategy(1546789515505491970L);
        // 维度指标数据
        DimensionIndexData emotionalDimensionIndexData = emotionalStrategy.dimensionScore(paperId);


        Map<Long, PersonalReport.ChartData> indexData = emotionalDimensionIndexData.getIndexData();

        // 个人报告基础数据
        // 综合压力 维度计分
        personalReportData.setEmotionalIndexScore(Func.toDouble(emotionalDimensionIndexData.getTotalData().getScore()));

        /**
         *  1546789515920728066L	强迫
         *  1546789524955258882L	偏执
         *  1546789530986668033L	敌对
         *  1546789540155416578L	人际敏感
         *  1546789547860353026L	焦虑
         *  1546789556760666113L	抑郁
         */
        PersonalReport.ChartData compulsionChartData = indexData.get(1546789515920728066L);
        PersonalReport.ChartData paranoiaChartData = indexData.get(1546789524955258882L);
        PersonalReport.ChartData hostilityChartData = indexData.get(1546789530986668033L);
        PersonalReport.ChartData interpersonalSensitivityChartData = indexData.get(1546789540155416578L);
        PersonalReport.ChartData anxietyChartData = indexData.get(1546789547860353026L);
        PersonalReport.ChartData depressionChartData = indexData.get(1546789556760666113L);


        // 强迫 指标计分
        personalReportData.setCompulsionScore(Func.toDouble(compulsionChartData.getScore()));
        // 偏执 指标计分
        personalReportData.setParanoiaScore(Func.toDouble(paranoiaChartData.getScore()));
        // 敌对 指标计分
        personalReportData.setHostilityScore(Func.toDouble(hostilityChartData.getScore()));
        // 人际敏感 指标计分
        personalReportData.setInterpersonalSensitivityScore(Func.toDouble(interpersonalSensitivityChartData.getScore()));
        // 焦虑 指标计分
        personalReportData.setAnxietyScore(Func.toDouble(anxietyChartData.getScore()));
        // 抑郁 指标计分
        personalReportData.setDepressionScore(Func.toDouble(depressionChartData.getScore()));


        // 个人报告数据
        PersonalReport.ResultData resultData = new PersonalReport.ResultData();

        resultData.setTotalResult(emotionalDimensionIndexData.getTotalData());

        List<PersonalReport.ChartData> chartDataList = indexData.values().stream().collect(Collectors.toList());
        resultData.setChartData(chartDataList);

        personalReport.setEmotionalIndex(resultData);
    }


    /**
     * 睡眠指数
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getSleepIndexData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        DimensionStrategy sleepIndexStrategy = scoreStrategyFactory.getStrategy(1546789785358622721L);
        // 维度指标数据
        DimensionIndexData sleepIndexData = sleepIndexStrategy.dimensionScore(paperId);

        // 个人报告基础数据
        // 综合压力 维度计分
        personalReportData.setSleepIndexScore(Func.toDouble(sleepIndexData.getTotalData().getScore()));

        // 个人报告数据
        PersonalReport.ResultData resultData = new PersonalReport.ResultData();

        resultData.setTotalResult(sleepIndexData.getTotalData());

        personalReport.setSleepIndex(resultData);
    }

    /**
     * 测评概况
     *
     * @param personalReport
     */
    void getTestOverview(PersonalReport personalReport) {

        PersonalReport.TestOverview testOverview = new PersonalReport.TestOverview();
        // 积极
        List<PersonalReport.OverviewData> activeList = new LinkedList<>();

        // 学习状态
        PersonalReport.TotalResult learnResult = personalReport.getLearningStatus().getTotalResult();
        PersonalReport.OverviewData learnOverviewData = new PersonalReport.OverviewData();
        learnOverviewData.setTitle(learnResult.getTitle());
        learnOverviewData.setResult(learnResult.getResult());
        learnOverviewData.setScore(learnResult.getScore());
        learnOverviewData.setFontColor(learnResult.getFontColor());

        // 品行表现
        PersonalReport.TotalResult behaviorResult = personalReport.getBehavior().getTotalResult();
        PersonalReport.OverviewData behaviorData = new PersonalReport.OverviewData();
        behaviorData.setTitle(behaviorResult.getTitle());
        behaviorData.setResult(behaviorResult.getResult());
        behaviorData.setScore(behaviorResult.getScore());
        behaviorData.setFontColor(behaviorResult.getFontColor());

        // 心理韧性
        PersonalReport.TotalResult mentalToughnessResult = personalReport.getMentalToughness().getTotalResult();
        PersonalReport.OverviewData mentalToughnessData = new PersonalReport.OverviewData();
        mentalToughnessData.setTitle(mentalToughnessResult.getTitle());
        mentalToughnessData.setResult(mentalToughnessResult.getResult());
        mentalToughnessData.setScore(mentalToughnessResult.getScore());
        mentalToughnessData.setFontColor(mentalToughnessResult.getFontColor());

        activeList.add(learnOverviewData);
        activeList.add(behaviorData);
        activeList.add(mentalToughnessData);

        // 消极
        List<PersonalReport.OverviewData> negativeList = new LinkedList<>();

        // 综合压力
        PersonalReport.TotalResult stressIndexResult = personalReport.getStressIndex().getTotalResult();
        PersonalReport.OverviewData stressIndexData = new PersonalReport.OverviewData();
        stressIndexData.setTitle(stressIndexResult.getTitle());
        stressIndexData.setResult(stressIndexResult.getResult());
        stressIndexData.setScore(stressIndexResult.getScore());
        stressIndexData.setFontColor(stressIndexResult.getFontColor());

        // 情绪指数
        PersonalReport.TotalResult emotionalIndexResult = personalReport.getEmotionalIndex().getTotalResult();
        PersonalReport.OverviewData emotionalIndexData = new PersonalReport.OverviewData();
        emotionalIndexData.setTitle(emotionalIndexResult.getTitle());
        emotionalIndexData.setResult(emotionalIndexResult.getResult());
        emotionalIndexData.setScore(emotionalIndexResult.getScore());
        emotionalIndexData.setFontColor(emotionalIndexResult.getFontColor());

        // 睡眠指数
        PersonalReport.TotalResult sleepIndexResult = personalReport.getSleepIndex().getTotalResult();
        PersonalReport.OverviewData sleepIndexData = new PersonalReport.OverviewData();
        sleepIndexData.setTitle(sleepIndexResult.getTitle());
        sleepIndexData.setResult(sleepIndexResult.getResult());
        sleepIndexData.setScore(sleepIndexResult.getScore());
        sleepIndexData.setFontColor(sleepIndexResult.getFontColor());

        negativeList.add(stressIndexData);
        negativeList.add(emotionalIndexData);
        negativeList.add(sleepIndexData);

        testOverview.setActive(activeList);
        testOverview.setNegative(negativeList);
        personalReport.setTestOverview(testOverview);
    }

    /**
     * 自杀意念
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getSuicidalIdeationData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {
        // 自杀意念原始分
        Integer suicidalIdeationScore = paperQuestionMapper.dimensionTotalScore(1546789617431273473L, paperId);

        List<TestDimensionIndexConclusion> suicidalIdeationConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery()
                .eq(TestDimensionIndexConclusion::getDimensionId, 1546789617431273473L)
                .isNull(TestDimensionIndexConclusion::getIndexId));

        // 心理健康评级
        PersonalReport.MentalHealthRatings mentalHealthRatings = new PersonalReport.MentalHealthRatings();

        suicidalIdeationConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(suicidalIdeationScore.toString(), dimensionIndex.getDimensionScope());
            if (result) {
                String riskResult = dimensionIndex.getRiskResult();
                Integer riskIndex = dimensionIndex.getRiskIndex();

                PersonalReport.LevelData psychologicalData = new PersonalReport.LevelData();
                psychologicalData.setResult(riskResult);
                psychologicalData.setRiskIndex(riskIndex);
                psychologicalData.setFontColor(getFontColor(riskIndex));
                mentalHealthRatings.setPsychologicalCrisisAlert(psychologicalData);

                // 个人报告基础数据
                personalReportData.setSuicidalIdeationScore(Func.toDouble(suicidalIdeationScore.toString()));
                personalReportData.setSuicidalIdeationLevel(riskIndex);
            }
        });

        // 个人报告数据
        personalReport.setMentalHealthRatings(mentalHealthRatings);

    }

    /**
     * 作答有效性
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getValidityAnswersData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        // 作答有效性 原始分
        Integer suicidalIdeationScore = paperQuestionMapper.validityAnswersScore(paperId);

        // 个人报告基础数据
        personalReportData.setValidityAnswersScore(Func.toDouble(suicidalIdeationScore.toString()));

        /**
         * 0-1  该学生作答十分可靠。
         * 2-8  该学生作答不太可靠。
         */
        String result = "";
        Integer answerIsValidity = 0;
        if (suicidalIdeationScore > 1) {
            result = "该学生作答不太可靠";
        } else {
            result = "该学生作答十分可靠";
            answerIsValidity = 1;
        }

        // 个人报告数据
        personalReport.setAnswerValidity(result);
        personalReport.setAnswerIsValidity(answerIsValidity);
    }


    /**
     * 学生评定等级 、建议关注等级
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getStuRatingLevel(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        /**
         * 学生自评计分
         *
         * 自评等级判定逻辑1  所有维度取最高等级（很差＞较差＞一般＞良好）
         *
         * 自评等级判定逻辑2（公式）
         * [学习状态+品行表现+心理韧性+（100-综合压力）+（100-情绪指数）+（100-睡眠指数）]÷6
         */

        // 自评等级判定逻辑1
        /**
         * 学习状态、品行表现、心理韧性、综合压力、情绪指数、睡眠指数
         */
        Map<Integer, String> indexMap = new ConcurrentHashMap<>(6);
        TreeSet<Integer> indexSet = new TreeSet<>();

        Integer LearnRiskIndex = personalReport.getLearningStatus().getTotalResult().getRiskIndex();
        String LearnResult = personalReport.getLearningStatus().getTotalResult().getResult();
        indexMap.put(LearnRiskIndex, LearnResult);
        indexSet.add(LearnRiskIndex);

        Integer behaviorRiskIndex = personalReport.getBehavior().getTotalResult().getRiskIndex();
        String behaviorResult = personalReport.getLearningStatus().getTotalResult().getResult();
        indexMap.put(behaviorRiskIndex, behaviorResult);
        indexSet.add(behaviorRiskIndex);

        Integer mentalToughnessRiskIndex = personalReport.getMentalToughness().getTotalResult().getRiskIndex();
        String mentalToughnessResult = personalReport.getLearningStatus().getTotalResult().getResult();
        indexMap.put(mentalToughnessRiskIndex, mentalToughnessResult);
        indexSet.add(mentalToughnessRiskIndex);

        Integer stressIndexRiskIndex = personalReport.getStressIndex().getTotalResult().getRiskIndex();
        String stressIndexResult = personalReport.getLearningStatus().getTotalResult().getResult();
        indexMap.put(stressIndexRiskIndex, stressIndexResult);
        indexSet.add(stressIndexRiskIndex);

        Integer emotionalIndexRiskIndex = personalReport.getEmotionalIndex().getTotalResult().getRiskIndex();
        String emotionalIndexResult = personalReport.getLearningStatus().getTotalResult().getResult();
        indexMap.put(emotionalIndexRiskIndex, emotionalIndexResult);
        indexSet.add(emotionalIndexRiskIndex);

        Integer sleepIndexRiskIndex = personalReport.getSleepIndex().getTotalResult().getRiskIndex();
        String sleepIndexResult = personalReport.getLearningStatus().getTotalResult().getResult();
        indexMap.put(sleepIndexRiskIndex, sleepIndexResult);
        indexSet.add(sleepIndexRiskIndex);


        // 自评等级判定逻辑2
        // [学习状态+品行表现+心理韧性+（100-综合压力）+（100-情绪指数）+（100-睡眠指数）]÷6

        String studyStatusScore = Func.toStr(personalReportData.getStudyStatusScore());
        String behaviorScore = Func.toStr(personalReportData.getBehaviorScore());
        String mentalToughnessScore = Func.toStr(personalReportData.getMentalToughnessScore());
        String overallStressScore = Func.toStr(personalReportData.getOverallStressScore());
        String emotionalIndexScore = Func.toStr(personalReportData.getEmotionalIndexScore());
        String sleepIndexScore = Func.toStr(personalReportData.getSleepIndexScore());


        String d1 = MathUtil.add(studyStatusScore, behaviorScore);

        String d2 = MathUtil.add(d1, mentalToughnessScore);

        String d3 = MathUtil.subtract("100", overallStressScore);
        String d4 = MathUtil.subtract("100", emotionalIndexScore);
        String d5 = MathUtil.subtract("100", sleepIndexScore);

        String d6 = MathUtil.add(d3, d4);
        String d7 = MathUtil.add(d2, d5);
        String d8 = MathUtil.add(d6, d7);

        String dimensScore = MathUtil.divide(d8, "6", 1);

        // 维度等级
        List<TestDimensionIndexConclusion> dimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery()
                .eq(TestDimensionIndexConclusion::getDimensionId, 1000000000000000001L)
                .isNull(TestDimensionIndexConclusion::getIndexId));
        dimensionIndexConclusionList.forEach(dimensionIndex -> {

            boolean result = IntervalUtil.isInTheInterval(dimensScore, dimensionIndex.getDimensionScope());
            if (result) {
                String riskResult = dimensionIndex.getRiskResult();
                Integer riskIndex = dimensionIndex.getRiskIndex();
                indexMap.put(riskIndex, riskResult);
                indexSet.add(riskIndex);
            }
        });

        // 维度最终等级
        Integer dRiskIndex = indexSet.last();
        String dimensionResult = getDimensionResult(dRiskIndex);

        PersonalReport.LevelData stuLevelData = new PersonalReport.LevelData();
        stuLevelData.setResult(dimensionResult);
        stuLevelData.setRiskIndex(dRiskIndex);
        stuLevelData.setFontColor(getFontColor(dRiskIndex));

        // 学生评定等级
        PersonalReport.MentalHealthRatings mentalHealthRatings = personalReport.getMentalHealthRatings();
        mentalHealthRatings.setStudentRatingLevel(stuLevelData);

    }


    /**
     * 家长他评
     */
    void getParentRatingLevel(PersonalReportData personalReportData, PersonalReport personalReport, Long userId) {

        /**
         * 8、9题原始分加和
         * 0-3  家长评定等级：良好   0
         * 4-27 家长评定等级：1级   1
         * 43809
         */
        // 查询用户 最新的家长他评试卷ID 获取最新试卷ID下第 8 9 题选项

        // 数据如果不存在 不操作
        Long paperId = paperQuestionMapper.getPaperIdByUserId(userId);

        PersonalReport.LevelData levelData = new PersonalReport.LevelData();

        levelData.setResult("暂无数据");
        levelData.setFontColor(getFontColor(-1));
        personalReportData.setParentalAssessmentScore(0d);

        if (Func.isNotEmpty(paperId)) {

            List<ParentPaperQuestionVO> paperQuestionList = paperQuestionMapper.getParentPaperQuestionByPaperId(paperId);

            if (paperQuestionList.size() > 0) {

                List<String> optionIdList = new ArrayList<>();
                paperQuestionList.forEach(p -> {
                    String optionId = p.getOptionId();
                    String[] options = toStringArray(optionId);
                    List<String> list = Stream.of(options).map(String::toString).collect(Collectors.toList());
                    optionIdList.addAll(list);
                });

                // 查询该选项所有分数
                Integer optionScore = paperQuestionMapper.getOptionScore(optionIdList);

                if (optionScore > 3) {
                    personalReportData.setParentalAssessmentLevel(1);
                    levelData.setRiskIndex(1);
                    levelData.setResult(getDimensionResult(1));
                    levelData.setFontColor(getFontColor(1));
                } else {
                    personalReportData.setParentalAssessmentLevel(0);
                    levelData.setRiskIndex(0);
                    levelData.setResult(getDimensionResult(0));
                    levelData.setFontColor(getFontColor(0));
                }

                // 个人报告基础数据
                personalReportData.setParentalAssessmentScore(Func.toDouble(optionScore.toString()));
            }
        }

        // 个人报告数据
        PersonalReport.MentalHealthRatings mentalHealthRatings = personalReport.getMentalHealthRatings();
        mentalHealthRatings.setParentsRatingLevel(levelData);

    }

    /**
     * 教师评定
     */
    void getTeacherRatingLevel(PersonalReportData personalReportData, PersonalReport personalReport, Long userId) {

        PersonalReport.LevelData levelData = new PersonalReport.LevelData();

        /**
         * 获取教师评定分数
         * 取最新的数据
         */
        List<TestTeacherPaperRecord> paperRecordList = testTeacherPaperRecordMapper.selectList(Wrappers.<TestTeacherPaperRecord>lambdaQuery()
                .eq(TestTeacherPaperRecord::getStuId, userId)
                .orderByDesc(TestTeacherPaperRecord::getCreateTime));

        if (paperRecordList.size() > 0) {

            TestTeacherPaperRecord testTeacherPaperRecord = paperRecordList.get(0);

            Integer riskLevel = testTeacherPaperRecord.getRiskLevel();

            // 个人报告基础数据
            personalReportData.setTeacherRatingsScore(Func.toDouble(testTeacherPaperRecord.getScore()));
            personalReportData.setTeacherRatingsLevel(riskLevel);

            levelData.setRiskIndex(riskLevel);
            levelData.setResult(testTeacherPaperRecord.getRiskResult());
            levelData.setFontColor(getFontColor(riskLevel));
        } else {
            levelData.setResult("暂无数据");
            levelData.setFontColor(getFontColor(-1));
            personalReportData.setTeacherRatingsScore(0d);
        }

        // 个人报告数据
        PersonalReport.MentalHealthRatings mentalHealthRatings = personalReport.getMentalHealthRatings();
        mentalHealthRatings.setTeachRatingLevel(levelData);
    }

    /**
     * 建议关注等级
     */
    void getRecommendedAttentionLevel(PersonalReport personalReport) {

        PersonalReport.LevelData levelData = new PersonalReport.LevelData();

        /**
         *  建议关注等级
         *
         * （自杀意念）心理危机预警1级/2级/3级，则关注等级为3级；
         * 心理危机预警为未发现，则按照学生自评等级、家长他评等级、教师他评等级取最高等级计算。
         */

        // 心里危机预警
        PersonalReport.LevelData psychologicalCrisisAlert = personalReport.getMentalHealthRatings().getPsychologicalCrisisAlert();

        Integer suRiskIndex = 0;

        Integer riskIndex = psychologicalCrisisAlert.getRiskIndex();
        if (riskIndex == 1 || riskIndex == 2 || riskIndex == 3) {
            // 获取心里危机预警等级 是否在 1级/2级/3级 如果是 则关注等级为3级
            suRiskIndex = 3;
        } else {
            //  否则 按照学生自评等级、家长他评等级、教师他评等级取最高等级计算
            suRiskIndex = getRiskIndex(personalReport);
        }

        levelData.setRiskIndex(suRiskIndex);
        levelData.setResult(getFocusDimensionResult(suRiskIndex));
        levelData.setFontColor(getFontColor(suRiskIndex));

        // 个人报告数据
        PersonalReport.MentalHealthRatings mentalHealthRatings = personalReport.getMentalHealthRatings();
        mentalHealthRatings.setRecommendedAttentionLevel(levelData);

    }

    /**
     * 获取等级数据
     *
     * @param personalReport
     * @return
     */
    Integer getRiskIndex(PersonalReport personalReport) {

        PersonalReport.LevelData studentRatingLevel = personalReport.getMentalHealthRatings().getStudentRatingLevel();
        PersonalReport.LevelData teachRatingLevel = personalReport.getMentalHealthRatings().getTeachRatingLevel();
        PersonalReport.LevelData parentsRatingLevel = personalReport.getMentalHealthRatings().getParentsRatingLevel();

        TreeSet<Integer> indexSet = new TreeSet<>();

        // 可能存在等级为空数据情况
        if (Func.isNotEmpty(studentRatingLevel) && Func.isNotEmpty(studentRatingLevel.getRiskIndex())) {
            indexSet.add(studentRatingLevel.getRiskIndex());
        }
        if (Func.isNotEmpty(teachRatingLevel) && Func.isNotEmpty(teachRatingLevel.getRiskIndex())) {
            indexSet.add(teachRatingLevel.getRiskIndex());
        }
        if (Func.isNotEmpty(parentsRatingLevel) && Func.isNotEmpty(parentsRatingLevel.getRiskIndex())) {
            indexSet.add(parentsRatingLevel.getRiskIndex());
        }

        Integer suRiskIndex = indexSet.last();

        return suRiskIndex;
    }

    /**
     * 个人信息
     */
    void getUserInfo(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId, Long userId, Long taskId) {

        PersonalReport.UserInfo userInfo = new PersonalReport.UserInfo();


        User user = userClient.userInfoById(userId).getData();

        userInfo.setUserId(userId);


        userInfo.setName(user.getRealName());
        userInfo.setStudentNo(user.getAccount());
        if (Func.isNotEmpty(user.getSex())) {
            userInfo.setSex(user.getSex() == 0 ? "男" : "女");
        }

        // 学校名称 取 任务名称
        TestPaper paper = paperMapper.selectById(paperId);
        TestTask testTask = taskMapper.selectById(paper.getTaskId());
        userInfo.setSchool(testTask.getTaskName());

        UserExt userExt = userClient.userExtInfoById(userId).getData();

        String grade = userExt.getGrade();
        Long classId = userExt.getClassId();
        DictVO gradeInfo = classUserMapper.getValueById(Long.valueOf(grade));
        userInfo.setGradeName(gradeInfo.getDictValue());
        DictVO classInfo = classUserMapper.getValueById(classId);
        userInfo.setClassName(classInfo.getDictValue());


        // 家庭结构 | 是否与父母生活

        Long parentPaperId = paperQuestionMapper.getPaperIdByUserId(userId);

        String familyStructures = "";

        String whetherLivingWithParents = "";

        if (Func.isNotEmpty(parentPaperId)) {

            ParentPaperQuestionVO question = paperQuestionMapper.getParentPaperQuestionByPaperIdQuestionId(parentPaperId, 2L);
            ParentPaperQuestionVO isQuestion = paperQuestionMapper.getParentPaperQuestionByPaperIdQuestionId(parentPaperId, 3L);

            //  option_type 0 单选 1 填空

            String optionId = question.getOptionId();
            String isOptionId = isQuestion.getOptionId();

            ParentOptionVO parentOptionVO = paperQuestionMapper.getOptionInfoById(Long.valueOf(optionId));

            ParentOptionVO isParentOptionVO = paperQuestionMapper.getOptionInfoById(Long.valueOf(isOptionId));

            // 家庭结构
            familyStructures = parentOptionVO.getTitle();

            if (parentOptionVO.getOptionType() == 1) {
                familyStructures = question.getOtherContent();
            }
            familyStructures = replaceStr(familyStructures);
            userInfo.setFamilyStructures(familyStructures);

            // 是否与父母生活
            whetherLivingWithParents = isParentOptionVO.getTitle();
            if (isParentOptionVO.getOptionType() == 1) {
                whetherLivingWithParents = isQuestion.getOtherContent();
            }
            whetherLivingWithParents = replaceStr(whetherLivingWithParents);
            userInfo.setWhetherLivingWithParents(whetherLivingWithParents);

            /**
             * 统计家庭结构数据
             *
             * 2135703722  双亲
             * 2135703723  单亲
             * 2135703724  重组
             * 2135703725  其他监护人
             * 2135703726  独自
             * 2135703727  其他
             */
            String fKey = "education" + ":" + "family_structure" + ":" + "statistic" + ":" + taskId + ":" + optionId;
            redisTemplate.opsForSet().add(fKey, userId);

            /**
             * 统计是否与父母生活
             *
             * 2135703729	 与父母双方或一方生活在一起
             * 2135703730	 与（外）祖父母生活在一起
             * 2135703731	 与其他亲戚（朋友）生活在一起
             * 2135703732	 独自生活
             * 2135703733  其他
             */
            String aloneKey = "education" + ":" + "is_alone" + ":" + "statistic" + ":" + taskId + ":" + isOptionId;
            redisTemplate.opsForSet().add(aloneKey, userId);

            // TODO 定时同步缓存数据到数据库

        } else {
            userInfo.setFamilyStructures("暂无数据");
            userInfo.setWhetherLivingWithParents("暂无数据");
        }


        // 个人报告数据
        personalReport.setUserInfo(userInfo);

        // 个人报告基础数据
        personalReportData.setUserId(userId.toString());
    }


    /**
     * 更新任务用户数据
     *
     * @param personalReport
     * @param paper
     * @param userId
     */
    void updateTaskUserData(PersonalReport personalReport, TestPaper paper, Long userId) {

        Long taskId = paper.getTaskId();
        List<TestTaskUser> testTaskUserList = taskUserMapper.selectList(
                Wrappers.<TestTaskUser>lambdaQuery().eq(TestTaskUser::getUserId, userId)
                        .eq(TestTaskUser::getTestTaskId, taskId).orderByDesc(TestTaskUser::getCreateTime));

        if (testTaskUserList.size() > 0) {

            TestTaskUser testTaskUser = testTaskUserList.get(0);

            // 预警等级
            Integer psychologicalLevel = personalReport.getMentalHealthRatings()
                    .getPsychologicalCrisisAlert().getRiskIndex();


            // 数据是否有效
            Integer answerIsValidity = personalReport.getAnswerIsValidity();

            // 关注等级
            if (Func.isNotEmpty(personalReport.getMentalHealthRatings().getRecommendedAttentionLevel())) {
                Integer recommendedAttentionLevel = personalReport.getMentalHealthRatings().getRecommendedAttentionLevel()
                        .getRiskIndex();
                testTaskUser.setFollowLevel(recommendedAttentionLevel);
            }
            // 家长他评
            if (Func.isNotEmpty(personalReport.getMentalHealthRatings().getParentsRatingLevel())) {
                Integer parentsRatingLevel = personalReport.getMentalHealthRatings().getParentsRatingLevel()
                        .getRiskIndex();
                testTaskUser.setParentComments(parentsRatingLevel);
            }

            // 教师他评
            if (Func.isNotEmpty(personalReport.getMentalHealthRatings().getTeachRatingLevel())) {
                Integer teachRatingLevel = personalReport.getMentalHealthRatings().getTeachRatingLevel()
                        .getRiskIndex();
                testTaskUser.setTeacherComments(teachRatingLevel);
            }
            // 学生自评
            if (Func.isNotEmpty(personalReport.getMentalHealthRatings().getStudentRatingLevel())) {
                Integer studentRatingLevel = personalReport.getMentalHealthRatings().getStudentRatingLevel()
                        .getRiskIndex();
                testTaskUser.setStudentComments(studentRatingLevel);
            }

            testTaskUser.setIsValid(answerIsValidity);
            testTaskUser.setIsWarn(psychologicalLevel);

            taskUserMapper.updateById(testTaskUser);
        }
    }

    void statisticData(Long taskId, Long paperId, Long userId) {

        /**
         * 统计 积极指标的影响程度是指该指标所有题目未选择“4”分选项的人数
         *     消极指标的影响程度是指该指标所有题目未选择“0”分选项的人数
         */

        // 所有选项
        List<String> optionList = paperQuestionMapper.selectList(
                Wrappers.<TestPaperQuestion>lambdaQuery().eq(TestPaperQuestion::getPaperId, paperId)
                        .select(TestPaperQuestion::getOptionId)).stream()
                .map(TestPaperQuestion::getOptionId).collect(
                        Collectors.toList());
        // 如果存在多选情况 optionList需处理下, 此问卷没有多选情况,暂不处理

        // 积极
        List<Long> activeIndexList = Arrays.asList(1546788164788609025L, 1546788174330650626L,
                1546788938151157761L, 1546788944375504898L, 1546788949077319682L,
                1546788953720414210L, 1546789344952508417L, 1546789349885009922L, 1546789354800734210L,
                1546789359766790145L, 1546789364607016961L, 1546789369476603906L);

        // 消极
        List<Long> noActiveIndexList = Arrays.asList(1546788189962821634L, 1546789419690811394L,
                1546789426871459841L, 1546789434203103233L, 1546789443141165057L, 1546789455623413761L,
                1546789515920728066L, 1546789524955258882L, 1546789530986668033L, 1546789540155416578L,
                1546789547860353026L, 1546789556760666113L);


        activeIndexList.forEach(indexId -> {

            List<String> optionIdList = optionMapper.optionIdList(indexId, 4);
            Boolean result = isContain(optionList, optionIdList);

            if (!result) {
                String key = "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + indexId;
                redisTemplate.opsForSet().add(key, userId);
            }

        });

        noActiveIndexList.forEach(indexId -> {

            List<String> optionIdList = optionMapper.optionIdList(indexId, 0);
            Boolean result = isContain(optionList, optionIdList);
            if (!result) {
                String key = "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + indexId;
                redisTemplate.opsForSet().add(key, userId);
            }

        });

    }

    void statisticStressData(Long taskId, Long paperId, Long userId) {

        /**
         *  综合压力	影响事件分布 影响事件分布是指题目未选择“0”分选项的人数÷有效人数*100%
         * 影响程度排名是按照每个事件的影响程度从大到小进行排序，取前3位输出，并在柱状图进行标注
         *
         * -- 1546789419250409474	综合压力
         */

        // 所有选项
        List<String> optionList = paperQuestionMapper.selectList(
                Wrappers.<TestPaperQuestion>lambdaQuery().eq(TestPaperQuestion::getPaperId, paperId)
                        .select(TestPaperQuestion::getOptionId)).stream()
                .map(TestPaperQuestion::getOptionId).collect(
                        Collectors.toList());
        // 如果存在多选情况 optionList需处理下, 此问卷没有多选情况,暂不处理

        List<TestIndex> testIndexList = indexMapper.indexList(1546789419250409474L);
        testIndexList.forEach(index -> {

            // 题干信息
            List<TestQuestion> questionList = questionMapper.questionList(index.getId());

            questionList.forEach(question -> {
                List<String> optionIdList = optionMapper.optionIdListByQuestionId(question.getId());

                // 判断用户选择是否包含 选项
                Boolean result = isContain(optionList, optionIdList);

                if (result) {
                    String key = "education" + ":" + "stress" + ":" + "statistic" + ":" + taskId + ":" + index.getId() + ":" + question.getId();
                    redisTemplate.opsForSet().add(key, userId);
                }

            });

        });


    }

    /**
     * @param l1
     * @param l2
     * @return eg  List<String> l1 = Arrays.asList("A", "B", "C", "D", "E", "F");
     * List<String> l2 = Arrays.asList("A", "B", "C");
     */
    static Boolean isContain(List<String> l1, List<String> l2) {
        // 判断 l1 是否包含 l2 中的一个
        AtomicBoolean result = new AtomicBoolean(false);
        l2.forEach(l -> {
            if (l1.contains(l)) {
                result.set(true);
            }
        });
        return result.get();
    }


    /**
     * 字符串格式 转为数组
     *
     * @param source
     * @return
     */
    public static String[] toStringArray(String source) {
        return source.substring(1, source.length() - 1).split(",");
    }

    /**
     * 自评等级
     *
     * @param riskIndex 等级
     * @return
     */
    static String getDimensionResult(Integer riskIndex) {

        String result = "";
        if (riskIndex == 0) {
            result = "良好";
        } else {
            result = riskIndex + "级";
        }
        return result;
    }

    /**
     * 关注等级
     *
     * @param riskIndex 等级
     * @return
     */
    static String getFocusDimensionResult(Integer riskIndex) {

        String result = "";
        if (riskIndex == 0) {
            result = "良好";
        } else {
            result = riskIndex + "级关注";
        }
        return result;
    }


    /**
     * 字体颜色
     *
     * @param index
     * @return
     */
    static String getFontColor(Integer index) {

        String color = ColorEnum.BLACK.getValue();

        switch (index) {
            case 0:
                color = ColorEnum.GREEN.getValue();
                break;
            case 1:
                color = ColorEnum.YELLOW.getValue();
                break;
            case 2:
                color = ColorEnum.ORANGE.getValue();
                break;
            case 3:
                color = ColorEnum.RED.getValue();
                break;
            default:
                ColorEnum.BLACK.getValue();
        }
        return color;
    }


    /**
     * 替换内容
     *
     * @param oldStr
     * @return
     */
    static String replaceStr(String oldStr) {

        String newStr = oldStr;

        if (Func.isNotEmpty(oldStr.trim())) {
            String regEx = "[a-zA-Z]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(oldStr);
            newStr = m.replaceAll("");
        }
        return newStr;
    }

    @Override
    public Page<ReportUserVO> reportList(IPage<ReportUserVO> page, ReportUserDTO reportUserDTO) {

        PlatformUser user = SecureUtil.getUser();
        List<Long> userList = new ArrayList<>();
        Page<ReportUserVO> reportUserVOPage = new Page<>();
        if ("5".equals(user.getRoleId())) {
            // 班主任
            userList = reportTeacherClassMapper.getUserList(reportUserDTO.getTaskId(),
                    user.getUserId());
            if (userList.size() == 0) {
                return reportUserVOPage;
            }
        }

        reportUserVOPage = taskUserMapper.reportList(page, reportUserDTO.getGradeId(), reportUserDTO.getRealName(), reportUserDTO.getClassId(),
                reportUserDTO.getTaskId(), userList);
        List<ReportUserVO> reportUserVOList = reportUserVOPage.getRecords();
        reportUserVOList.forEach(reportUserVO -> {
            if (Func.isNotEmpty(reportUserVO.getSex())) {
                reportUserVO.setSexName(reportUserVO.getSex() == 0 ? "男" : "女");
            }
        });
        return reportUserVOPage;
    }

    @Override
    public boolean updatePersonalReportsTeacherRating(List<Long> userIdList) {

        /**
         *  获取最新 个人报告基础数据 PersonalReportData  个人报告数据  PersonalReport
         */
        userIdList.forEach(userId -> {

            // 获取个人报告基础数据
            Query queryData = new Query();
            queryData.addCriteria(Criteria.where("userId").is(userId.toString()));
            queryData.with(Sort.by((Order.desc("createTime"))));
            PersonalReportData personalReportData = mongoTemplate.findOne(queryData, PersonalReportData.class);
            // 获取个人报告数据
            Query query = new Query();
            query.addCriteria(Criteria.where("userInfo.userId").is(userId));
            query.with(Sort.by((Order.desc("createTime"))));
            PersonalReport personalReport = mongoTemplate.findOne(query, PersonalReport.class);

            // 修改教师他评数据
            if (Func.isNotEmpty(personalReportData) && Func.isNotEmpty(personalReport)) {

                List<TestTeacherPaperRecord> paperRecordList = testTeacherPaperRecordMapper.selectList(Wrappers.<TestTeacherPaperRecord>lambdaQuery()
                        .eq(TestTeacherPaperRecord::getStuId, userId)
                        .orderByDesc(TestTeacherPaperRecord::getCreateTime));

                if (paperRecordList.size() > 0) {

                    TestTeacherPaperRecord testTeacherPaperRecord = paperRecordList.get(0);

                    Integer riskLevel = testTeacherPaperRecord.getRiskLevel();

                    // 个人报告基础数据
                    personalReportData.setTeacherRatingsScore(Func.toDouble(testTeacherPaperRecord.getScore()));
                    personalReportData.setTeacherRatingsLevel(riskLevel);

                    PersonalReport.LevelData levelData = new PersonalReport.LevelData();

                    levelData.setRiskIndex(riskLevel);
                    levelData.setResult(testTeacherPaperRecord.getRiskResult());
                    levelData.setFontColor(getFontColor(riskLevel));

                    // 个人报告数据
                    PersonalReport.MentalHealthRatings mentalHealthRatings = personalReport.getMentalHealthRatings();
                    mentalHealthRatings.setTeachRatingLevel(levelData);

                    mongoTemplate.save(personalReport);
                    mongoTemplate.save(personalReportData);

                    // 修改 建议关注等级数据
                    updateRecommendedAttentionLevel(userId);

                    String testPaperId = personalReportData.getPaperId();
                    TestPaper testPaper = paperMapper.selectById(testPaperId);

                    // 修改教师他评数据
                    updateTeacherData(levelData.getRiskIndex(), userId, testPaper.getTaskId());

                    String tenantCode = SecureUtil.getTenantCode();
                    // 更新班级报告
                    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                    CompletableFuture.runAsync(() -> {
                        RequestContextHolder.setRequestAttributes(requestAttributes);
                        updateClassReportData(testPaper.getTaskId(), userId, tenantCode);
                    });
                }

            }

        });

        return true;
    }

    @Override
    public boolean updatePersonalReportsParentRating(Long userId) {

        // 修改个人报告家长他评数据
        /**
         *  获取最新 个人报告基础数据 PersonalReportData  个人报告数据  PersonalReport
         */

        // 获取个人报告基础数据
        Query queryData = new Query();
        queryData.addCriteria(Criteria.where("userId").is(userId.toString()));
        queryData.with(Sort.by((Order.desc("createTime"))));
        PersonalReportData personalReportData = mongoTemplate.findOne(queryData, PersonalReportData.class);
        // 获取个人报告数据
        Query query = new Query();
        query.addCriteria(Criteria.where("userInfo.userId").is(userId));
        query.with(Sort.by((Order.desc("createTime"))));
        PersonalReport personalReport = mongoTemplate.findOne(query, PersonalReport.class);

        // 修改家长他评数据
        if (Func.isNotEmpty(personalReportData) && Func.isNotEmpty(personalReport)) {

            // 数据如果不存在 不操作
            Long paperId = paperQuestionMapper.getPaperIdByUserId(userId);

            if (Func.isNotEmpty(paperId)) {

                PersonalReport.LevelData levelData = new PersonalReport.LevelData();

                List<ParentPaperQuestionVO> paperQuestionList = paperQuestionMapper.getParentPaperQuestionByPaperId(paperId);

                List<String> optionIdList = new ArrayList<>();
                paperQuestionList.forEach(p -> {
                    String optionId = p.getOptionId();
                    String[] options = toStringArray(optionId);
                    List<String> list = Stream.of(options).map(String::toString).collect(Collectors.toList());
                    optionIdList.addAll(list);
                });

                // 查询该选项所有分数
                Integer optionScore = paperQuestionMapper.getOptionScore(optionIdList);

                if (optionScore > 3) {
                    personalReportData.setParentalAssessmentLevel(1);
                    levelData.setRiskIndex(1);
                    levelData.setResult(getDimensionResult(1));
                    levelData.setFontColor(getFontColor(1));
                } else {
                    personalReportData.setParentalAssessmentLevel(0);
                    levelData.setRiskIndex(0);
                    levelData.setResult(getDimensionResult(0));
                    levelData.setFontColor(getFontColor(0));
                }

                // 家庭结构 | 是否与父母生活
                ParentPaperQuestionVO question = paperQuestionMapper.getParentPaperQuestionByPaperIdQuestionId(paperId, 2L);
                ParentPaperQuestionVO isQuestion = paperQuestionMapper.getParentPaperQuestionByPaperIdQuestionId(paperId, 3L);

                //  option_type 0 单选 1 填空

                String optionId = question.getOptionId();
                String isOptionId = isQuestion.getOptionId();

                ParentOptionVO parentOptionVO = paperQuestionMapper.getOptionInfoById(Long.valueOf(optionId));
                ParentOptionVO isParentOptionVO = paperQuestionMapper.getOptionInfoById(Long.valueOf(isOptionId));

                String familyStructures = "";

                String whetherLivingWithParents = "";


                UserInfo userInfo = personalReport.getUserInfo();

                // 家庭结构
                familyStructures = parentOptionVO.getTitle();

                if (parentOptionVO.getOptionType() == 1) {
                    familyStructures = question.getOtherContent();
                }
                familyStructures = replaceStr(familyStructures);
                userInfo.setFamilyStructures(familyStructures);

                // 是否与父母生活
                whetherLivingWithParents = isParentOptionVO.getTitle();
                if (isParentOptionVO.getOptionType() == 1) {
                    whetherLivingWithParents = isQuestion.getOtherContent();
                }
                whetherLivingWithParents = replaceStr(whetherLivingWithParents);
                userInfo.setWhetherLivingWithParents(whetherLivingWithParents);

                String testPaperId = personalReportData.getPaperId();
                TestPaper testPaper = paperMapper.selectById(testPaperId);
                /**
                 * 统计家庭结构数据
                 *
                 * 2135703722  双亲
                 * 2135703723  单亲
                 * 2135703724  重组
                 * 2135703725  其他监护人
                 * 2135703726  独自
                 * 2135703727  其他
                 */
                String fKey = "education" + ":" + "family_structure" + ":" + "statistic" + ":" + testPaper.getTaskId() + ":" + optionId;
                redisTemplate.opsForSet().add(fKey, userId);

                /**
                 * 统计是否与父母生活
                 *
                 * 2135703729	 与父母双方或一方生活在一起
                 * 2135703730	 与（外）祖父母生活在一起
                 * 2135703731	 与其他亲戚（朋友）生活在一起
                 * 2135703732	 独自生活
                 * 2135703733  其他
                 */
                String aloneKey = "education" + ":" + "is_alone" + ":" + "statistic" + ":" + testPaper.getTaskId() + ":" + isOptionId;
                redisTemplate.opsForSet().add(aloneKey, userId);


                // 个人报告基础数据
                personalReportData.setParentalAssessmentScore(Func.toDouble(optionScore.toString()));

                // 个人报告数据
                PersonalReport.MentalHealthRatings mentalHealthRatings = personalReport.getMentalHealthRatings();
                mentalHealthRatings.setParentsRatingLevel(levelData);

                mongoTemplate.save(personalReport);
                mongoTemplate.save(personalReportData);

                // 修改 建议关注等级数据
                updateRecommendedAttentionLevel(userId);

                // 修改家长评定数据
                updateParentData(levelData.getRiskIndex(), userId, testPaper.getTaskId());

                String tenantCode = testPaper.getTenantCode();
                // 更新班级报告
                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                CompletableFuture.runAsync(() -> {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                    updateClassReportData(testPaper.getTaskId(), userId, tenantCode);
                });
            }

        }

        return true;
    }

    /**
     * 修改家长评定数据
     */
    void updateParentData(Integer riskIndex, Long userId, Long taskId) {
        //   修改家长评定数据
        TestTaskUser testTaskUser = taskUserMapper.getTaskUser(userId, taskId);
        if (ObjectUtils.isNotEmpty(testTaskUser)) {
            taskUserMapper.updateTaskUser(testTaskUser.getId(), riskIndex);
        }
    }

    /**
     * 修改教师评定数据
     */
    void updateTeacherData(Integer riskIndex, Long userId, Long taskId) {
        //   修改家长评定数据
        List<TestTaskUser> testTaskUserList = taskUserMapper.selectList(
                Wrappers.<TestTaskUser>lambdaQuery().eq(TestTaskUser::getUserId, userId)
                        .eq(TestTaskUser::getTestTaskId, taskId).eq(TestTaskUser::getCompletionStatus, 1)
                        .orderByDesc(TestTaskUser::getCreateTime));

        if (testTaskUserList.size() > 0) {
            TestTaskUser testTaskUser = testTaskUserList.get(0);
            testTaskUser.setTeacherComments(riskIndex);
            taskUserMapper.updateById(testTaskUser);
        }
    }


    /**
     * 修改建议关注等级
     *
     * @param userId
     * @return
     */
    public boolean updateRecommendedAttentionLevel(Long userId) {

        // 获取个人报告数据
        Query query = new Query();
        query.addCriteria(Criteria.where("userInfo.userId").is(userId));
        query.with(Sort.by((Order.desc("createTime"))));
        PersonalReport personalReport = mongoTemplate.findOne(query, PersonalReport.class);

        // 修改建议关注等级数据
        if (Func.isNotEmpty(personalReport)) {

            PersonalReport.LevelData levelData = new PersonalReport.LevelData();

            /**
             *  建议关注等级
             *
             * （自杀意念）心理危机预警1级/2级/3级，则关注等级为3级；
             * 心理危机预警为未发现，则按照学生自评等级、家长他评等级、教师他评等级取最高等级计算。
             */

            // 心里危机预警
            PersonalReport.LevelData psychologicalCrisisAlert = personalReport.getMentalHealthRatings().getPsychologicalCrisisAlert();

            Integer suRiskIndex = 0;

            Integer riskIndex = psychologicalCrisisAlert.getRiskIndex();
            if (riskIndex == 1 || riskIndex == 2 || riskIndex == 3) {
                // 获取心里危机预警等级 是否在 1级/2级/3级 如果是 则关注等级为3级
                suRiskIndex = 3;
            } else {
                //  否则 按照学生自评等级、家长他评等级、教师他评等级取最高等级计算
                suRiskIndex = getRiskIndex(personalReport);
            }

            levelData.setRiskIndex(suRiskIndex);
            levelData.setResult(getFocusDimensionResult(suRiskIndex));
            levelData.setFontColor(getFontColor(suRiskIndex));

            // 个人报告数据
            PersonalReport.MentalHealthRatings mentalHealthRatings = personalReport.getMentalHealthRatings();
            mentalHealthRatings.setRecommendedAttentionLevel(levelData);
            mongoTemplate.save(personalReport);
        }

        return true;
    }
}
