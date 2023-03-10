package org.mentpeak.test.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mentpeak.common.util.IntervalUtil;
import org.mentpeak.core.auth.PlatformUser;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.log.exception.PlatformApiException;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.test.dto.BindTeacherDTO;
import org.mentpeak.test.entity.*;
import org.mentpeak.test.entity.mongo.*;
import org.mentpeak.test.mapper.*;
import org.mentpeak.test.service.IReportTeacherGradeService;
import org.mentpeak.test.service.ReportGroupsService;
import org.mentpeak.test.strategy.scoring.ColorEnum;
import org.mentpeak.test.vo.*;
import org.mentpeak.user.entity.MenuData;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ????????????????????? ???????????????
 *
 * @author lxp
 * @since 2022-07-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportTeacherGradeServiceImpl extends BaseServiceImpl<ReportTeacherGradeMapper, ReportTeacherGrade> implements IReportTeacherGradeService {

    private final TestPaperMapper testPaperMapper;

    private final MongoTemplate mongoTemplate;

    private final TestDimensionIndexConclusionMapper dimensionIndexConclusionMapper;

    private final TestTaskDepartmentMapper departmentMapper;

    private final TestIndexMapper testIndexMapper;

    private final ReportTeacherGradeMapper reportTeacherGradeMapper;

    private final MenuDataMapper menuDataMapper;

    private final ReportGroupsService reportGroupsService;

    @Override
    public IPage<ReportTeacherGradeVO> selectReportTeacherGradePage(IPage<ReportTeacherGradeVO> page, ReportTeacherGradeVO reportTeacherGrade) {
        return page.setRecords(baseMapper.selectReportTeacherGradePage(page, reportTeacherGrade));
    }

    @Override
    public GradeReportVO addGradeReport(Long taskId, Long gradeId, String tenantCode) {
        GradeReportVO vo = new GradeReportVO();
        // ?????????????????????????????????????????????????????????
        GradeReportData reportData = new GradeReportData();
        // ??????????????????????????????????????????????????????
        List<Long> classIdList = testPaperMapper.getClassByGradeId(gradeId);
        Criteria criteria = Criteria.where("classId").in(classIdList);
        Criteria criteria1 = Criteria.where("taskId").is(taskId).and("gradeId").is(gradeId);
        TypedAggregation<ClassReportData> agg = Aggregation.newAggregation(ClassReportData.class,
                Aggregation.match(criteria1),
                Aggregation.match(criteria),
                Aggregation.group("null")
                        .sum("totalPeople").as("totalPeople")
                        .sum("testPeople").as("testPeople")
                        .sum("noTestPeople").as("noTestPeople")
                        .sum("invalidPeople").as("invalidPeople")
                        .avg("completionRate").as("completionRate")
                        .sum("invalidPeople").as("invalidPeople")
                        .sum("followPeopleOne").as("followPeopleOne")
                        .avg("followRateOne").as("followRateOne")
                        .sum("followPeopleTwo").as("followPeopleTwo")
                        .avg("followRateTwo").as("followRateTwo")
                        .sum("followPeopleThree").as("followPeopleThree")
                        .avg("followRateThree").as("followRateThree")
                        .sum("followPeople").as("followPeople")
                        .avg("followRate").as("followRate")
                        .sum("warnPeopleOne").as("warnPeopleOne")
                        .avg("warnRateOne").as("warnRateOne")
                        .sum("warnPeopleTwo").as("warnPeopleTwo")
                        .avg("warnRateTwo").as("warnRateTwo")
                        .sum("warnPeopleThree").as("warnPeopleThree")
                        .avg("warnRateThree").as("warnRateThree")
                        .sum("warnPeople").as("warnPeople")
                        .avg("warnRate").as("warnRate")
                        .avg("studentRateOne").as("studentRateOne")
                        .avg("studentRateTwo").as("studentRateTwo")
                        .avg("studentRateThree").as("studentRateThree")
                        .avg("studentRate").as("studentRate")
                        .avg("teacherRateOne").as("teacherRateOne")
                        .avg("teacherRateTwo").as("teacherRateTwo")
                        .avg("teacherRateThree").as("teacherRateThree")
                        .avg("teacherRate").as("teacherRate")
                        .avg("parentRateOne").as("parentRateOne")
                        .avg("parentRate").as("parentRate")

                        .avg("studyStatusScore").as("studyStatusScore")
                        .avg("behaviorScore").as("behaviorScore")
                        .avg("mentalToughnessScore").as("mentalToughnessScore")
                        .avg("overallStressScore").as("overallStressScore")
                        .avg("emotionalIndexScore").as("emotionalIndexScore")
                        .avg("sleepIndexScore").as("sleepIndexScore")
                        .avg("suicidalIdeationScore").as("suicidalIdeationScore")
                        .avg("validityAnswersScore").as("validityAnswersScore")
                        .avg("parentalAssessmentScore").as("parentalAssessmentScore")
                        .avg("teacherRatingsScore").as("teacherRatingsScore")
                        .avg("suicidalIdeationLevel").as("suicidalIdeationLevel")
                        .avg("parentalAssessmentLevel").as("parentalAssessmentLevel")
                        .avg("teacherRatingsLevel").as("teacherRatingsLevel")
                        .avg("learningAttitudeScore").as("learningAttitudeScore")
                        .avg("timeManagementScore").as("timeManagementScore")
                        .avg("learningBurnoutScore").as("learningBurnoutScore")
                        .avg("moralScore").as("moralScore")
                        .avg("stabilityScore").as("stabilityScore")
                        .avg("disciplineScore").as("disciplineScore")
                        .avg("otherPerformanceScore").as("otherPerformanceScore")
                        .avg("emotionManagementScore").as("emotionManagementScore")
                        .avg("goalMotivationScore").as("goalMotivationScore")
                        .avg("positiveAttentionScore").as("positiveAttentionScore")
                        .avg("schoolSupportScore").as("schoolSupportScore")
                        .avg("interpersonalSupportScore").as("interpersonalSupportScore")
                        .avg("familySupportScore").as("familySupportScore")
                        .avg("studyStressScore").as("studyStressScore")
                        .avg("interpersonalStressScore").as("interpersonalStressScore")
                        .avg("punishmentStressScore").as("punishmentStressScore")
                        .avg("lossStressScore").as("lossStressScore")
                        .avg("adaptationStressScore").as("adaptationStressScore")
                        .avg("compulsionScore").as("compulsionScore")
                        .avg("paranoiaScore").as("paranoiaScore")
                        .avg("hostilityScore").as("hostilityScore")
                        .avg("interpersonalSensitivityScore").as("interpersonalSensitivityScore")
                        .avg("anxietyScore").as("anxietyScore")
                        .avg("depressionScore").as("depressionScore")
                        .sum("learningAttitudeScoreVeryBad").as("learningAttitudeScoreVeryBad")
                        .sum("learningAttitudeScoreBad").as("learningAttitudeScoreBad")
                        .sum("learningAttitudeScoreGenerally").as("learningAttitudeScoreGenerally")
                        .sum("learningAttitudeScoreGood").as("learningAttitudeScoreGood")
                        .sum("timeManagementScoreVeryBad").as("timeManagementScoreVeryBad")
                        .sum("timeManagementScoreBad").as("timeManagementScoreBad")
                        .sum("timeManagementScoreGenerally").as("timeManagementScoreGenerally")
                        .sum("timeManagementScoreGood").as("timeManagementScoreGood")
                        .sum("learningBurnoutScoreVeryBad").as("learningBurnoutScoreVeryBad")
                        .sum("learningBurnoutScoreBad").as("learningBurnoutScoreBad")
                        .sum("learningBurnoutScoreGenerally").as("learningBurnoutScoreGenerally")
                        .sum("learningBurnoutScoreGood").as("learningBurnoutScoreGood")
                        .sum("moralScoreVeryBad").as("moralScoreVeryBad")
                        .sum("moralScoreBad").as("moralScoreBad")
                        .sum("moralScoreGenerally").as("moralScoreGenerally")
                        .sum("moralScoreGood").as("moralScoreGood")
                        .sum("stabilityScoreVeryBad").as("stabilityScoreVeryBad")
                        .sum("stabilityScoreBad").as("stabilityScoreBad")
                        .sum("stabilityScoreGenerally").as("stabilityScoreGenerally")
                        .sum("stabilityScoreGood").as("stabilityScoreGood")
                        .sum("disciplineScoreVeryBad").as("disciplineScoreVeryBad")
                        .sum("disciplineScoreBad").as("disciplineScoreBad")
                        .sum("disciplineScoreGenerally").as("disciplineScoreGenerally")
                        .sum("disciplineScoreGood").as("disciplineScoreGood")
                        .sum("otherPerformanceScoreVeryBad").as("otherPerformanceScoreVeryBad")
                        .sum("otherPerformanceScoreBad").as("otherPerformanceScoreBad")
                        .sum("otherPerformanceScoreGenerally").as("otherPerformanceScoreGenerally")
                        .sum("otherPerformanceScoreGood").as("otherPerformanceScoreGood")
                        .sum("emotionManagementScoreVeryBad").as("emotionManagementScoreVeryBad")
                        .sum("emotionManagementScoreBad").as("emotionManagementScoreBad")
                        .sum("emotionManagementScoreGenerally").as("emotionManagementScoreGenerally")
                        .sum("emotionManagementScoreGood").as("emotionManagementScoreGood")
                        .sum("goalMotivationScoreVeryBad").as("goalMotivationScoreVeryBad")
                        .sum("goalMotivationScoreBad").as("goalMotivationScoreBad")
                        .sum("goalMotivationScoreGenerally").as("goalMotivationScoreGenerally")
                        .sum("goalMotivationScoreGood").as("goalMotivationScoreGood")
                        .sum("positiveAttentionScoreVeryBad").as("positiveAttentionScoreVeryBad")
                        .sum("positiveAttentionScoreBad").as("positiveAttentionScoreBad")
                        .sum("positiveAttentionScoreGenerally").as("positiveAttentionScoreGenerally")
                        .sum("positiveAttentionScoreGood").as("positiveAttentionScoreGood")
                        .sum("schoolSupportScoreVeryBad").as("schoolSupportScoreVeryBad")
                        .sum("schoolSupportScoreBad").as("schoolSupportScoreBad")
                        .sum("schoolSupportScoreGenerally").as("schoolSupportScoreGenerally")
                        .sum("schoolSupportScoreGood").as("schoolSupportScoreGood")
                        .sum("interpersonalSupportScoreVeryBad").as("interpersonalSupportScoreVeryBad")
                        .sum("interpersonalSupportScoreBad").as("interpersonalSupportScoreBad")
                        .sum("interpersonalSupportScoreGenerally").as("interpersonalSupportScoreGenerally")
                        .sum("interpersonalSupportScoreGood").as("interpersonalSupportScoreGood")
                        .sum("familySupportScoreVeryBad").as("familySupportScoreVeryBad")
                        .sum("familySupportScoreBad").as("familySupportScoreBad")
                        .sum("familySupportScoreGenerally").as("familySupportScoreGenerally")
                        .sum("familySupportScoreGood").as("familySupportScoreGood")
                        .sum("studyStressScoreVeryBad").as("studyStressScoreVeryBad")
                        .sum("studyStressScoreBad").as("studyStressScoreBad")
                        .sum("studyStressScoreGenerally").as("studyStressScoreGenerally")
                        .sum("studyStressScoreGood").as("studyStressScoreGood")
                        .sum("interpersonalStressScoreVeryBad").as("interpersonalStressScoreVeryBad")
                        .sum("interpersonalStressScoreBad").as("interpersonalStressScoreBad")
                        .sum("interpersonalStressScoreGenerally").as("interpersonalStressScoreGenerally")
                        .sum("interpersonalStressScoreGood").as("interpersonalStressScoreGood")
                        .sum("punishmentStressScoreVeryBad").as("punishmentStressScoreVeryBad")
                        .sum("punishmentStressScoreBad").as("punishmentStressScoreBad")
                        .sum("punishmentStressScoreGenerally").as("punishmentStressScoreGenerally")
                        .sum("punishmentStressScoreGood").as("punishmentStressScoreGood")
                        .sum("lossStressScoreVeryBad").as("lossStressScoreVeryBad")
                        .sum("lossStressScoreBad").as("lossStressScoreBad")
                        .sum("lossStressScoreGenerally").as("lossStressScoreGenerally")
                        .sum("lossStressScoreGood").as("lossStressScoreGood")
                        .sum("adaptationStressScoreVeryBad").as("adaptationStressScoreVeryBad")
                        .sum("adaptationStressScoreBad").as("adaptationStressScoreBad")
                        .sum("adaptationStressScoreGenerally").as("adaptationStressScoreGenerally")
                        .sum("adaptationStressScoreGood").as("adaptationStressScoreGood")
                        .sum("compulsionScoreVeryBad").as("compulsionScoreVeryBad")
                        .sum("compulsionScoreBad").as("compulsionScoreBad")
                        .sum("compulsionScoreGenerally").as("compulsionScoreGenerally")
                        .sum("compulsionScoreGood").as("compulsionScoreGood")
                        .sum("paranoiaScoreVeryBad").as("paranoiaScoreVeryBad")
                        .sum("paranoiaScoreBad").as("paranoiaScoreBad")
                        .sum("paranoiaScoreGenerally").as("paranoiaScoreGenerally")
                        .sum("paranoiaScoreGood").as("paranoiaScoreGood")
                        .sum("hostilityScoreVeryBad").as("hostilityScoreVeryBad")
                        .sum("hostilityScoreBad").as("hostilityScoreBad")
                        .sum("hostilityScoreGenerally").as("hostilityScoreGenerally")
                        .sum("hostilityScoreGood").as("hostilityScoreGood")
                        .sum("interpersonalSensitivityScoreVeryBad").as("interpersonalSensitivityScoreVeryBad")
                        .sum("interpersonalSensitivityScoreBad").as("interpersonalSensitivityScoreBad")
                        .sum("interpersonalSensitivityScoreGenerally").as("interpersonalSensitivityScoreGenerally")
                        .sum("interpersonalSensitivityScoreGood").as("interpersonalSensitivityScoreGood")
                        .sum("anxietyScoreVeryBad").as("anxietyScoreVeryBad")
                        .sum("anxietyScoreBad").as("anxietyScoreBad")
                        .sum("anxietyScoreGenerally").as("anxietyScoreGenerally")
                        .sum("anxietyScoreGood").as("anxietyScoreGood")
                        .sum("depressionScoreVeryBad").as("depressionScoreVeryBad")
                        .sum("depressionScoreBad").as("depressionScoreBad")
                        .sum("depressionScoreGenerally").as("depressionScoreGenerally")
                        .sum("depressionScoreGood").as("depressionScoreGood")
                        .sum("sleepVeryBad").as("sleepVeryBad")
                        .sum("sleepBad").as("sleepBad")
                        .sum("sleepGenerally").as("sleepGenerally")
                        .sum("sleepGood").as("sleepGood")

        );
        AggregationResults<ClassReportData> result = mongoTemplate.aggregate(agg, "classReportData", ClassReportData.class);
        // ??????????????????
        List<ClassReportData> mappedResults = result.getMappedResults();
        if (ObjectUtils.isEmpty(mappedResults)) {
            return null;
        }
        ClassReportData report = mappedResults.get(0);
        BeanUtil.copy(report, reportData);
        // ???????????????????????????????????????
        // ???????????????
        Long totalPeople = testPaperMapper.getTotalPeople(gradeId, null, tenantCode);
        // ????????????
        List<Long> testPeople = testPaperMapper.getTestPeople(taskId, gradeId, null, tenantCode);
        // ??????????????????
        long noTestPeople = totalPeople - testPeople.size();
        reportData.setTotalPeople(Func.toInt(totalPeople));
        reportData.setNoTestPeople(Func.toInt(noTestPeople));
        reportData.setTaskId(taskId);
        reportData.setGradeId(gradeId);
        // ??????mongodb-???????????????id,??????id???????????????????????????????????????????????????
        Query query = new Query();
        query.addCriteria(Criteria.where("gradeId").is(gradeId).and("taskId").is(taskId));
        GradeReportData one = mongoTemplate.findOne(query, GradeReportData.class, "gradeReportData");
        if (ObjectUtils.isNotEmpty(one)) {
            mongoTemplate.remove(query, "gradeReportData");
            mongoTemplate.save(reportData, "gradeReportData");
        } else {
            // ??????
            mongoTemplate.save(reportData, "gradeReportData");
        }


        // ????????????
        String gradeName = testPaperMapper.getClassName(gradeId);
        vo.setTitle(gradeName + "????????????");
        // ??????????????????
        GTestFInishVO gTestFInishVO = new GTestFInishVO();
        gTestFInishVO.setTotalTestPeople(reportData.getTestPeople());
        gTestFInishVO.setTotalNoTestPeople(reportData.getNoTestPeople());
        gTestFInishVO.setTotalInvalidPeople(reportData.getInvalidPeople());
        gTestFInishVO.setTotalCompletionRate(dealRate(reportData.getCompletionRate()));
        // ????????????-?????????????????? ???????????????
        List<GInvalidVO> gInvalidVOList = new ArrayList<>();
        addClassTest(classIdList, taskId, gTestFInishVO, vo, gInvalidVOList, gradeId);

        GradeInvalidVO gradeInvalidVO = new GradeInvalidVO();
        gradeInvalidVO.setTotalInvalidPeople(report.getInvalidPeople());
        gradeInvalidVO.setGInvalidVOList(gInvalidVOList);
        vo.setGradeInvalidVOList(gradeInvalidVO);

        // todo:??????????????????????????????
        if (report.getTestPeople() == report.getInvalidPeople()) {
            GradeReport byId = mongoTemplate.findById("gradeReport", GradeReport.class, "gradeReport");
            if (ObjectUtils.isEmpty(byId)) {
                byId = saveGrade();
            }
            GradeReport gradeReport = new GradeReport();
            gradeReport.setTaskId(taskId);
            gradeReport.setGradeId(gradeId);
            gradeReport.setTestModuleVO(vo.getTestModuleVO());
            gradeReport.setTestModuleTwoVO(byId.getTestModuleTwoVO());
            gradeReport.setTestOverview(byId.getTestOverview());
            gradeReport.setDimensionList(byId.getDimensionList());
            gradeReport.setTitle(vo.getTitle());
            gradeReport.setGradeInvalidVOList(byId.getGradeInvalidVOList());

            saveReport(query, gradeReport);
            log.info("=============??????????????????==============");
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            CompletableFuture.runAsync(() -> {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                CompletableFuture.runAsync(() -> updateSchoolReport(taskId));
            });

            return null;
        }

        // ??????????????????
        GTestModuleTwoVO testModuleTwoVO = new GTestModuleTwoVO();
        // ??????????????????
        List<FollowLevelVO> followLevelVOList = new ArrayList<>();
        FollowLevelVO followLevelVO1 = new FollowLevelVO();
        FollowLevelVO followLevelVO2 = new FollowLevelVO();
        FollowLevelVO followLevelVO3 = new FollowLevelVO();
        FollowLevelVO followLevelVO4 = new FollowLevelVO();
        followLevelVO1.setFollowName("1?????????");
        followLevelVO1.setPeople(report.getFollowPeopleOne());
        followLevelVO1.setClassRatio(dealRate(report.getFollowRateOne()));
        followLevelVO2.setFollowName("2?????????");
        followLevelVO2.setPeople(report.getFollowPeopleTwo());
        followLevelVO2.setClassRatio(dealRate(report.getFollowRateTwo()));
        followLevelVO3.setFollowName("3?????????");
        followLevelVO3.setPeople(report.getFollowPeopleThree());
        followLevelVO3.setClassRatio(dealRate(report.getFollowRateThree()));
        followLevelVO4.setFollowName("??????");
        followLevelVO4.setPeople(report.getFollowPeople());
        followLevelVO4.setClassRatio(dealRate(report.getFollowRate()));

        followLevelVOList.add(followLevelVO1);
        followLevelVOList.add(followLevelVO2);
        followLevelVOList.add(followLevelVO3);
        followLevelVOList.add(followLevelVO4);
        testModuleTwoVO.setAttentionLevelTable(followLevelVOList);

        List<GTestModuleTwoVO.TypeData> attentionLevel = new ArrayList<>();
        // ????????????????????????
        GTestModuleTwoVO.TypeData typeData1 = addChartData(report, 1);
        // ??????????????????
        GTestModuleTwoVO.TypeData typeData2 = addChartData(report, 2);
        // ??????????????????
        GTestModuleTwoVO.TypeData typeData3 = addChartData(report, 3);
        // ??????????????????
        GTestModuleTwoVO.TypeData typeData4 = addChartData(report, 4);
        attentionLevel.add(typeData1);
        attentionLevel.add(typeData2);
        attentionLevel.add(typeData3);
        attentionLevel.add(typeData4);
        testModuleTwoVO.setAttentionLevel(attentionLevel);

        // ????????????
        List<GTestModuleTwoVO.TypeData> warnAttentionLevel = new ArrayList<>();
        GTestModuleTwoVO.TypeData typeData5 = addChartData(report, 5);
        warnAttentionLevel.add(typeData5);
        testModuleTwoVO.setWarningLevel(warnAttentionLevel);
        List<FollowLevelVO> warnLevelVOList = new ArrayList<>();
        FollowLevelVO warnLevelVO1 = new FollowLevelVO();
        FollowLevelVO warnLevelVO2 = new FollowLevelVO();
        FollowLevelVO warnLevelVO3 = new FollowLevelVO();
        FollowLevelVO warnLevelVO4 = new FollowLevelVO();
        warnLevelVO1.setFollowName("1?????????");
        warnLevelVO1.setPeople(report.getWarnPeopleOne());
        warnLevelVO1.setClassRatio(dealRate(report.getWarnRateOne()));
        warnLevelVO2.setFollowName("2?????????");
        warnLevelVO2.setPeople(report.getWarnPeopleTwo());
        warnLevelVO2.setClassRatio(dealRate(report.getWarnRateTwo()));
        warnLevelVO3.setFollowName("3?????????");
        warnLevelVO3.setPeople(report.getWarnPeopleThree());
        warnLevelVO3.setClassRatio(dealRate(report.getWarnRateThree()));
        warnLevelVO4.setFollowName("?????????");
        warnLevelVO4.setPeople(report.getWarnPeople());
        warnLevelVO4.setClassRatio(dealRate(report.getWarnRate()));
        warnLevelVOList.add(warnLevelVO1);
        warnLevelVOList.add(warnLevelVO2);
        warnLevelVOList.add(warnLevelVO3);
        warnLevelVOList.add(warnLevelVO4);
        testModuleTwoVO.setWarningLevelTable(warnLevelVOList);

        // ????????????-????????????
        addFollowPeople(classIdList, taskId, testModuleTwoVO, gradeId);
        vo.setTestModuleTwoVO(testModuleTwoVO);

        // ????????????
        PersonalReport.TestOverview testTotalScore = getTestTotalScore(dealRate2(report.getStudyStatusScore()), dealRate2(report.getBehaviorScore()), dealRate2(report.getMentalToughnessScore()),
                dealRate2(report.getOverallStressScore()), dealRate2(report.getEmotionalIndexScore()), dealRate2(report.getSleepIndexScore()));
        vo.setTestOverview(testTotalScore);

        // ????????????
        List<GDimensionReportVO> dimensionVO = getStudyStatus(taskId, gradeId, reportData, classIdList);
        vo.setDimensionList(dimensionVO);

        // ?????????????????????????????????
        GradeReport gradeReport = new GradeReport();
        gradeReport.setGradeId(gradeId);
        gradeReport.setTaskId(taskId);
        gradeReport.setTestModuleVO(vo.getTestModuleVO());
        gradeReport.setTestModuleTwoVO(vo.getTestModuleTwoVO());
        gradeReport.setTestOverview(vo.getTestOverview());
        gradeReport.setDimensionList(vo.getDimensionList());
        gradeReport.setTitle(vo.getTitle());
        gradeReport.setGradeInvalidVOList(vo.getGradeInvalidVOList());

        saveReport(query, gradeReport);

        // ?????????????????????????????????
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            CompletableFuture.runAsync(() -> updateSchoolReport(taskId));
        });

        return vo;
    }

    void saveReport(Query query, GradeReport gradeReport) {
        GradeReport two = mongoTemplate.findOne(query, GradeReport.class, "gradeReport");
        if (ObjectUtils.isNotEmpty(two)) {
            mongoTemplate.remove(query, "gradeReport");
            mongoTemplate.save(gradeReport, "gradeReport");
        } else {
            // ??????
            mongoTemplate.save(gradeReport, "gradeReport");
        }
    }

    public GradeReport saveGrade() {
        log.info("===???????????????????????????===");
        String fileName = "gradeReport.json";
        String path = this.getClass().getClassLoader().getResource(fileName).getPath();

        String jsonStr = "";
        try {
            File jsonFile = new File(path);
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }

            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ???????????????
        GradeReport report = JSONObject.parseObject(jsonStr, GradeReport.class);
        report.setId("gradeReport");
        mongoTemplate.save(report, "gradeReport");
        return report;
    }

    void updateSchoolReport(Long taskId) {
        reportGroupsService.generateReport(taskId);
        log.info("????????????????????????");
    }

    @Override
    public GradeReportVO getGradeReport(Long taskId, Long gradeId) {
        GradeReportVO vo = new GradeReportVO();
        Query query = new Query();
        query.addCriteria(Criteria.where("gradeId").is(gradeId).and("taskId").is(taskId));
        GradeReport one = mongoTemplate.findOne(query, GradeReport.class, "gradeReport");
        if (ObjectUtils.isNotEmpty(one)) {
            BeanUtil.copyProperties(one, vo);
        }
        return vo;
    }

    @Override
    public IPage<ReportListVO> getReportList(IPage<ReportListVO> page, Long id, Long taskId) {
        PlatformUser user = SecureUtil.getUser();
        IPage<ReportListVO> selectPage = null;
        if ("5".equals(user.getRoleId())) {
            // ????????????id,??????id?????????????????????????????????id
            IPage<ReportTeacherGrade> page1 = new Page<>();
            page1.setCurrent(page.getCurrent());
            page1.setSize(page.getSize());
            IPage<ReportTeacherGrade> list = reportTeacherGradeMapper.selectPage(page1, Wrappers.<ReportTeacherGrade>lambdaQuery()
                    .eq(ReportTeacherGrade::getTaskId, taskId)
                    .eq(ReportTeacherGrade::getTeacherId, user.getUserId())
                    .eq(Func.isNotEmpty(id), ReportTeacherGrade::getGradeId, id)
                    .eq(ReportTeacherGrade::getIsDeleted, 0));
            List<ReportTeacherGrade> records = list.getRecords();

            List<ReportListVO> result = new ArrayList<>();
            records.stream().forEach(reportTeacherClass -> {
                List<ReportListVO> list2 = departmentMapper.getGradeList2(reportTeacherClass.getTaskId(), reportTeacherClass.getGradeId());
                result.addAll(list2);
            });
            selectPage = new Page<>(list.getCurrent(), list.getSize(), list.getTotal());
            selectPage.setRecords(result);
        } else {
            // ????????????????????????????????????
            selectPage = departmentMapper.getGradeList(page, taskId, id);
        }
        // ????????????id,??????id?????????????????????????????????id
        List<ReportTeacherGrade> classList = reportTeacherGradeMapper.selectList(Wrappers.<ReportTeacherGrade>lambdaQuery()
                .eq(ReportTeacherGrade::getTaskId, taskId));
        List<Long> collect = classList.stream().map(ReportTeacherGrade::getTeacherId).collect(Collectors.toList());
        Long userId = SecureUtil.getUserId();
        List<ReportListVO> records = selectPage.getRecords();
        List<ReportListVO> result = new ArrayList<>();
        AtomicInteger total = new AtomicInteger(0);
        records.stream().forEach(reportListVO -> {
            // ????????????id?????????id??????mongodb
            reportListVO.setTaskId(taskId);
            Query query = new Query();
            query.addCriteria(Criteria.where("gradeId").is(reportListVO.getGradeId()).and("taskId").is(taskId));
            GradeReportData one = mongoTemplate.findOne(query, GradeReportData.class, "gradeReportData");
            if (ObjectUtils.isNotEmpty(one)) {
                reportListVO.setTestPeople(one.getTestPeople());
                reportListVO.setTotalPeople(one.getTotalPeople());
                reportListVO.setIsReport(1);
                if ("5".equals(user.getRoleId())) {
                    if (!collect.contains(userId)) {
                        reportListVO.setIsPermission(0);
                    }
                }
                result.add(reportListVO);
                total.getAndIncrement();
            }


        });
        selectPage.setTotal(total.get());
        selectPage.setRecords(result);
        return selectPage;
    }

    @Override
    public boolean bindTeacher(BindTeacherDTO dto) {
        // ??????????????????????????????
        BindTeacher userByPhone = departmentMapper.getUserByPhone(dto.getPhone());
        if (ObjectUtils.isEmpty(userByPhone)) {
            throw new PlatformApiException("????????????????????????");
        }
        // ???????????????????????????????????????
        if (!dto.getName().equals(userByPhone.getName())) {
            throw new PlatformApiException("????????????????????????");
        }
        if (!IsIdCard(dto.getIdCard())) {
            throw new PlatformApiException("??????????????????????????????");
        }
        Long count = reportTeacherGradeMapper.selectCount(Wrappers.<ReportTeacherGrade>lambdaQuery().eq(ReportTeacherGrade::getGradeId, dto.getGradeId())
                .eq(ReportTeacherGrade::getTaskId, dto.getTaskId())
                .eq(ReportTeacherGrade::getTeacherId, userByPhone.getUserId()));
        if (count > 0) {
            throw new PlatformApiException("??????????????????");
        }
        ReportTeacherGrade grade = new ReportTeacherGrade();
        grade.setGradeId(dto.getGradeId());
        grade.setTeacherId(userByPhone.getUserId());
        grade.setTenantCode(SecureUtil.getTenantCode());
        grade.setTaskId(dto.getTaskId());
        grade.setCreateTime(LocalDateTime.now());
        grade.setCreateUser(SecureUtil.getUserId());
        grade.setRoleName(dto.getRoleName());
        reportTeacherGradeMapper.insert(grade);

        // ????????????????????????????????????
        MenuData menuData = new MenuData();
        menuData.setUserId(userByPhone.getUserId());
        menuData.setMenuId(8L);
        menuData.setRoleId(5L);
        menuData.setDataId(dto.getTaskId());
        menuDataMapper.insert(menuData);
        return true;
    }

    /**
     * ????????????????????????
     *
     * @param str
     * @return ?????????????????????????????????, ?????? <b>true </b>,????????? <b>false </b>
     */
    public static boolean IsIdCard(String str) {
        String regex = "\\d{17}[\\d|x]|\\d{15}";
        Pattern r = Pattern.compile(regex);
        Matcher m = r.matcher(str);
        return m.matches();
    }

    @Override
    public List<BindTeacherVO> bindTeacherList(Long taskId, Long gradeId) {
        return reportTeacherGradeMapper.getBindTeacherList(taskId, gradeId);
    }

    @Override
    public boolean removeBindTeacher(Long userId, Long taskId, Long gradeId) {
        int delete = reportTeacherGradeMapper.delete(Wrappers.<ReportTeacherGrade>lambdaQuery().eq(ReportTeacherGrade::getTeacherId, userId)
                .eq(ReportTeacherGrade::getTaskId, taskId)
                .eq(ReportTeacherGrade::getGradeId, gradeId)
                .eq(ReportTeacherGrade::getIsDeleted, 0));
        // ???????????????????????????
        menuDataMapper.delete(Wrappers.<MenuData>lambdaQuery().eq(MenuData::getUserId, userId).eq(MenuData::getMenuId, 8));
        return delete > 0 ? true : false;
    }

    /**
     * ????????????????????????
     *
     * @param report
     * @param type   1???????????? 2???????????? 3???????????? 4???????????? 5????????????
     * @author hzl
     * @date 2022/9/6 11:57
     */
    public GTestModuleTwoVO.TypeData addChartData(ClassReportData report, Integer type) {
        GTestModuleTwoVO.TypeData typeData = new GTestModuleTwoVO.TypeData();
        List<GTestModuleTwoVO.ChartData> chartData = new ArrayList<>();
        GTestModuleTwoVO.ChartData chart1 = new GTestModuleTwoVO.ChartData();
        GTestModuleTwoVO.ChartData chart2 = new GTestModuleTwoVO.ChartData();
        GTestModuleTwoVO.ChartData chart3 = new GTestModuleTwoVO.ChartData();
        GTestModuleTwoVO.ChartData chart4 = new GTestModuleTwoVO.ChartData();

        if (type == 1) {
            typeData.setTitle("");
            chart1.setName("3?????????");
            chart1.setValue(Func.toDouble(dealRate2(report.getFollowRateThree())));
            chart1.setChartColor(ColorEnum.THREECOLOR.getValue());
            chart2.setName("2?????????");
            chart2.setValue(Func.toDouble(dealRate2(report.getFollowRateTwo())));
            chart2.setChartColor(ColorEnum.TWOCOLOR.getValue());
            chart3.setName("1?????????");
            chart3.setValue(Func.toDouble(dealRate2(report.getFollowRateOne())));
            chart3.setChartColor(ColorEnum.ONECOLOR.getValue());
            chart4.setName("??????");
            chart4.setValue(Func.toDouble(dealRate2(report.getFollowRate())));
            chart4.setChartColor(ColorEnum.ZEROCOLOR.getValue());
        } else if (type == 2) {
            typeData.setTitle("??????????????????");
            chart1.setName("3?????????");
            chart1.setValue(Func.toDouble(dealRate2(report.getStudentRateThree())));
            chart1.setChartColor(ColorEnum.THREECOLOR.getValue());
            chart2.setName("2?????????");
            chart2.setValue(Func.toDouble(dealRate2(report.getStudentRateTwo())));
            chart2.setChartColor(ColorEnum.TWOCOLOR.getValue());
            chart3.setName("1?????????");
            chart3.setValue(Func.toDouble(dealRate2(report.getStudentRateOne())));
            chart3.setChartColor(ColorEnum.ONECOLOR.getValue());
            chart4.setName("??????");
            chart4.setValue(Func.toDouble(dealRate2(report.getStudentRate())));
            chart4.setChartColor(ColorEnum.ZEROCOLOR.getValue());
        } else if (type == 3) {
            typeData.setTitle("??????????????????");
            chart1.setName("3?????????");
            chart1.setValue(Func.toDouble(dealRate2(report.getTeacherRateThree())));
            chart1.setChartColor(ColorEnum.THREECOLOR.getValue());
            chart2.setName("2?????????");
            chart2.setValue(Func.toDouble(dealRate2(report.getTeacherRateTwo())));
            chart2.setChartColor(ColorEnum.TWOCOLOR.getValue());
            chart3.setName("1?????????");
            chart3.setValue(Func.toDouble(dealRate2(report.getTeacherRateOne())));
            chart3.setChartColor(ColorEnum.ONECOLOR.getValue());
            chart4.setName("??????");
            chart4.setValue(Func.toDouble(dealRate2(report.getTeacherRate())));
            chart4.setChartColor(ColorEnum.ZEROCOLOR.getValue());
        } else if (type == 4) {
            typeData.setTitle("??????????????????");
            chart3.setName("1?????????");
            chart3.setValue(Func.toDouble(dealRate2(report.getParentRateOne())));
            chart3.setChartColor(ColorEnum.ONECOLOR.getValue());
            chart4.setName("??????");
            chart4.setValue(Func.toDouble(dealRate2(report.getParentRate())));
            chart4.setChartColor(ColorEnum.ZEROCOLOR.getValue());
        } else if (type == 5) {
            typeData.setTitle("");
            chart1.setName("3?????????");
            chart1.setValue(Func.toDouble(dealRate2(report.getWarnRateThree())));
            chart1.setChartColor(ColorEnum.THREECOLOR.getValue());
            chart2.setName("2?????????");
            chart2.setValue(Func.toDouble(dealRate2(report.getWarnRateTwo())));
            chart2.setChartColor(ColorEnum.TWOCOLOR.getValue());
            chart3.setName("1?????????");
            chart3.setValue(Func.toDouble(dealRate2(report.getWarnRateOne())));
            chart3.setChartColor(ColorEnum.ONECOLOR.getValue());
            chart4.setName("?????????");
            chart4.setValue(Func.toDouble(dealRate2(report.getWarnRate())));
            chart4.setChartColor(ColorEnum.ZEROCOLOR.getValue());
        }
        if (type == 4) {
            chartData.add(chart3);
            chartData.add(chart4);
        } else {
            chartData.add(chart1);
            chartData.add(chart2);
            chartData.add(chart3);
            chartData.add(chart4);
        }
        typeData.setChartData(chartData);
        return typeData;
    }

    // ????????????
    public List<GDimensionReportVO> getStudyStatus(Long taskId, Long gradeId, GradeReportData gradeReportData, List<Long> classIdList) {
        List<GDimensionReportVO> voList = new ArrayList<>();
        GDimensionReportVO vo = new GDimensionReportVO();
        // ????????????id,??????????????????????????????id
        QueryWrapper<TestTaskDepartment> queryWrapper = Wrappers.query();
        queryWrapper.select("test_department_id").eq("test_task_id", taskId);
        List<Object> gradeIdList = departmentMapper.selectObjs(queryWrapper);
        Criteria criteria = Criteria.where("gradeId").in(gradeIdList);
        TypedAggregation<PersonalReportData2> agg = Aggregation.newAggregation(PersonalReportData2.class,
                Aggregation.match(criteria),
                Aggregation.group("null")
                        .avg("studyStatusScore").as("studyStatusScore")
                        .avg("behaviorScore").as("behaviorScore")
                        .avg("mentalToughnessScore").as("mentalToughnessScore")
                        .avg("overallStressScore").as("overallStressScore")
                        .avg("emotionalIndexScore").as("emotionalIndexScore")
                        .avg("sleepIndexScore").as("sleepIndexScore")
                        .avg("suicidalIdeationScore").as("suicidalIdeationScore")
                        .avg("validityAnswersScore").as("validityAnswersScore")
                        .avg("parentalAssessmentScore").as("parentalAssessmentScore")
                        .avg("teacherRatingsScore").as("teacherRatingsScore")
                        .avg("suicidalIdeationLevel").as("suicidalIdeationLevel")
                        .avg("parentalAssessmentLevel").as("parentalAssessmentLevel")
                        .avg("teacherRatingsLevel").as("teacherRatingsLevel")
                        .avg("learningAttitudeScore").as("learningAttitudeScore")
                        .avg("timeManagementScore").as("timeManagementScore")
                        .avg("learningBurnoutScore").as("learningBurnoutScore")
                        .avg("moralScore").as("moralScore")
                        .avg("stabilityScore").as("stabilityScore")
                        .avg("disciplineScore").as("disciplineScore")
                        .avg("otherPerformanceScore").as("otherPerformanceScore")
                        .avg("emotionManagementScore").as("emotionManagementScore")
                        .avg("goalMotivationScore").as("goalMotivationScore")
                        .avg("positiveAttentionScore").as("positiveAttentionScore")
                        .avg("schoolSupportScore").as("schoolSupportScore")
                        .avg("interpersonalSupportScore").as("interpersonalSupportScore")
                        .avg("familySupportScore").as("familySupportScore")
                        .avg("studyStressScore").as("studyStressScore")
                        .avg("interpersonalStressScore").as("interpersonalStressScore")
                        .avg("punishmentStressScore").as("punishmentStressScore")
                        .avg("lossStressScore").as("lossStressScore")
                        .avg("adaptationStressScore").as("adaptationStressScore")
                        .avg("compulsionScore").as("compulsionScore")
                        .avg("paranoiaScore").as("paranoiaScore")
                        .avg("hostilityScore").as("hostilityScore")
                        .avg("interpersonalSensitivityScore").as("interpersonalSensitivityScore")
                        .avg("anxietyScore").as("anxietyScore")
                        .avg("depressionScore").as("depressionScore"));
        AggregationResults<PersonalReportData2> result = mongoTemplate.aggregate(agg, "gradeReportData", PersonalReportData2.class);
        // ??????????????????
        List<PersonalReportData2> mappedResults = result.getMappedResults();
        if (ObjectUtils.isEmpty(mappedResults)) {
            return null;
        }
        PersonalReportData2 report = mappedResults.get(0);

        // ????????????
        // ????????????
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        String className = testPaperMapper.getClassName(gradeId);
        totalResultVO1.setTitle(className);
        totalResultVO1.setFontColor(ColorEnum.BLUE.getValue());
        totalResultVO1.setScore(dealRate2(gradeReportData.getStudyStatusScore()).toString());
        TotalResultVO totalResultVO2 = new TotalResultVO();
        totalResultVO2.setTitle("????????????");
        totalResultVO2.setFontColor(ColorEnum.BLUE2.getValue());
        totalResultVO2.setScore(dealRate2(report.getStudyStatusScore()).toString());
        totalResultVOList.add(totalResultVO1);
        totalResultVOList.add(totalResultVO2);
        vo.setTotalResultVOList(totalResultVOList);
        // ???????????????
        List<TestIndex> testIndices = testIndexMapper.selectList(Wrappers.<TestIndex>lambdaQuery()
                .eq(TestIndex::getIsDeleted, 0)
                .eq(TestIndex::getDimensionId, 1546788164255932417L));
        List<String> targetList = testIndices.stream().map(TestIndex::getName).collect(Collectors.toList());
        TargetResultVO targetResultVO = addTargetResult(targetList, gradeReportData, report, vo, className);

        // ???????????????????????????
        addTitleName(targetResultVO, 1, className);
        // ??????????????????
        List<GDimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();

        targetList.stream().forEach(s -> {
            typicalStudent(gradeReportData, typicalStudentList, s);
        });
        vo.setTypicalStudentList(typicalStudentList);
        // ??????????????????
        List<Object> classDiffList = new ArrayList<>();
        // ????????????
        Map<String, Double> map1 = new HashMap<>();
        // ????????????
        Map<String, Double> map2 = new HashMap<>();
        // ????????????
        Map<String, Double> map3 = new HashMap<>();
        // ????????????????????????
        classIdList.stream().forEach(classId -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // ????????????
                String name = testPaperMapper.getClassName(classId);
                map1.put(name, classReport.getLearningAttitudeScoreDiff());
                map2.put(name, classReport.getTimeManagementScoreDiff());
                map3.put(name, classReport.getLearningBurnoutScoreDiff());
                addClassDiffOne(classDiffList, classReport, targetList, name);
            }
        });
        vo.setClassDiffList(classDiffList);

        // ??????????????????
        List<String> illustrate = new ArrayList<>();
        List<Map.Entry<String, Double>> list1 = mapSort(map1);
        addTargetResult(list1, "????????????", illustrate);
        List<Map.Entry<String, Double>> list2 = mapSort(map2);
        addTargetResult(list2, "????????????", illustrate);
        List<Map.Entry<String, Double>> list3 = mapSort(map3);
        addTargetResult(list3, "????????????", illustrate);
        vo.setIllustrate(illustrate);

        // ????????????
        GDimensionReportVO behavior = behavior(className, gradeReportData, report, classIdList, taskId, gradeId);
        GDimensionReportVO mentalToughness = mentalToughness(className, gradeReportData, report, classIdList, taskId, gradeId);
        GDimensionReportVO overallStress = overallStress(className, gradeReportData, report, classIdList, taskId, gradeId);
        GDimensionReportVO emotionalIndex = emotionalIndex(className, gradeReportData, report, classIdList, taskId, gradeId);
        GDimensionReportVO sleepIndex = sleepIndex(className, gradeReportData, report, classIdList, taskId, gradeId);
        voList.add(vo);
        voList.add(behavior);
        voList.add(mentalToughness);
        voList.add(overallStress);
        voList.add(emotionalIndex);
        voList.add(sleepIndex);
        return voList;
    }

    // ????????????
    public PersonalReport.TestOverview getTestTotalScore(Double studyStatusScore, Double behaviorScore, Double mentalToughnessScore,
                                                         Double overallStressScore, Double emotionalIndexScore, Double sleepIndexScore) {
        PersonalReport.TestOverview testOverview = new PersonalReport.TestOverview();
        // ??????
        List<PersonalReport.OverviewData> active = new ArrayList<>();
        // ????????????
        addTestTotal(active, 1546788164255932417L, dealRate3(studyStatusScore));
        // ????????????
        addTestTotal(active, 1546788937710755842L, dealRate3(behaviorScore));
        // ????????????
        addTestTotal(active, 1546789344491134978L, dealRate3(mentalToughnessScore));
        // ??????
        List<PersonalReport.OverviewData> negative = new ArrayList<>();
        // ????????????
        addTestTotal(negative, 1546789419250409474L, dealRate3(overallStressScore));
        // ????????????
        addTestTotal(negative, 1546789515505491970L, dealRate3(emotionalIndexScore));
        // ????????????
        addTestTotal(negative, 1546789785358622721L, dealRate3(sleepIndexScore));

        testOverview.setActive(active);
        testOverview.setNegative(negative);
        return testOverview;
    }

    // ????????????
    public GDimensionReportVO behavior(String gradeName, GradeReportData gradeReportData, PersonalReportData2 report, List<Long> classIdList, Long taskId, Long gradeId) {
        GDimensionReportVO vo = new GDimensionReportVO();
        // ????????????
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        totalResultVO1.setTitle(gradeName);
        totalResultVO1.setFontColor(ColorEnum.BLUE.getValue());
        totalResultVO1.setScore(dealRate2(gradeReportData.getBehaviorScore()).toString());
        TotalResultVO totalResultVO2 = new TotalResultVO();
        totalResultVO2.setTitle("????????????");
        totalResultVO2.setFontColor(ColorEnum.BLUE2.getValue());
        totalResultVO2.setScore(dealRate2(report.getBehaviorScore()).toString());
        totalResultVOList.add(totalResultVO1);
        totalResultVOList.add(totalResultVO2);
        vo.setTotalResultVOList(totalResultVOList);
        // ???????????????
        List<TestIndex> testIndices = testIndexMapper.selectList(Wrappers.<TestIndex>lambdaQuery()
                .eq(TestIndex::getIsDeleted, 0)
                .eq(TestIndex::getDimensionId, 1546788937710755842L));
        List<String> targetList = testIndices.stream().map(TestIndex::getName).collect(Collectors.toList());
        TargetResultVO targetResultVO = addTargetResult(targetList, gradeReportData, report, vo, gradeName);
        addTitleName(targetResultVO, 3, gradeName);
        // ??????????????????
        List<GDimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();

        targetList.stream().forEach(s -> {
            typicalStudent(gradeReportData, typicalStudentList, s);
        });
        vo.setTypicalStudentList(typicalStudentList);
        // ??????????????????
        List<Object> classDiffList = new ArrayList<>();
        // ?????????
        Map<String, Double> map1 = new HashMap<>();
        // ?????????
        Map<String, Double> map2 = new HashMap<>();
        // ?????????
        Map<String, Double> map3 = new HashMap<>();
        // ????????????
        Map<String, Double> map4 = new HashMap<>();
        // ????????????????????????
        classIdList.stream().forEach(classId -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // ????????????
                String name = testPaperMapper.getClassName(classId);
                map1.put(name, classReport.getMoralScoreDiff());
                map2.put(name, classReport.getStabilityScoreDiff());
                map3.put(name, classReport.getDisciplineScoreDiff());
                map4.put(name, classReport.getOtherPerformanceScoreDiff());
                addClassDiffTwo(classDiffList, classReport, targetList, name);
            }
        });
        vo.setClassDiffList(classDiffList);

        // ??????????????????
        List<String> illustrate = new ArrayList<>();
        List<Map.Entry<String, Double>> list1 = mapSort(map1);
        addTargetResult(list1, "?????????", illustrate);
        List<Map.Entry<String, Double>> list2 = mapSort(map2);
        addTargetResult(list2, "?????????", illustrate);
        List<Map.Entry<String, Double>> list3 = mapSort(map3);
        addTargetResult(list3, "?????????", illustrate);
        List<Map.Entry<String, Double>> list4 = mapSort(map4);
        addTargetResult(list4, "????????????", illustrate);
        vo.setIllustrate(illustrate);
        return vo;
    }

    // ????????????
    public GDimensionReportVO mentalToughness(String gradeName, GradeReportData gradeReportData, PersonalReportData2 report, List<Long> classIdList, Long taskId, Long gradeId) {
        GDimensionReportVO vo = new GDimensionReportVO();
        // ????????????
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        totalResultVO1.setTitle(gradeName);
        totalResultVO1.setFontColor(ColorEnum.BLUE.getValue());
        totalResultVO1.setScore(dealRate2(gradeReportData.getMentalToughnessScore()).toString());
        TotalResultVO totalResultVO2 = new TotalResultVO();
        totalResultVO2.setTitle("????????????");
        totalResultVO2.setFontColor(ColorEnum.BLUE2.getValue());
        totalResultVO2.setScore(dealRate2(report.getMentalToughnessScore()).toString());
        totalResultVOList.add(totalResultVO1);
        totalResultVOList.add(totalResultVO2);
        vo.setTotalResultVOList(totalResultVOList);
        // ???????????????
        List<TestIndex> testIndices = testIndexMapper.selectList(Wrappers.<TestIndex>lambdaQuery()
                .eq(TestIndex::getIsDeleted, 0)
                .eq(TestIndex::getDimensionId, 1546789344491134978L));
        List<String> targetList = testIndices.stream().map(TestIndex::getName).collect(Collectors.toList());
        TargetResultVO targetResultVO = addTargetResult(targetList, gradeReportData, report, vo, gradeName);
        addTitleName(targetResultVO, 3, gradeName);
        // ??????????????????
        List<GDimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();

        targetList.stream().forEach(s -> {
            typicalStudent(gradeReportData, typicalStudentList, s);
        });
        vo.setTypicalStudentList(typicalStudentList);
        // ??????????????????
        List<Object> classDiffList = new ArrayList<>();
        // ????????????
        Map<String, Double> map1 = new HashMap<>();
        // ????????????
        Map<String, Double> map2 = new HashMap<>();
        // ????????????
        Map<String, Double> map3 = new HashMap<>();
        // ????????????
        Map<String, Double> map4 = new HashMap<>();
        // ????????????
        Map<String, Double> map5 = new HashMap<>();
        // ????????????
        Map<String, Double> map6 = new HashMap<>();
        // ????????????????????????
        classIdList.stream().forEach(classId -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // ????????????
                String name = testPaperMapper.getClassName(classId);
                map1.put(name, classReport.getEmotionManagementScoreDiff());
                map2.put(name, classReport.getGoalMotivationScoreDiff());
                map3.put(name, classReport.getPositiveAttentionScoreDiff());
                map4.put(name, classReport.getSchoolSupportScoreDiff());
                map5.put(name, classReport.getInterpersonalSupportScoreDiff());
                map6.put(name, classReport.getFamilySupportScoreDiff());
                addClassDiffFour(classDiffList, classReport, targetList, name);
            }
        });
        vo.setClassDiffList(classDiffList);

        // ??????????????????
        List<String> illustrate = new ArrayList<>();
        List<Map.Entry<String, Double>> list1 = mapSort(map1);
        addTargetResult(list1, "????????????", illustrate);
        List<Map.Entry<String, Double>> list2 = mapSort(map2);
        addTargetResult(list2, "????????????", illustrate);
        List<Map.Entry<String, Double>> list3 = mapSort(map3);
        addTargetResult(list3, "????????????", illustrate);
        List<Map.Entry<String, Double>> list4 = mapSort(map4);
        addTargetResult(list4, "????????????", illustrate);
        List<Map.Entry<String, Double>> list5 = mapSort(map5);
        addTargetResult(list5, "????????????", illustrate);
        List<Map.Entry<String, Double>> list6 = mapSort(map6);
        addTargetResult(list6, "????????????", illustrate);
        vo.setIllustrate(illustrate);
        return vo;
    }

    // ????????????
    public GDimensionReportVO overallStress(String gradeName, GradeReportData gradeReportData, PersonalReportData2 report, List<Long> classIdList, Long taskId, Long gradeId) {
        GDimensionReportVO vo = new GDimensionReportVO();
        // ????????????
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        totalResultVO1.setTitle(gradeName);
        totalResultVO1.setFontColor(ColorEnum.ORANGE.getValue());
        totalResultVO1.setScore(dealRate2(gradeReportData.getOverallStressScore()).toString());
        TotalResultVO totalResultVO2 = new TotalResultVO();
        totalResultVO2.setTitle("????????????");
        totalResultVO2.setFontColor(ColorEnum.ORANGE2.getValue());
        totalResultVO2.setScore(dealRate2(report.getOverallStressScore()).toString());
        totalResultVOList.add(totalResultVO1);
        totalResultVOList.add(totalResultVO2);
        vo.setTotalResultVOList(totalResultVOList);
        // ???????????????
        List<TestIndex> testIndices = testIndexMapper.selectList(Wrappers.<TestIndex>lambdaQuery()
                .eq(TestIndex::getIsDeleted, 0)
                .eq(TestIndex::getDimensionId, 1546789419250409474L));
        List<String> targetList = testIndices.stream().map(TestIndex::getName).collect(Collectors.toList());
        TargetResultVO targetResultVO = addTargetResult(targetList, gradeReportData, report, vo, gradeName);
        addTitleName(targetResultVO, 2, gradeName);
        // ??????????????????
        List<GDimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();

        targetList.stream().forEach(s -> {
            typicalStudent(gradeReportData, typicalStudentList, s);
        });
        vo.setTypicalStudentList(typicalStudentList);
        // ??????????????????
        List<Object> classDiffList = new ArrayList<>();
        // ????????????
        Map<String, Double> map1 = new HashMap<>();
        // ????????????
        Map<String, Double> map2 = new HashMap<>();
        // ???????????????
        Map<String, Double> map3 = new HashMap<>();
        // ????????????
        Map<String, Double> map4 = new HashMap<>();
        // ????????????
        Map<String, Double> map5 = new HashMap<>();
        // ????????????????????????
        classIdList.stream().forEach(classId -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // ????????????
                String name = testPaperMapper.getClassName(classId);
                map1.put(name, classReport.getStudyStressScoreDiff());
                map2.put(name, classReport.getInterpersonalStressScoreDiff());
                map3.put(name, classReport.getPunishmentStressScoreDiff());
                map4.put(name, classReport.getLossStressScoreDiff());
                map5.put(name, classReport.getAdaptationStressScoreDiff());
                addClassDiffThree(classDiffList, classReport, targetList, name);
            }
        });
        vo.setClassDiffList(classDiffList);

        // ??????????????????
        List<String> illustrate = new ArrayList<>();
        List<Map.Entry<String, Double>> list1 = mapSort(map1);
        addTargetResult(list1, "????????????", illustrate);
        List<Map.Entry<String, Double>> list2 = mapSort(map2);
        addTargetResult(list2, "????????????", illustrate);
        List<Map.Entry<String, Double>> list3 = mapSort(map3);
        addTargetResult(list3, "???????????????", illustrate);
        List<Map.Entry<String, Double>> list4 = mapSort(map4);
        addTargetResult(list4, "????????????", illustrate);
        List<Map.Entry<String, Double>> list5 = mapSort(map5);
        addTargetResult(list5, "????????????", illustrate);
        vo.setIllustrate(illustrate);
        return vo;
    }

    // ????????????
    public GDimensionReportVO emotionalIndex(String gradeName, GradeReportData gradeReportData, PersonalReportData2 report, List<Long> classIdList, Long taskId, Long gradeId) {
        GDimensionReportVO vo = new GDimensionReportVO();
        // ????????????
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        totalResultVO1.setTitle(gradeName);
        totalResultVO1.setFontColor(ColorEnum.ORANGE.getValue());
        totalResultVO1.setScore(dealRate2(gradeReportData.getEmotionalIndexScore()).toString());
        TotalResultVO totalResultVO2 = new TotalResultVO();
        totalResultVO2.setTitle("????????????");
        totalResultVO2.setFontColor(ColorEnum.ORANGE2.getValue());
        totalResultVO2.setScore(dealRate2(report.getEmotionalIndexScore()).toString());
        totalResultVOList.add(totalResultVO1);
        totalResultVOList.add(totalResultVO2);
        vo.setTotalResultVOList(totalResultVOList);
        // ???????????????
        List<TestIndex> testIndices = testIndexMapper.selectList(Wrappers.<TestIndex>lambdaQuery()
                .eq(TestIndex::getIsDeleted, 0)
                .eq(TestIndex::getDimensionId, 1546789515505491970L));
        List<String> targetList = testIndices.stream().map(TestIndex::getName).collect(Collectors.toList());
        TargetResultVO targetResultVO = addTargetResult(targetList, gradeReportData, report, vo, gradeName);
        addTitleName(targetResultVO, 2, gradeName);
        // ??????????????????
        List<GDimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();

        targetList.stream().forEach(s -> {
            typicalStudent(gradeReportData, typicalStudentList, s);
        });
        vo.setTypicalStudentList(typicalStudentList);
        // ??????????????????
        List<Object> classDiffList = new ArrayList<>();
        // ??????
        Map<String, Double> map1 = new HashMap<>();
        // ??????
        Map<String, Double> map2 = new HashMap<>();
        // ??????
        Map<String, Double> map3 = new HashMap<>();
        // ????????????
        Map<String, Double> map4 = new HashMap<>();
        // ??????
        Map<String, Double> map5 = new HashMap<>();
        // ??????
        Map<String, Double> map6 = new HashMap<>();
        // ????????????????????????
        classIdList.stream().forEach(classId -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // ????????????
                String name = testPaperMapper.getClassName(classId);
                map1.put(name, classReport.getCompulsionScoreDiff());
                map2.put(name, classReport.getParanoiaScoreDiff());
                map3.put(name, classReport.getHostilityScoreDiff());
                map4.put(name, classReport.getInterpersonalSensitivityScoreDiff());
                map5.put(name, classReport.getAnxietyScoreDiff());
                map6.put(name, classReport.getDepressionScoreDiff());
                addClassDiffFour(classDiffList, classReport, targetList, name);
            }
        });
        vo.setClassDiffList(classDiffList);

        // ??????????????????
        List<String> illustrate = new ArrayList<>();
        List<Map.Entry<String, Double>> list1 = mapSort(map1);
        addTargetResult(list1, "??????", illustrate);
        List<Map.Entry<String, Double>> list2 = mapSort(map2);
        addTargetResult(list2, "??????", illustrate);
        List<Map.Entry<String, Double>> list3 = mapSort(map3);
        addTargetResult(list3, "??????", illustrate);
        List<Map.Entry<String, Double>> list4 = mapSort(map4);
        addTargetResult(list4, "????????????", illustrate);
        List<Map.Entry<String, Double>> list5 = mapSort(map5);
        addTargetResult(list5, "??????", illustrate);
        List<Map.Entry<String, Double>> list6 = mapSort(map6);
        addTargetResult(list6, "??????", illustrate);
        vo.setIllustrate(illustrate);
        return vo;
    }

    // ????????????
    public GDimensionReportVO sleepIndex(String gradeName, GradeReportData gradeReportData, PersonalReportData2 report, List<Long> classIdList, Long taskId, Long gradeId) {
        GDimensionReportVO vo = new GDimensionReportVO();
        // ????????????
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        totalResultVO1.setTitle(gradeName);
        totalResultVO1.setFontColor(ColorEnum.ORANGE.getValue());
        totalResultVO1.setScore(dealRate2(gradeReportData.getSleepIndexScore()).toString());
        TotalResultVO totalResultVO2 = new TotalResultVO();
        totalResultVO2.setTitle("????????????");
        totalResultVO2.setFontColor(ColorEnum.ORANGE2.getValue());
        totalResultVO2.setScore(dealRate2(report.getSleepIndexScore()).toString());
        totalResultVOList.add(totalResultVO1);
        totalResultVOList.add(totalResultVO2);
        vo.setTotalResultVOList(totalResultVOList);

        // ??????????????????
        List<GDimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();

        typicalStudent(gradeReportData, typicalStudentList, "????????????");
        vo.setTypicalStudentList(typicalStudentList);
        // ??????????????????
        List<Object> classDiffList = new ArrayList<>();
        // ????????????
        Map<String, Double> map1 = new HashMap<>();
        List<String> targetList = new ArrayList<>();
        targetList.add("????????????");
        // ????????????????????????
        classIdList.stream().forEach(classId -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // ????????????
                String name = testPaperMapper.getClassName(classId);
                map1.put(name, classReport.getLearningAttitudeScoreDiff());
                GDimensionReportVO.ClassDiffFive classDiff = new GDimensionReportVO.ClassDiffFive();
                classDiff.setTitle(name);
                GDimensionReportVO.TargetScore targetOne = new GDimensionReportVO.TargetScore();
                targetOne.setTargetScore(dealRate3(classReport.getSleepIndexScore()));
                targetOne.setTargetDiff(diffResult(classReport.getSleepDiff(), 2));
                classDiff.setTargetOne(targetOne);
                classDiffList.add(classDiff);
            }
        });
        vo.setClassDiffList(classDiffList);

        // ??????????????????
        List<String> illustrate = new ArrayList<>();
        List<Map.Entry<String, Double>> list1 = mapSort(map1);
        addTargetResult(list1, "????????????", illustrate);
        vo.setIllustrate(illustrate);
        return vo;
    }

    // ??????????????????
    public void addTargetResult(List<Map.Entry<String, Double>> list, String targetName, List<String> illustrate) {
        //1.???????????????????????????-1.96???Z???1.96????????????xx??????????????????????????????????????????
        // ??????????????????
        double max = list.get(0).getValue();
        double min = list.get(list.size() - 1).getValue();
        String s = "";
        if (max < 1.96 && max > -1.96 && min < 1.96 && min > -1.96) {
            s = targetName + "??????????????????????????????????????????";
        } else {
            if ("????????????".equals(targetName) || "????????????".equals(targetName) || "???????????????".equals(targetName) || "????????????".equals(targetName) ||
                    "????????????".equals(targetName) || "??????".equals(targetName) || "??????".equals(targetName) || "??????".equals(targetName) ||
                    "????????????".equals(targetName) || "??????".equals(targetName) || "??????".equals(targetName) || "????????????".equals(targetName)) {
                s = targetName + "??????????????????????????????" + list.get(list.size() - 1).getKey() + "?????????????????????" + list.get(0).getKey();
            } else {
                s = targetName + "??????????????????????????????" + list.get(0).getKey() + "?????????????????????" + list.get(list.size() - 1).getKey();
            }

        }
        illustrate.add(s);

    }

    // map?????? ????????????
    public List<Map.Entry<String, Double>> mapSort(Map<String, Double> map) {
        List<Map.Entry<String, Double>> wordMap = new ArrayList<Map.Entry<String, Double>>(map.entrySet());
        Collections.sort(wordMap, new Comparator<Map.Entry<String, Double>>() {//??????value??????
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                double result = o2.getValue() - o1.getValue();
                if (result > 0)
                    return 1;
                else if (result == 0)
                    return 0;
                else
                    return -1;
            }
        });
        return wordMap;
    }

    /**
     * ??????????????????-1
     *
     * @param classDiffList
     * @param report        ????????????
     * @param targetList    ????????????
     * @param className     ????????????
     * @return java.util.Map<java.lang.String, java.lang.Double>
     * @author hzl
     * @date 2022/8/30 18:38
     */
    public Map<String, Double> addClassDiffOne(List<Object> classDiffList, ClassReportData report, List<String> targetList, String className) {
        Map<String, Double> map = new HashMap<>();
        GDimensionReportVO.ClassDiffOne classDiff = new GDimensionReportVO.ClassDiffOne();
        classDiff.setTitle(className);
        for (String s : targetList) {
            if ("????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getLearningAttitudeScore()));
                Double diff = report.getLearningAttitudeScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetOne(target);
                map.put(s, diff);
            } else if ("????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getTimeManagementScore()));
                Double diff = report.getTimeManagementScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetTwo(target);
                map.put(s, diff);
            } else if ("????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getLearningBurnoutScore()));
                Double diff = report.getLearningBurnoutScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetThree(target);
                map.put(s, diff);
            }
        }
        classDiffList.add(classDiff);
        return map;
    }

    /**
     * ??????????????????-2
     *
     * @param classDiffList
     * @param report        ????????????
     * @param targetList    ????????????
     * @param className     ????????????
     * @return java.util.Map<java.lang.String, java.lang.Double>
     * @author hzl
     * @date 2022/8/30 18:38
     */
    public Map<String, Double> addClassDiffTwo(List<Object> classDiffList, ClassReportData report, List<String> targetList, String className) {
        Map<String, Double> map = new HashMap<>();
        GDimensionReportVO.ClassDiffTwo classDiff = new GDimensionReportVO.ClassDiffTwo();
        classDiff.setTitle(className);
        for (String s : targetList) {
            if ("?????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getMoralScore()));
                Double diff = report.getMoralScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetOne(target);
                map.put(s, diff);
            } else if ("?????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getStabilityScore()));
                Double diff = report.getStabilityScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetTwo(target);
                map.put(s, diff);
            } else if ("?????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getDisciplineScore()));
                Double diff = report.getDisciplineScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetThree(target);
                map.put(s, diff);
            } else if ("????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getOtherPerformanceScore()));
                Double diff = report.getOtherPerformanceScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetFour(target);
                map.put(s, diff);
            }
        }
        classDiffList.add(classDiff);
        return map;
    }

    /**
     * ??????????????????-3
     *
     * @param classDiffList
     * @param report        ????????????
     * @param targetList    ????????????
     * @param className     ????????????
     * @return java.util.Map<java.lang.String, java.lang.Double>
     * @author hzl
     * @date 2022/8/30 18:38
     */
    public Map<String, Double> addClassDiffThree(List<Object> classDiffList, ClassReportData report, List<String> targetList, String className) {
        Map<String, Double> map = new HashMap<>();
        GDimensionReportVO.ClassDiffThree classDiff = new GDimensionReportVO.ClassDiffThree();
        classDiff.setTitle(className);
        for (String s : targetList) {
            if ("????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getStudyStressScore()));
                Double diff = report.getStudyStressScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetOne(target);
                map.put(s, diff);
            } else if ("????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getInterpersonalStressScore()));
                Double diff = report.getInterpersonalStressScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetTwo(target);
                map.put(s, diff);
            } else if ("???????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getPunishmentStressScore()));
                Double diff = report.getPunishmentStressScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetThree(target);
                map.put(s, diff);
            } else if ("????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getLossStressScore()));
                Double diff = report.getLossStressScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetFour(target);
                map.put(s, diff);
            } else if ("????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getAdaptationStressScore()));
                Double diff = report.getAdaptationStressScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetFive(target);
                map.put(s, diff);
            }
        }
        classDiffList.add(classDiff);
        return map;
    }

    /**
     * ??????????????????-4
     *
     * @param classDiffList
     * @param report        ????????????
     * @param targetList    ????????????
     * @param className     ????????????
     * @return java.util.Map<java.lang.String, java.lang.Double>
     * @author hzl
     * @date 2022/8/30 18:38
     */
    public Map<String, Double> addClassDiffFour(List<Object> classDiffList, ClassReportData report, List<String> targetList, String className) {
        Map<String, Double> map = new HashMap<>();
        GDimensionReportVO.ClassDiffFour classDiff = new GDimensionReportVO.ClassDiffFour();
        classDiff.setTitle(className);
        for (String s : targetList) {
            if ("????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getEmotionManagementScore()));
                Double diff = report.getEmotionManagementScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetOne(target);
                map.put(s, diff);
            } else if ("????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getGoalMotivationScore()));
                Double diff = report.getGoalMotivationScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetTwo(target);
                map.put(s, diff);
            } else if ("????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getPositiveAttentionScore()));
                Double diff = report.getPositiveAttentionScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetThree(target);
                map.put(s, diff);
            } else if ("????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getSchoolSupportScore()));
                Double diff = report.getSchoolSupportScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetFour(target);
                map.put(s, diff);
            } else if ("????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getInterpersonalSupportScore()));
                Double diff = report.getInterpersonalSupportScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetFive(target);
                map.put(s, diff);
            } else if ("????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getFamilySupportScore()));
                Double diff = report.getFamilySupportScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetSix(target);
                map.put(s, diff);
            } else if ("??????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getCompulsionScore()));
                Double diff = report.getCompulsionScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetOne(target);
                map.put(s, diff);
            } else if ("??????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getParanoiaScore()));
                Double diff = report.getParanoiaScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetTwo(target);
                map.put(s, diff);
            } else if ("??????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getHostilityScore()));
                Double diff = report.getHostilityScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetThree(target);
                map.put(s, diff);
            } else if ("????????????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getInterpersonalSensitivityScore()));
                Double diff = report.getInterpersonalSensitivityScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetFour(target);
                map.put(s, diff);
            } else if ("??????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getAnxietyScore()));
                Double diff = report.getAnxietyScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetFive(target);
                map.put(s, diff);
            } else if ("??????".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getDepressionScore()));
                Double diff = report.getDepressionScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetSix(target);
                map.put(s, diff);
            }
        }
        classDiffList.add(classDiff);
        return map;
    }

    /**
     * ?????????????????? 1??????  2????????????  3??????
     * type ?????????1????????????  2????????????
     *
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @author hzl
     * @date 2022/8/18 19:01
     */
    private int diffResult(double diff, Integer type) {
        if (diff >= 1.96) {
            if (type == 1) {
                return 3;
            } else {
                return 1;
            }
        } else if (diff < 1.96 && diff > -1.96) {
            return 2;
        } else {
            if (type == 1) {
                return 1;
            } else {
                return 3;
            }
        }
    }

    // ??????????????????
    public void typicalStudent(GradeReportData reportData, List<GDimensionReportVO.TypicalStudent> typicalStudentList, String target) {
        // ?????????????????? = ????????????-????????????
        int gradePeople = reportData.getTestPeople() - reportData.getInvalidPeople();
        int veryBadPeople = 0, badPeople = 0, generallyPeople = 0, goodPeople = 0;
        // ??????????????????
        if ("????????????".equals(target)) {
            veryBadPeople = reportData.getLearningAttitudeScoreVeryBad();
            badPeople = reportData.getLearningAttitudeScoreBad();
            generallyPeople = reportData.getLearningAttitudeScoreGenerally();
            goodPeople = reportData.getLearningAttitudeScoreGood();
        } else if ("????????????".equals(target)) {
            veryBadPeople = reportData.getTimeManagementScoreVeryBad();
            badPeople = reportData.getTimeManagementScoreBad();
            generallyPeople = reportData.getTimeManagementScoreGenerally();
            goodPeople = reportData.getTimeManagementScoreGood();
        } else if ("????????????".equals(target)) {
            veryBadPeople = reportData.getLearningBurnoutScoreVeryBad();
            badPeople = reportData.getLearningBurnoutScoreBad();
            generallyPeople = reportData.getLearningBurnoutScoreGenerally();
            goodPeople = reportData.getLearningBurnoutScoreGood();
        } else if ("?????????".equals(target)) {
            veryBadPeople = reportData.getMoralScoreVeryBad();
            badPeople = reportData.getMoralScoreBad();
            generallyPeople = reportData.getMoralScoreGenerally();
            goodPeople = reportData.getMoralScoreGood();
        } else if ("?????????".equals(target)) {
            veryBadPeople = reportData.getStabilityScoreVeryBad();
            badPeople = reportData.getStabilityScoreBad();
            generallyPeople = reportData.getStabilityScoreGenerally();
            goodPeople = reportData.getStabilityScoreGood();
        } else if ("?????????".equals(target)) {
            veryBadPeople = reportData.getDisciplineScoreVeryBad();
            badPeople = reportData.getDisciplineScoreBad();
            generallyPeople = reportData.getDisciplineScoreGenerally();
            goodPeople = reportData.getDisciplineScoreGood();
        } else if ("????????????".equals(target)) {
            veryBadPeople = reportData.getOtherPerformanceScoreVeryBad();
            badPeople = reportData.getOtherPerformanceScoreBad();
            generallyPeople = reportData.getOtherPerformanceScoreGenerally();
            goodPeople = reportData.getOtherPerformanceScoreGood();
        } else if ("????????????".equals(target)) {
            veryBadPeople = reportData.getEmotionManagementScoreVeryBad();
            badPeople = reportData.getEmotionManagementScoreBad();
            generallyPeople = reportData.getEmotionManagementScoreGenerally();
            goodPeople = reportData.getEmotionManagementScoreGood();
        } else if ("????????????".equals(target)) {
            veryBadPeople = reportData.getGoalMotivationScoreVeryBad();
            badPeople = reportData.getGoalMotivationScoreBad();
            generallyPeople = reportData.getGoalMotivationScoreGenerally();
            goodPeople = reportData.getGoalMotivationScoreGood();
        } else if ("????????????".equals(target)) {
            veryBadPeople = reportData.getPositiveAttentionScoreVeryBad();
            badPeople = reportData.getPositiveAttentionScoreBad();
            generallyPeople = reportData.getPositiveAttentionScoreGenerally();
            goodPeople = reportData.getPositiveAttentionScoreGood();
        } else if ("????????????".equals(target)) {
            veryBadPeople = reportData.getSchoolSupportScoreVeryBad();
            badPeople = reportData.getSchoolSupportScoreBad();
            generallyPeople = reportData.getSchoolSupportScoreGenerally();
            goodPeople = reportData.getSchoolSupportScoreGood();
        } else if ("????????????".equals(target)) {
            veryBadPeople = reportData.getInterpersonalSupportScoreVeryBad();
            badPeople = reportData.getInterpersonalSupportScoreBad();
            generallyPeople = reportData.getInterpersonalSupportScoreGenerally();
            goodPeople = reportData.getInterpersonalSupportScoreGood();
        } else if ("????????????".equals(target)) {
            veryBadPeople = reportData.getFamilySupportScoreVeryBad();
            badPeople = reportData.getFamilySupportScoreBad();
            generallyPeople = reportData.getFamilySupportScoreGenerally();
            goodPeople = reportData.getFamilySupportScoreGood();
        } else if ("????????????".equals(target)) {
            veryBadPeople = reportData.getStudyStressScoreVeryBad();
            badPeople = reportData.getStudyStressScoreBad();
            generallyPeople = reportData.getStudyStressScoreGenerally();
            goodPeople = reportData.getStudyStressScoreGood();
        } else if ("????????????".equals(target)) {
            veryBadPeople = reportData.getInterpersonalStressScoreVeryBad();
            badPeople = reportData.getInterpersonalStressScoreBad();
            generallyPeople = reportData.getInterpersonalStressScoreGenerally();
            goodPeople = reportData.getInterpersonalStressScoreGood();
        } else if ("???????????????".equals(target)) {
            veryBadPeople = reportData.getPunishmentStressScoreVeryBad();
            badPeople = reportData.getPunishmentStressScoreBad();
            generallyPeople = reportData.getPunishmentStressScoreGenerally();
            goodPeople = reportData.getPunishmentStressScoreGood();
        } else if ("????????????".equals(target)) {
            veryBadPeople = reportData.getLossStressScoreVeryBad();
            badPeople = reportData.getLossStressScoreBad();
            generallyPeople = reportData.getLossStressScoreGenerally();
            goodPeople = reportData.getLossStressScoreGood();
        } else if ("????????????".equals(target)) {
            veryBadPeople = reportData.getAdaptationStressScoreVeryBad();
            badPeople = reportData.getAdaptationStressScoreBad();
            generallyPeople = reportData.getAdaptationStressScoreGenerally();
            goodPeople = reportData.getAdaptationStressScoreGood();
        } else if ("??????".equals(target)) {
            veryBadPeople = reportData.getCompulsionScoreVeryBad();
            badPeople = reportData.getCompulsionScoreBad();
            generallyPeople = reportData.getCompulsionScoreGenerally();
            goodPeople = reportData.getCompulsionScoreGood();
        } else if ("??????".equals(target)) {
            veryBadPeople = reportData.getParanoiaScoreVeryBad();
            badPeople = reportData.getParanoiaScoreBad();
            generallyPeople = reportData.getParanoiaScoreGenerally();
            goodPeople = reportData.getParanoiaScoreGood();
        } else if ("??????".equals(target)) {
            veryBadPeople = reportData.getHostilityScoreVeryBad();
            badPeople = reportData.getHostilityScoreBad();
            generallyPeople = reportData.getHostilityScoreGenerally();
            goodPeople = reportData.getHostilityScoreGood();
        } else if ("????????????".equals(target)) {
            veryBadPeople = reportData.getInterpersonalSensitivityScoreVeryBad();
            badPeople = reportData.getInterpersonalSensitivityScoreBad();
            generallyPeople = reportData.getInterpersonalSensitivityScoreGenerally();
            goodPeople = reportData.getInterpersonalSensitivityScoreGood();
        } else if ("??????".equals(target)) {
            veryBadPeople = reportData.getAnxietyScoreVeryBad();
            badPeople = reportData.getAnxietyScoreBad();
            generallyPeople = reportData.getAnxietyScoreGenerally();
            goodPeople = reportData.getAnxietyScoreGood();
        } else if ("??????".equals(target)) {
            veryBadPeople = reportData.getDepressionScoreVeryBad();
            badPeople = reportData.getDepressionScoreBad();
            generallyPeople = reportData.getDepressionScoreGenerally();
            goodPeople = reportData.getDepressionScoreGood();
        } else if ("????????????".equals(target)) {
            veryBadPeople = reportData.getSleepVeryBad();
            badPeople = reportData.getSleepBad();
            generallyPeople = reportData.getSleepGenerally();
            goodPeople = reportData.getSleepGood();
        }
        double v1 = calculatePercentage(gradePeople, veryBadPeople);
        double v2 = calculatePercentage(gradePeople, badPeople);
        double v3 = calculatePercentage(gradePeople, generallyPeople);
        double v4 = calculatePercentage(gradePeople, goodPeople);

        GDimensionReportVO.TypicalStudent typicalStudent1 = new GDimensionReportVO.TypicalStudent();
        typicalStudent1.setTitle(target);
        typicalStudent1.setVeryBad(veryBadPeople + "???(" + v1 + "%)");
        typicalStudent1.setBad(badPeople + "???(" + v2 + "%)");
        typicalStudent1.setGenerally(generallyPeople + "???(" + v3 + "%)");
        typicalStudent1.setGood(goodPeople + "???(" + v4 + "%)");
        typicalStudentList.add(typicalStudent1);
    }

    // ???????????????
    public double calculatePercentage(int total, int num) {
        DecimalFormat df = new DecimalFormat("#0.0");
        // ??????double????????????
        if (total == 0 || num == 0) {
            return 0;
        }
        double v = (double) num / (double) total * 100;
        // ??????????????????
        String format = df.format(v);
        return Func.toDouble(format);
    }

    // ???????????????????????????

    /**
     * @param targetResultVO
     * @param type           1:????????????   2???????????????????????????  3.???????????????????????????
     * @param name
     * @author hzl
     * @date 2022/9/6 14:45
     */
    public void addTitleName(TargetResultVO targetResultVO, Integer type, String name) {
        List<TargetResultVO.TitleNameVO> list = new ArrayList<>();
        TargetResultVO.TitleNameVO vo1 = new TargetResultVO.TitleNameVO();
        TargetResultVO.TitleNameVO vo2 = new TargetResultVO.TitleNameVO();
        if (type == 1) {
            vo1.setName(name);
            vo1.setColor(ColorEnum.BLUE.getValue());
            vo2.setName("????????????");
            vo2.setColor(ColorEnum.BLUE2.getValue());
            TargetResultVO.TitleNameVO vo3 = new TargetResultVO.TitleNameVO();
            vo3.setName(name);
            vo3.setColor(ColorEnum.ORANGE.getValue());
            TargetResultVO.TitleNameVO vo4 = new TargetResultVO.TitleNameVO();
            vo4.setName("????????????");
            vo4.setColor(ColorEnum.ORANGE2.getValue());

            list.add(vo1);
            list.add(vo3);
            list.add(vo2);
            list.add(vo4);
        } else if (type == 2) {
            vo1.setName(name);
            vo1.setColor(ColorEnum.ORANGE.getValue());
            vo2.setName("????????????");
            vo2.setColor(ColorEnum.ORANGE2.getValue());

            list.add(vo1);
            list.add(vo2);
        } else if (type == 3) {
            vo1.setName(name);
            vo1.setColor(ColorEnum.BLUE.getValue());
            vo2.setName("????????????");
            vo2.setColor(ColorEnum.BLUE2.getValue());

            list.add(vo1);
            list.add(vo2);
        }
        targetResultVO.setTitleNameVOList(list);
    }

    // ???????????????
    public TargetResultVO addTargetResult(List<String> targetList, GradeReportData classReportData, PersonalReportData2 report, GDimensionReportVO vo, String gradeName) {
        TargetResultVO targetResultVO = new TargetResultVO();
        List<TargetResultVO.TargetVO> targetVOList = new ArrayList<>();


        targetList.stream().forEach(s -> {
            TargetResultVO.TargetVO targetVO1 = new TargetResultVO.TargetVO();
            targetVO1.setTitle(s);
            if ("????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getLearningAttitudeScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getLearningAttitudeScore()).toString());
            } else if ("????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getTimeManagementScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getTimeManagementScore()).toString());
            } else if ("????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getLearningBurnoutScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getLearningBurnoutScore()).toString());
            } else if ("?????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getMoralScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getMoralScore()).toString());
            } else if ("?????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getStabilityScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getStabilityScore()).toString());
            } else if ("?????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getDisciplineScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getDisciplineScore()).toString());
            } else if ("????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getOtherPerformanceScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getOtherPerformanceScore()).toString());
            } else if ("????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getEmotionManagementScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getEmotionManagementScore()).toString());
            } else if ("????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getGoalMotivationScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getGoalMotivationScore()).toString());
            } else if ("????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getPositiveAttentionScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getPositiveAttentionScore()).toString());
            } else if ("????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getSchoolSupportScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getSchoolSupportScore()).toString());
            } else if ("????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getInterpersonalSupportScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getInterpersonalSupportScore()).toString());
            } else if ("????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getFamilySupportScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getFamilySupportScore()).toString());
            } else if ("????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getStudyStressScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getStudyStressScore()).toString());
            } else if ("????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getInterpersonalStressScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getInterpersonalStressScore()).toString());
            } else if ("???????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getPunishmentStressScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getPunishmentStressScore()).toString());
            } else if ("????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getLossStressScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getLossStressScore()).toString());
            } else if ("????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getAdaptationStressScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getAdaptationStressScore()).toString());
            } else if ("??????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getCompulsionScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getCompulsionScore()).toString());
            } else if ("??????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getParanoiaScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getParanoiaScore()).toString());
            } else if ("??????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getHostilityScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getHostilityScore()).toString());
            } else if ("????????????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getInterpersonalSensitivityScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getInterpersonalSensitivityScore()).toString());
            } else if ("??????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getAnxietyScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getAnxietyScore()).toString());
            } else if ("??????".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getDepressionScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getDepressionScore()).toString());
            }
            targetVOList.add(targetVO1);
        });
        targetResultVO.setTargetVOList(targetVOList);
        vo.setTargetResultVO(targetResultVO);

        return targetResultVO;
    }

    // ??????????????????-??????????????????
    public void addFollowPeople(List<Long> classIdList, Long taskId, GTestModuleTwoVO testModuleTwoVO, Long gradeId) {
        // ????????????
        List<GFollowUserVO> followUserList = new ArrayList<>();
        // ??????????????????
        List<GFollowVO> followClassList = new ArrayList<>();
        // ????????????
        List<GFollowUserVO> warnUserList = new ArrayList<>();
        // ??????????????????
        List<GFollowVO> warnClassList = new ArrayList<>();
        classIdList.stream().forEach(classId -> {
            // ????????????id,??????id,??????????????????
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // ????????????
                String className = testPaperMapper.getClassName(classId);
                // ????????????
                GFollowUserVO followUserVO = new GFollowUserVO();
                followUserVO.setClassName(className);
                followUserVO.setVoOneList(classReport.getUserInfoOneList());
                followUserVO.setVoTwoList(classReport.getUserInfoTwoList());
                followUserVO.setVoThreeList(classReport.getUserInfoThreeList());
                followUserList.add(followUserVO);
                // ??????????????????
                GFollowVO followVO = new GFollowVO();
                followVO.setTaskId(taskId);
                followVO.setGradeId(gradeId);
                followVO.setClassId(classId);
                followVO.setClassName(className);
                followVO.setRate(classReport.getFollowPeople() + "??? ???" + dealRate(classReport.getFollowRate()) + "???");
                followVO.setRateOne(classReport.getFollowPeopleOne() + "??? ???" + dealRate(classReport.getFollowRateOne()) + "???");
                followVO.setRateTwo(classReport.getFollowPeopleTwo() + "??? ???" + dealRate(classReport.getFollowRateTwo()) + "???");
                followVO.setRateThree(classReport.getFollowPeopleThree() + "??? ???" + dealRate(classReport.getFollowRateThree()) + "???");
                followClassList.add(followVO);
                // ????????????
                GFollowUserVO warnUserVo = new GFollowUserVO();
                warnUserVo.setClassName(className);
                warnUserVo.setVoOneList(classReport.getUserInfoWarnOneList());
                warnUserVo.setVoTwoList(classReport.getUserInfoWranTwoList());
                warnUserVo.setVoThreeList(classReport.getUserInfoWranThreeList());
                warnUserList.add(warnUserVo);
                // ??????????????????
                GFollowVO warnVO = new GFollowVO();
                warnVO.setTaskId(taskId);
                warnVO.setGradeId(gradeId);
                warnVO.setClassId(classId);
                warnVO.setClassName(className);
                warnVO.setRate(classReport.getWarnPeople() + "??? (" + dealRate(classReport.getWarnRate()) + ")");
                warnVO.setRateOne(classReport.getWarnPeopleOne() + "??? (" + dealRate(classReport.getWarnRateOne()) + ")");
                warnVO.setRateTwo(classReport.getWarnPeopleTwo() + "??? (" + dealRate(classReport.getWarnRateTwo()) + ")");
                warnVO.setRateThree(classReport.getWarnPeopleThree() + "??? (" + dealRate(classReport.getWarnRateThree()) + ")");
                warnClassList.add(warnVO);
            }

        });
        testModuleTwoVO.setAttentionStudent(followUserList);
        testModuleTwoVO.setAttentionLevelDistribution(followClassList);
        testModuleTwoVO.setWarningStudent(warnUserList);
        testModuleTwoVO.setWarningLevelDistribution(warnClassList);
    }

    // ????????????-?????????????????? ???????????????
    public void addClassTest(List<Long> classIdList, Long taskId, GTestFInishVO gTestFInishVO, GradeReportVO reportVO, List<GInvalidVO> gInvalidVOList, Long gradeId) {
        // ????????????-??????????????????
        List<TestFinishVo> testFinishVoList = new ArrayList<>();
        // ???????????????
        classIdList.stream().forEach(classId -> {
            // ????????????id,??????id,??????????????????
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // ????????????????????????
                TestFinishVo vo = new TestFinishVo();
                GInvalidVO invalidVO = new GInvalidVO();
                // ????????????
                String className = testPaperMapper.getClassName(classId);
                vo.setClassName(className);
                vo.setTotalPeople(classReport.getTotalPeople());
                vo.setTestPeople(classReport.getTestPeople());
                vo.setNoTestPeople(classReport.getNoTestPeople());
                vo.setInvalidPeople(classReport.getInvalidPeople());
                vo.setCompletionRate(dealRate(classReport.getCompletionRate()));
                testFinishVoList.add(vo);

                // ?????????
                invalidVO.setClassName(className);
                invalidVO.setVoList(classReport.getInvalidVO().getVoList());
                gInvalidVOList.add(invalidVO);
            }

        });
        gTestFInishVO.setTestFinishVoList(testFinishVoList);
        reportVO.setTestModuleVO(gTestFInishVO);
    }

    // ???????????????
    public String dealRate(double score) {
        if (Double.isNaN(score)) {
            return "0%";
        }
        if (score == 0) {
            return "0%";
        } else {
            return String.format("%.1f", score) + "%";
        }
    }

    // ???????????????
    public Double dealRate2(double score) {
        if (score == 0) {
            return 0D;
        } else {
            String format = String.format("%.1f", score);
            return Func.toDouble(format);
        }
    }

    // ???????????????
    public String dealRate3(double score) {
        String format = String.format("%.1f", score);
        return format;
    }

    /**
     * ??????????????????????????????
     *
     * @param list
     * @param dimensionId
     * @param score
     * @author hzl
     * @date 2022/8/16 10:40
     */
    private void addTestTotal(List<PersonalReport.OverviewData> list, Long dimensionId, String score) {
        // ????????????
        List<TestDimensionIndexConclusion> conclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery()
                .eq(TestDimensionIndexConclusion::getDimensionId, dimensionId)
                .isNull(TestDimensionIndexConclusion::getIndexId));
        conclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(score, dimensionIndex.getDimensionScope());
            if (result) {
                Integer riskIndex = dimensionIndex.getRiskIndex();
                PersonalReport.OverviewData overviewData = new PersonalReport.OverviewData();
                overviewData.setTitle(dimensionIndex.getDimensionName());
                overviewData.setResult(dimensionIndex.getRiskResult());
                overviewData.setScore(score);
                overviewData.setFontColor(getFontColor(riskIndex));
                list.add(overviewData);
            }
        });
    }

    /**
     * ????????????
     *
     * @param index
     * @return
     */
    static String getFontColor(Integer index) {

        String color = ColorEnum.GREEN.getValue();

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
                ColorEnum.GREEN.getValue();
        }
        return color;
    }
}
