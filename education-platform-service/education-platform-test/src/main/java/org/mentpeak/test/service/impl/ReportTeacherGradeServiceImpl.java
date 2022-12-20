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
 * 老师年级报告表 服务实现类
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
        // 年级报告基础数据另存一份，为校级报告用
        GradeReportData reportData = new GradeReportData();
        // 查询该年级下所有的班级，进行统计计算
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
        // 年级平均报告
        List<ClassReportData> mappedResults = result.getMappedResults();
        if (ObjectUtils.isEmpty(mappedResults)) {
            return null;
        }
        ClassReportData report = mappedResults.get(0);
        BeanUtil.copy(report, reportData);
        // 总人数、未测人数需重新计算
        // 年级总人数
        Long totalPeople = testPaperMapper.getTotalPeople(gradeId, null, tenantCode);
        // 已测人数
        List<Long> testPeople = testPaperMapper.getTestPeople(taskId, gradeId, null, tenantCode);
        // 年级未测人数
        long noTestPeople = totalPeople - testPeople.size();
        reportData.setTotalPeople(Func.toInt(totalPeople));
        reportData.setNoTestPeople(Func.toInt(noTestPeople));
        reportData.setTaskId(taskId);
        reportData.setGradeId(gradeId);
        // 存入mongodb-先根据年级id,任务id查询是否存在，存在更新，不存在插入
        Query query = new Query();
        query.addCriteria(Criteria.where("gradeId").is(gradeId).and("taskId").is(taskId));
        GradeReportData one = mongoTemplate.findOne(query, GradeReportData.class, "gradeReportData");
        if (ObjectUtils.isNotEmpty(one)) {
            mongoTemplate.remove(query, "gradeReportData");
            mongoTemplate.save(reportData, "gradeReportData");
        } else {
            // 新增
            mongoTemplate.save(reportData, "gradeReportData");
        }


        // 报告名称
        String gradeName = testPaperMapper.getClassName(gradeId);
        vo.setTitle(gradeName + "测评报告");
        // 测评完成情况
        GTestFInishVO gTestFInishVO = new GTestFInishVO();
        gTestFInishVO.setTotalTestPeople(reportData.getTestPeople());
        gTestFInishVO.setTotalNoTestPeople(reportData.getNoTestPeople());
        gTestFInishVO.setTotalInvalidPeople(reportData.getInvalidPeople());
        gTestFInishVO.setTotalCompletionRate(dealRate(reportData.getCompletionRate()));
        // 参与班级-测试完成情况 作答有效性
        List<GInvalidVO> gInvalidVOList = new ArrayList<>();
        addClassTest(classIdList, taskId, gTestFInishVO, vo, gInvalidVOList, gradeId);

        GradeInvalidVO gradeInvalidVO = new GradeInvalidVO();
        gradeInvalidVO.setTotalInvalidPeople(report.getInvalidPeople());
        gradeInvalidVO.setGInvalidVOList(gInvalidVOList);
        vo.setGradeInvalidVOList(gradeInvalidVO);

        // todo:测试人数等于无效人数
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
            log.info("=============年级报告结束==============");
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            CompletableFuture.runAsync(() -> {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                CompletableFuture.runAsync(() -> updateSchoolReport(taskId));
            });

            return null;
        }

        // 心理健康评级
        GTestModuleTwoVO testModuleTwoVO = new GTestModuleTwoVO();
        // 建议关注等级
        List<FollowLevelVO> followLevelVOList = new ArrayList<>();
        FollowLevelVO followLevelVO1 = new FollowLevelVO();
        FollowLevelVO followLevelVO2 = new FollowLevelVO();
        FollowLevelVO followLevelVO3 = new FollowLevelVO();
        FollowLevelVO followLevelVO4 = new FollowLevelVO();
        followLevelVO1.setFollowName("1级关注");
        followLevelVO1.setPeople(report.getFollowPeopleOne());
        followLevelVO1.setClassRatio(dealRate(report.getFollowRateOne()));
        followLevelVO2.setFollowName("2级关注");
        followLevelVO2.setPeople(report.getFollowPeopleTwo());
        followLevelVO2.setClassRatio(dealRate(report.getFollowRateTwo()));
        followLevelVO3.setFollowName("3级关注");
        followLevelVO3.setPeople(report.getFollowPeopleThree());
        followLevelVO3.setClassRatio(dealRate(report.getFollowRateThree()));
        followLevelVO4.setFollowName("良好");
        followLevelVO4.setPeople(report.getFollowPeople());
        followLevelVO4.setClassRatio(dealRate(report.getFollowRate()));

        followLevelVOList.add(followLevelVO1);
        followLevelVOList.add(followLevelVO2);
        followLevelVOList.add(followLevelVO3);
        followLevelVOList.add(followLevelVO4);
        testModuleTwoVO.setAttentionLevelTable(followLevelVOList);

        List<GTestModuleTwoVO.TypeData> attentionLevel = new ArrayList<>();
        // 建议关注等级表格
        GTestModuleTwoVO.TypeData typeData1 = addChartData(report, 1);
        // 学生评定等级
        GTestModuleTwoVO.TypeData typeData2 = addChartData(report, 2);
        // 教师评定等级
        GTestModuleTwoVO.TypeData typeData3 = addChartData(report, 3);
        // 家长评定等级
        GTestModuleTwoVO.TypeData typeData4 = addChartData(report, 4);
        attentionLevel.add(typeData1);
        attentionLevel.add(typeData2);
        attentionLevel.add(typeData3);
        attentionLevel.add(typeData4);
        testModuleTwoVO.setAttentionLevel(attentionLevel);

        // 预警等级
        List<GTestModuleTwoVO.TypeData> warnAttentionLevel = new ArrayList<>();
        GTestModuleTwoVO.TypeData typeData5 = addChartData(report, 5);
        warnAttentionLevel.add(typeData5);
        testModuleTwoVO.setWarningLevel(warnAttentionLevel);
        List<FollowLevelVO> warnLevelVOList = new ArrayList<>();
        FollowLevelVO warnLevelVO1 = new FollowLevelVO();
        FollowLevelVO warnLevelVO2 = new FollowLevelVO();
        FollowLevelVO warnLevelVO3 = new FollowLevelVO();
        FollowLevelVO warnLevelVO4 = new FollowLevelVO();
        warnLevelVO1.setFollowName("1级预警");
        warnLevelVO1.setPeople(report.getWarnPeopleOne());
        warnLevelVO1.setClassRatio(dealRate(report.getWarnRateOne()));
        warnLevelVO2.setFollowName("2级预警");
        warnLevelVO2.setPeople(report.getWarnPeopleTwo());
        warnLevelVO2.setClassRatio(dealRate(report.getWarnRateTwo()));
        warnLevelVO3.setFollowName("3级预警");
        warnLevelVO3.setPeople(report.getWarnPeopleThree());
        warnLevelVO3.setClassRatio(dealRate(report.getWarnRateThree()));
        warnLevelVO4.setFollowName("未发现");
        warnLevelVO4.setPeople(report.getWarnPeople());
        warnLevelVO4.setClassRatio(dealRate(report.getWarnRate()));
        warnLevelVOList.add(warnLevelVO1);
        warnLevelVOList.add(warnLevelVO2);
        warnLevelVOList.add(warnLevelVO3);
        warnLevelVOList.add(warnLevelVO4);
        testModuleTwoVO.setWarningLevelTable(warnLevelVOList);

        // 关注人数-预警人数
        addFollowPeople(classIdList, taskId, testModuleTwoVO, gradeId);
        vo.setTestModuleTwoVO(testModuleTwoVO);

        // 测评概况
        PersonalReport.TestOverview testTotalScore = getTestTotalScore(dealRate2(report.getStudyStatusScore()), dealRate2(report.getBehaviorScore()), dealRate2(report.getMentalToughnessScore()),
                dealRate2(report.getOverallStressScore()), dealRate2(report.getEmotionalIndexScore()), dealRate2(report.getSleepIndexScore()));
        vo.setTestOverview(testTotalScore);

        // 学习状态
        List<GDimensionReportVO> dimensionVO = getStudyStatus(taskId, gradeId, reportData, classIdList);
        vo.setDimensionList(dimensionVO);

        // 另存一份，前端直接获取
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

        // 异步调用，更新年级报告
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
            // 新增
            mongoTemplate.save(gradeReport, "gradeReport");
        }
    }

    public GradeReport saveGrade() {
        log.info("===缓存年级报告空数据===");
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
        // 报告空数据
        GradeReport report = JSONObject.parseObject(jsonStr, GradeReport.class);
        report.setId("gradeReport");
        mongoTemplate.save(report, "gradeReport");
        return report;
    }

    void updateSchoolReport(Long taskId) {
        reportGroupsService.generateReport(taskId);
        log.info("更新年级报告数据");
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
            // 根据年级id,班级id查询所有拥有权限的教师id
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
            // 查询该任务下所有一级部门
            selectPage = departmentMapper.getGradeList(page, taskId, id);
        }
        // 根据年级id,班级id查询所有拥有权限的教师id
        List<ReportTeacherGrade> classList = reportTeacherGradeMapper.selectList(Wrappers.<ReportTeacherGrade>lambdaQuery()
                .eq(ReportTeacherGrade::getTaskId, taskId));
        List<Long> collect = classList.stream().map(ReportTeacherGrade::getTeacherId).collect(Collectors.toList());
        Long userId = SecureUtil.getUserId();
        List<ReportListVO> records = selectPage.getRecords();
        List<ReportListVO> result = new ArrayList<>();
        AtomicInteger total = new AtomicInteger(0);
        records.stream().forEach(reportListVO -> {
            // 通过年级id、任务id查找mongodb
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
        // 根据手机号查询该用户
        BindTeacher userByPhone = departmentMapper.getUserByPhone(dto.getPhone());
        if (ObjectUtils.isEmpty(userByPhone)) {
            throw new PlatformApiException("教师身份信息错误");
        }
        // 比对身份证号、姓名是否一致
        if (!dto.getName().equals(userByPhone.getName())) {
            throw new PlatformApiException("教师身份信息错误");
        }
        if (!IsIdCard(dto.getIdCard())) {
            throw new PlatformApiException("教师身份证号格式错误");
        }
        Long count = reportTeacherGradeMapper.selectCount(Wrappers.<ReportTeacherGrade>lambdaQuery().eq(ReportTeacherGrade::getGradeId, dto.getGradeId())
                .eq(ReportTeacherGrade::getTaskId, dto.getTaskId())
                .eq(ReportTeacherGrade::getTeacherId, userByPhone.getUserId()));
        if (count > 0) {
            throw new PlatformApiException("该教师已绑定");
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

        // 给该老师开通报告管理权限
        MenuData menuData = new MenuData();
        menuData.setUserId(userByPhone.getUserId());
        menuData.setMenuId(8L);
        menuData.setRoleId(5L);
        menuData.setDataId(dto.getTaskId());
        menuDataMapper.insert(menuData);
        return true;
    }

    /**
     * 验证身份证号输入
     *
     * @param str
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
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
        // 移除该老师菜单权限
        menuDataMapper.delete(Wrappers.<MenuData>lambdaQuery().eq(MenuData::getUserId, userId).eq(MenuData::getMenuId, 8));
        return delete > 0 ? true : false;
    }

    /**
     * 心理预警图表数据
     *
     * @param report
     * @param type   1关注等级 2学生评定 3教师评定 4家长评定 5预警等级
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
            chart1.setName("3级关注");
            chart1.setValue(Func.toDouble(dealRate2(report.getFollowRateThree())));
            chart1.setChartColor(ColorEnum.THREECOLOR.getValue());
            chart2.setName("2级关注");
            chart2.setValue(Func.toDouble(dealRate2(report.getFollowRateTwo())));
            chart2.setChartColor(ColorEnum.TWOCOLOR.getValue());
            chart3.setName("1级关注");
            chart3.setValue(Func.toDouble(dealRate2(report.getFollowRateOne())));
            chart3.setChartColor(ColorEnum.ONECOLOR.getValue());
            chart4.setName("良好");
            chart4.setValue(Func.toDouble(dealRate2(report.getFollowRate())));
            chart4.setChartColor(ColorEnum.ZEROCOLOR.getValue());
        } else if (type == 2) {
            typeData.setTitle("学生评定等级");
            chart1.setName("3级关注");
            chart1.setValue(Func.toDouble(dealRate2(report.getStudentRateThree())));
            chart1.setChartColor(ColorEnum.THREECOLOR.getValue());
            chart2.setName("2级关注");
            chart2.setValue(Func.toDouble(dealRate2(report.getStudentRateTwo())));
            chart2.setChartColor(ColorEnum.TWOCOLOR.getValue());
            chart3.setName("1级关注");
            chart3.setValue(Func.toDouble(dealRate2(report.getStudentRateOne())));
            chart3.setChartColor(ColorEnum.ONECOLOR.getValue());
            chart4.setName("良好");
            chart4.setValue(Func.toDouble(dealRate2(report.getStudentRate())));
            chart4.setChartColor(ColorEnum.ZEROCOLOR.getValue());
        } else if (type == 3) {
            typeData.setTitle("教师评定等级");
            chart1.setName("3级关注");
            chart1.setValue(Func.toDouble(dealRate2(report.getTeacherRateThree())));
            chart1.setChartColor(ColorEnum.THREECOLOR.getValue());
            chart2.setName("2级关注");
            chart2.setValue(Func.toDouble(dealRate2(report.getTeacherRateTwo())));
            chart2.setChartColor(ColorEnum.TWOCOLOR.getValue());
            chart3.setName("1级关注");
            chart3.setValue(Func.toDouble(dealRate2(report.getTeacherRateOne())));
            chart3.setChartColor(ColorEnum.ONECOLOR.getValue());
            chart4.setName("良好");
            chart4.setValue(Func.toDouble(dealRate2(report.getTeacherRate())));
            chart4.setChartColor(ColorEnum.ZEROCOLOR.getValue());
        } else if (type == 4) {
            typeData.setTitle("家长评定等级");
            chart3.setName("1级关注");
            chart3.setValue(Func.toDouble(dealRate2(report.getParentRateOne())));
            chart3.setChartColor(ColorEnum.ONECOLOR.getValue());
            chart4.setName("良好");
            chart4.setValue(Func.toDouble(dealRate2(report.getParentRate())));
            chart4.setChartColor(ColorEnum.ZEROCOLOR.getValue());
        } else if (type == 5) {
            typeData.setTitle("");
            chart1.setName("3级预警");
            chart1.setValue(Func.toDouble(dealRate2(report.getWarnRateThree())));
            chart1.setChartColor(ColorEnum.THREECOLOR.getValue());
            chart2.setName("2级预警");
            chart2.setValue(Func.toDouble(dealRate2(report.getWarnRateTwo())));
            chart2.setChartColor(ColorEnum.TWOCOLOR.getValue());
            chart3.setName("1级预警");
            chart3.setValue(Func.toDouble(dealRate2(report.getWarnRateOne())));
            chart3.setChartColor(ColorEnum.ONECOLOR.getValue());
            chart4.setName("未发现");
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

    // 学习状态
    public List<GDimensionReportVO> getStudyStatus(Long taskId, Long gradeId, GradeReportData gradeReportData, List<Long> classIdList) {
        List<GDimensionReportVO> voList = new ArrayList<>();
        GDimensionReportVO vo = new GDimensionReportVO();
        // 根据任务id,查询该任务下所有年级id
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
        // 校级平均报告
        List<PersonalReportData2> mappedResults = result.getMappedResults();
        if (ObjectUtils.isEmpty(mappedResults)) {
            return null;
        }
        PersonalReportData2 report = mappedResults.get(0);

        // 学习状态
        // 总体结果
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        String className = testPaperMapper.getClassName(gradeId);
        totalResultVO1.setTitle(className);
        totalResultVO1.setFontColor(ColorEnum.BLUE.getValue());
        totalResultVO1.setScore(dealRate2(gradeReportData.getStudyStatusScore()).toString());
        TotalResultVO totalResultVO2 = new TotalResultVO();
        totalResultVO2.setTitle("全校平均");
        totalResultVO2.setFontColor(ColorEnum.BLUE2.getValue());
        totalResultVO2.setScore(dealRate2(report.getStudyStatusScore()).toString());
        totalResultVOList.add(totalResultVO1);
        totalResultVOList.add(totalResultVO2);
        vo.setTotalResultVOList(totalResultVOList);
        // 各指标结果
        List<TestIndex> testIndices = testIndexMapper.selectList(Wrappers.<TestIndex>lambdaQuery()
                .eq(TestIndex::getIsDeleted, 0)
                .eq(TestIndex::getDimensionId, 1546788164255932417L));
        List<String> targetList = testIndices.stream().map(TestIndex::getName).collect(Collectors.toList());
        TargetResultVO targetResultVO = addTargetResult(targetList, gradeReportData, report, vo, className);

        // 各指标年级校级名称
        addTitleName(targetResultVO, 1, className);
        // 典型学生分布
        List<GDimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();

        targetList.stream().forEach(s -> {
            typicalStudent(gradeReportData, typicalStudentList, s);
        });
        vo.setTypicalStudentList(typicalStudentList);
        // 班级差异情况
        List<Object> classDiffList = new ArrayList<>();
        // 学习态度
        Map<String, Double> map1 = new HashMap<>();
        // 时间管理
        Map<String, Double> map2 = new HashMap<>();
        // 时间倦怠
        Map<String, Double> map3 = new HashMap<>();
        // 该年级下所有班级
        classIdList.stream().forEach(classId -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // 班级名称
                String name = testPaperMapper.getClassName(classId);
                map1.put(name, classReport.getLearningAttitudeScoreDiff());
                map2.put(name, classReport.getTimeManagementScoreDiff());
                map3.put(name, classReport.getLearningBurnoutScoreDiff());
                addClassDiffOne(classDiffList, classReport, targetList, name);
            }
        });
        vo.setClassDiffList(classDiffList);

        // 指标结果说明
        List<String> illustrate = new ArrayList<>();
        List<Map.Entry<String, Double>> list1 = mapSort(map1);
        addTargetResult(list1, "学习态度", illustrate);
        List<Map.Entry<String, Double>> list2 = mapSort(map2);
        addTargetResult(list2, "时间管理", illustrate);
        List<Map.Entry<String, Double>> list3 = mapSort(map3);
        addTargetResult(list3, "时间倦怠", illustrate);
        vo.setIllustrate(illustrate);

        // 品行表现
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

    // 测评概况
    public PersonalReport.TestOverview getTestTotalScore(Double studyStatusScore, Double behaviorScore, Double mentalToughnessScore,
                                                         Double overallStressScore, Double emotionalIndexScore, Double sleepIndexScore) {
        PersonalReport.TestOverview testOverview = new PersonalReport.TestOverview();
        // 积极
        List<PersonalReport.OverviewData> active = new ArrayList<>();
        // 学习状态
        addTestTotal(active, 1546788164255932417L, dealRate3(studyStatusScore));
        // 品行表现
        addTestTotal(active, 1546788937710755842L, dealRate3(behaviorScore));
        // 心理韧性
        addTestTotal(active, 1546789344491134978L, dealRate3(mentalToughnessScore));
        // 消极
        List<PersonalReport.OverviewData> negative = new ArrayList<>();
        // 综合压力
        addTestTotal(negative, 1546789419250409474L, dealRate3(overallStressScore));
        // 情绪指数
        addTestTotal(negative, 1546789515505491970L, dealRate3(emotionalIndexScore));
        // 睡眠指数
        addTestTotal(negative, 1546789785358622721L, dealRate3(sleepIndexScore));

        testOverview.setActive(active);
        testOverview.setNegative(negative);
        return testOverview;
    }

    // 品行表现
    public GDimensionReportVO behavior(String gradeName, GradeReportData gradeReportData, PersonalReportData2 report, List<Long> classIdList, Long taskId, Long gradeId) {
        GDimensionReportVO vo = new GDimensionReportVO();
        // 总体结果
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        totalResultVO1.setTitle(gradeName);
        totalResultVO1.setFontColor(ColorEnum.BLUE.getValue());
        totalResultVO1.setScore(dealRate2(gradeReportData.getBehaviorScore()).toString());
        TotalResultVO totalResultVO2 = new TotalResultVO();
        totalResultVO2.setTitle("全校平均");
        totalResultVO2.setFontColor(ColorEnum.BLUE2.getValue());
        totalResultVO2.setScore(dealRate2(report.getBehaviorScore()).toString());
        totalResultVOList.add(totalResultVO1);
        totalResultVOList.add(totalResultVO2);
        vo.setTotalResultVOList(totalResultVOList);
        // 各指标结果
        List<TestIndex> testIndices = testIndexMapper.selectList(Wrappers.<TestIndex>lambdaQuery()
                .eq(TestIndex::getIsDeleted, 0)
                .eq(TestIndex::getDimensionId, 1546788937710755842L));
        List<String> targetList = testIndices.stream().map(TestIndex::getName).collect(Collectors.toList());
        TargetResultVO targetResultVO = addTargetResult(targetList, gradeReportData, report, vo, gradeName);
        addTitleName(targetResultVO, 3, gradeName);
        // 典型学生分布
        List<GDimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();

        targetList.stream().forEach(s -> {
            typicalStudent(gradeReportData, typicalStudentList, s);
        });
        vo.setTypicalStudentList(typicalStudentList);
        // 班级差异情况
        List<Object> classDiffList = new ArrayList<>();
        // 道德性
        Map<String, Double> map1 = new HashMap<>();
        // 稳定性
        Map<String, Double> map2 = new HashMap<>();
        // 纪律性
        Map<String, Double> map3 = new HashMap<>();
        // 其他表现
        Map<String, Double> map4 = new HashMap<>();
        // 该年级下所有班级
        classIdList.stream().forEach(classId -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // 班级名称
                String name = testPaperMapper.getClassName(classId);
                map1.put(name, classReport.getMoralScoreDiff());
                map2.put(name, classReport.getStabilityScoreDiff());
                map3.put(name, classReport.getDisciplineScoreDiff());
                map4.put(name, classReport.getOtherPerformanceScoreDiff());
                addClassDiffTwo(classDiffList, classReport, targetList, name);
            }
        });
        vo.setClassDiffList(classDiffList);

        // 指标结果说明
        List<String> illustrate = new ArrayList<>();
        List<Map.Entry<String, Double>> list1 = mapSort(map1);
        addTargetResult(list1, "道德性", illustrate);
        List<Map.Entry<String, Double>> list2 = mapSort(map2);
        addTargetResult(list2, "稳定性", illustrate);
        List<Map.Entry<String, Double>> list3 = mapSort(map3);
        addTargetResult(list3, "纪律性", illustrate);
        List<Map.Entry<String, Double>> list4 = mapSort(map4);
        addTargetResult(list4, "其他表现", illustrate);
        vo.setIllustrate(illustrate);
        return vo;
    }

    // 心理韧性
    public GDimensionReportVO mentalToughness(String gradeName, GradeReportData gradeReportData, PersonalReportData2 report, List<Long> classIdList, Long taskId, Long gradeId) {
        GDimensionReportVO vo = new GDimensionReportVO();
        // 总体结果
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        totalResultVO1.setTitle(gradeName);
        totalResultVO1.setFontColor(ColorEnum.BLUE.getValue());
        totalResultVO1.setScore(dealRate2(gradeReportData.getMentalToughnessScore()).toString());
        TotalResultVO totalResultVO2 = new TotalResultVO();
        totalResultVO2.setTitle("全校平均");
        totalResultVO2.setFontColor(ColorEnum.BLUE2.getValue());
        totalResultVO2.setScore(dealRate2(report.getMentalToughnessScore()).toString());
        totalResultVOList.add(totalResultVO1);
        totalResultVOList.add(totalResultVO2);
        vo.setTotalResultVOList(totalResultVOList);
        // 各指标结果
        List<TestIndex> testIndices = testIndexMapper.selectList(Wrappers.<TestIndex>lambdaQuery()
                .eq(TestIndex::getIsDeleted, 0)
                .eq(TestIndex::getDimensionId, 1546789344491134978L));
        List<String> targetList = testIndices.stream().map(TestIndex::getName).collect(Collectors.toList());
        TargetResultVO targetResultVO = addTargetResult(targetList, gradeReportData, report, vo, gradeName);
        addTitleName(targetResultVO, 3, gradeName);
        // 典型学生分布
        List<GDimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();

        targetList.stream().forEach(s -> {
            typicalStudent(gradeReportData, typicalStudentList, s);
        });
        vo.setTypicalStudentList(typicalStudentList);
        // 班级差异情况
        List<Object> classDiffList = new ArrayList<>();
        // 情绪管理
        Map<String, Double> map1 = new HashMap<>();
        // 目标激励
        Map<String, Double> map2 = new HashMap<>();
        // 积极关注
        Map<String, Double> map3 = new HashMap<>();
        // 学校支持
        Map<String, Double> map4 = new HashMap<>();
        // 人际支持
        Map<String, Double> map5 = new HashMap<>();
        // 家庭支持
        Map<String, Double> map6 = new HashMap<>();
        // 该年级下所有班级
        classIdList.stream().forEach(classId -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // 班级名称
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

        // 指标结果说明
        List<String> illustrate = new ArrayList<>();
        List<Map.Entry<String, Double>> list1 = mapSort(map1);
        addTargetResult(list1, "情绪管理", illustrate);
        List<Map.Entry<String, Double>> list2 = mapSort(map2);
        addTargetResult(list2, "目标激励", illustrate);
        List<Map.Entry<String, Double>> list3 = mapSort(map3);
        addTargetResult(list3, "积极关注", illustrate);
        List<Map.Entry<String, Double>> list4 = mapSort(map4);
        addTargetResult(list4, "学校支持", illustrate);
        List<Map.Entry<String, Double>> list5 = mapSort(map5);
        addTargetResult(list5, "人际支持", illustrate);
        List<Map.Entry<String, Double>> list6 = mapSort(map6);
        addTargetResult(list6, "家庭支持", illustrate);
        vo.setIllustrate(illustrate);
        return vo;
    }

    // 综合压力
    public GDimensionReportVO overallStress(String gradeName, GradeReportData gradeReportData, PersonalReportData2 report, List<Long> classIdList, Long taskId, Long gradeId) {
        GDimensionReportVO vo = new GDimensionReportVO();
        // 总体结果
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        totalResultVO1.setTitle(gradeName);
        totalResultVO1.setFontColor(ColorEnum.ORANGE.getValue());
        totalResultVO1.setScore(dealRate2(gradeReportData.getOverallStressScore()).toString());
        TotalResultVO totalResultVO2 = new TotalResultVO();
        totalResultVO2.setTitle("全校平均");
        totalResultVO2.setFontColor(ColorEnum.ORANGE2.getValue());
        totalResultVO2.setScore(dealRate2(report.getOverallStressScore()).toString());
        totalResultVOList.add(totalResultVO1);
        totalResultVOList.add(totalResultVO2);
        vo.setTotalResultVOList(totalResultVOList);
        // 各指标结果
        List<TestIndex> testIndices = testIndexMapper.selectList(Wrappers.<TestIndex>lambdaQuery()
                .eq(TestIndex::getIsDeleted, 0)
                .eq(TestIndex::getDimensionId, 1546789419250409474L));
        List<String> targetList = testIndices.stream().map(TestIndex::getName).collect(Collectors.toList());
        TargetResultVO targetResultVO = addTargetResult(targetList, gradeReportData, report, vo, gradeName);
        addTitleName(targetResultVO, 2, gradeName);
        // 典型学生分布
        List<GDimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();

        targetList.stream().forEach(s -> {
            typicalStudent(gradeReportData, typicalStudentList, s);
        });
        vo.setTypicalStudentList(typicalStudentList);
        // 班级差异情况
        List<Object> classDiffList = new ArrayList<>();
        // 学习压力
        Map<String, Double> map1 = new HashMap<>();
        // 人际压力
        Map<String, Double> map2 = new HashMap<>();
        // 受惩罚压力
        Map<String, Double> map3 = new HashMap<>();
        // 丧失压力
        Map<String, Double> map4 = new HashMap<>();
        // 适应压力
        Map<String, Double> map5 = new HashMap<>();
        // 该年级下所有班级
        classIdList.stream().forEach(classId -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // 班级名称
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

        // 指标结果说明
        List<String> illustrate = new ArrayList<>();
        List<Map.Entry<String, Double>> list1 = mapSort(map1);
        addTargetResult(list1, "学习压力", illustrate);
        List<Map.Entry<String, Double>> list2 = mapSort(map2);
        addTargetResult(list2, "人际压力", illustrate);
        List<Map.Entry<String, Double>> list3 = mapSort(map3);
        addTargetResult(list3, "受惩罚压力", illustrate);
        List<Map.Entry<String, Double>> list4 = mapSort(map4);
        addTargetResult(list4, "丧失压力", illustrate);
        List<Map.Entry<String, Double>> list5 = mapSort(map5);
        addTargetResult(list5, "适应压力", illustrate);
        vo.setIllustrate(illustrate);
        return vo;
    }

    // 情绪指数
    public GDimensionReportVO emotionalIndex(String gradeName, GradeReportData gradeReportData, PersonalReportData2 report, List<Long> classIdList, Long taskId, Long gradeId) {
        GDimensionReportVO vo = new GDimensionReportVO();
        // 总体结果
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        totalResultVO1.setTitle(gradeName);
        totalResultVO1.setFontColor(ColorEnum.ORANGE.getValue());
        totalResultVO1.setScore(dealRate2(gradeReportData.getEmotionalIndexScore()).toString());
        TotalResultVO totalResultVO2 = new TotalResultVO();
        totalResultVO2.setTitle("全校平均");
        totalResultVO2.setFontColor(ColorEnum.ORANGE2.getValue());
        totalResultVO2.setScore(dealRate2(report.getEmotionalIndexScore()).toString());
        totalResultVOList.add(totalResultVO1);
        totalResultVOList.add(totalResultVO2);
        vo.setTotalResultVOList(totalResultVOList);
        // 各指标结果
        List<TestIndex> testIndices = testIndexMapper.selectList(Wrappers.<TestIndex>lambdaQuery()
                .eq(TestIndex::getIsDeleted, 0)
                .eq(TestIndex::getDimensionId, 1546789515505491970L));
        List<String> targetList = testIndices.stream().map(TestIndex::getName).collect(Collectors.toList());
        TargetResultVO targetResultVO = addTargetResult(targetList, gradeReportData, report, vo, gradeName);
        addTitleName(targetResultVO, 2, gradeName);
        // 典型学生分布
        List<GDimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();

        targetList.stream().forEach(s -> {
            typicalStudent(gradeReportData, typicalStudentList, s);
        });
        vo.setTypicalStudentList(typicalStudentList);
        // 班级差异情况
        List<Object> classDiffList = new ArrayList<>();
        // 强迫
        Map<String, Double> map1 = new HashMap<>();
        // 偏执
        Map<String, Double> map2 = new HashMap<>();
        // 敌对
        Map<String, Double> map3 = new HashMap<>();
        // 人际敏感
        Map<String, Double> map4 = new HashMap<>();
        // 焦虑
        Map<String, Double> map5 = new HashMap<>();
        // 抑郁
        Map<String, Double> map6 = new HashMap<>();
        // 该年级下所有班级
        classIdList.stream().forEach(classId -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // 班级名称
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

        // 指标结果说明
        List<String> illustrate = new ArrayList<>();
        List<Map.Entry<String, Double>> list1 = mapSort(map1);
        addTargetResult(list1, "强迫", illustrate);
        List<Map.Entry<String, Double>> list2 = mapSort(map2);
        addTargetResult(list2, "偏执", illustrate);
        List<Map.Entry<String, Double>> list3 = mapSort(map3);
        addTargetResult(list3, "敌对", illustrate);
        List<Map.Entry<String, Double>> list4 = mapSort(map4);
        addTargetResult(list4, "人际敏感", illustrate);
        List<Map.Entry<String, Double>> list5 = mapSort(map5);
        addTargetResult(list5, "焦虑", illustrate);
        List<Map.Entry<String, Double>> list6 = mapSort(map6);
        addTargetResult(list6, "抑郁", illustrate);
        vo.setIllustrate(illustrate);
        return vo;
    }

    // 睡眠指数
    public GDimensionReportVO sleepIndex(String gradeName, GradeReportData gradeReportData, PersonalReportData2 report, List<Long> classIdList, Long taskId, Long gradeId) {
        GDimensionReportVO vo = new GDimensionReportVO();
        // 总体结果
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        totalResultVO1.setTitle(gradeName);
        totalResultVO1.setFontColor(ColorEnum.ORANGE.getValue());
        totalResultVO1.setScore(dealRate2(gradeReportData.getSleepIndexScore()).toString());
        TotalResultVO totalResultVO2 = new TotalResultVO();
        totalResultVO2.setTitle("全校平均");
        totalResultVO2.setFontColor(ColorEnum.ORANGE2.getValue());
        totalResultVO2.setScore(dealRate2(report.getSleepIndexScore()).toString());
        totalResultVOList.add(totalResultVO1);
        totalResultVOList.add(totalResultVO2);
        vo.setTotalResultVOList(totalResultVOList);

        // 典型学生分布
        List<GDimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();

        typicalStudent(gradeReportData, typicalStudentList, "睡眠指数");
        vo.setTypicalStudentList(typicalStudentList);
        // 班级差异情况
        List<Object> classDiffList = new ArrayList<>();
        // 睡眠指数
        Map<String, Double> map1 = new HashMap<>();
        List<String> targetList = new ArrayList<>();
        targetList.add("睡眠指数");
        // 该年级下所有班级
        classIdList.stream().forEach(classId -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // 班级名称
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

        // 指标结果说明
        List<String> illustrate = new ArrayList<>();
        List<Map.Entry<String, Double>> list1 = mapSort(map1);
        addTargetResult(list1, "睡眠指数", illustrate);
        vo.setIllustrate(illustrate);
        return vo;
    }

    // 指标结果输出
    public void addTargetResult(List<Map.Entry<String, Double>> list, String targetName, List<String> illustrate) {
        //1.最大值，最下值皆为-1.96＜Z＜1.96，则输出xx指标中，各班之间相比没有差异
        // 消极指标相反
        double max = list.get(0).getValue();
        double min = list.get(list.size() - 1).getValue();
        String s = "";
        if (max < 1.96 && max > -1.96 && min < 1.96 && min > -1.96) {
            s = targetName + "指标中，各班之间相比没有差异";
        } else {
            if ("学习压力".equals(targetName) || "人际压力".equals(targetName) || "受惩罚压力".equals(targetName) || "丧失压力".equals(targetName) ||
                    "适应压力".equals(targetName) || "强迫".equals(targetName) || "偏执".equals(targetName) || "敌对".equals(targetName) ||
                    "人际敏感".equals(targetName) || "焦虑".equals(targetName) || "抑郁".equals(targetName) || "睡眠指数".equals(targetName)) {
                s = targetName + "指标中，情况较好的是" + list.get(list.size() - 1).getKey() + "，情况较差的是" + list.get(0).getKey();
            } else {
                s = targetName + "指标中，情况较好的是" + list.get(0).getKey() + "，情况较差的是" + list.get(list.size() - 1).getKey();
            }

        }
        illustrate.add(s);

    }

    // map排序 从大到小
    public List<Map.Entry<String, Double>> mapSort(Map<String, Double> map) {
        List<Map.Entry<String, Double>> wordMap = new ArrayList<Map.Entry<String, Double>>(map.entrySet());
        Collections.sort(wordMap, new Comparator<Map.Entry<String, Double>>() {//根据value排序
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
     * 班级差异情况-1
     *
     * @param classDiffList
     * @param report        班级报告
     * @param targetList    指标集合
     * @param className     班级名称
     * @return java.util.Map<java.lang.String, java.lang.Double>
     * @author hzl
     * @date 2022/8/30 18:38
     */
    public Map<String, Double> addClassDiffOne(List<Object> classDiffList, ClassReportData report, List<String> targetList, String className) {
        Map<String, Double> map = new HashMap<>();
        GDimensionReportVO.ClassDiffOne classDiff = new GDimensionReportVO.ClassDiffOne();
        classDiff.setTitle(className);
        for (String s : targetList) {
            if ("学习态度".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getLearningAttitudeScore()));
                Double diff = report.getLearningAttitudeScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetOne(target);
                map.put(s, diff);
            } else if ("时间管理".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getTimeManagementScore()));
                Double diff = report.getTimeManagementScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetTwo(target);
                map.put(s, diff);
            } else if ("学习倦怠".equals(s)) {
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
     * 班级差异情况-2
     *
     * @param classDiffList
     * @param report        班级报告
     * @param targetList    指标集合
     * @param className     班级名称
     * @return java.util.Map<java.lang.String, java.lang.Double>
     * @author hzl
     * @date 2022/8/30 18:38
     */
    public Map<String, Double> addClassDiffTwo(List<Object> classDiffList, ClassReportData report, List<String> targetList, String className) {
        Map<String, Double> map = new HashMap<>();
        GDimensionReportVO.ClassDiffTwo classDiff = new GDimensionReportVO.ClassDiffTwo();
        classDiff.setTitle(className);
        for (String s : targetList) {
            if ("道德性".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getMoralScore()));
                Double diff = report.getMoralScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetOne(target);
                map.put(s, diff);
            } else if ("稳定性".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getStabilityScore()));
                Double diff = report.getStabilityScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetTwo(target);
                map.put(s, diff);
            } else if ("纪律性".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getDisciplineScore()));
                Double diff = report.getDisciplineScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetThree(target);
                map.put(s, diff);
            } else if ("其他表现".equals(s)) {
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
     * 班级差异情况-3
     *
     * @param classDiffList
     * @param report        班级报告
     * @param targetList    指标集合
     * @param className     班级名称
     * @return java.util.Map<java.lang.String, java.lang.Double>
     * @author hzl
     * @date 2022/8/30 18:38
     */
    public Map<String, Double> addClassDiffThree(List<Object> classDiffList, ClassReportData report, List<String> targetList, String className) {
        Map<String, Double> map = new HashMap<>();
        GDimensionReportVO.ClassDiffThree classDiff = new GDimensionReportVO.ClassDiffThree();
        classDiff.setTitle(className);
        for (String s : targetList) {
            if ("学习压力".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getStudyStressScore()));
                Double diff = report.getStudyStressScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetOne(target);
                map.put(s, diff);
            } else if ("人际压力".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getInterpersonalStressScore()));
                Double diff = report.getInterpersonalStressScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetTwo(target);
                map.put(s, diff);
            } else if ("受惩罚压力".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getPunishmentStressScore()));
                Double diff = report.getPunishmentStressScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetThree(target);
                map.put(s, diff);
            } else if ("丧失压力".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getLossStressScore()));
                Double diff = report.getLossStressScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetFour(target);
                map.put(s, diff);
            } else if ("适应压力".equals(s)) {
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
     * 班级差异情况-4
     *
     * @param classDiffList
     * @param report        班级报告
     * @param targetList    指标集合
     * @param className     班级名称
     * @return java.util.Map<java.lang.String, java.lang.Double>
     * @author hzl
     * @date 2022/8/30 18:38
     */
    public Map<String, Double> addClassDiffFour(List<Object> classDiffList, ClassReportData report, List<String> targetList, String className) {
        Map<String, Double> map = new HashMap<>();
        GDimensionReportVO.ClassDiffFour classDiff = new GDimensionReportVO.ClassDiffFour();
        classDiff.setTitle(className);
        for (String s : targetList) {
            if ("情绪管理".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getEmotionManagementScore()));
                Double diff = report.getEmotionManagementScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetOne(target);
                map.put(s, diff);
            } else if ("目标激励".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getGoalMotivationScore()));
                Double diff = report.getGoalMotivationScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetTwo(target);
                map.put(s, diff);
            } else if ("积极关注".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getPositiveAttentionScore()));
                Double diff = report.getPositiveAttentionScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetThree(target);
                map.put(s, diff);
            } else if ("学校支持".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getSchoolSupportScore()));
                Double diff = report.getSchoolSupportScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetFour(target);
                map.put(s, diff);
            } else if ("人际支持".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getInterpersonalSupportScore()));
                Double diff = report.getInterpersonalSupportScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetFive(target);
                map.put(s, diff);
            } else if ("家庭支持".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getFamilySupportScore()));
                Double diff = report.getFamilySupportScoreDiff();
                target.setTargetDiff(diffResult(diff, 1));
                classDiff.setTargetSix(target);
                map.put(s, diff);
            } else if ("强迫".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getCompulsionScore()));
                Double diff = report.getCompulsionScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetOne(target);
                map.put(s, diff);
            } else if ("偏执".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getParanoiaScore()));
                Double diff = report.getParanoiaScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetTwo(target);
                map.put(s, diff);
            } else if ("敌对".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getHostilityScore()));
                Double diff = report.getHostilityScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetThree(target);
                map.put(s, diff);
            } else if ("人际敏感".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getInterpersonalSensitivityScore()));
                Double diff = report.getInterpersonalSensitivityScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetFour(target);
                map.put(s, diff);
            } else if ("焦虑".equals(s)) {
                GDimensionReportVO.TargetScore target = new GDimensionReportVO.TargetScore();
                target.setTargetScore(dealRate3(report.getAnxietyScore()));
                Double diff = report.getAnxietyScoreDiff();
                target.setTargetDiff(diffResult(diff, 2));
                classDiff.setTargetFive(target);
                map.put(s, diff);
            } else if ("抑郁".equals(s)) {
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
     * 差异检验结果 1较差  2没有差异  3较好
     * type 类型，1积极指标  2消极指标
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

    // 典型学生分布
    public void typicalStudent(GradeReportData reportData, List<GDimensionReportVO.TypicalStudent> typicalStudentList, String target) {
        // 年级有效人数 = 已测人数-无效人数
        int gradePeople = reportData.getTestPeople() - reportData.getInvalidPeople();
        int veryBadPeople = 0, badPeople = 0, generallyPeople = 0, goodPeople = 0;
        // 指标人数赋值
        if ("学习态度".equals(target)) {
            veryBadPeople = reportData.getLearningAttitudeScoreVeryBad();
            badPeople = reportData.getLearningAttitudeScoreBad();
            generallyPeople = reportData.getLearningAttitudeScoreGenerally();
            goodPeople = reportData.getLearningAttitudeScoreGood();
        } else if ("时间管理".equals(target)) {
            veryBadPeople = reportData.getTimeManagementScoreVeryBad();
            badPeople = reportData.getTimeManagementScoreBad();
            generallyPeople = reportData.getTimeManagementScoreGenerally();
            goodPeople = reportData.getTimeManagementScoreGood();
        } else if ("学习倦怠".equals(target)) {
            veryBadPeople = reportData.getLearningBurnoutScoreVeryBad();
            badPeople = reportData.getLearningBurnoutScoreBad();
            generallyPeople = reportData.getLearningBurnoutScoreGenerally();
            goodPeople = reportData.getLearningBurnoutScoreGood();
        } else if ("道德性".equals(target)) {
            veryBadPeople = reportData.getMoralScoreVeryBad();
            badPeople = reportData.getMoralScoreBad();
            generallyPeople = reportData.getMoralScoreGenerally();
            goodPeople = reportData.getMoralScoreGood();
        } else if ("稳定性".equals(target)) {
            veryBadPeople = reportData.getStabilityScoreVeryBad();
            badPeople = reportData.getStabilityScoreBad();
            generallyPeople = reportData.getStabilityScoreGenerally();
            goodPeople = reportData.getStabilityScoreGood();
        } else if ("纪律性".equals(target)) {
            veryBadPeople = reportData.getDisciplineScoreVeryBad();
            badPeople = reportData.getDisciplineScoreBad();
            generallyPeople = reportData.getDisciplineScoreGenerally();
            goodPeople = reportData.getDisciplineScoreGood();
        } else if ("其他表现".equals(target)) {
            veryBadPeople = reportData.getOtherPerformanceScoreVeryBad();
            badPeople = reportData.getOtherPerformanceScoreBad();
            generallyPeople = reportData.getOtherPerformanceScoreGenerally();
            goodPeople = reportData.getOtherPerformanceScoreGood();
        } else if ("情绪管理".equals(target)) {
            veryBadPeople = reportData.getEmotionManagementScoreVeryBad();
            badPeople = reportData.getEmotionManagementScoreBad();
            generallyPeople = reportData.getEmotionManagementScoreGenerally();
            goodPeople = reportData.getEmotionManagementScoreGood();
        } else if ("目标激励".equals(target)) {
            veryBadPeople = reportData.getGoalMotivationScoreVeryBad();
            badPeople = reportData.getGoalMotivationScoreBad();
            generallyPeople = reportData.getGoalMotivationScoreGenerally();
            goodPeople = reportData.getGoalMotivationScoreGood();
        } else if ("积极关注".equals(target)) {
            veryBadPeople = reportData.getPositiveAttentionScoreVeryBad();
            badPeople = reportData.getPositiveAttentionScoreBad();
            generallyPeople = reportData.getPositiveAttentionScoreGenerally();
            goodPeople = reportData.getPositiveAttentionScoreGood();
        } else if ("学校支持".equals(target)) {
            veryBadPeople = reportData.getSchoolSupportScoreVeryBad();
            badPeople = reportData.getSchoolSupportScoreBad();
            generallyPeople = reportData.getSchoolSupportScoreGenerally();
            goodPeople = reportData.getSchoolSupportScoreGood();
        } else if ("人际支持".equals(target)) {
            veryBadPeople = reportData.getInterpersonalSupportScoreVeryBad();
            badPeople = reportData.getInterpersonalSupportScoreBad();
            generallyPeople = reportData.getInterpersonalSupportScoreGenerally();
            goodPeople = reportData.getInterpersonalSupportScoreGood();
        } else if ("家庭支持".equals(target)) {
            veryBadPeople = reportData.getFamilySupportScoreVeryBad();
            badPeople = reportData.getFamilySupportScoreBad();
            generallyPeople = reportData.getFamilySupportScoreGenerally();
            goodPeople = reportData.getFamilySupportScoreGood();
        } else if ("学习压力".equals(target)) {
            veryBadPeople = reportData.getStudyStressScoreVeryBad();
            badPeople = reportData.getStudyStressScoreBad();
            generallyPeople = reportData.getStudyStressScoreGenerally();
            goodPeople = reportData.getStudyStressScoreGood();
        } else if ("人际压力".equals(target)) {
            veryBadPeople = reportData.getInterpersonalStressScoreVeryBad();
            badPeople = reportData.getInterpersonalStressScoreBad();
            generallyPeople = reportData.getInterpersonalStressScoreGenerally();
            goodPeople = reportData.getInterpersonalStressScoreGood();
        } else if ("受惩罚压力".equals(target)) {
            veryBadPeople = reportData.getPunishmentStressScoreVeryBad();
            badPeople = reportData.getPunishmentStressScoreBad();
            generallyPeople = reportData.getPunishmentStressScoreGenerally();
            goodPeople = reportData.getPunishmentStressScoreGood();
        } else if ("丧失压力".equals(target)) {
            veryBadPeople = reportData.getLossStressScoreVeryBad();
            badPeople = reportData.getLossStressScoreBad();
            generallyPeople = reportData.getLossStressScoreGenerally();
            goodPeople = reportData.getLossStressScoreGood();
        } else if ("适应压力".equals(target)) {
            veryBadPeople = reportData.getAdaptationStressScoreVeryBad();
            badPeople = reportData.getAdaptationStressScoreBad();
            generallyPeople = reportData.getAdaptationStressScoreGenerally();
            goodPeople = reportData.getAdaptationStressScoreGood();
        } else if ("强迫".equals(target)) {
            veryBadPeople = reportData.getCompulsionScoreVeryBad();
            badPeople = reportData.getCompulsionScoreBad();
            generallyPeople = reportData.getCompulsionScoreGenerally();
            goodPeople = reportData.getCompulsionScoreGood();
        } else if ("偏执".equals(target)) {
            veryBadPeople = reportData.getParanoiaScoreVeryBad();
            badPeople = reportData.getParanoiaScoreBad();
            generallyPeople = reportData.getParanoiaScoreGenerally();
            goodPeople = reportData.getParanoiaScoreGood();
        } else if ("敌对".equals(target)) {
            veryBadPeople = reportData.getHostilityScoreVeryBad();
            badPeople = reportData.getHostilityScoreBad();
            generallyPeople = reportData.getHostilityScoreGenerally();
            goodPeople = reportData.getHostilityScoreGood();
        } else if ("人际敏感".equals(target)) {
            veryBadPeople = reportData.getInterpersonalSensitivityScoreVeryBad();
            badPeople = reportData.getInterpersonalSensitivityScoreBad();
            generallyPeople = reportData.getInterpersonalSensitivityScoreGenerally();
            goodPeople = reportData.getInterpersonalSensitivityScoreGood();
        } else if ("焦虑".equals(target)) {
            veryBadPeople = reportData.getAnxietyScoreVeryBad();
            badPeople = reportData.getAnxietyScoreBad();
            generallyPeople = reportData.getAnxietyScoreGenerally();
            goodPeople = reportData.getAnxietyScoreGood();
        } else if ("抑郁".equals(target)) {
            veryBadPeople = reportData.getDepressionScoreVeryBad();
            badPeople = reportData.getDepressionScoreBad();
            generallyPeople = reportData.getDepressionScoreGenerally();
            goodPeople = reportData.getDepressionScoreGood();
        } else if ("睡眠指数".equals(target)) {
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
        typicalStudent1.setVeryBad(veryBadPeople + "人(" + v1 + "%)");
        typicalStudent1.setBad(badPeople + "人(" + v2 + "%)");
        typicalStudent1.setGenerally(generallyPeople + "人(" + v3 + "%)");
        typicalStudent1.setGood(goodPeople + "人(" + v4 + "%)");
        typicalStudentList.add(typicalStudent1);
    }

    // 计算百分比
    public double calculatePercentage(int total, int num) {
        DecimalFormat df = new DecimalFormat("#0.0");
        // 转成double进行计算
        if (total == 0 || num == 0) {
            return 0;
        }
        double v = (double) num / (double) total * 100;
        // 保留两位小数
        String format = df.format(v);
        return Func.toDouble(format);
    }

    // 各指标年级校级名称

    /**
     * @param targetResultVO
     * @param type           1:学习状态   2综合压力、情绪指数  3.品行表现、心理韧性
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
            vo2.setName("全校平均");
            vo2.setColor(ColorEnum.BLUE2.getValue());
            TargetResultVO.TitleNameVO vo3 = new TargetResultVO.TitleNameVO();
            vo3.setName(name);
            vo3.setColor(ColorEnum.ORANGE.getValue());
            TargetResultVO.TitleNameVO vo4 = new TargetResultVO.TitleNameVO();
            vo4.setName("全校平均");
            vo4.setColor(ColorEnum.ORANGE2.getValue());

            list.add(vo1);
            list.add(vo3);
            list.add(vo2);
            list.add(vo4);
        } else if (type == 2) {
            vo1.setName(name);
            vo1.setColor(ColorEnum.ORANGE.getValue());
            vo2.setName("全校平均");
            vo2.setColor(ColorEnum.ORANGE2.getValue());

            list.add(vo1);
            list.add(vo2);
        } else if (type == 3) {
            vo1.setName(name);
            vo1.setColor(ColorEnum.BLUE.getValue());
            vo2.setName("全校平均");
            vo2.setColor(ColorEnum.BLUE2.getValue());

            list.add(vo1);
            list.add(vo2);
        }
        targetResultVO.setTitleNameVOList(list);
    }

    // 各指标结果
    public TargetResultVO addTargetResult(List<String> targetList, GradeReportData classReportData, PersonalReportData2 report, GDimensionReportVO vo, String gradeName) {
        TargetResultVO targetResultVO = new TargetResultVO();
        List<TargetResultVO.TargetVO> targetVOList = new ArrayList<>();


        targetList.stream().forEach(s -> {
            TargetResultVO.TargetVO targetVO1 = new TargetResultVO.TargetVO();
            targetVO1.setTitle(s);
            if ("学习态度".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getLearningAttitudeScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getLearningAttitudeScore()).toString());
            } else if ("时间管理".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getTimeManagementScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getTimeManagementScore()).toString());
            } else if ("学习倦怠".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getLearningBurnoutScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getLearningBurnoutScore()).toString());
            } else if ("道德性".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getMoralScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getMoralScore()).toString());
            } else if ("稳定性".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getStabilityScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getStabilityScore()).toString());
            } else if ("纪律性".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getDisciplineScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getDisciplineScore()).toString());
            } else if ("其他表现".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getOtherPerformanceScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getOtherPerformanceScore()).toString());
            } else if ("情绪管理".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getEmotionManagementScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getEmotionManagementScore()).toString());
            } else if ("目标激励".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getGoalMotivationScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getGoalMotivationScore()).toString());
            } else if ("积极关注".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getPositiveAttentionScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getPositiveAttentionScore()).toString());
            } else if ("学校支持".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getSchoolSupportScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getSchoolSupportScore()).toString());
            } else if ("人际支持".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getInterpersonalSupportScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getInterpersonalSupportScore()).toString());
            } else if ("家庭支持".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.BLUE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getFamilySupportScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.BLUE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getFamilySupportScore()).toString());
            } else if ("学习压力".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getStudyStressScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getStudyStressScore()).toString());
            } else if ("人际压力".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getInterpersonalStressScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getInterpersonalStressScore()).toString());
            } else if ("受惩罚压力".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getPunishmentStressScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getPunishmentStressScore()).toString());
            } else if ("丧失压力".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getLossStressScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getLossStressScore()).toString());
            } else if ("适应压力".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getAdaptationStressScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getAdaptationStressScore()).toString());
            } else if ("强迫".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getCompulsionScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getCompulsionScore()).toString());
            } else if ("偏执".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getParanoiaScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getParanoiaScore()).toString());
            } else if ("敌对".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getHostilityScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getHostilityScore()).toString());
            } else if ("人际敏感".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getInterpersonalSensitivityScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getInterpersonalSensitivityScore()).toString());
            } else if ("焦虑".equals(s)) {
                targetVO1.setFontColorOne(ColorEnum.ORANGE.getValue());
                targetVO1.setScoreOne(dealRate2(classReportData.getAnxietyScore()).toString());
                targetVO1.setFontColorTwo(ColorEnum.ORANGE2.getValue());
                targetVO1.setScoreTwo(dealRate2(report.getAnxietyScore()).toString());
            } else if ("抑郁".equals(s)) {
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

    // 关注等级人数-预警等级人数
    public void addFollowPeople(List<Long> classIdList, Long taskId, GTestModuleTwoVO testModuleTwoVO, Long gradeId) {
        // 关注人数
        List<GFollowUserVO> followUserList = new ArrayList<>();
        // 关注等级分布
        List<GFollowVO> followClassList = new ArrayList<>();
        // 预警人数
        List<GFollowUserVO> warnUserList = new ArrayList<>();
        // 预警等级分布
        List<GFollowVO> warnClassList = new ArrayList<>();
        classIdList.stream().forEach(classId -> {
            // 根据班级id,任务id,查询班级报告
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // 班级名称
                String className = testPaperMapper.getClassName(classId);
                // 关注人数
                GFollowUserVO followUserVO = new GFollowUserVO();
                followUserVO.setClassName(className);
                followUserVO.setVoOneList(classReport.getUserInfoOneList());
                followUserVO.setVoTwoList(classReport.getUserInfoTwoList());
                followUserVO.setVoThreeList(classReport.getUserInfoThreeList());
                followUserList.add(followUserVO);
                // 关注等级分布
                GFollowVO followVO = new GFollowVO();
                followVO.setTaskId(taskId);
                followVO.setGradeId(gradeId);
                followVO.setClassId(classId);
                followVO.setClassName(className);
                followVO.setRate(classReport.getFollowPeople() + "人 （" + dealRate(classReport.getFollowRate()) + "）");
                followVO.setRateOne(classReport.getFollowPeopleOne() + "人 （" + dealRate(classReport.getFollowRateOne()) + "）");
                followVO.setRateTwo(classReport.getFollowPeopleTwo() + "人 （" + dealRate(classReport.getFollowRateTwo()) + "）");
                followVO.setRateThree(classReport.getFollowPeopleThree() + "人 （" + dealRate(classReport.getFollowRateThree()) + "）");
                followClassList.add(followVO);
                // 预警人数
                GFollowUserVO warnUserVo = new GFollowUserVO();
                warnUserVo.setClassName(className);
                warnUserVo.setVoOneList(classReport.getUserInfoWarnOneList());
                warnUserVo.setVoTwoList(classReport.getUserInfoWranTwoList());
                warnUserVo.setVoThreeList(classReport.getUserInfoWranThreeList());
                warnUserList.add(warnUserVo);
                // 预警等级分布
                GFollowVO warnVO = new GFollowVO();
                warnVO.setTaskId(taskId);
                warnVO.setGradeId(gradeId);
                warnVO.setClassId(classId);
                warnVO.setClassName(className);
                warnVO.setRate(classReport.getWarnPeople() + "人 (" + dealRate(classReport.getWarnRate()) + ")");
                warnVO.setRateOne(classReport.getWarnPeopleOne() + "人 (" + dealRate(classReport.getWarnRateOne()) + ")");
                warnVO.setRateTwo(classReport.getWarnPeopleTwo() + "人 (" + dealRate(classReport.getWarnRateTwo()) + ")");
                warnVO.setRateThree(classReport.getWarnPeopleThree() + "人 (" + dealRate(classReport.getWarnRateThree()) + ")");
                warnClassList.add(warnVO);
            }

        });
        testModuleTwoVO.setAttentionStudent(followUserList);
        testModuleTwoVO.setAttentionLevelDistribution(followClassList);
        testModuleTwoVO.setWarningStudent(warnUserList);
        testModuleTwoVO.setWarningLevelDistribution(warnClassList);
    }

    // 参与班级-测试完成情况 作答有效性
    public void addClassTest(List<Long> classIdList, Long taskId, GTestFInishVO gTestFInishVO, GradeReportVO reportVO, List<GInvalidVO> gInvalidVOList, Long gradeId) {
        // 参与班级-测试完成情况
        List<TestFinishVo> testFinishVoList = new ArrayList<>();
        // 作答有效性
        classIdList.stream().forEach(classId -> {
            // 根据班级id,任务id,查询班级报告
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId).and("gradeId").is(gradeId));
            ClassReportData classReport = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(classReport)) {
                // 班级测试完成情况
                TestFinishVo vo = new TestFinishVo();
                GInvalidVO invalidVO = new GInvalidVO();
                // 班级名称
                String className = testPaperMapper.getClassName(classId);
                vo.setClassName(className);
                vo.setTotalPeople(classReport.getTotalPeople());
                vo.setTestPeople(classReport.getTestPeople());
                vo.setNoTestPeople(classReport.getNoTestPeople());
                vo.setInvalidPeople(classReport.getInvalidPeople());
                vo.setCompletionRate(dealRate(classReport.getCompletionRate()));
                testFinishVoList.add(vo);

                // 有效性
                invalidVO.setClassName(className);
                invalidVO.setVoList(classReport.getInvalidVO().getVoList());
                gInvalidVOList.add(invalidVO);
            }

        });
        gTestFInishVO.setTestFinishVoList(testFinishVoList);
        reportVO.setTestModuleVO(gTestFInishVO);
    }

    // 百分比处理
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

    // 百分比处理
    public Double dealRate2(double score) {
        if (score == 0) {
            return 0D;
        } else {
            String format = String.format("%.1f", score);
            return Func.toDouble(format);
        }
    }

    // 百分比处理
    public String dealRate3(double score) {
        String format = String.format("%.1f", score);
        return format;
    }

    /**
     * 测评概率六个维度添加
     *
     * @param list
     * @param dimensionId
     * @param score
     * @author hzl
     * @date 2022/8/16 10:40
     */
    private void addTestTotal(List<PersonalReport.OverviewData> list, Long dimensionId, String score) {
        // 学习状态
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
     * 字体颜色
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
