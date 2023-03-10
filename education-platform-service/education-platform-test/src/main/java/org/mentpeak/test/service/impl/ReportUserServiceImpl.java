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
 * ???????????? ????????????
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

        // ???????????????????????????????????????
        String roleId = SecureUtil.getUser().getRoleId();
        Long teacherId = SecureUtil.getUserId();
        if (roleId.equals("5")) {
            // ??????
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
     * ??????????????????
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
            throw new PlatformApiException("????????????");
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(reportId));
        report = mongoTemplate.findOne(query, PersonalReport.class);

        // ???????????? ??? X ?????????
        User user = userClient.allUserInfoById(userId).getData();
        String realName = user.getRealName();

        report = replacePersonalReport(report, realName);
        report.setIsPermission(1);
        return report;
    }


    /**
     * ??????????????????
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
     * ????????????
     *
     * @param oldStr     ?????????
     * @param replaceStr ????????????
     * @return ???????????????
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

        log.info("===== ??????????????????{}???????????? ==== {}", userId, paperId);
        // ??????????????????
        TestPaper paper = paperMapper.selectById(paperId);
        if (paper.getIsFinish() == 0) {
            throw new PlatformApiException("???????????????");
        }

        /**
         * ??????
         *
         * 1546788164255932417   ????????????
         * 1546788937710755842   ????????????
         * 1546789344491134978   ????????????
         * 1546789419250409474   ????????????
         * 1546789515505491970   ????????????
         *
         * 1546789617431273473  ????????????
         * 1546789785358622721  ????????????
         * 1546789869571858434  ???????????????
         */

        // ????????????????????????????????? ??????????????????

        // ????????????????????????
        PersonalReportData personalReportData = PersonalReportData.builder().build();

        // ??????????????????
        PersonalReport personalReport = PersonalReport.builder().build();

        // ????????????
        getStuDimensionIndexData(personalReportData, personalReport, paperId);

        // ????????????
        getBehaviorDimensionIndexData(personalReportData, personalReport, paperId);

        // ????????????
        getMentalDimensionIndexData(personalReportData, personalReport, paperId);

        // ????????????
        getOverallDimensionIndexData(personalReportData, personalReport, paperId);

        // ????????????
        getEmotionalDimensionIndexData(personalReportData, personalReport, paperId);

        // ????????????
        getSleepIndexData(personalReportData, personalReport, paperId);

        // ????????????
        getTestOverview(personalReport);

        // ???????????????
        getValidityAnswersData(personalReportData, personalReport, paperId);

        // ???????????? (???????????????????????? ???????????? ????????? )
        getSuicidalIdeationData(personalReportData, personalReport, paperId);

        // ??????????????????
        getStuRatingLevel(personalReportData, personalReport, paperId);

        // ????????????
        getParentRatingLevel(personalReportData, personalReport, userId);

        // ????????????
        getTeacherRatingLevel(personalReportData, personalReport, userId);

        // ??????????????????
        getRecommendedAttentionLevel(personalReport);

        // ????????????
        getUserInfo(personalReportData, personalReport, paperId, userId, paper.getTaskId());

        personalReport.setTestTime(DateUtil.LocalDateTimeToStringTime(paper.getStartTime()));

        personalReportData.setPaperId(paperId.toString());

        personalReport.setCreateTime(DateUtil.LocalDateTimeToStringTime(LocalDateTime.now()));
        personalReportData.setCreateTime(DateUtil.LocalDateTimeToStringTime(LocalDateTime.now()));

        mongoTemplate.save(personalReportData);
        mongoTemplate.save(personalReport);

        // ?????? ????????????????????????
        updateTaskUserData(personalReport, paper, userId);

        // ????????????????????? ??????
        statisticData(paper.getTaskId(), paperId, userId);

        // ????????????????????????
        if (personalReport.getAnswerIsValidity() == 1) {
            // ???????????????????????????
            statisticStressData(paper.getTaskId(), paperId, userId);
        }

        // ????????????????????????
        updatePaperRecordData(paper.getTaskId(), userId, personalReport.getId());

        // ??????????????????
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String tenantCode = SecureUtil.getTenantCode();
        CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            CompletableFuture.runAsync(() -> updateClassReportData(paper.getTaskId(), userId, tenantCode));
        });

        log.info("===== ??????{}???????????????????????? ==== {}", userId, paperId);
        return true;
    }


    /**
     * ????????????????????????
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
     * ??????????????????
     *
     * @param taskId
     * @param userId
     */
    void updateClassReportData(Long taskId, Long userId, String tenantCode) {
        UserExt userExt = taskUserMapper.getUserExtById(userId);
        if (Func.isNotEmpty(userExt.getGrade()) && Func.isNotEmpty(userExt.getClassId())) {
            reportTeacherClassService.addClassReport(taskId, Func.toLong(userExt.getGrade()), userExt.getClassId(), tenantCode);
        }
        log.info("????????????????????????");
    }


    /**
     * ????????????
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getStuDimensionIndexData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        DimensionStrategy stuStrategy = scoreStrategyFactory.getStrategy(1546788164255932417L);
        // ??????????????????
        DimensionIndexData stuDimensionIndexData = stuStrategy.dimensionScore(paperId);

        Map<Long, PersonalReport.ChartData> indexData = stuDimensionIndexData.getIndexData();

        // ????????????????????????
        // ???????????? ????????????
        personalReportData.setStudyStatusScore(Func.toDouble(stuDimensionIndexData.getTotalData().getScore()));

        /**
         *  1546788164788609025 ????????????
         *  1546788174330650626 ????????????
         *  1546788189962821634 ????????????
         */
        PersonalReport.ChartData learningChartData = indexData.get(1546788164788609025L);
        PersonalReport.ChartData timeManagementChartData = indexData.get(1546788174330650626L);
        PersonalReport.ChartData learningBurnoutChartData = indexData.get(1546788189962821634L);

        // ???????????? ????????????
        personalReportData.setLearningAttitudeScore(Func.toDouble(learningChartData.getScore()));
        // ???????????? ????????????
        personalReportData.setTimeManagementScore(Func.toDouble(timeManagementChartData.getScore()));
        // ???????????? ????????????
        personalReportData.setLearningBurnoutScore(Func.toDouble(learningBurnoutChartData.getScore()));

        // ??????????????????
        PersonalReport.ResultData resultData = new PersonalReport.ResultData();

        resultData.setTotalResult(stuDimensionIndexData.getTotalData());

        List<PersonalReport.ChartData> chartDataList = indexData.values().stream().collect(Collectors.toList());
        resultData.setChartData(chartDataList);

        personalReport.setLearningStatus(resultData);
    }


    /**
     * ????????????
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getBehaviorDimensionIndexData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        DimensionStrategy behaviorStrategy = scoreStrategyFactory.getStrategy(1546788937710755842L);
        // ??????????????????
        DimensionIndexData behaviorDimensionIndexData = behaviorStrategy.dimensionScore(paperId);


        Map<Long, PersonalReport.ChartData> indexData = behaviorDimensionIndexData.getIndexData();

        // ????????????????????????
        // ???????????? ????????????
        personalReportData.setBehaviorScore(Func.toDouble(behaviorDimensionIndexData.getTotalData().getScore()));

        /**
         *  1546788938151157761	?????????
         *  1546788944375504898	?????????
         *  1546788949077319682	?????????
         *  1546788953720414210	????????????
         */
        PersonalReport.ChartData moralChartData = indexData.get(1546788938151157761L);
        PersonalReport.ChartData stabilityChartData = indexData.get(1546788944375504898L);
        PersonalReport.ChartData disciplineChartData = indexData.get(1546788949077319682L);
        PersonalReport.ChartData otherPerformanceChartData = indexData.get(1546788953720414210L);

        // ????????? ????????????
        personalReportData.setMoralScore(Func.toDouble(moralChartData.getScore()));
        // ????????? ????????????
        personalReportData.setStabilityScore(Func.toDouble(stabilityChartData.getScore()));
        // ????????? ????????????
        personalReportData.setDisciplineScore(Func.toDouble(disciplineChartData.getScore()));
        // ???????????? ????????????
        personalReportData.setOtherPerformanceScore(Func.toDouble(otherPerformanceChartData.getScore()));

        // ??????????????????
        PersonalReport.ResultData resultData = new PersonalReport.ResultData();

        resultData.setTotalResult(behaviorDimensionIndexData.getTotalData());

        List<PersonalReport.ChartData> chartDataList = indexData.values().stream().collect(Collectors.toList());
        resultData.setChartData(chartDataList);

        personalReport.setBehavior(resultData);
    }


    /**
     * ????????????
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getMentalDimensionIndexData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        DimensionStrategy mentalStrategy = scoreStrategyFactory.getStrategy(1546789344491134978L);
        // ??????????????????
        DimensionIndexData mentalDimensionIndexData = mentalStrategy.dimensionScore(paperId);


        Map<Long, PersonalReport.ChartData> indexData = mentalDimensionIndexData.getIndexData();

        // ????????????????????????
        // ???????????? ????????????
        personalReportData.setMentalToughnessScore(Func.toDouble(mentalDimensionIndexData.getTotalData().getScore()));

        /**
         *  1546789344952508417	????????????
         *  1546789349885009922	????????????
         *  1546789354800734210	????????????
         *  1546789359766790145	????????????
         *  1546789364607016961	????????????
         *  1546789369476603906	????????????
         */
        PersonalReport.ChartData emotionManagementChartData = indexData.get(1546789344952508417L);
        PersonalReport.ChartData goalMotivationChartData = indexData.get(1546789349885009922L);
        PersonalReport.ChartData positiveAttentionChartData = indexData.get(1546789354800734210L);
        PersonalReport.ChartData schoolSupportChartData = indexData.get(1546789359766790145L);
        PersonalReport.ChartData interpersonalSupportData = indexData.get(1546789364607016961L);
        PersonalReport.ChartData familySupportChartData = indexData.get(1546789369476603906L);


        // ???????????? ????????????
        personalReportData.setEmotionManagementScore(Func.toDouble(emotionManagementChartData.getScore()));
        // ???????????? ????????????
        personalReportData.setGoalMotivationScore(Func.toDouble(goalMotivationChartData.getScore()));
        // ???????????? ????????????
        personalReportData.setPositiveAttentionScore(Func.toDouble(positiveAttentionChartData.getScore()));
        // ???????????? ????????????
        personalReportData.setSchoolSupportScore(Func.toDouble(schoolSupportChartData.getScore()));
        // ???????????? ????????????
        personalReportData.setInterpersonalSupportScore(Func.toDouble(interpersonalSupportData.getScore()));
        // ???????????? ????????????
        personalReportData.setFamilySupportScore(Func.toDouble(familySupportChartData.getScore()));


        // ??????????????????
        PersonalReport.ResultData resultData = new PersonalReport.ResultData();

        resultData.setTotalResult(mentalDimensionIndexData.getTotalData());

        List<PersonalReport.ChartData> chartDataList = indexData.values().stream().collect(Collectors.toList());
        resultData.setChartData(chartDataList);

        personalReport.setMentalToughness(resultData);
    }


    /**
     * ????????????
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getOverallDimensionIndexData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        DimensionStrategy overallStrategy = scoreStrategyFactory.getStrategy(1546789419250409474L);
        // ??????????????????
        DimensionIndexData overallDimensionIndexData = overallStrategy.dimensionScore(paperId);

        Map<Long, PersonalReport.ChartData> indexData = overallDimensionIndexData.getIndexData();

        // ????????????????????????
        // ???????????? ????????????
        personalReportData.setOverallStressScore(Func.toDouble(overallDimensionIndexData.getTotalData().getScore()));

        /**
         *  1546789419690811394L	????????????
         *  1546789426871459841L	????????????
         *  1546789434203103233L	???????????????
         *  1546789443141165057L	????????????
         *  1546789455623413761L	????????????
         */
        PersonalReport.ChartData studyStressChartData = indexData.get(1546789419690811394L);
        PersonalReport.ChartData interpersonalChartData = indexData.get(1546789426871459841L);
        PersonalReport.ChartData punishmentChartData = indexData.get(1546789434203103233L);
        PersonalReport.ChartData lossStressChartData = indexData.get(1546789443141165057L);
        PersonalReport.ChartData adaptationStressChartData = indexData.get(1546789455623413761L);


        // ???????????? ????????????
        personalReportData.setStudyStressScore(Func.toDouble(studyStressChartData.getScore()));
        // ???????????? ????????????
        personalReportData.setInterpersonalStressScore(Func.toDouble(interpersonalChartData.getScore()));
        // ??????????????? ????????????
        personalReportData.setPunishmentStressScore(Func.toDouble(punishmentChartData.getScore()));
        // ???????????? ????????????
        personalReportData.setLossStressScore(Func.toDouble(lossStressChartData.getScore()));
        // ???????????? ????????????
        personalReportData.setAdaptationStressScore(Func.toDouble(adaptationStressChartData.getScore()));


        // ??????????????????
        PersonalReport.ResultData resultData = new PersonalReport.ResultData();

        resultData.setTotalResult(overallDimensionIndexData.getTotalData());

        List<PersonalReport.ChartData> chartDataList = indexData.values().stream().collect(Collectors.toList());
        resultData.setChartData(chartDataList);

        personalReport.setStressIndex(resultData);
    }

    /**
     * ????????????
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getEmotionalDimensionIndexData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        DimensionStrategy emotionalStrategy = scoreStrategyFactory.getStrategy(1546789515505491970L);
        // ??????????????????
        DimensionIndexData emotionalDimensionIndexData = emotionalStrategy.dimensionScore(paperId);


        Map<Long, PersonalReport.ChartData> indexData = emotionalDimensionIndexData.getIndexData();

        // ????????????????????????
        // ???????????? ????????????
        personalReportData.setEmotionalIndexScore(Func.toDouble(emotionalDimensionIndexData.getTotalData().getScore()));

        /**
         *  1546789515920728066L	??????
         *  1546789524955258882L	??????
         *  1546789530986668033L	??????
         *  1546789540155416578L	????????????
         *  1546789547860353026L	??????
         *  1546789556760666113L	??????
         */
        PersonalReport.ChartData compulsionChartData = indexData.get(1546789515920728066L);
        PersonalReport.ChartData paranoiaChartData = indexData.get(1546789524955258882L);
        PersonalReport.ChartData hostilityChartData = indexData.get(1546789530986668033L);
        PersonalReport.ChartData interpersonalSensitivityChartData = indexData.get(1546789540155416578L);
        PersonalReport.ChartData anxietyChartData = indexData.get(1546789547860353026L);
        PersonalReport.ChartData depressionChartData = indexData.get(1546789556760666113L);


        // ?????? ????????????
        personalReportData.setCompulsionScore(Func.toDouble(compulsionChartData.getScore()));
        // ?????? ????????????
        personalReportData.setParanoiaScore(Func.toDouble(paranoiaChartData.getScore()));
        // ?????? ????????????
        personalReportData.setHostilityScore(Func.toDouble(hostilityChartData.getScore()));
        // ???????????? ????????????
        personalReportData.setInterpersonalSensitivityScore(Func.toDouble(interpersonalSensitivityChartData.getScore()));
        // ?????? ????????????
        personalReportData.setAnxietyScore(Func.toDouble(anxietyChartData.getScore()));
        // ?????? ????????????
        personalReportData.setDepressionScore(Func.toDouble(depressionChartData.getScore()));


        // ??????????????????
        PersonalReport.ResultData resultData = new PersonalReport.ResultData();

        resultData.setTotalResult(emotionalDimensionIndexData.getTotalData());

        List<PersonalReport.ChartData> chartDataList = indexData.values().stream().collect(Collectors.toList());
        resultData.setChartData(chartDataList);

        personalReport.setEmotionalIndex(resultData);
    }


    /**
     * ????????????
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getSleepIndexData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        DimensionStrategy sleepIndexStrategy = scoreStrategyFactory.getStrategy(1546789785358622721L);
        // ??????????????????
        DimensionIndexData sleepIndexData = sleepIndexStrategy.dimensionScore(paperId);

        // ????????????????????????
        // ???????????? ????????????
        personalReportData.setSleepIndexScore(Func.toDouble(sleepIndexData.getTotalData().getScore()));

        // ??????????????????
        PersonalReport.ResultData resultData = new PersonalReport.ResultData();

        resultData.setTotalResult(sleepIndexData.getTotalData());

        personalReport.setSleepIndex(resultData);
    }

    /**
     * ????????????
     *
     * @param personalReport
     */
    void getTestOverview(PersonalReport personalReport) {

        PersonalReport.TestOverview testOverview = new PersonalReport.TestOverview();
        // ??????
        List<PersonalReport.OverviewData> activeList = new LinkedList<>();

        // ????????????
        PersonalReport.TotalResult learnResult = personalReport.getLearningStatus().getTotalResult();
        PersonalReport.OverviewData learnOverviewData = new PersonalReport.OverviewData();
        learnOverviewData.setTitle(learnResult.getTitle());
        learnOverviewData.setResult(learnResult.getResult());
        learnOverviewData.setScore(learnResult.getScore());
        learnOverviewData.setFontColor(learnResult.getFontColor());

        // ????????????
        PersonalReport.TotalResult behaviorResult = personalReport.getBehavior().getTotalResult();
        PersonalReport.OverviewData behaviorData = new PersonalReport.OverviewData();
        behaviorData.setTitle(behaviorResult.getTitle());
        behaviorData.setResult(behaviorResult.getResult());
        behaviorData.setScore(behaviorResult.getScore());
        behaviorData.setFontColor(behaviorResult.getFontColor());

        // ????????????
        PersonalReport.TotalResult mentalToughnessResult = personalReport.getMentalToughness().getTotalResult();
        PersonalReport.OverviewData mentalToughnessData = new PersonalReport.OverviewData();
        mentalToughnessData.setTitle(mentalToughnessResult.getTitle());
        mentalToughnessData.setResult(mentalToughnessResult.getResult());
        mentalToughnessData.setScore(mentalToughnessResult.getScore());
        mentalToughnessData.setFontColor(mentalToughnessResult.getFontColor());

        activeList.add(learnOverviewData);
        activeList.add(behaviorData);
        activeList.add(mentalToughnessData);

        // ??????
        List<PersonalReport.OverviewData> negativeList = new LinkedList<>();

        // ????????????
        PersonalReport.TotalResult stressIndexResult = personalReport.getStressIndex().getTotalResult();
        PersonalReport.OverviewData stressIndexData = new PersonalReport.OverviewData();
        stressIndexData.setTitle(stressIndexResult.getTitle());
        stressIndexData.setResult(stressIndexResult.getResult());
        stressIndexData.setScore(stressIndexResult.getScore());
        stressIndexData.setFontColor(stressIndexResult.getFontColor());

        // ????????????
        PersonalReport.TotalResult emotionalIndexResult = personalReport.getEmotionalIndex().getTotalResult();
        PersonalReport.OverviewData emotionalIndexData = new PersonalReport.OverviewData();
        emotionalIndexData.setTitle(emotionalIndexResult.getTitle());
        emotionalIndexData.setResult(emotionalIndexResult.getResult());
        emotionalIndexData.setScore(emotionalIndexResult.getScore());
        emotionalIndexData.setFontColor(emotionalIndexResult.getFontColor());

        // ????????????
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
     * ????????????
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getSuicidalIdeationData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {
        // ?????????????????????
        Integer suicidalIdeationScore = paperQuestionMapper.dimensionTotalScore(1546789617431273473L, paperId);

        List<TestDimensionIndexConclusion> suicidalIdeationConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery()
                .eq(TestDimensionIndexConclusion::getDimensionId, 1546789617431273473L)
                .isNull(TestDimensionIndexConclusion::getIndexId));

        // ??????????????????
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

                // ????????????????????????
                personalReportData.setSuicidalIdeationScore(Func.toDouble(suicidalIdeationScore.toString()));
                personalReportData.setSuicidalIdeationLevel(riskIndex);
            }
        });

        // ??????????????????
        personalReport.setMentalHealthRatings(mentalHealthRatings);

    }

    /**
     * ???????????????
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getValidityAnswersData(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        // ??????????????? ?????????
        Integer suicidalIdeationScore = paperQuestionMapper.validityAnswersScore(paperId);

        // ????????????????????????
        personalReportData.setValidityAnswersScore(Func.toDouble(suicidalIdeationScore.toString()));

        /**
         * 0-1  ??????????????????????????????
         * 2-8  ??????????????????????????????
         */
        String result = "";
        Integer answerIsValidity = 0;
        if (suicidalIdeationScore > 1) {
            result = "???????????????????????????";
        } else {
            result = "???????????????????????????";
            answerIsValidity = 1;
        }

        // ??????????????????
        personalReport.setAnswerValidity(result);
        personalReport.setAnswerIsValidity(answerIsValidity);
    }


    /**
     * ?????????????????? ?????????????????????
     *
     * @param personalReportData
     * @param personalReport
     * @param paperId
     */
    void getStuRatingLevel(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId) {

        /**
         * ??????????????????
         *
         * ????????????????????????1  ??????????????????????????????????????????????????????????????????
         *
         * ????????????????????????2????????????
         * [????????????+????????????+????????????+???100-???????????????+???100-???????????????+???100-???????????????]??6
         */

        // ????????????????????????1
        /**
         * ???????????????????????????????????????????????????????????????????????????????????????
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


        // ????????????????????????2
        // [????????????+????????????+????????????+???100-???????????????+???100-???????????????+???100-???????????????]??6

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

        // ????????????
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

        // ??????????????????
        Integer dRiskIndex = indexSet.last();
        String dimensionResult = getDimensionResult(dRiskIndex);

        PersonalReport.LevelData stuLevelData = new PersonalReport.LevelData();
        stuLevelData.setResult(dimensionResult);
        stuLevelData.setRiskIndex(dRiskIndex);
        stuLevelData.setFontColor(getFontColor(dRiskIndex));

        // ??????????????????
        PersonalReport.MentalHealthRatings mentalHealthRatings = personalReport.getMentalHealthRatings();
        mentalHealthRatings.setStudentRatingLevel(stuLevelData);

    }


    /**
     * ????????????
     */
    void getParentRatingLevel(PersonalReportData personalReportData, PersonalReport personalReport, Long userId) {

        /**
         * 8???9??????????????????
         * 0-3  ???????????????????????????   0
         * 4-27 ?????????????????????1???   1
         * 43809
         */
        // ???????????? ???????????????????????????ID ??????????????????ID?????? 8 9 ?????????

        // ????????????????????? ?????????
        Long paperId = paperQuestionMapper.getPaperIdByUserId(userId);

        PersonalReport.LevelData levelData = new PersonalReport.LevelData();

        levelData.setResult("????????????");
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

                // ???????????????????????????
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

                // ????????????????????????
                personalReportData.setParentalAssessmentScore(Func.toDouble(optionScore.toString()));
            }
        }

        // ??????????????????
        PersonalReport.MentalHealthRatings mentalHealthRatings = personalReport.getMentalHealthRatings();
        mentalHealthRatings.setParentsRatingLevel(levelData);

    }

    /**
     * ????????????
     */
    void getTeacherRatingLevel(PersonalReportData personalReportData, PersonalReport personalReport, Long userId) {

        PersonalReport.LevelData levelData = new PersonalReport.LevelData();

        /**
         * ????????????????????????
         * ??????????????????
         */
        List<TestTeacherPaperRecord> paperRecordList = testTeacherPaperRecordMapper.selectList(Wrappers.<TestTeacherPaperRecord>lambdaQuery()
                .eq(TestTeacherPaperRecord::getStuId, userId)
                .orderByDesc(TestTeacherPaperRecord::getCreateTime));

        if (paperRecordList.size() > 0) {

            TestTeacherPaperRecord testTeacherPaperRecord = paperRecordList.get(0);

            Integer riskLevel = testTeacherPaperRecord.getRiskLevel();

            // ????????????????????????
            personalReportData.setTeacherRatingsScore(Func.toDouble(testTeacherPaperRecord.getScore()));
            personalReportData.setTeacherRatingsLevel(riskLevel);

            levelData.setRiskIndex(riskLevel);
            levelData.setResult(testTeacherPaperRecord.getRiskResult());
            levelData.setFontColor(getFontColor(riskLevel));
        } else {
            levelData.setResult("????????????");
            levelData.setFontColor(getFontColor(-1));
            personalReportData.setTeacherRatingsScore(0d);
        }

        // ??????????????????
        PersonalReport.MentalHealthRatings mentalHealthRatings = personalReport.getMentalHealthRatings();
        mentalHealthRatings.setTeachRatingLevel(levelData);
    }

    /**
     * ??????????????????
     */
    void getRecommendedAttentionLevel(PersonalReport personalReport) {

        PersonalReport.LevelData levelData = new PersonalReport.LevelData();

        /**
         *  ??????????????????
         *
         * ????????????????????????????????????1???/2???/3????????????????????????3??????
         * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         */

        // ??????????????????
        PersonalReport.LevelData psychologicalCrisisAlert = personalReport.getMentalHealthRatings().getPsychologicalCrisisAlert();

        Integer suRiskIndex = 0;

        Integer riskIndex = psychologicalCrisisAlert.getRiskIndex();
        if (riskIndex == 1 || riskIndex == 2 || riskIndex == 3) {
            // ?????????????????????????????? ????????? 1???/2???/3??? ????????? ??????????????????3???
            suRiskIndex = 3;
        } else {
            //  ?????? ???????????????????????????????????????????????????????????????????????????????????????
            suRiskIndex = getRiskIndex(personalReport);
        }

        levelData.setRiskIndex(suRiskIndex);
        levelData.setResult(getFocusDimensionResult(suRiskIndex));
        levelData.setFontColor(getFontColor(suRiskIndex));

        // ??????????????????
        PersonalReport.MentalHealthRatings mentalHealthRatings = personalReport.getMentalHealthRatings();
        mentalHealthRatings.setRecommendedAttentionLevel(levelData);

    }

    /**
     * ??????????????????
     *
     * @param personalReport
     * @return
     */
    Integer getRiskIndex(PersonalReport personalReport) {

        PersonalReport.LevelData studentRatingLevel = personalReport.getMentalHealthRatings().getStudentRatingLevel();
        PersonalReport.LevelData teachRatingLevel = personalReport.getMentalHealthRatings().getTeachRatingLevel();
        PersonalReport.LevelData parentsRatingLevel = personalReport.getMentalHealthRatings().getParentsRatingLevel();

        TreeSet<Integer> indexSet = new TreeSet<>();

        // ????????????????????????????????????
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
     * ????????????
     */
    void getUserInfo(PersonalReportData personalReportData, PersonalReport personalReport, Long paperId, Long userId, Long taskId) {

        PersonalReport.UserInfo userInfo = new PersonalReport.UserInfo();


        User user = userClient.userInfoById(userId).getData();

        userInfo.setUserId(userId);


        userInfo.setName(user.getRealName());
        userInfo.setStudentNo(user.getAccount());
        if (Func.isNotEmpty(user.getSex())) {
            userInfo.setSex(user.getSex() == 0 ? "???" : "???");
        }

        // ???????????? ??? ????????????
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


        // ???????????? | ?????????????????????

        Long parentPaperId = paperQuestionMapper.getPaperIdByUserId(userId);

        String familyStructures = "";

        String whetherLivingWithParents = "";

        if (Func.isNotEmpty(parentPaperId)) {

            ParentPaperQuestionVO question = paperQuestionMapper.getParentPaperQuestionByPaperIdQuestionId(parentPaperId, 2L);
            ParentPaperQuestionVO isQuestion = paperQuestionMapper.getParentPaperQuestionByPaperIdQuestionId(parentPaperId, 3L);

            //  option_type 0 ?????? 1 ??????

            String optionId = question.getOptionId();
            String isOptionId = isQuestion.getOptionId();

            ParentOptionVO parentOptionVO = paperQuestionMapper.getOptionInfoById(Long.valueOf(optionId));

            ParentOptionVO isParentOptionVO = paperQuestionMapper.getOptionInfoById(Long.valueOf(isOptionId));

            // ????????????
            familyStructures = parentOptionVO.getTitle();

            if (parentOptionVO.getOptionType() == 1) {
                familyStructures = question.getOtherContent();
            }
            familyStructures = replaceStr(familyStructures);
            userInfo.setFamilyStructures(familyStructures);

            // ?????????????????????
            whetherLivingWithParents = isParentOptionVO.getTitle();
            if (isParentOptionVO.getOptionType() == 1) {
                whetherLivingWithParents = isQuestion.getOtherContent();
            }
            whetherLivingWithParents = replaceStr(whetherLivingWithParents);
            userInfo.setWhetherLivingWithParents(whetherLivingWithParents);

            /**
             * ????????????????????????
             *
             * 2135703722  ??????
             * 2135703723  ??????
             * 2135703724  ??????
             * 2135703725  ???????????????
             * 2135703726  ??????
             * 2135703727  ??????
             */
            String fKey = "education" + ":" + "family_structure" + ":" + "statistic" + ":" + taskId + ":" + optionId;
            redisTemplate.opsForSet().add(fKey, userId);

            /**
             * ???????????????????????????
             *
             * 2135703729	 ???????????????????????????????????????
             * 2135703730	 ????????????????????????????????????
             * 2135703731	 ??????????????????????????????????????????
             * 2135703732	 ????????????
             * 2135703733  ??????
             */
            String aloneKey = "education" + ":" + "is_alone" + ":" + "statistic" + ":" + taskId + ":" + isOptionId;
            redisTemplate.opsForSet().add(aloneKey, userId);

            // TODO ????????????????????????????????????

        } else {
            userInfo.setFamilyStructures("????????????");
            userInfo.setWhetherLivingWithParents("????????????");
        }


        // ??????????????????
        personalReport.setUserInfo(userInfo);

        // ????????????????????????
        personalReportData.setUserId(userId.toString());
    }


    /**
     * ????????????????????????
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

            // ????????????
            Integer psychologicalLevel = personalReport.getMentalHealthRatings()
                    .getPsychologicalCrisisAlert().getRiskIndex();


            // ??????????????????
            Integer answerIsValidity = personalReport.getAnswerIsValidity();

            // ????????????
            if (Func.isNotEmpty(personalReport.getMentalHealthRatings().getRecommendedAttentionLevel())) {
                Integer recommendedAttentionLevel = personalReport.getMentalHealthRatings().getRecommendedAttentionLevel()
                        .getRiskIndex();
                testTaskUser.setFollowLevel(recommendedAttentionLevel);
            }
            // ????????????
            if (Func.isNotEmpty(personalReport.getMentalHealthRatings().getParentsRatingLevel())) {
                Integer parentsRatingLevel = personalReport.getMentalHealthRatings().getParentsRatingLevel()
                        .getRiskIndex();
                testTaskUser.setParentComments(parentsRatingLevel);
            }

            // ????????????
            if (Func.isNotEmpty(personalReport.getMentalHealthRatings().getTeachRatingLevel())) {
                Integer teachRatingLevel = personalReport.getMentalHealthRatings().getTeachRatingLevel()
                        .getRiskIndex();
                testTaskUser.setTeacherComments(teachRatingLevel);
            }
            // ????????????
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
         * ?????? ??????????????????????????????????????????????????????????????????4?????????????????????
         *     ??????????????????????????????????????????????????????????????????0?????????????????????
         */

        // ????????????
        List<String> optionList = paperQuestionMapper.selectList(
                Wrappers.<TestPaperQuestion>lambdaQuery().eq(TestPaperQuestion::getPaperId, paperId)
                        .select(TestPaperQuestion::getOptionId)).stream()
                .map(TestPaperQuestion::getOptionId).collect(
                        Collectors.toList());
        // ???????????????????????? optionList????????????, ???????????????????????????,????????????

        // ??????
        List<Long> activeIndexList = Arrays.asList(1546788164788609025L, 1546788174330650626L,
                1546788938151157761L, 1546788944375504898L, 1546788949077319682L,
                1546788953720414210L, 1546789344952508417L, 1546789349885009922L, 1546789354800734210L,
                1546789359766790145L, 1546789364607016961L, 1546789369476603906L);

        // ??????
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
         *  ????????????	?????????????????? ??????????????????????????????????????????0???????????????????????????????????*100%
         * ???????????????????????????????????????????????????????????????????????????????????????3???????????????????????????????????????
         *
         * -- 1546789419250409474	????????????
         */

        // ????????????
        List<String> optionList = paperQuestionMapper.selectList(
                Wrappers.<TestPaperQuestion>lambdaQuery().eq(TestPaperQuestion::getPaperId, paperId)
                        .select(TestPaperQuestion::getOptionId)).stream()
                .map(TestPaperQuestion::getOptionId).collect(
                        Collectors.toList());
        // ???????????????????????? optionList????????????, ???????????????????????????,????????????

        List<TestIndex> testIndexList = indexMapper.indexList(1546789419250409474L);
        testIndexList.forEach(index -> {

            // ????????????
            List<TestQuestion> questionList = questionMapper.questionList(index.getId());

            questionList.forEach(question -> {
                List<String> optionIdList = optionMapper.optionIdListByQuestionId(question.getId());

                // ?????????????????????????????? ??????
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
        // ?????? l1 ???????????? l2 ????????????
        AtomicBoolean result = new AtomicBoolean(false);
        l2.forEach(l -> {
            if (l1.contains(l)) {
                result.set(true);
            }
        });
        return result.get();
    }


    /**
     * ??????????????? ????????????
     *
     * @param source
     * @return
     */
    public static String[] toStringArray(String source) {
        return source.substring(1, source.length() - 1).split(",");
    }

    /**
     * ????????????
     *
     * @param riskIndex ??????
     * @return
     */
    static String getDimensionResult(Integer riskIndex) {

        String result = "";
        if (riskIndex == 0) {
            result = "??????";
        } else {
            result = riskIndex + "???";
        }
        return result;
    }

    /**
     * ????????????
     *
     * @param riskIndex ??????
     * @return
     */
    static String getFocusDimensionResult(Integer riskIndex) {

        String result = "";
        if (riskIndex == 0) {
            result = "??????";
        } else {
            result = riskIndex + "?????????";
        }
        return result;
    }


    /**
     * ????????????
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
     * ????????????
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
            // ?????????
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
                reportUserVO.setSexName(reportUserVO.getSex() == 0 ? "???" : "???");
            }
        });
        return reportUserVOPage;
    }

    @Override
    public boolean updatePersonalReportsTeacherRating(List<Long> userIdList) {

        /**
         *  ???????????? ???????????????????????? PersonalReportData  ??????????????????  PersonalReport
         */
        userIdList.forEach(userId -> {

            // ??????????????????????????????
            Query queryData = new Query();
            queryData.addCriteria(Criteria.where("userId").is(userId.toString()));
            queryData.with(Sort.by((Order.desc("createTime"))));
            PersonalReportData personalReportData = mongoTemplate.findOne(queryData, PersonalReportData.class);
            // ????????????????????????
            Query query = new Query();
            query.addCriteria(Criteria.where("userInfo.userId").is(userId));
            query.with(Sort.by((Order.desc("createTime"))));
            PersonalReport personalReport = mongoTemplate.findOne(query, PersonalReport.class);

            // ????????????????????????
            if (Func.isNotEmpty(personalReportData) && Func.isNotEmpty(personalReport)) {

                List<TestTeacherPaperRecord> paperRecordList = testTeacherPaperRecordMapper.selectList(Wrappers.<TestTeacherPaperRecord>lambdaQuery()
                        .eq(TestTeacherPaperRecord::getStuId, userId)
                        .orderByDesc(TestTeacherPaperRecord::getCreateTime));

                if (paperRecordList.size() > 0) {

                    TestTeacherPaperRecord testTeacherPaperRecord = paperRecordList.get(0);

                    Integer riskLevel = testTeacherPaperRecord.getRiskLevel();

                    // ????????????????????????
                    personalReportData.setTeacherRatingsScore(Func.toDouble(testTeacherPaperRecord.getScore()));
                    personalReportData.setTeacherRatingsLevel(riskLevel);

                    PersonalReport.LevelData levelData = new PersonalReport.LevelData();

                    levelData.setRiskIndex(riskLevel);
                    levelData.setResult(testTeacherPaperRecord.getRiskResult());
                    levelData.setFontColor(getFontColor(riskLevel));

                    // ??????????????????
                    PersonalReport.MentalHealthRatings mentalHealthRatings = personalReport.getMentalHealthRatings();
                    mentalHealthRatings.setTeachRatingLevel(levelData);

                    mongoTemplate.save(personalReport);
                    mongoTemplate.save(personalReportData);

                    // ?????? ????????????????????????
                    updateRecommendedAttentionLevel(userId);

                    String testPaperId = personalReportData.getPaperId();
                    TestPaper testPaper = paperMapper.selectById(testPaperId);

                    // ????????????????????????
                    updateTeacherData(levelData.getRiskIndex(), userId, testPaper.getTaskId());

                    String tenantCode = SecureUtil.getTenantCode();
                    // ??????????????????
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

        // ????????????????????????????????????
        /**
         *  ???????????? ???????????????????????? PersonalReportData  ??????????????????  PersonalReport
         */

        // ??????????????????????????????
        Query queryData = new Query();
        queryData.addCriteria(Criteria.where("userId").is(userId.toString()));
        queryData.with(Sort.by((Order.desc("createTime"))));
        PersonalReportData personalReportData = mongoTemplate.findOne(queryData, PersonalReportData.class);
        // ????????????????????????
        Query query = new Query();
        query.addCriteria(Criteria.where("userInfo.userId").is(userId));
        query.with(Sort.by((Order.desc("createTime"))));
        PersonalReport personalReport = mongoTemplate.findOne(query, PersonalReport.class);

        // ????????????????????????
        if (Func.isNotEmpty(personalReportData) && Func.isNotEmpty(personalReport)) {

            // ????????????????????? ?????????
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

                // ???????????????????????????
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

                // ???????????? | ?????????????????????
                ParentPaperQuestionVO question = paperQuestionMapper.getParentPaperQuestionByPaperIdQuestionId(paperId, 2L);
                ParentPaperQuestionVO isQuestion = paperQuestionMapper.getParentPaperQuestionByPaperIdQuestionId(paperId, 3L);

                //  option_type 0 ?????? 1 ??????

                String optionId = question.getOptionId();
                String isOptionId = isQuestion.getOptionId();

                ParentOptionVO parentOptionVO = paperQuestionMapper.getOptionInfoById(Long.valueOf(optionId));
                ParentOptionVO isParentOptionVO = paperQuestionMapper.getOptionInfoById(Long.valueOf(isOptionId));

                String familyStructures = "";

                String whetherLivingWithParents = "";


                UserInfo userInfo = personalReport.getUserInfo();

                // ????????????
                familyStructures = parentOptionVO.getTitle();

                if (parentOptionVO.getOptionType() == 1) {
                    familyStructures = question.getOtherContent();
                }
                familyStructures = replaceStr(familyStructures);
                userInfo.setFamilyStructures(familyStructures);

                // ?????????????????????
                whetherLivingWithParents = isParentOptionVO.getTitle();
                if (isParentOptionVO.getOptionType() == 1) {
                    whetherLivingWithParents = isQuestion.getOtherContent();
                }
                whetherLivingWithParents = replaceStr(whetherLivingWithParents);
                userInfo.setWhetherLivingWithParents(whetherLivingWithParents);

                String testPaperId = personalReportData.getPaperId();
                TestPaper testPaper = paperMapper.selectById(testPaperId);
                /**
                 * ????????????????????????
                 *
                 * 2135703722  ??????
                 * 2135703723  ??????
                 * 2135703724  ??????
                 * 2135703725  ???????????????
                 * 2135703726  ??????
                 * 2135703727  ??????
                 */
                String fKey = "education" + ":" + "family_structure" + ":" + "statistic" + ":" + testPaper.getTaskId() + ":" + optionId;
                redisTemplate.opsForSet().add(fKey, userId);

                /**
                 * ???????????????????????????
                 *
                 * 2135703729	 ???????????????????????????????????????
                 * 2135703730	 ????????????????????????????????????
                 * 2135703731	 ??????????????????????????????????????????
                 * 2135703732	 ????????????
                 * 2135703733  ??????
                 */
                String aloneKey = "education" + ":" + "is_alone" + ":" + "statistic" + ":" + testPaper.getTaskId() + ":" + isOptionId;
                redisTemplate.opsForSet().add(aloneKey, userId);


                // ????????????????????????
                personalReportData.setParentalAssessmentScore(Func.toDouble(optionScore.toString()));

                // ??????????????????
                PersonalReport.MentalHealthRatings mentalHealthRatings = personalReport.getMentalHealthRatings();
                mentalHealthRatings.setParentsRatingLevel(levelData);

                mongoTemplate.save(personalReport);
                mongoTemplate.save(personalReportData);

                // ?????? ????????????????????????
                updateRecommendedAttentionLevel(userId);

                // ????????????????????????
                updateParentData(levelData.getRiskIndex(), userId, testPaper.getTaskId());

                String tenantCode = testPaper.getTenantCode();
                // ??????????????????
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
     * ????????????????????????
     */
    void updateParentData(Integer riskIndex, Long userId, Long taskId) {
        //   ????????????????????????
        TestTaskUser testTaskUser = taskUserMapper.getTaskUser(userId, taskId);
        if (ObjectUtils.isNotEmpty(testTaskUser)) {
            taskUserMapper.updateTaskUser(testTaskUser.getId(), riskIndex);
        }
    }

    /**
     * ????????????????????????
     */
    void updateTeacherData(Integer riskIndex, Long userId, Long taskId) {
        //   ????????????????????????
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
     * ????????????????????????
     *
     * @param userId
     * @return
     */
    public boolean updateRecommendedAttentionLevel(Long userId) {

        // ????????????????????????
        Query query = new Query();
        query.addCriteria(Criteria.where("userInfo.userId").is(userId));
        query.with(Sort.by((Order.desc("createTime"))));
        PersonalReport personalReport = mongoTemplate.findOne(query, PersonalReport.class);

        // ??????????????????????????????
        if (Func.isNotEmpty(personalReport)) {

            PersonalReport.LevelData levelData = new PersonalReport.LevelData();

            /**
             *  ??????????????????
             *
             * ????????????????????????????????????1???/2???/3????????????????????????3??????
             * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
             */

            // ??????????????????
            PersonalReport.LevelData psychologicalCrisisAlert = personalReport.getMentalHealthRatings().getPsychologicalCrisisAlert();

            Integer suRiskIndex = 0;

            Integer riskIndex = psychologicalCrisisAlert.getRiskIndex();
            if (riskIndex == 1 || riskIndex == 2 || riskIndex == 3) {
                // ?????????????????????????????? ????????? 1???/2???/3??? ????????? ??????????????????3???
                suRiskIndex = 3;
            } else {
                //  ?????? ???????????????????????????????????????????????????????????????????????????????????????
                suRiskIndex = getRiskIndex(personalReport);
            }

            levelData.setRiskIndex(suRiskIndex);
            levelData.setResult(getFocusDimensionResult(suRiskIndex));
            levelData.setFontColor(getFontColor(suRiskIndex));

            // ??????????????????
            PersonalReport.MentalHealthRatings mentalHealthRatings = personalReport.getMentalHealthRatings();
            mentalHealthRatings.setRecommendedAttentionLevel(levelData);
            mongoTemplate.save(personalReport);
        }

        return true;
    }
}
