package org.mentpeak.test.service.impl;

import com.alibaba.fastjson.JSONObject;
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
import org.mentpeak.test.service.IReportTeacherClassService;
import org.mentpeak.test.service.IReportTeacherGradeService;
import org.mentpeak.test.strategy.scoring.ColorEnum;
import org.mentpeak.test.vo.*;
import org.mentpeak.user.entity.MenuData;
import org.springframework.beans.BeanUtils;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ????????????????????? ???????????????
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportTeacherClassServiceImpl extends BaseServiceImpl<ReportTeacherClassMapper, ReportTeacherClass> implements IReportTeacherClassService {

    private final TestPaperMapper testPaperMapper;

    private final TestTaskUserMapper taskUserMapper;

    private final MongoTemplate mongoTemplate;

    private final TestDimensionIndexConclusionMapper dimensionIndexConclusionMapper;

    private final TestIndexMapper testIndexMapper;

    private final TestTaskDepartmentMapper departmentMapper;

    private final ReportTeacherClassMapper reportTeacherClassMapper;

    private final MenuDataMapper menuDataMapper;

    private final IReportTeacherGradeService reportTeacherGradeService;

    @Override
    public IPage<ReportTeacherClassVO> selectReportTeacherClassPage(IPage<ReportTeacherClassVO> page, ReportTeacherClassVO reportTeacherClass) {
        return page.setRecords(baseMapper.selectReportTeacherClassPage(page, reportTeacherClass));
    }

    @Override
    public ClassReportVO getClassReport(Long taskId, Long gradeId, Long classId) {
        ClassReportVO vo = new ClassReportVO();
        // ??????????????????????????????????????????????????????????????????
        PlatformUser user = SecureUtil.getUser();
        if ("5".equals(user.getRoleId())) {
            Long count = reportTeacherClassMapper.selectCount(Wrappers.<ReportTeacherClass>lambdaQuery()
                    .eq(ReportTeacherClass::getTeacherId, user.getUserId())
                    .eq(ReportTeacherClass::getTaskId, taskId)
                    .eq(ReportTeacherClass::getGradeId, gradeId)
                    .eq(ReportTeacherClass::getClassId, classId)
                    .eq(ReportTeacherClass::getIsDeleted, 0));
            if (count == 0) {
                vo.setIsPermission(0);
                return vo;
            }
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("taskId").is(taskId));
        query.addCriteria(Criteria.where("classId").is(classId));
        query.addCriteria(Criteria.where("gradeId").is(gradeId));
        ClassReport classReport = mongoTemplate.findOne(query, ClassReport.class, "classReport");
        if (ObjectUtils.isNotEmpty(classReport)) {
            BeanUtil.copyProperties(classReport, vo);
        }
        return vo;
    }

    @Override
    public ClassReportVO addClassReport(Long taskId, Long gradeId, Long classId, String tenantCode) {
        ClassReportVO vo = new ClassReportVO();
        ClassReport report = new ClassReport();
        // ??????????????????
        ClassReportData reportData = new ClassReportData();
        reportData.setGradeId(gradeId);
        // ????????????????????????
        TestModuleVO testModuleVO = getTestPelple(taskId, gradeId, classId, reportData, vo, tenantCode);
        // ????????????????????????
        // ????????????
        List<Long> testPeople = testPaperMapper.getTestPeople(taskId, gradeId, classId, tenantCode);
        if (testPeople.size() == 0) {
            // ???????????????????????????????????????????????????????????????????????????
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            CompletableFuture.runAsync(() -> {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                CompletableFuture.runAsync(() -> updateGradeReport(gradeId, taskId, tenantCode));
            });
            return null;
        }
        // ????????????
        List<UserInfoVO> invalidList = taskUserMapper.getInvalidPeople(taskId, gradeId, classId,tenantCode);
        Query query = new Query();
        query.addCriteria(Criteria.where("gradeId").is(gradeId).and("classId").is(classId).and("taskId").is(taskId));
        if (testPeople.size() == invalidList.size()) {
            ClassReport byId = mongoTemplate.findById("classReport", ClassReport.class, "classReport");
            if (ObjectUtils.isEmpty(byId)) {
                byId = saveClass();
            }
            saveReportData(query, reportData);
            report.setTestModuleVO(testModuleVO);
            report.setTestModuleTwoVO(byId.getTestModuleTwoVO());
            report.setTestOverview(byId.getTestOverview());
            report.setDimensionList(byId.getDimensionList());
            report.setTaskId(taskId);
            report.setClassId(classId);
            report.setGradeId(gradeId);
            report.setTitle(vo.getTitle());
            report.setInvalidVO(vo.getInvalidVO());
            saveReport(query, report);

            log.info("=============??????==============");

            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            CompletableFuture.runAsync(() -> {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                CompletableFuture.runAsync(() -> updateGradeReport(gradeId, taskId, tenantCode));
            });
            return null;
        }
        // ??????????????????????????????
        TestModuleTwoVO testModuleTwoVO = getMentalHealthy(taskId, gradeId, classId, reportData, tenantCode);
        // ????????????????????????
        PersonalReport.TestOverview testOverview = getTestScore(taskId, gradeId, classId, reportData, tenantCode);
        // ????????????????????????
        List<DimensionReportVO> dimensionList = getStudyStatus(taskId, gradeId, classId, reportData,tenantCode);

        saveReportData(query, reportData);

        vo.setTestModuleVO(testModuleVO);
        vo.setTestModuleTwoVO(testModuleTwoVO);
        vo.setTestOverview(testOverview);
        vo.setDimensionList(dimensionList);

        report.setTestModuleVO(testModuleVO);
        report.setTestModuleTwoVO(testModuleTwoVO);
        report.setTestOverview(testOverview);
        report.setDimensionList(dimensionList);
        report.setTaskId(taskId);
        report.setClassId(classId);
        report.setGradeId(gradeId);
        report.setTitle(vo.getTitle());
        report.setInvalidVO(vo.getInvalidVO());
        saveReport(query, report);

        // ?????????????????????????????????
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            CompletableFuture.runAsync(() -> updateGradeReport(gradeId, taskId, tenantCode));
        });
        return vo;
    }

    public ClassReport saveClass() {
        log.info("===???????????????????????????===");
        String fileName = "classReport.json";
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
        ClassReport classReport = JSONObject.parseObject(jsonStr, ClassReport.class);
        classReport.setId("classReport");
        mongoTemplate.save(classReport, "classReport");
        return classReport;
    }

    // ????????????mongodb
    void saveReportData(Query query, ClassReportData reportData) {
        ClassReportData one = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
        if (ObjectUtils.isNotEmpty(one)) {
            // ??????
            mongoTemplate.remove(query, "classReportData");
            mongoTemplate.save(reportData, "classReportData");
        } else {
            // ??????
            mongoTemplate.save(reportData, "classReportData");
        }
    }

    void saveReport(Query query, ClassReport report) {
        ClassReport two = mongoTemplate.findOne(query, ClassReport.class, "classReport");
        if (ObjectUtils.isNotEmpty(two)) {
            // ??????????????????
            mongoTemplate.remove(query, "classReport");
            mongoTemplate.save(report, "classReport");
        } else {
            mongoTemplate.save(report, "classReport");
        }
    }

    void updateGradeReport(Long gradeId, Long taskId, String tenantCode) {
        reportTeacherGradeService.addGradeReport(taskId, gradeId, tenantCode);
        log.info("????????????????????????");
    }

    @Override
    public TestModuleVO getTestPelple(Long taskId, Long gradeId, Long classId, ClassReportData reportData, ClassReportVO reportVO, String tenantCode) {
        TestModuleVO vo = new TestModuleVO();
        List<TestFinishVo> list = new ArrayList<>();
        // ??????
        TestFinishVo studentVo = new TestFinishVo();
        studentVo.setEvaluationCrowd("??????");
        // ????????????
        String className = testPaperMapper.getClassName(classId);
        reportVO.setTitle(className + "??????????????????");
        studentVo.setClassName(className);
        // ????????????
        Long count = testPaperMapper.getTotalPeople(gradeId, classId, tenantCode);
        studentVo.setTotalPeople(Func.toInt(count));
        // ????????????
        List<Long> testPeople = testPaperMapper.getTestPeople(taskId, gradeId, classId, tenantCode);
        studentVo.setTestPeople(testPeople.size());
        // ????????????
        int noTestPeople = Func.toInt(count - testPeople.size());
        if (noTestPeople < 0) {
            noTestPeople = 0;
        }
        studentVo.setNoTestPeople(noTestPeople);
        // ????????????
        List<UserInfoVO> invalidList = taskUserMapper.getInvalidPeople(taskId, gradeId, classId,tenantCode);
        studentVo.setInvalidPeople(invalidList.size());
        // ???????????? = ?????? - ??????
        long effPeople = testPeople.size() - invalidList.size();
        // ????????? = ??????/??????
        double l = Func.toDouble(effPeople) / Func.toDouble(count) * 100;
        String completionRate = dealRate(l);
        studentVo.setCompletionRate(completionRate);
        // ??????
        TestFinishVo parentVo = new TestFinishVo();
        parentVo.setEvaluationCrowd("??????");
        parentVo.setClassName(className);
        // ?????????????????????0
        parentVo.setInvalidPeople(0);
        // ????????????
        Integer parentPeople = testPaperMapper.getParentPeople(gradeId, classId);
        parentVo.setTotalPeople(parentPeople);
        // ????????????
        List<Integer> testParent = testPaperMapper.getTestParent(gradeId, classId);
        parentVo.setTestPeople(testParent.size());
        // ????????????
        parentVo.setNoTestPeople(parentPeople - testParent.size());
        // ????????? = ??????/??????
        double v = 0;
        if (parentPeople == 0) {
            v = 0;
        } else {
            v = Func.toDouble(testParent.size()) / Func.toDouble(parentPeople);
        }
        String completion = dealRate(v);
        parentVo.setCompletionRate(completion);
        list.add(studentVo);
        list.add(parentVo);
        // ???????????????
        InvalidVO invalidVO = new InvalidVO();
        invalidVO.setInvalidPeople(invalidList.size());
        invalidVO.setVoList(invalidList);
        vo.setTestFinishVoList(list);
        reportVO.setInvalidVO(invalidVO);

        // ???mongodb
        if (ObjectUtils.isNotEmpty(reportData)) {
            reportData.setClassId(classId);
            reportData.setTaskId(taskId);
            reportData.setTotalPeople(Func.toInt(count));
            reportData.setTestPeople(testPeople.size());
            reportData.setNoTestPeople(Func.toInt(count - testPeople.size()));
            reportData.setInvalidPeople(invalidList.size());
            reportData.setCompletionRate(dealRate2(l));
            reportData.setInvalidVO(invalidVO);
        }
        return vo;
    }

    @Override
    public TestModuleTwoVO getMentalHealthy(Long taskId, Long gradeId, Long classId, ClassReportData reportData, String tenantCode) {
        TestModuleTwoVO vo = new TestModuleTwoVO();

        List<FollowWarn> list = taskUserMapper.getFollowWarn(taskId, gradeId, classId);
        // ????????????
        List<Long> testPeople = testPaperMapper.getTestPeople(taskId, gradeId, classId, tenantCode);
        // ????????????
        List<UserInfoVO> invalidList = taskUserMapper.getInvalidPeople(taskId, gradeId, classId,tenantCode);
        // ???????????? = ?????? - ??????
        long effPeople = testPeople.size() - invalidList.size();
        // ??????????????????-????????????????????????
        Map<Integer, List<FollowWarn>> followMap = list.stream().collect(Collectors.groupingByConcurrent(x -> Optional.ofNullable(x.getFollowLevel()).orElse(-1)));
        List<FollowLevelVO> followLevelVOList = new ArrayList<>();
        FollowLevelVO followLevelVO1 = new FollowLevelVO();
        FollowLevelVO followLevelVO2 = new FollowLevelVO();
        FollowLevelVO followLevelVO3 = new FollowLevelVO();
        FollowLevelVO followLevelVO4 = new FollowLevelVO();
        followLevelVO1.setFollowName("1?????????");
        followLevelVO2.setFollowName("2?????????");
        followLevelVO3.setFollowName("3?????????");
        followLevelVO4.setFollowName("??????");
        // ????????????
        AtomicReference<List<UserInfoVO>> follow1 = new AtomicReference<>();
        AtomicReference<List<UserInfoVO>> follow2 = new AtomicReference<>();
        AtomicReference<List<UserInfoVO>> follow3 = new AtomicReference<>();
        // ??????????????????
        followMap.forEach((integer, followWarns) -> {
            int people = followWarns.size();
            // ??????
            double v = 0;
            if (effPeople != 0) {
                v = Func.toDouble(people) / Func.toDouble(effPeople) * 100;
            }
            String completionRate = dealRate(v);
            if (integer == 0) {
                followLevelVO4.setPeople(people);
                followLevelVO4.setClassRatio(completionRate);
                reportData.setFollowPeople(people);
                reportData.setFollowRate(v);
            } else if (integer == 1) {
                followLevelVO1.setPeople(people);
                followLevelVO1.setClassRatio(completionRate);
                reportData.setFollowPeopleOne(people);
                reportData.setFollowRateOne(v);
                follow1.set(BeanUtil.copyProperties(followWarns, UserInfoVO.class));
            } else if (integer == 2) {
                followLevelVO2.setPeople(people);
                followLevelVO2.setClassRatio(completionRate);
                follow2.set(BeanUtil.copyProperties(followWarns, UserInfoVO.class));
                reportData.setFollowPeopleTwo(people);
                reportData.setFollowRateTwo(v);
            } else if (integer == 3) {
                followLevelVO3.setPeople(people);
                followLevelVO3.setClassRatio(completionRate);
                follow3.set(BeanUtil.copyProperties(followWarns, UserInfoVO.class));
                reportData.setFollowPeopleThree(people);
                reportData.setFollowRateThree(v);
            }

        });
        reportData.setUserInfoOneList(follow1.get());
        reportData.setUserInfoTwoList(follow2.get());
        reportData.setUserInfoThreeList(follow3.get());
        followLevelVOList.add(followLevelVO1);
        followLevelVOList.add(followLevelVO2);
        followLevelVOList.add(followLevelVO3);
        followLevelVOList.add(followLevelVO4);
        // ????????????????????????
        List<FollowUserVO> followUserVOList = getFollowUser(follow1.get(), follow2.get(), follow3.get());

        List<TestModuleTwoVO.FollowUser> followUserList = new ArrayList<>();
        TestModuleTwoVO.FollowUser followStudent = new TestModuleTwoVO.FollowUser();
        followStudent.setVoThreeList(follow3.get());
        followStudent.setVoTwoList(follow2.get());
        followStudent.setVoOneList(follow1.get());
        followUserList.add(followStudent);

        vo.setAttentionLevelTable(followLevelVOList);
        vo.setAttentionStudent(followUserList);
        List<GTestModuleTwoVO.TypeData> attentionLevel = new ArrayList<>();
        // ????????????????????????
        GTestModuleTwoVO.TypeData typeData1 = addChartData(reportData, 1);
        // ??????????????????
        // ????????????????????????
        Map<Integer, List<FollowWarn>> studentMap = list.stream().collect(Collectors.groupingByConcurrent(x -> Optional.ofNullable(x.getStudentComments()).orElse(-1)));
        // ??????????????????
        studentMap.forEach((integer, followWarns) -> {
            int people = followWarns.size();
            // ??????
            double v = 0;
            if (effPeople != 0) {
                v = Func.toDouble(people) / Func.toDouble(effPeople) * 100;
            }
            if (integer == 0) {
                reportData.setStudentRate(v);
            } else if (integer == 1) {
                reportData.setStudentRateOne(v);
            } else if (integer == 2) {
                reportData.setStudentRateTwo(v);
            } else if (integer == 3) {
                reportData.setStudentRateThree(v);
            }
        });
        // ????????????????????????
        Map<Integer, List<FollowWarn>> teacherMap = list.stream().collect(Collectors.groupingByConcurrent(x -> Optional.ofNullable(x.getTeacherComments()).orElse(-1)));
        // ??????????????????
        teacherMap.forEach((integer, followWarns) -> {
            int people = followWarns.size();
            // ??????
            double v = 0;
            if (effPeople != 0) {
                v = Func.toDouble(people) / Func.toDouble(effPeople) * 100;
            }
            if (integer == 0) {
                reportData.setTeacherRate(v);
            } else if (integer == 1) {
                reportData.setTeacherRateOne(v);
            } else if (integer == 2) {
                reportData.setTeacherRateTwo(v);
            } else if (integer == 3) {
                reportData.setTeacherRateThree(v);
            }
        });
        // ????????????????????????
        Map<Integer, List<FollowWarn>> parentMap = list.stream().collect(Collectors.groupingByConcurrent(x -> Optional.ofNullable(x.getParentComments()).orElse(-1)));
        // ??????????????????
        parentMap.forEach((integer, followWarns) -> {
            int people = followWarns.size();
            // ??????
            double v = 0;
            if (effPeople != 0) {
                v = Func.toDouble(people) / Func.toDouble(effPeople) * 100;
            }
            if (integer == 0) {
                reportData.setParentRate(v);
            } else if (integer == 1) {
                reportData.setParentRateOne(v);
            }
        });
        GTestModuleTwoVO.TypeData typeData2 = addChartData(reportData, 2);
        // ??????????????????
        GTestModuleTwoVO.TypeData typeData3 = addChartData(reportData, 3);
        // ??????????????????
        GTestModuleTwoVO.TypeData typeData4 = addChartData(reportData, 4);
        attentionLevel.add(typeData1);
        attentionLevel.add(typeData2);
        attentionLevel.add(typeData3);
        attentionLevel.add(typeData4);
        vo.setAttentionLevel(attentionLevel);

        // ????????????????????????-????????????????????????
        Map<Integer, List<FollowWarn>> warnMap = list.stream().collect(Collectors.groupingByConcurrent(x -> Optional.ofNullable(x.getIsWarn()).orElse(-1)));
        List<FollowLevelVO> warnLevelVOList = new ArrayList<>();
        FollowLevelVO warnLevelVO1 = new FollowLevelVO();
        FollowLevelVO warnLevelVO2 = new FollowLevelVO();
        FollowLevelVO warnLevelVO3 = new FollowLevelVO();
        FollowLevelVO warnLevelVO4 = new FollowLevelVO();
        warnLevelVO1.setFollowName("1?????????");
        warnLevelVO2.setFollowName("2?????????");
        warnLevelVO3.setFollowName("3?????????");
        warnLevelVO4.setFollowName("?????????");
        // ????????????
        AtomicReference<List<UserInfoVO>> warn1 = new AtomicReference<>();
        AtomicReference<List<UserInfoVO>> warn2 = new AtomicReference<>();
        AtomicReference<List<UserInfoVO>> warn3 = new AtomicReference<>();
        // ??????????????????
        warnMap.forEach((integer, followWarns) -> {
            int people = followWarns.size();
            // ??????
            double v = 0;
            if (effPeople != 0) {
                v = Func.toDouble(people) / Func.toDouble(effPeople) * 100;
            }
            String completionRate = dealRate(v);

            if (integer == 0) {
                warnLevelVO4.setPeople(people);
                warnLevelVO4.setClassRatio(completionRate);
                reportData.setWarnPeople(people);
                reportData.setWarnRate(v);
            } else if (integer == 1) {
                warnLevelVO1.setPeople(people);
                warnLevelVO1.setClassRatio(completionRate);
                warn1.set(BeanUtil.copyProperties(followWarns, UserInfoVO.class));
                reportData.setWarnPeopleOne(people);
                reportData.setWarnRateOne(v);
            } else if (integer == 2) {
                warnLevelVO2.setPeople(people);
                warnLevelVO2.setClassRatio(completionRate);
                warn2.set(BeanUtil.copyProperties(followWarns, UserInfoVO.class));
                reportData.setWarnPeopleTwo(people);
                reportData.setWarnRateTwo(v);
            } else if (integer == 3) {
                warnLevelVO3.setPeople(people);
                warnLevelVO3.setClassRatio(completionRate);
                warn3.set(BeanUtil.copyProperties(followWarns, UserInfoVO.class));
                reportData.setWarnPeopleThree(people);
                reportData.setWarnRateThree(v);
            }

        });
        reportData.setUserInfoWarnOneList(warn1.get());
        reportData.setUserInfoWranTwoList(warn2.get());
        reportData.setUserInfoWranThreeList(warn3.get());
        warnLevelVOList.add(warnLevelVO1);
        warnLevelVOList.add(warnLevelVO2);
        warnLevelVOList.add(warnLevelVO3);
        warnLevelVOList.add(warnLevelVO4);

        // ????????????
        List<GTestModuleTwoVO.TypeData> warnAttentionLevel = new ArrayList<>();
        GTestModuleTwoVO.TypeData typeData5 = addChartData(reportData, 5);
        warnAttentionLevel.add(typeData5);
        vo.setWarningLevel(warnAttentionLevel);
        // ????????????????????????
        List<FollowUserVO> warnUserVOList = getFollowUser(warn1.get(), warn2.get(), warn3.get());
        List<TestModuleTwoVO.FollowUser> warnUserList = new ArrayList<>();
        TestModuleTwoVO.FollowUser warningStudent = new TestModuleTwoVO.FollowUser();
        warningStudent.setVoThreeList(warn3.get());
        warningStudent.setVoTwoList(warn2.get());
        warningStudent.setVoOneList(warn1.get());
        warnUserList.add(warningStudent);
        vo.setWarningLevelTable(warnLevelVOList);
        vo.setWarningStudent(warnUserList);

        // ???mongodb
        reportData.setFollowUserList(followUserVOList);
        reportData.setWarnUserList(warnUserVOList);
        return vo;
    }

    @Override
    public PersonalReport.TestOverview getTestScore(Long taskId, Long gradeId, Long classId, ClassReportData reportData, String tenantCode) {
        // ????????????id??????????????????????????????????????????id
        List<String> paperIdList = testPaperMapper.getPaperIdByCId(taskId, gradeId, classId, tenantCode);
        Criteria criteria = Criteria.where("paperId").in(paperIdList);
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
        AggregationResults<PersonalReportData2> result = mongoTemplate.aggregate(agg, "personalReportData", PersonalReportData2.class);
        PersonalReportData2 mappedResults = result.getMappedResults().get(0);
        // ???????????????????????????????????????mongodb???
        BeanUtils.copyProperties(mappedResults, reportData);
        Query query = new Query();
        query.addCriteria(Criteria.where("gradeId").is(gradeId).and("classId").is(classId).and("taskId").is(taskId));
        ClassReportData one = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
        if (ObjectUtils.isNotEmpty(one)) {
            mongoTemplate.remove(query, "classReportData");
            mongoTemplate.save(reportData, "classReportData");
        } else {
            // ??????
            mongoTemplate.save(reportData, "classReportData");
        }
        // ????????????
        PersonalReport.TestOverview testTotalScore = getTestTotalScore(mappedResults);
        return testTotalScore;

    }

    @Override
    public List<DimensionReportVO> getStudyStatus(Long taskId, Long gradeId, Long classId, ClassReportData classReportData,String tenantCode) {
        List<DimensionReportVO> voList = new ArrayList<>();
        DimensionReportVO vo = new DimensionReportVO();
        // ????????????id,????????????id,??????????????????????????????id
        // ????????????id,??????????????????????????????id
        Criteria criteria = Criteria.where("gradeId").is(gradeId).and("taskId").is(taskId);
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
        AggregationResults<PersonalReportData2> result = mongoTemplate.aggregate(agg, "classReportData", PersonalReportData2.class);
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
        String className = testPaperMapper.getClassName(classId);
        totalResultVO1.setTitle(className);
        totalResultVO1.setFontColor(ColorEnum.BLUE.getValue());
        totalResultVO1.setScore(dealRate2(classReportData.getStudyStatusScore()).toString());
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
        TargetResultVO targetResultVO = addTargetResult(targetList, classReportData, report, vo, className);
        addTitleName(targetResultVO, 1, className);

        // ?????????
        // ??????????????????????????????-????????????
        List<String> paperIdList = testPaperMapper.getAllClassPaperId(taskId, gradeId, classId,tenantCode);
        List<PersonalReportData> peosonReportList = mongoTemplate.find(new Query().addCriteria(Criteria.where("paperId").in(paperIdList)), PersonalReportData.class);
        // ??????????????????????????????-????????????
        List<String> gradePaperIdList = testPaperMapper.getAllGradePaperId(taskId, gradeId, classId,tenantCode);
        List<PersonalReportData> gradePeosonReportList = mongoTemplate.find(new Query().addCriteria(Criteria.where("paperId").in(gradePaperIdList)), PersonalReportData.class);
        // ???????????????
        PersonalReportData2 classMap = classDiffent(peosonReportList, classReportData);
        // ???????????????
        ClassReportData classReportData1 = BeanUtil.copy(report, ClassReportData.class);
        PersonalReportData2 gradeMap = classDiffent(gradePeosonReportList, classReportData1);
        // ??????????????? == ????????????????????????????????????
//        PersonalReportData2 gradeMap = gradeDiffent(classIdList, taskId, gradeId);

        // ?????????????????????
        Map<String, String> map1 = diffResult(classReportData.getStudyStatusScore(), report.getStudyStatusScore(), classMap.getStudyStatusScore(), gradeMap.getStudyStatusScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
        // ???????????????????????????
        addTargetCompare("????????????", targetList, className, map1, classReportData, report, classMap, gradeMap, vo);


        // ????????????????????????id
        List<Long> userIdList = taskUserMapper.getEffPeople(taskId, gradeId, classId);

        // ????????????????????????id
        List<Long> userIdList2 = taskUserMapper.getAllPeople(taskId, gradeId, classId);
        String learningAttitudeSql = "learningStatus.chartData.riskIndex";
        String targetSql = "learningStatus.chartData.title";
        String unwind = "learningStatus.chartData";
        // ??????????????????
        List<DimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();
        // ??????????????????
        List<DimensionReportVO.TypicalStudentName> typicalNameList = new ArrayList<>();
        targetList.stream().forEach(s -> {
            typicalStudent(userIdList, userIdList2, learningAttitudeSql, targetSql, unwind, typicalStudentList, typicalNameList, s, classReportData);
        });

        vo.setTypicalStudentList(typicalStudentList);
        vo.setTypicalNameList(typicalNameList);
        voList.add(vo);
        // ????????????
        DimensionReportVO behaviorVo = behavior(className, classReportData, report, classMap, gradeMap, userIdList, userIdList2);
        DimensionReportVO mentalToughnessVo = mentalToughness(className, classReportData, report, classMap, gradeMap, userIdList, userIdList2);
        DimensionReportVO overallStressVo = overallStress(className, classReportData, report, classMap, gradeMap, userIdList, userIdList2);
        DimensionReportVO emotionalIndexVo = emotionalIndex(className, classReportData, report, classMap, gradeMap, userIdList, userIdList2);
        DimensionReportVO sleepIndexVo = sleepIndex(className, classReportData, report, classMap, gradeMap, userIdList, userIdList2);
        voList.add(behaviorVo);
        voList.add(mentalToughnessVo);
        voList.add(overallStressVo);
        voList.add(emotionalIndexVo);
        voList.add(sleepIndexVo);

        return voList;
    }

    @Override
    public IPage<ReportClassListVO> getReportList(IPage<ReportClassListVO> page, Long taskId, Long gradeId, Long classId) {
        PlatformUser user = SecureUtil.getUser();
        IPage<ReportClassListVO> selectPage = null;
        if ("5".equals(user.getRoleId())) {
            // ????????????id,??????id?????????????????????????????????id
            IPage<ReportTeacherClass> page1 = new Page<>();
            page1.setCurrent(page.getCurrent());
            page1.setSize(page.getSize());
            IPage<ReportTeacherClass> list = reportTeacherClassMapper.selectPage(page1, Wrappers.<ReportTeacherClass>lambdaQuery()
                    .eq(ReportTeacherClass::getTaskId, taskId)
                    .eq(ReportTeacherClass::getTeacherId, user.getUserId())
                    .eq(Func.isNotEmpty(gradeId), ReportTeacherClass::getGradeId, gradeId)
                    .eq(Func.isNotEmpty(classId), ReportTeacherClass::getClassId, classId)
                    .eq(ReportTeacherClass::getIsDeleted, 0));
            List<ReportTeacherClass> records = list.getRecords();

            List<ReportClassListVO> result = new ArrayList<>();
            records.stream().forEach(reportTeacherClass -> {
                List<ReportClassListVO> list2 = departmentMapper.getClassList2(reportTeacherClass.getTaskId(), reportTeacherClass.getGradeId(), reportTeacherClass.getClassId());
                result.addAll(list2);
            });
            selectPage = new Page<>(list.getCurrent(), list.getSize(), list.getTotal());
            selectPage.setRecords(result);
        } else {
            // ???????????????????????????????????????????????????
            selectPage = departmentMapper.getClassList(page, taskId, gradeId, classId);
        }

        List<ReportClassListVO> records = selectPage.getRecords();
        records.stream().forEach(reportListVO -> {
            // ????????????id?????????id??????mongodb
            reportListVO.setTaskId(taskId);
            Query query = new Query();
            query.addCriteria(Criteria.where("classId").is(reportListVO.getClassId()).and("taskId").is(taskId).and("gradeId").is(reportListVO.getGradeId()));
            ClassReportData one = mongoTemplate.findOne(query, ClassReportData.class, "classReportData");
            if (ObjectUtils.isNotEmpty(one)) {
                reportListVO.setTestPeople(one.getTestPeople());
                reportListVO.setTotalPeople(one.getTotalPeople());
                reportListVO.setIsReport(1);
            }
            if ("5".equals(user.getRoleId())) {
                // ????????????id,??????id?????????????????????????????????id
                Long count = reportTeacherClassMapper.selectCount(Wrappers.<ReportTeacherClass>lambdaQuery()
                        .eq(ReportTeacherClass::getTaskId, taskId)
                        .eq(ReportTeacherClass::getGradeId, reportListVO.getGradeId())
                        .eq(ReportTeacherClass::getClassId, reportListVO.getClassId())
                        .eq(ReportTeacherClass::getTeacherId, user.getUserId())
                        .eq(ReportTeacherClass::getIsDeleted, 0));
                if (count == 0) {
                    reportListVO.setIsPermission(0);
                }
            }
        });
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
        Long count = reportTeacherClassMapper.selectCount(Wrappers.<ReportTeacherClass>lambdaQuery().eq(ReportTeacherClass::getClassId, dto.getGradeId())
                .eq(ReportTeacherClass::getTaskId, dto.getTaskId())
                .eq(ReportTeacherClass::getTeacherId, userByPhone.getUserId()));
        if (count > 0) {
            throw new PlatformApiException("??????????????????");
        }
        ReportTeacherClass grade = new ReportTeacherClass();
        grade.setGradeId(dto.getGradeId());
        grade.setClassId(dto.getClassId());
        grade.setTeacherId(userByPhone.getUserId());
        grade.setTenantCode(SecureUtil.getTenantCode());
        grade.setTaskId(dto.getTaskId());
        grade.setCreateTime(LocalDateTime.now());
        grade.setCreateUser(SecureUtil.getUserId());
        grade.setRoleName(dto.getRoleName());
        reportTeacherClassMapper.insert(grade);

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
    public List<BindTeacherVO> bindTeacherList(Long taskId, Long gradeId, Long classId) {
        return reportTeacherClassMapper.getBindTeacherList(taskId, gradeId, classId);
    }

    @Override
    public boolean removeBindTeacher(Long userId, Long taskId, Long gradeId, Long classId) {
        int delete = reportTeacherClassMapper.delete(Wrappers.<ReportTeacherClass>lambdaQuery().eq(ReportTeacherClass::getTeacherId, userId)
                .eq(ReportTeacherClass::getTaskId, taskId)
                .eq(ReportTeacherClass::getGradeId, gradeId)
                .eq(ReportTeacherClass::getClassId, classId)
                .eq(ReportTeacherClass::getIsDeleted, 0));
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
            chart2.setChartColor(ColorEnum.TWOCOLOR.getValue());
            chart3.setChartColor(ColorEnum.ONECOLOR.getValue());
            chart4.setChartColor(ColorEnum.ZEROCOLOR.getValue());
            chart2.setName("2?????????");
            chart2.setValue(Func.toDouble(dealRate2(report.getFollowRateTwo())));
            chart3.setName("1?????????");
            chart3.setValue(Func.toDouble(dealRate2(report.getFollowRateOne())));
            chart4.setName("??????");
            chart4.setValue(Func.toDouble(dealRate2(report.getFollowRate())));
        } else if (type == 2) {
            typeData.setTitle("??????????????????");
            chart1.setName("3?????????");
            chart1.setValue(Func.toDouble(dealRate2(report.getStudentRateThree())));
            chart2.setName("2?????????");
            chart2.setValue(Func.toDouble(dealRate2(report.getStudentRateTwo())));
            chart3.setName("1?????????");
            chart3.setValue(Func.toDouble(dealRate2(report.getStudentRateOne())));
            chart4.setName("??????");
            chart4.setValue(Func.toDouble(dealRate2(report.getStudentRate())));
            chart1.setChartColor(ColorEnum.THREECOLOR.getValue());
            chart2.setChartColor(ColorEnum.TWOCOLOR.getValue());
            chart3.setChartColor(ColorEnum.ONECOLOR.getValue());
            chart4.setChartColor(ColorEnum.ZEROCOLOR.getValue());
        } else if (type == 3) {
            typeData.setTitle("??????????????????");
            chart1.setName("3?????????");
            chart1.setValue(Func.toDouble(dealRate2(report.getTeacherRateThree())));
            chart2.setName("2?????????");
            chart2.setValue(Func.toDouble(dealRate2(report.getTeacherRateTwo())));
            chart3.setName("1?????????");
            chart3.setValue(Func.toDouble(dealRate2(report.getTeacherRateOne())));
            chart4.setName("??????");
            chart4.setValue(Func.toDouble(dealRate2(report.getTeacherRate())));
            chart1.setChartColor(ColorEnum.THREECOLOR.getValue());
            chart2.setChartColor(ColorEnum.TWOCOLOR.getValue());
            chart3.setChartColor(ColorEnum.ONECOLOR.getValue());
            chart4.setChartColor(ColorEnum.ZEROCOLOR.getValue());
        } else if (type == 4) {
            typeData.setTitle("??????????????????");
            chart3.setName("1?????????");
            chart3.setValue(Func.toDouble(dealRate2(report.getParentRateOne())));
            chart4.setName("??????");
            chart4.setValue(Func.toDouble(dealRate2(report.getParentRate())));
            chart3.setChartColor(ColorEnum.ONECOLOR.getValue());
            chart4.setChartColor(ColorEnum.ZEROCOLOR.getValue());
        } else if (type == 5) {
            typeData.setTitle("");
            chart1.setName("3?????????");
            chart1.setValue(Func.toDouble(dealRate2(report.getWarnRateThree())));
            chart2.setName("2?????????");
            chart2.setValue(Func.toDouble(dealRate2(report.getWarnRateTwo())));
            chart3.setName("1?????????");
            chart3.setValue(Func.toDouble(dealRate2(report.getWarnRateOne())));
            chart4.setName("?????????");
            chart4.setValue(Func.toDouble(dealRate2(report.getWarnRate())));
            chart1.setChartColor(ColorEnum.THREECOLOR.getValue());
            chart2.setChartColor(ColorEnum.TWOCOLOR.getValue());
            chart3.setChartColor(ColorEnum.ONECOLOR.getValue());
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
    public DimensionReportVO behavior(String className, ClassReportData classReportData, PersonalReportData2 report, PersonalReportData2 classMap, PersonalReportData2 gradeMap, List<Long> userIdList, List<Long> userIdList2) {
        DimensionReportVO vo = new DimensionReportVO();
        // ????????????
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        totalResultVO1.setTitle(className);
        totalResultVO1.setFontColor(ColorEnum.BLUE.getValue());
        totalResultVO1.setScore(dealRate2(classReportData.getBehaviorScore()).toString());
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
        TargetResultVO targetResultVO = addTargetResult(targetList, classReportData, report, vo, className);
        addTitleName(targetResultVO, 3, className);
        // ?????????????????????
        Map<String, String> map1 = diffResult(classReportData.getBehaviorScore(), report.getBehaviorScore(), classMap.getBehaviorScore(), gradeMap.getBehaviorScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
        addTargetCompare("????????????", targetList, className, map1, classReportData, report, classMap, gradeMap, vo);

        String sql = "behavior.chartData.riskIndex";
        String targetSql = "behavior.chartData.title";
        String unwind = "behavior.chartData";
        // ??????????????????
        List<DimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();
        // ??????????????????
        List<DimensionReportVO.TypicalStudentName> typicalNameList = new ArrayList<>();
        targetList.stream().forEach(s -> {
            typicalStudent(userIdList, userIdList2, sql, targetSql, unwind, typicalStudentList, typicalNameList, s, classReportData);
        });

        vo.setTypicalStudentList(typicalStudentList);
        vo.setTypicalNameList(typicalNameList);
        return vo;
    }

    // ????????????
    public DimensionReportVO mentalToughness(String className, ClassReportData classReportData, PersonalReportData2 report, PersonalReportData2 classMap, PersonalReportData2 gradeMap, List<Long> userIdList, List<Long> userIdList2) {
        DimensionReportVO vo = new DimensionReportVO();
        // ????????????
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        totalResultVO1.setTitle(className);
        totalResultVO1.setFontColor(ColorEnum.BLUE.getValue());
        totalResultVO1.setScore(dealRate2(classReportData.getMentalToughnessScore()).toString());
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
        TargetResultVO targetResultVO = addTargetResult(targetList, classReportData, report, vo, className);
        addTitleName(targetResultVO, 3, className);

        // ?????????????????????
        Map<String, String> map1 = diffResult(classReportData.getMentalToughnessScore(), report.getMentalToughnessScore(), classMap.getMentalToughnessScore(), gradeMap.getMentalToughnessScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
        addTargetCompare("????????????", targetList, className, map1, classReportData, report, classMap, gradeMap, vo);

        String sql = "mentalToughness.chartData.riskIndex";
        String targetSql = "mentalToughness.chartData.title";
        String unwind = "mentalToughness.chartData";
        // ??????????????????
        List<DimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();
        // ??????????????????
        List<DimensionReportVO.TypicalStudentName> typicalNameList = new ArrayList<>();
        targetList.stream().forEach(s -> {
            typicalStudent(userIdList, userIdList2, sql, targetSql, unwind, typicalStudentList, typicalNameList, s, classReportData);
        });

        vo.setTypicalStudentList(typicalStudentList);
        vo.setTypicalNameList(typicalNameList);
        return vo;
    }

    // ????????????
    public DimensionReportVO overallStress(String className, ClassReportData classReportData, PersonalReportData2 report, PersonalReportData2 classMap, PersonalReportData2 gradeMap, List<Long> userIdList, List<Long> userIdList2) {
        DimensionReportVO vo = new DimensionReportVO();
        // ????????????
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        totalResultVO1.setTitle(className);
        totalResultVO1.setFontColor(ColorEnum.ORANGE.getValue());
        totalResultVO1.setScore(dealRate2(classReportData.getOverallStressScore()).toString());
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
        TargetResultVO targetResultVO = addTargetResult(targetList, classReportData, report, vo, className);
        addTitleName(targetResultVO, 2, className);

        // ?????????????????????
        Map<String, String> map1 = diffResult(classReportData.getOverallStressScore(), report.getOverallStressScore(), classMap.getOverallStressScore(), gradeMap.getOverallStressScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
        addTargetCompare("????????????", targetList, className, map1, classReportData, report, classMap, gradeMap, vo);

        String sql = "stressIndex.chartData.riskIndex";
        String targetSql = "stressIndex.chartData.title";
        String unwind = "stressIndex.chartData";
        // ??????????????????
        List<DimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();
        // ??????????????????
        List<DimensionReportVO.TypicalStudentName> typicalNameList = new ArrayList<>();
        targetList.stream().forEach(s -> {
            typicalStudent(userIdList, userIdList2, sql, targetSql, unwind, typicalStudentList, typicalNameList, s, classReportData);
        });

        vo.setTypicalStudentList(typicalStudentList);
        vo.setTypicalNameList(typicalNameList);
        return vo;
    }

    // ????????????
    public DimensionReportVO emotionalIndex(String className, ClassReportData classReportData, PersonalReportData2 report, PersonalReportData2 classMap, PersonalReportData2 gradeMap, List<Long> userIdList, List<Long> userIdList2) {
        DimensionReportVO vo = new DimensionReportVO();
        // ????????????
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        totalResultVO1.setTitle(className);
        totalResultVO1.setFontColor(ColorEnum.ORANGE.getValue());
        totalResultVO1.setScore(dealRate2(classReportData.getEmotionalIndexScore()).toString());
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
        TargetResultVO targetResultVO = addTargetResult(targetList, classReportData, report, vo, className);
        addTitleName(targetResultVO, 2, className);

        // ?????????????????????
        Map<String, String> map1 = diffResult(classReportData.getEmotionalIndexScore(), report.getEmotionalIndexScore(), classMap.getEmotionalIndexScore(), gradeMap.getEmotionalIndexScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
        addTargetCompare("????????????", targetList, className, map1, classReportData, report, classMap, gradeMap, vo);

        String sql = "emotionalIndex.chartData.riskIndex";
        String targetSql = "emotionalIndex.chartData.title";
        String unwind = "emotionalIndex.chartData";
        // ??????????????????
        List<DimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();
        // ??????????????????
        List<DimensionReportVO.TypicalStudentName> typicalNameList = new ArrayList<>();
        targetList.stream().forEach(s -> {
            typicalStudent(userIdList, userIdList2, sql, targetSql, unwind, typicalStudentList, typicalNameList, s, classReportData);
        });

        vo.setTypicalStudentList(typicalStudentList);
        vo.setTypicalNameList(typicalNameList);
        return vo;
    }

    // ????????????
    public DimensionReportVO sleepIndex(String className, ClassReportData classReportData, PersonalReportData2 report, PersonalReportData2 classMap, PersonalReportData2 gradeMap, List<Long> userIdList, List<Long> userIdList2) {
        DimensionReportVO vo = new DimensionReportVO();
        // ????????????
        List<TotalResultVO> totalResultVOList = new ArrayList<>();
        TotalResultVO totalResultVO1 = new TotalResultVO();
        totalResultVO1.setTitle(className);
        totalResultVO1.setFontColor(ColorEnum.ORANGE.getValue());
        totalResultVO1.setScore(dealRate2(classReportData.getSleepIndexScore()).toString());
        TotalResultVO totalResultVO2 = new TotalResultVO();
        totalResultVO2.setTitle("????????????");
        totalResultVO2.setFontColor(ColorEnum.ORANGE2.getValue());
        totalResultVO2.setScore(dealRate2(report.getSleepIndexScore()).toString());
        totalResultVOList.add(totalResultVO1);
        totalResultVOList.add(totalResultVO2);
        vo.setTotalResultVOList(totalResultVOList);

        // ?????????????????????
        Map<String, String> map1 = diffResult(classReportData.getSleepIndexScore(), report.getSleepIndexScore(), classMap.getSleepIndexScore(), gradeMap.getSleepIndexScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
        DimensionReportVO.DiffCheck diffCheck = new DimensionReportVO.DiffCheck();
        String diff = map1.get("result");
        if ("1".equals(diff)) {
            diffCheck.setDiffent(map1.get("explain"));
            diffCheck.setExplanation("????????????" + "????????????" + "???????????????????????????????????????");
        } else if ("2".equals(diff)) {
            diffCheck.setDiffent(map1.get("explain"));
            diffCheck.setExplanation("????????????" + "????????????" + "???????????????????????????????????????");
        } else {
            diffCheck.setDiffent(map1.get("explain"));
            diffCheck.setExplanation("????????????" + "????????????" + "???????????????????????????????????????");
        }
        vo.setDiffCheck(diffCheck);

        String sql = "sleepIndex.totalResult.riskIndex";
        Criteria criteria = Criteria.where("userInfo.userId").in(userIdList);
        Criteria criteria1 = Criteria.where(sql).is(0);
        Criteria criteria2 = Criteria.where(sql).is(1);
        Criteria criteria3 = Criteria.where(sql).is(2);
        Criteria criteria4 = Criteria.where(sql).is(3);
        TypedAggregation<UserInfoVO> agg = Aggregation.newAggregation(UserInfoVO.class,
                Aggregation.match(criteria),
                Aggregation.match(criteria1),
                Aggregation.project("userInfo.userId", "userInfo.name").and("userInfo.name").as("userName"));
        AggregationResults<UserInfoVO> goodReport = mongoTemplate.aggregate(agg, "personalReport", UserInfoVO.class);

        TypedAggregation<UserInfoVO> agg2 = Aggregation.newAggregation(UserInfoVO.class,
                Aggregation.match(criteria),
                Aggregation.match(criteria2),
                Aggregation.project("userInfo.userId", "userInfo.name").and("userInfo.name").as("userName"));
        AggregationResults<UserInfoVO> generallyReport = mongoTemplate.aggregate(agg2, "personalReport", UserInfoVO.class);

        TypedAggregation<UserInfoVO> agg3 = Aggregation.newAggregation(UserInfoVO.class,
                Aggregation.match(criteria),
                Aggregation.match(criteria3),
                Aggregation.project("userInfo.userId", "userInfo.name").and("userInfo.name").as("userName"));
        AggregationResults<UserInfoVO> badReport = mongoTemplate.aggregate(agg3, "personalReport", UserInfoVO.class);

        TypedAggregation<UserInfoVO> agg4 = Aggregation.newAggregation(UserInfoVO.class,
                Aggregation.match(criteria),
                Aggregation.match(criteria4),
                Aggregation.project("userInfo.userId", "userInfo.name").and("userInfo.name").as("userName"));
        AggregationResults<UserInfoVO> veryBadReport = mongoTemplate.aggregate(agg4, "personalReport", UserInfoVO.class);
        // ??????
        List<UserInfoVO> goodUserInfo = goodReport.getMappedResults();
        // ??????
        List<UserInfoVO> generallyUserInfo = generallyReport.getMappedResults();
        // ??????
        List<UserInfoVO> badUserInfo = badReport.getMappedResults();
        // ??????
        List<UserInfoVO> veryBadUserInfo = veryBadReport.getMappedResults();

        // ??????????????????
        classReportData.setSleepGood(goodUserInfo.size());
        classReportData.setSleepGenerally(generallyUserInfo.size());
        classReportData.setSleepBad(badUserInfo.size());
        classReportData.setSleepVeryBad(veryBadUserInfo.size());
        // ??????????????????
        List<DimensionReportVO.TypicalStudent> typicalStudentList = new ArrayList<>();


        double v1 = calculatePercentage(userIdList.size(), veryBadUserInfo.size());

        double v2 = calculatePercentage(userIdList.size(), badUserInfo.size());

        double v3 = calculatePercentage(userIdList.size(), generallyUserInfo.size());

        double v4 = calculatePercentage(userIdList.size(), goodUserInfo.size());

        DimensionReportVO.TypicalStudent typicalStudent1 = new DimensionReportVO.TypicalStudent();
        typicalStudent1.setTitle("????????????");
        typicalStudent1.setVeryBad(veryBadUserInfo.size() + "???(" + v1 + "%)");
        typicalStudent1.setBad(badUserInfo.size() + "???(" + v2 + "%)");
        typicalStudent1.setGenerally(generallyUserInfo.size() + "???(" + v3 + "%)");
        typicalStudent1.setGood(goodUserInfo.size() + "???(" + v4 + "%)");
        typicalStudentList.add(typicalStudent1);
        // ??????????????????
        List<DimensionReportVO.TypicalStudentName> typicalNameList = new ArrayList<>();
        DimensionReportVO.TypicalStudentName typicalStudentName = new DimensionReportVO.TypicalStudentName();
        typicalStudentName.setVeryBadList(veryBadUserInfo);
        typicalStudentName.setBadList(badUserInfo);
        typicalStudentName.setGenerallyList(generallyUserInfo);
        typicalNameList.add(typicalStudentName);
        vo.setTypicalStudentList(typicalStudentList);
        vo.setTypicalNameList(typicalNameList);
        return vo;
    }

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

    // ?????????????????????????????????
    public void addTargetCompare(String dimensionName, List<String> targetList, String className, Map<String, String> map1, ClassReportData classReportData, PersonalReportData2 report, PersonalReportData2 classMap, PersonalReportData2 gradeMap, DimensionReportVO vo) {
        DimensionReportVO.DiffCheck diffCheck = new DimensionReportVO.DiffCheck();
        String diff = map1.get("result");
        if ("1".equals(diff)) {
            diffCheck.setDiffent(map1.get("explain"));
            diffCheck.setExplanation("????????????" + dimensionName + "???????????????????????????????????????");
        } else if ("2".equals(diff)) {
            diffCheck.setDiffent(map1.get("explain"));
            diffCheck.setExplanation("????????????" + dimensionName + "???????????????????????????????????????");
        } else {
            diffCheck.setDiffent(map1.get("explain"));
            diffCheck.setExplanation("????????????" + dimensionName + "???????????????????????????????????????");
        }
        vo.setDiffCheck(diffCheck);
        // ??????????????????
        DimensionReportVO.TargetResult targetResult = new DimensionReportVO.TargetResult();
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        List<String> list3 = new ArrayList<>();

        // ???????????????
        GDimensionReportVO.ClassDiffOne classDiffOne1 = new GDimensionReportVO.ClassDiffOne();
        GDimensionReportVO.ClassDiffOne classDiffOne2 = new GDimensionReportVO.ClassDiffOne();
        GDimensionReportVO.ClassDiffOne classDiffOne3 = new GDimensionReportVO.ClassDiffOne();
        GDimensionReportVO.ClassDiffTwo classDiffTwo1 = new GDimensionReportVO.ClassDiffTwo();
        GDimensionReportVO.ClassDiffTwo classDiffTwo2 = new GDimensionReportVO.ClassDiffTwo();
        GDimensionReportVO.ClassDiffTwo classDiffTwo3 = new GDimensionReportVO.ClassDiffTwo();
        GDimensionReportVO.ClassDiffThree classDiffThree1 = new GDimensionReportVO.ClassDiffThree();
        GDimensionReportVO.ClassDiffThree classDiffThree2 = new GDimensionReportVO.ClassDiffThree();
        GDimensionReportVO.ClassDiffThree classDiffThree3 = new GDimensionReportVO.ClassDiffThree();
        GDimensionReportVO.ClassDiffFour classDiffFour1 = new GDimensionReportVO.ClassDiffFour();
        GDimensionReportVO.ClassDiffFour classDiffFour2 = new GDimensionReportVO.ClassDiffFour();
        GDimensionReportVO.ClassDiffFour classDiffFour3 = new GDimensionReportVO.ClassDiffFour();
        GDimensionReportVO.ClassDiffFive classDiffFive1 = new GDimensionReportVO.ClassDiffFive();
        GDimensionReportVO.ClassDiffFive classDiffFive2 = new GDimensionReportVO.ClassDiffFive();
        GDimensionReportVO.ClassDiffFive classDiffFive3 = new GDimensionReportVO.ClassDiffFive();

        classDiffOne1.setTitle(className);
        classDiffOne2.setTitle("????????????");
        classDiffOne3.setTitle("????????????");

        classDiffTwo1.setTitle(className);
        classDiffTwo2.setTitle("????????????");
        classDiffTwo3.setTitle("????????????");

        classDiffThree1.setTitle(className);
        classDiffThree2.setTitle("????????????");
        classDiffThree3.setTitle("????????????");

        classDiffFour1.setTitle(className);
        classDiffFour2.setTitle("????????????");
        classDiffFour3.setTitle("????????????");

        classDiffFive1.setTitle(className);
        classDiffFive2.setTitle("????????????");
        classDiffFive3.setTitle("????????????");
        targetList.stream().forEach(s -> {
            Map<String, String> map = null;
            if ("????????????".equals(s)) {
                map = diffResult(classReportData.getLearningAttitudeScore(), report.getLearningAttitudeScore(), classMap.getLearningAttitudeScore(), gradeMap.getLearningAttitudeScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setLearningAttitudeScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getLearningAttitudeScore(), report.getLearningAttitudeScore(), map);

                classDiffOne1.setTargetOne(map2.get("targetOne"));
                classDiffOne2.setTargetOne(map2.get("targetTwo"));
                classDiffOne3.setTargetOne(map2.get("targetThree"));

            } else if ("????????????".equals(s)) {
                map = diffResult(classReportData.getTimeManagementScore(), report.getTimeManagementScore(), classMap.getTimeManagementScore(), gradeMap.getTimeManagementScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setTimeManagementScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getTimeManagementScore(), report.getTimeManagementScore(), map);

                classDiffOne1.setTargetTwo(map2.get("targetOne"));
                classDiffOne2.setTargetTwo(map2.get("targetTwo"));
                classDiffOne3.setTargetTwo(map2.get("targetThree"));
            } else if ("????????????".equals(s)) {
                map = diffResult(classReportData.getLearningBurnoutScore(), report.getLearningBurnoutScore(), classMap.getLearningBurnoutScore(), gradeMap.getLearningBurnoutScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setLearningBurnoutScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getLearningBurnoutScore(), report.getLearningBurnoutScore(), map);

                classDiffOne1.setTargetThree(map2.get("targetOne"));
                classDiffOne2.setTargetThree(map2.get("targetTwo"));
                classDiffOne3.setTargetThree(map2.get("targetThree"));
            } else if ("?????????".equals(s)) {
                map = diffResult(classReportData.getMoralScore(), report.getMoralScore(), classMap.getMoralScore(), gradeMap.getMoralScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setMoralScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getMoralScore(), report.getMoralScore(), map);

                classDiffTwo1.setTargetOne(map2.get("targetOne"));
                classDiffTwo2.setTargetOne(map2.get("targetTwo"));
                classDiffTwo3.setTargetOne(map2.get("targetThree"));
            } else if ("?????????".equals(s)) {
                map = diffResult(classReportData.getStabilityScore(), report.getStabilityScore(), classMap.getStabilityScore(), gradeMap.getStabilityScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setStabilityScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getStabilityScore(), report.getStabilityScore(), map);

                classDiffTwo1.setTargetTwo(map2.get("targetOne"));
                classDiffTwo2.setTargetTwo(map2.get("targetTwo"));
                classDiffTwo3.setTargetTwo(map2.get("targetThree"));
            } else if ("?????????".equals(s)) {
                map = diffResult(classReportData.getDisciplineScore(), report.getDisciplineScore(), classMap.getDisciplineScore(), gradeMap.getDisciplineScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setDisciplineScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getDisciplineScore(), report.getDisciplineScore(), map);

                classDiffTwo1.setTargetThree(map2.get("targetOne"));
                classDiffTwo2.setTargetThree(map2.get("targetTwo"));
                classDiffTwo3.setTargetThree(map2.get("targetThree"));
            } else if ("????????????".equals(s)) {
                map = diffResult(classReportData.getOtherPerformanceScore(), report.getOtherPerformanceScore(), classMap.getOtherPerformanceScore(), gradeMap.getOtherPerformanceScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setOtherPerformanceScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getOtherPerformanceScore(), report.getOtherPerformanceScore(), map);

                classDiffTwo1.setTargetFour(map2.get("targetOne"));
                classDiffTwo2.setTargetFour(map2.get("targetTwo"));
                classDiffTwo3.setTargetFour(map2.get("targetThree"));
            } else if ("????????????".equals(s)) {
                map = diffResult(classReportData.getEmotionManagementScore(), report.getEmotionManagementScore(), classMap.getEmotionManagementScore(), gradeMap.getEmotionManagementScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setEmotionManagementScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getEmotionManagementScore(), report.getEmotionManagementScore(), map);

                classDiffFour1.setTargetOne(map2.get("targetOne"));
                classDiffFour2.setTargetOne(map2.get("targetTwo"));
                classDiffFour3.setTargetOne(map2.get("targetThree"));
            } else if ("????????????".equals(s)) {
                map = diffResult(classReportData.getGoalMotivationScore(), report.getGoalMotivationScore(), classMap.getGoalMotivationScore(), gradeMap.getGoalMotivationScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setGoalMotivationScoreDiff(Func.toDouble(map.get("diff")));


                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getGoalMotivationScore(), report.getGoalMotivationScore(), map);

                classDiffFour1.setTargetTwo(map2.get("targetOne"));
                classDiffFour2.setTargetTwo(map2.get("targetTwo"));
                classDiffFour3.setTargetTwo(map2.get("targetThree"));
            } else if ("????????????".equals(s)) {
                map = diffResult(classReportData.getPositiveAttentionScore(), report.getPositiveAttentionScore(), classMap.getPositiveAttentionScore(), gradeMap.getPositiveAttentionScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setPositiveAttentionScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getPositiveAttentionScore(), report.getPositiveAttentionScore(), map);

                classDiffFour1.setTargetThree(map2.get("targetOne"));
                classDiffFour2.setTargetThree(map2.get("targetTwo"));
                classDiffFour3.setTargetThree(map2.get("targetThree"));
            } else if ("????????????".equals(s)) {
                map = diffResult(classReportData.getSchoolSupportScore(), report.getSchoolSupportScore(), classMap.getSchoolSupportScore(), gradeMap.getSchoolSupportScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setSchoolSupportScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getSchoolSupportScore(), report.getSchoolSupportScore(), map);

                classDiffFour1.setTargetFour(map2.get("targetOne"));
                classDiffFour2.setTargetFour(map2.get("targetTwo"));
                classDiffFour3.setTargetFour(map2.get("targetThree"));
            } else if ("????????????".equals(s)) {
                map = diffResult(classReportData.getInterpersonalSupportScore(), report.getInterpersonalSupportScore(), classMap.getInterpersonalSupportScore(), gradeMap.getInterpersonalSupportScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setInterpersonalSupportScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getSchoolSupportScore(), report.getSchoolSupportScore(), map);

                classDiffFour1.setTargetFive(map2.get("targetOne"));
                classDiffFour2.setTargetFive(map2.get("targetTwo"));
                classDiffFour3.setTargetFive(map2.get("targetThree"));
            } else if ("????????????".equals(s)) {
                map = diffResult(classReportData.getFamilySupportScore(), report.getFamilySupportScore(), classMap.getFamilySupportScore(), gradeMap.getFamilySupportScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setFamilySupportScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getFamilySupportScore(), report.getFamilySupportScore(), map);

                classDiffFour1.setTargetSix(map2.get("targetOne"));
                classDiffFour2.setTargetSix(map2.get("targetTwo"));
                classDiffFour3.setTargetSix(map2.get("targetThree"));
            } else if ("????????????".equals(s)) {
                map = diffResult(classReportData.getStudyStressScore(), report.getStudyStressScore(), classMap.getStudyStressScore(), gradeMap.getStudyStressScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setStudyStressScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getStudyStressScore(), report.getStudyStressScore(), map);

                classDiffThree1.setTargetOne(map2.get("targetOne"));
                classDiffThree2.setTargetOne(map2.get("targetTwo"));
                classDiffThree3.setTargetOne(map2.get("targetThree"));
            } else if ("????????????".equals(s)) {
                map = diffResult(classReportData.getInterpersonalStressScore(), report.getInterpersonalStressScore(), classMap.getInterpersonalStressScore(), gradeMap.getInterpersonalStressScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setInterpersonalStressScoreDiff(Func.toDouble(map.get("diff")));


                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getInterpersonalStressScore(), report.getInterpersonalStressScore(), map);

                classDiffThree1.setTargetTwo(map2.get("targetOne"));
                classDiffThree2.setTargetTwo(map2.get("targetTwo"));
                classDiffThree3.setTargetTwo(map2.get("targetThree"));
            } else if ("???????????????".equals(s)) {
                map = diffResult(classReportData.getPunishmentStressScore(), report.getPunishmentStressScore(), classMap.getPunishmentStressScore(), gradeMap.getPunishmentStressScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setPunishmentStressScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getPunishmentStressScore(), report.getPunishmentStressScore(), map);

                classDiffThree1.setTargetThree(map2.get("targetOne"));
                classDiffThree2.setTargetThree(map2.get("targetTwo"));
                classDiffThree3.setTargetThree(map2.get("targetThree"));
            } else if ("????????????".equals(s)) {
                map = diffResult(classReportData.getLossStressScore(), report.getLossStressScore(), classMap.getLossStressScore(), gradeMap.getLossStressScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setLossStressScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getLossStressScore(), report.getLossStressScore(), map);

                classDiffThree1.setTargetFour(map2.get("targetOne"));
                classDiffThree2.setTargetFour(map2.get("targetTwo"));
                classDiffThree3.setTargetFour(map2.get("targetThree"));
            } else if ("????????????".equals(s)) {
                map = diffResult(classReportData.getAdaptationStressScore(), report.getAdaptationStressScore(), classMap.getAdaptationStressScore(), gradeMap.getAdaptationStressScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setAdaptationStressScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getAdaptationStressScore(), report.getAdaptationStressScore(), map);

                classDiffThree1.setTargetFive(map2.get("targetOne"));
                classDiffThree2.setTargetFive(map2.get("targetTwo"));
                classDiffThree3.setTargetFive(map2.get("targetThree"));
            } else if ("??????".equals(s)) {
                map = diffResult(classReportData.getCompulsionScore(), report.getCompulsionScore(), classMap.getCompulsionScore(), gradeMap.getCompulsionScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setCompulsionScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getCompulsionScore(), report.getCompulsionScore(), map);

                classDiffFour1.setTargetOne(map2.get("targetOne"));
                classDiffFour2.setTargetOne(map2.get("targetTwo"));
                classDiffFour3.setTargetOne(map2.get("targetThree"));
            } else if ("??????".equals(s)) {
                map = diffResult(classReportData.getParanoiaScore(), report.getParanoiaScore(), classMap.getParanoiaScore(), gradeMap.getParanoiaScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setParanoiaScoreDiff(Func.toDouble(map.get("diff")));


                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getParanoiaScore(), report.getParanoiaScore(), map);

                classDiffFour1.setTargetTwo(map2.get("targetOne"));
                classDiffFour2.setTargetTwo(map2.get("targetTwo"));
                classDiffFour3.setTargetTwo(map2.get("targetThree"));
            } else if ("??????".equals(s)) {
                map = diffResult(classReportData.getHostilityScore(), report.getHostilityScore(), classMap.getHostilityScore(), gradeMap.getHostilityScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setHostilityScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getHostilityScore(), report.getHostilityScore(), map);

                classDiffFour1.setTargetThree(map2.get("targetOne"));
                classDiffFour2.setTargetThree(map2.get("targetTwo"));
                classDiffFour3.setTargetThree(map2.get("targetThree"));
            } else if ("????????????".equals(s)) {
                map = diffResult(classReportData.getInterpersonalSensitivityScore(), report.getInterpersonalSensitivityScore(), classMap.getInterpersonalSensitivityScore(), gradeMap.getInterpersonalSensitivityScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setInterpersonalSensitivityScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getInterpersonalSensitivityScore(), report.getInterpersonalSensitivityScore(), map);

                classDiffFour1.setTargetFour(map2.get("targetOne"));
                classDiffFour2.setTargetFour(map2.get("targetTwo"));
                classDiffFour3.setTargetFour(map2.get("targetThree"));
            } else if ("??????".equals(s)) {
                map = diffResult(classReportData.getAnxietyScore(), report.getAnxietyScore(), classMap.getAnxietyScore(), gradeMap.getAnxietyScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setAnxietyScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getAnxietyScore(), report.getAnxietyScore(), map);

                classDiffFour1.setTargetFive(map2.get("targetOne"));
                classDiffFour2.setTargetFive(map2.get("targetTwo"));
                classDiffFour3.setTargetFive(map2.get("targetThree"));
            } else if ("??????".equals(s)) {
                map = diffResult(classReportData.getDepressionScore(), report.getDepressionScore(), classMap.getDepressionScore(), gradeMap.getDepressionScore(), classMap.getPeopleCount(), gradeMap.getPeopleCount());
                classReportData.setDepressionScoreDiff(Func.toDouble(map.get("diff")));

                Map<String, GDimensionReportVO.TargetScore> map2 = dealDiff(classReportData.getDepressionScore(), report.getDepressionScore(), map);

                classDiffFour1.setTargetSix(map2.get("targetOne"));
                classDiffFour2.setTargetSix(map2.get("targetTwo"));
                classDiffFour3.setTargetSix(map2.get("targetThree"));
            }
            targetResult(list1, list2, list3, s, map.get("result"));
        });
        if ("????????????".equals(dimensionName) || "????????????".equals(dimensionName)) {
            // ????????????
            targetResult.setGoodTarget(list3.toArray(new String[]{}));
            targetResult.setNoTarget(list2.toArray(new String[]{}));
            targetResult.setBadTarget(list1.toArray(new String[]{}));
        } else {
            targetResult.setGoodTarget(list1.toArray(new String[]{}));
            targetResult.setNoTarget(list2.toArray(new String[]{}));
            targetResult.setBadTarget(list3.toArray(new String[]{}));
        }

        vo.setTargetResult(targetResult);
        // ?????????????????????????????????????????????
        List<Object> classDiffList = new ArrayList<>();
        if ("????????????".equals(dimensionName)) {
            classDiffList.add(classDiffOne1);
            classDiffList.add(classDiffOne2);
            classDiffList.add(classDiffOne3);
        } else if ("????????????".equals(dimensionName)) {
            classDiffList.add(classDiffTwo1);
            classDiffList.add(classDiffTwo2);
            classDiffList.add(classDiffTwo3);
        } else if ("????????????".equals(dimensionName) || "????????????".equals(dimensionName)) {
            classDiffList.add(classDiffFour1);
            classDiffList.add(classDiffFour2);
            classDiffList.add(classDiffFour3);
        } else if ("????????????".equals(dimensionName)) {
            classDiffList.add(classDiffThree1);
            classDiffList.add(classDiffThree2);
            classDiffList.add(classDiffThree3);
        } else if ("????????????".equals(dimensionName)) {
//            DimensionReportVO.ClassDiffFive classDiff1 = new DimensionReportVO.ClassDiffFive();
//            DimensionReportVO.ClassDiffFive classDiff2 = new DimensionReportVO.ClassDiffFive();
//            DimensionReportVO.ClassDiffFive classDiff3 = new DimensionReportVO.ClassDiffFive();
//            classDiff1.setTitle(className);
//            classDiff2.setTitle("????????????");
//            classDiff3.setTitle("????????????");
//
//            classDiff1.setTargetOne(targetScore1.getTargetOne());
//
//            classDiff2.setTargetOne(targetScore2.getTargetOne());
//
//            classDiff3.setTargetOne(targetScore3.getTargetOne());
//
//            classDiffList.add(classDiff1);
//            classDiffList.add(classDiff2);
//            classDiffList.add(classDiff3);
        }
        vo.setClassDiffList(classDiffList);
    }

    // ???????????????????????????
    public Map<String, GDimensionReportVO.TargetScore> dealDiff(Double classScore, Double gradeScore, Map<String, String> map) {
        Map<String, GDimensionReportVO.TargetScore> result = new HashMap<>();
        // ??????
        GDimensionReportVO.TargetScore targetOne = new GDimensionReportVO.TargetScore();
        targetOne.setTargetScore(dealRate3(classScore));
        targetOne.setTargetDiff(3);
        // ??????
        GDimensionReportVO.TargetScore targetTwo = new GDimensionReportVO.TargetScore();
        targetTwo.setTargetScore(dealRate3(gradeScore));
        targetTwo.setTargetDiff(3);
        // ?????????
        GDimensionReportVO.TargetScore targetThree = new GDimensionReportVO.TargetScore();
        targetThree.setTargetScore(dealRate3(Func.toDouble(map.get("diff"))));
        targetThree.setTargetDiff(Func.toInt(map.get("classDiff")));

        result.put("targetOne", targetOne);
        result.put("targetTwo", targetTwo);
        result.put("targetThree", targetThree);

        return result;
    }

    // ???????????????
    public TargetResultVO addTargetResult(List<String> targetList, ClassReportData classReportData, PersonalReportData2 report, DimensionReportVO vo, String className) {
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
                targetVO1.setScoreOne(classReportData.getAdaptationStressScore().toString());
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

    // ??????????????????
    public void typicalStudent(List<Long> userIdList, List<Long> userIdList2, String sql, String targetSql, String unwind, List<DimensionReportVO.TypicalStudent> typicalStudentList, List<DimensionReportVO.TypicalStudentName> typicalNameList, String target, ClassReportData classReportData) {
        Map<String, List<UserInfoVO>> learningAttitudeScoreMap = getLearningAttitudeScore(userIdList, sql, targetSql, unwind, target);
        List<UserInfoVO> learningAttitudeVeryBad = learningAttitudeScoreMap.get("veryBad");
        double v1 = calculatePercentage(userIdList.size(), learningAttitudeVeryBad.size());
        List<UserInfoVO> learningAttitudeBad = learningAttitudeScoreMap.get("bad");
        double v2 = calculatePercentage(userIdList.size(), learningAttitudeBad.size());
        List<UserInfoVO> learningAttitudeGenerally = learningAttitudeScoreMap.get("generally");
        double v3 = calculatePercentage(userIdList.size(), learningAttitudeGenerally.size());
        List<UserInfoVO> learningAttitudeGood = learningAttitudeScoreMap.get("good");
        double v4 = calculatePercentage(userIdList.size(), learningAttitudeGood.size());

        DimensionReportVO.TypicalStudent typicalStudent1 = new DimensionReportVO.TypicalStudent();
        typicalStudent1.setTitle(target);
        typicalStudent1.setVeryBad(learningAttitudeVeryBad.size() + "???(" + v1 + "%)");
        typicalStudent1.setBad(learningAttitudeBad.size() + "???(" + v2 + "%)");
        typicalStudent1.setGenerally(learningAttitudeGenerally.size() + "???(" + v3 + "%)");
        typicalStudent1.setGood(learningAttitudeGood.size() + "???(" + v4 + "%)");
        typicalStudentList.add(typicalStudent1);

        DimensionReportVO.TypicalStudentName typicalStudentName1 = new DimensionReportVO.TypicalStudentName();
        typicalStudentName1.setTitle(target);
        typicalStudentName1.setBadList(learningAttitudeBad);
        typicalStudentName1.setGenerallyList(learningAttitudeGenerally);
        typicalStudentName1.setVeryBadList(learningAttitudeVeryBad);
        typicalNameList.add(typicalStudentName1);

        // ??????????????????????????????
        if ("????????????".equals(target)) {
            classReportData.setLearningAttitudeScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setLearningAttitudeScoreBad(learningAttitudeBad.size());
            classReportData.setLearningAttitudeScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setLearningAttitudeScoreGood(learningAttitudeGood.size());
        } else if ("????????????".equals(target)) {
            classReportData.setTimeManagementScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setTimeManagementScoreBad(learningAttitudeBad.size());
            classReportData.setTimeManagementScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setTimeManagementScoreGood(learningAttitudeGood.size());
        } else if ("????????????".equals(target)) {
            classReportData.setLearningBurnoutScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setLearningBurnoutScoreBad(learningAttitudeBad.size());
            classReportData.setLearningBurnoutScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setLearningBurnoutScoreGood(learningAttitudeGood.size());
        } else if ("?????????".equals(target)) {
            classReportData.setMoralScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setMoralScoreBad(learningAttitudeBad.size());
            classReportData.setMoralScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setMoralScoreGood(learningAttitudeGood.size());
        } else if ("?????????".equals(target)) {
            classReportData.setStabilityScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setStabilityScoreBad(learningAttitudeBad.size());
            classReportData.setStabilityScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setStabilityScoreGood(learningAttitudeGood.size());
        } else if ("?????????".equals(target)) {
            classReportData.setDisciplineScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setDisciplineScoreBad(learningAttitudeBad.size());
            classReportData.setDisciplineScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setDisciplineScoreGood(learningAttitudeGood.size());
        } else if ("????????????".equals(target)) {
            classReportData.setOtherPerformanceScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setOtherPerformanceScoreBad(learningAttitudeBad.size());
            classReportData.setOtherPerformanceScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setOtherPerformanceScoreGood(learningAttitudeGood.size());
        } else if ("????????????".equals(target)) {
            classReportData.setEmotionManagementScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setEmotionManagementScoreBad(learningAttitudeBad.size());
            classReportData.setEmotionManagementScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setEmotionManagementScoreGood(learningAttitudeGood.size());
        } else if ("????????????".equals(target)) {
            classReportData.setGoalMotivationScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setGoalMotivationScoreBad(learningAttitudeBad.size());
            classReportData.setGoalMotivationScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setGoalMotivationScoreGood(learningAttitudeGood.size());
        } else if ("????????????".equals(target)) {
            classReportData.setPositiveAttentionScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setPositiveAttentionScoreBad(learningAttitudeBad.size());
            classReportData.setPositiveAttentionScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setPositiveAttentionScoreGood(learningAttitudeGood.size());
        } else if ("????????????".equals(target)) {
            classReportData.setSchoolSupportScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setSchoolSupportScoreBad(learningAttitudeBad.size());
            classReportData.setSchoolSupportScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setSchoolSupportScoreGood(learningAttitudeGood.size());
        } else if ("????????????".equals(target)) {
            classReportData.setInterpersonalSupportScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setInterpersonalSupportScoreBad(learningAttitudeBad.size());
            classReportData.setInterpersonalSupportScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setInterpersonalSupportScoreGood(learningAttitudeGood.size());
        } else if ("????????????".equals(target)) {
            classReportData.setFamilySupportScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setFamilySupportScoreBad(learningAttitudeBad.size());
            classReportData.setFamilySupportScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setFamilySupportScoreGood(learningAttitudeGood.size());
        } else if ("????????????".equals(target)) {
            classReportData.setStudyStressScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setStudyStressScoreBad(learningAttitudeBad.size());
            classReportData.setStudyStressScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setStudyStressScoreGood(learningAttitudeGood.size());
        } else if ("????????????".equals(target)) {
            classReportData.setInterpersonalStressScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setInterpersonalStressScoreBad(learningAttitudeBad.size());
            classReportData.setInterpersonalStressScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setInterpersonalStressScoreGood(learningAttitudeGood.size());
        } else if ("???????????????".equals(target)) {
            classReportData.setPunishmentStressScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setPunishmentStressScoreBad(learningAttitudeBad.size());
            classReportData.setPunishmentStressScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setPunishmentStressScoreGood(learningAttitudeGood.size());
        } else if ("????????????".equals(target)) {
            classReportData.setLossStressScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setLossStressScoreBad(learningAttitudeBad.size());
            classReportData.setLossStressScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setLossStressScoreGood(learningAttitudeGood.size());
        } else if ("????????????".equals(target)) {
            classReportData.setAdaptationStressScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setAdaptationStressScoreBad(learningAttitudeBad.size());
            classReportData.setAdaptationStressScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setAdaptationStressScoreGood(learningAttitudeGood.size());
        } else if ("??????".equals(target)) {
            classReportData.setCompulsionScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setCompulsionScoreBad(learningAttitudeBad.size());
            classReportData.setCompulsionScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setCompulsionScoreGood(learningAttitudeGood.size());
        } else if ("??????".equals(target)) {
            classReportData.setParanoiaScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setParanoiaScoreBad(learningAttitudeBad.size());
            classReportData.setParanoiaScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setParanoiaScoreGood(learningAttitudeGood.size());
        } else if ("??????".equals(target)) {
            classReportData.setHostilityScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setHostilityScoreBad(learningAttitudeBad.size());
            classReportData.setHostilityScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setHostilityScoreGood(learningAttitudeGood.size());
        } else if ("????????????".equals(target)) {
            classReportData.setInterpersonalSensitivityScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setInterpersonalSensitivityScoreBad(learningAttitudeBad.size());
            classReportData.setInterpersonalSensitivityScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setInterpersonalSensitivityScoreGood(learningAttitudeGood.size());
        } else if ("??????".equals(target)) {
            classReportData.setAnxietyScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setAnxietyScoreBad(learningAttitudeBad.size());
            classReportData.setAnxietyScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setAnxietyScoreGood(learningAttitudeGood.size());
        } else if ("??????".equals(target)) {
            classReportData.setDepressionScoreVeryBad(learningAttitudeVeryBad.size());
            classReportData.setDepressionScoreBad(learningAttitudeBad.size());
            classReportData.setDepressionScoreGenerally(learningAttitudeGenerally.size());
            classReportData.setDepressionScoreGood(learningAttitudeGood.size());
        }
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

    // ????????????
    public Map<String, List<UserInfoVO>> getLearningAttitudeScore(List<Long> userIdList, String sql, String targetSql, String unwind, String target) {
        Map<String, List<UserInfoVO>> map = new HashMap<>();
        // ??????
        List<UserInfoVO> goodUserInfo = getUserInfo(userIdList, sql, 0, targetSql, unwind, target);
        // ??????
        List<UserInfoVO> generallyUserInfo = getUserInfo(userIdList, sql, 1, targetSql, unwind, target);
        // ??????
        List<UserInfoVO> badUserInfo = getUserInfo(userIdList, sql, 2, targetSql, unwind, target);
        // ??????
        List<UserInfoVO> veryBadUserInfo = getUserInfo(userIdList, sql, 3, targetSql, unwind, target);
        map.put("veryBad", veryBadUserInfo);
        map.put("bad", badUserInfo);
        map.put("generally", generallyUserInfo);
        map.put("good", goodUserInfo);
        return map;
    }

    /**
     * ??????????????????mongodb
     *
     * @param sql
     * @param riskIndex 3?????? 2?????? 1?????? 0??????
     * @return org.springframework.data.mongodb.core.query.Criteria
     * @author hzl
     * @date 2022/8/23 11:37
     */
    public Criteria getCriteria(String sql, int riskIndex, String targetSql, String target) {
        return Criteria.where(sql).is(riskIndex).and(targetSql).is(target);
    }

    public List<UserInfoVO> getUserInfo(List<Long> userIdList, String sql, int riskIndex, String targetSql, String unwind, String target) {
        Criteria criteria = Criteria.where("userInfo.userId").in(userIdList);
        Criteria criteria1 = getCriteria(sql, riskIndex, targetSql, target);
        TypedAggregation<UserInfoVO> agg = Aggregation.newAggregation(UserInfoVO.class,
                Aggregation.unwind(unwind),
                Aggregation.match(criteria),
                Aggregation.match(criteria1),
                Aggregation.project("userInfo.userId", "userInfo.name").and("userInfo.name").as("userName"));
        AggregationResults<UserInfoVO> report = mongoTemplate.aggregate(agg, "personalReport", UserInfoVO.class);
        return report.getMappedResults();
    }

    // ??????????????????
    public void targetResult(List<String> list1, List<String> list2, List<String> list3, String name, String score) {
        if ("1".equals(score)) {
            list1.add(name);
        } else if ("2".equals(score)) {
            list2.add(name);
        } else {
            list3.add(name);
        }
    }

    // ???????????????????????????
    public PersonalReportData2 gradeDiffent(List<Long> classIdList, Long taskId, Long gradeId, String tenantCode) {
        PersonalReportData2 data = new PersonalReportData2();
        // ?????????????????????
        AtomicInteger totalPeople = new AtomicInteger(0);
        List<PersonalReportData2> list = new ArrayList<>();
        classIdList.stream().forEach(classId -> {
            // ??????????????????????????????
            List<String> paperIdList = testPaperMapper.getPaperIdByCId(taskId, gradeId, classId, tenantCode);
            totalPeople.set(totalPeople.get() + paperIdList.size());
            List<PersonalReportData> peosonReportList = mongoTemplate.find(new Query().addCriteria(Criteria.where("paperId").in(paperIdList)), PersonalReportData.class);
            // ????????????
            Query query = new Query();
            query.addCriteria(Criteria.where("taskId").is(taskId));
            query.addCriteria(Criteria.where("classId").is(classId));
            ClassReportData classReportData = mongoTemplate.findOne(query, ClassReportData.class);
            PersonalReportData2 personalReportData2 = classDiffent(peosonReportList, classReportData);
            list.add(personalReportData2);
        });
        data.setStudyStatusScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getStudyStatusScore)));
        data.setBehaviorScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getBehaviorScore)));
        data.setMentalToughnessScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getMentalToughnessScore)));
        data.setOverallStressScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getOverallStressScore)));
        data.setEmotionalIndexScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getEmotionalIndexScore)));
        data.setSleepIndexScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getSleepIndexScore)));
        data.setSuicidalIdeationScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getSuicidalIdeationScore)));
        data.setLearningAttitudeScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getLearningAttitudeScore)));
        data.setTimeManagementScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getTimeManagementScore)));
        data.setLearningBurnoutScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getLearningBurnoutScore)));
        data.setMoralScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getMoralScore)));
        data.setStabilityScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getStabilityScore)));
        data.setDisciplineScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getDisciplineScore)));
        data.setOtherPerformanceScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getOtherPerformanceScore)));
        data.setEmotionManagementScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getEmotionManagementScore)));
        data.setGoalMotivationScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getGoalMotivationScore)));
        data.setPositiveAttentionScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getPositiveAttentionScore)));
        data.setSchoolSupportScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getSchoolSupportScore)));
        data.setInterpersonalSupportScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getInterpersonalSupportScore)));
        data.setFamilySupportScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getFamilySupportScore)));
        data.setStudyStressScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getStudyStressScore)));
        data.setInterpersonalStressScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getInterpersonalStressScore)));
        data.setPunishmentStressScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getPunishmentStressScore)));
        data.setLossStressScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getLossStressScore)));
        data.setAdaptationStressScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getAdaptationStressScore)));
        data.setCompulsionScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getCompulsionScore)));
        data.setParanoiaScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getParanoiaScore)));
        data.setHostilityScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getHostilityScore)));
        data.setInterpersonalSensitivityScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getInterpersonalSensitivityScore)));
        data.setAnxietyScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getAnxietyScore)));
        data.setDepressionScore(list.stream().collect(Collectors.averagingDouble(PersonalReportData2::getDepressionScore)));

        data.setPeopleCount(totalPeople.get());
        return data;
    }

    // ???????????????????????????
    public PersonalReportData2 classDiffent(List<PersonalReportData> peosonReportList, ClassReportData classReportData) {
        PersonalReportData2 data = new PersonalReportData2();
        // ?????????????????????????????????-????????????????????????+.../?????????
        Map<String, Double> map = new HashMap<>();
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> studyStatusScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> behaviorScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> mentalToughnessScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> overallStressScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> emotionalIndexScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> sleepIndexScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> suicidalIdeationScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> learningAttitudeScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> timeManagementScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> learningBurnoutScore = new AtomicReference<>(0d);
        /**
         * ????????? ????????????
         */
        AtomicReference<Double> moralScore = new AtomicReference<>(0d);
        /**
         * ????????? ????????????
         */
        AtomicReference<Double> stabilityScore = new AtomicReference<>(0d);
        /**
         * ????????? ????????????
         */
        AtomicReference<Double> disciplineScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> otherPerformanceScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> emotionManagementScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> goalMotivationScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> positiveAttentionScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> schoolSupportScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> interpersonalSupportScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> familySupportScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> studyStressScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> interpersonalStressScore = new AtomicReference<>(0d);
        /**
         * ??????????????? ????????????
         */
        AtomicReference<Double> punishmentStressScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> lossStressScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> adaptationStressScore = new AtomicReference<>(0d);
        /**
         * ?????? ????????????
         */
        AtomicReference<Double> compulsionScore = new AtomicReference<>(0d);
        /**
         * ????????? ????????????
         */
        AtomicReference<Double> paranoiaScore = new AtomicReference<>(0d);
        /**
         * ?????? ????????????
         */
        AtomicReference<Double> hostilityScore = new AtomicReference<>(0d);
        /**
         * ???????????? ????????????
         */
        AtomicReference<Double> interpersonalSensitivityScore = new AtomicReference<>(0d);
        /**
         * ?????? ????????????
         */
        AtomicReference<Double> anxietyScore = new AtomicReference<>(0d);
        /**
         * ?????? ????????????
         */
        AtomicReference<Double> depressionScore = new AtomicReference<>(0d);
        int total = peosonReportList.size();
        data.setPeopleCount(total);
        peosonReportList.stream().forEach(report -> {
            studyStatusScore.updateAndGet(v -> v + Math.pow((report.getStudyStatusScore() - classReportData.getStudyStatusScore()), 2));
            behaviorScore.updateAndGet(v -> v + Math.pow((report.getBehaviorScore() - classReportData.getBehaviorScore()), 2));
            mentalToughnessScore.updateAndGet(v -> v + Math.pow((report.getMentalToughnessScore() - classReportData.getMentalToughnessScore()), 2));
            overallStressScore.updateAndGet(v -> v + Math.pow((report.getOverallStressScore() - classReportData.getOverallStressScore()), 2));
            emotionalIndexScore.updateAndGet(v -> v + Math.pow((report.getEmotionalIndexScore() - classReportData.getEmotionalIndexScore()), 2));
            sleepIndexScore.updateAndGet(v -> v + Math.pow((report.getSleepIndexScore() - classReportData.getSleepIndexScore()), 2));
            suicidalIdeationScore.updateAndGet(v -> v + Math.pow((report.getSuicidalIdeationScore() - classReportData.getSuicidalIdeationScore()), 2));
            learningAttitudeScore.updateAndGet(v -> v + Math.pow((report.getLearningAttitudeScore() - classReportData.getLearningAttitudeScore()), 2));
            timeManagementScore.updateAndGet(v -> v + Math.pow((report.getTimeManagementScore() - classReportData.getTimeManagementScore()), 2));
            learningBurnoutScore.updateAndGet(v -> v + Math.pow((report.getLearningBurnoutScore() - classReportData.getLearningBurnoutScore()), 2));
            moralScore.updateAndGet(v -> v + Math.pow((report.getMoralScore() - classReportData.getMoralScore()), 2));
            stabilityScore.updateAndGet(v -> v + Math.pow((report.getStabilityScore() - classReportData.getStabilityScore()), 2));
            disciplineScore.updateAndGet(v -> v + Math.pow((report.getDisciplineScore() - classReportData.getDisciplineScore()), 2));
            otherPerformanceScore.updateAndGet(v -> v + Math.pow((report.getOtherPerformanceScore() - classReportData.getOtherPerformanceScore()), 2));
            emotionManagementScore.updateAndGet(v -> v + Math.pow((report.getEmotionManagementScore() - classReportData.getEmotionManagementScore()), 2));
            goalMotivationScore.updateAndGet(v -> v + Math.pow((report.getGoalMotivationScore() - classReportData.getGoalMotivationScore()), 2));
            positiveAttentionScore.updateAndGet(v -> v + Math.pow((report.getPositiveAttentionScore() - classReportData.getPositiveAttentionScore()), 2));
            schoolSupportScore.updateAndGet(v -> v + Math.pow((report.getSchoolSupportScore() - classReportData.getSchoolSupportScore()), 2));
            interpersonalSupportScore.updateAndGet(v -> v + Math.pow((report.getInterpersonalSupportScore() - classReportData.getInterpersonalSupportScore()), 2));
            familySupportScore.updateAndGet(v -> v + Math.pow((report.getFamilySupportScore() - classReportData.getFamilySupportScore()), 2));
            studyStressScore.updateAndGet(v -> v + Math.pow((report.getStudyStressScore() - classReportData.getStudyStressScore()), 2));
            interpersonalStressScore.updateAndGet(v -> v + Math.pow((report.getInterpersonalStressScore() - classReportData.getInterpersonalStressScore()), 2));
            punishmentStressScore.updateAndGet(v -> v + Math.pow((report.getPunishmentStressScore() - classReportData.getPunishmentStressScore()), 2));
            lossStressScore.updateAndGet(v -> v + Math.pow((report.getLossStressScore() - classReportData.getLossStressScore()), 2));
            adaptationStressScore.updateAndGet(v -> v + Math.pow((report.getAdaptationStressScore() - classReportData.getAdaptationStressScore()), 2));
            compulsionScore.updateAndGet(v -> v + Math.pow((report.getCompulsionScore() - classReportData.getCompulsionScore()), 2));
            paranoiaScore.updateAndGet(v -> v + Math.pow((report.getParanoiaScore() - classReportData.getParanoiaScore()), 2));
            hostilityScore.updateAndGet(v -> v + Math.pow((report.getHostilityScore() - classReportData.getHostilityScore()), 2));
            interpersonalSensitivityScore.updateAndGet(v -> v + Math.pow((report.getInterpersonalSensitivityScore() - classReportData.getInterpersonalSensitivityScore()), 2));
            anxietyScore.updateAndGet(v -> v + Math.pow((report.getAnxietyScore() - classReportData.getAnxietyScore()), 2));
            depressionScore.updateAndGet(v -> v + Math.pow((report.getDepressionScore() - classReportData.getDepressionScore()), 2));
        });
        if (total != 0) {
            double studyStatusScore1 = studyStatusScore.get() / total;
            data.setStudyStatusScore(studyStatusScore1);
            map.put("studyStatusScore", studyStatusScore1);
            double behaviorScore1 = behaviorScore.get() / total;
            data.setBehaviorScore(behaviorScore1);
            map.put("behaviorScore", behaviorScore1);
            double mentalToughnessScore1 = mentalToughnessScore.get() / total;
            data.setMentalToughnessScore(mentalToughnessScore1);
            map.put("mentalToughnessScore", mentalToughnessScore1);
            double overallStressScore1 = overallStressScore.get() / total;
            data.setOverallStressScore(overallStressScore1);
            map.put("overallStressScore", overallStressScore1);
            double emotionalIndexScore1 = emotionalIndexScore.get() / total;
            data.setEmotionalIndexScore(emotionalIndexScore1);
            map.put("emotionalIndexScore", emotionalIndexScore1);
            double sleepIndexScore1 = sleepIndexScore.get() / total;
            data.setSleepIndexScore(sleepIndexScore1);
            map.put("sleepIndexScore", sleepIndexScore1);
            double suicidalIdeationScore1 = suicidalIdeationScore.get() / total;
            data.setSuicidalIdeationScore(suicidalIdeationScore1);
            map.put("suicidalIdeationScore", suicidalIdeationScore1);
            double learningAttitudeScore1 = learningAttitudeScore.get() / total;
            data.setLearningAttitudeScore(learningAttitudeScore1);
            map.put("learningAttitudeScore", learningAttitudeScore1);
            double timeManagementScore1 = timeManagementScore.get() / total;
            data.setTimeManagementScore(timeManagementScore1);
            map.put("timeManagementScore", timeManagementScore1);
            double learningBurnoutScore1 = learningBurnoutScore.get() / total;
            data.setLearningBurnoutScore(learningBurnoutScore1);
            map.put("learningBurnoutScore", learningBurnoutScore1);
            double moralScore1 = moralScore.get() / total;
            data.setMoralScore(moralScore1);
            map.put("moralScore", moralScore1);
            double stabilityScore1 = stabilityScore.get() / total;
            data.setStabilityScore(stabilityScore1);
            map.put("stabilityScore", stabilityScore1);
            double disciplineScore1 = disciplineScore.get() / total;
            data.setDisciplineScore(disciplineScore1);
            map.put("disciplineScore", disciplineScore1);
            double otherPerformanceScore1 = otherPerformanceScore.get() / total;
            data.setOtherPerformanceScore(otherPerformanceScore1);
            map.put("otherPerformanceScore", otherPerformanceScore1);
            double emotionManagementScore1 = emotionManagementScore.get() / total;
            data.setEmotionManagementScore(emotionManagementScore1);
            map.put("emotionManagementScore", emotionManagementScore1);
            double goalMotivationScore1 = goalMotivationScore.get() / total;
            data.setGoalMotivationScore(goalMotivationScore1);
            map.put("goalMotivationScore", goalMotivationScore1);
            double positiveAttentionScore1 = positiveAttentionScore.get() / total;
            data.setPositiveAttentionScore(positiveAttentionScore1);
            map.put("positiveAttentionScore", positiveAttentionScore1);
            double schoolSupportScore1 = schoolSupportScore.get() / total;
            data.setSchoolSupportScore(schoolSupportScore1);
            map.put("schoolSupportScore", schoolSupportScore1);
            double interpersonalSupportScore1 = interpersonalSupportScore.get() / total;
            data.setInterpersonalSupportScore(interpersonalSupportScore1);
            map.put("interpersonalSupportScore", interpersonalSupportScore1);
            double familySupportScore1 = familySupportScore.get() / total;
            data.setFamilySupportScore(familySupportScore1);
            map.put("familySupportScore", familySupportScore1);
            double studyStressScore1 = studyStressScore.get() / total;
            data.setStudyStressScore(studyStressScore1);
            map.put("studyStressScore", studyStressScore1);
            double interpersonalStressScore1 = interpersonalStressScore.get() / total;
            data.setInterpersonalStressScore(interpersonalStressScore1);
            map.put("interpersonalStressScore", interpersonalStressScore1);
            double punishmentStressScore1 = punishmentStressScore.get() / total;
            data.setPunishmentStressScore(punishmentStressScore1);
            map.put("punishmentStressScore", punishmentStressScore1);
            double lossStressScore1 = lossStressScore.get() / total;
            data.setLossStressScore(lossStressScore1);
            map.put("lossStressScore", lossStressScore1);
            double adaptationStressScore1 = adaptationStressScore.get() / total;
            data.setAdaptationStressScore(adaptationStressScore1);
            map.put("adaptationStressScore", adaptationStressScore1);
            double compulsionScore1 = compulsionScore.get() / total;
            data.setCompulsionScore(compulsionScore1);
            map.put("compulsionScore", compulsionScore1);
            double paranoiaScore1 = paranoiaScore.get() / total;
            data.setParanoiaScore(paranoiaScore1);
            map.put("paranoiaScore", paranoiaScore1);
            double hostilityScore1 = hostilityScore.get() / total;
            data.setHostilityScore(hostilityScore1);
            map.put("hostilityScore", hostilityScore1);
            double interpersonalSensitivityScore1 = interpersonalSensitivityScore.get() / total;
            data.setInterpersonalSensitivityScore(interpersonalSensitivityScore1);
            map.put("interpersonalSensitivityScore", interpersonalSensitivityScore1);
            double anxietyScore1 = anxietyScore.get() / total;
            data.setAnxietyScore(anxietyScore1);
            map.put("anxietyScore", anxietyScore1);
            double depressionScore1 = depressionScore.get() / total;
            data.setDepressionScore(depressionScore1);
            map.put("depressionScore", depressionScore1);
        }
        return data;
    }

    // ????????????
    public PersonalReport.TestOverview getTestTotalScore(PersonalReportData2 data) {
        PersonalReport.TestOverview testOverview = new PersonalReport.TestOverview();
        // ??????
        List<PersonalReport.OverviewData> active = new ArrayList<>();
        // ????????????
        addTestTotal(active, 1546788164255932417L, data.getStudyStatusScore().toString());
        // ????????????
        addTestTotal(active, 1546788937710755842L, data.getBehaviorScore().toString());
        // ????????????
        addTestTotal(active, 1546789344491134978L, data.getMentalToughnessScore().toString());
        // ??????
        List<PersonalReport.OverviewData> negative = new ArrayList<>();
        // ????????????
        addTestTotal(negative, 1546789419250409474L, data.getOverallStressScore().toString());
        // ????????????
        addTestTotal(negative, 1546789515505491970L, data.getEmotionalIndexScore().toString());
        // ????????????
        addTestTotal(negative, 1546789785358622721L, data.getSleepIndexScore().toString());

        testOverview.setActive(active);
        testOverview.setNegative(negative);
        return testOverview;
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
                overviewData.setScore(dealRate2(Func.toDouble(score)).toString());
                overviewData.setFontColor(getFontColor(riskIndex));
                list.add(overviewData);
            }
        });
    }

    public List<FollowUserVO> getFollowUser(List<UserInfoVO> list1, List<UserInfoVO> list2, List<UserInfoVO> list3) {
        List<FollowUserVO> list = new ArrayList<>();
        // ???????????????
        int a = 0, b = 0, c = 0;
        if (ObjectUtils.isNotEmpty(list1)) {
            a = list1.size();
        }
        if (ObjectUtils.isNotEmpty(list2)) {
            b = list2.size();
        }
        if (ObjectUtils.isNotEmpty(list3)) {
            c = list3.size();
        }
        int max = getMax(a, b, c);
        for (int i = 0; i < max; i++) {
            FollowUserVO vo = new FollowUserVO();
            // ????????????
            if (ObjectUtils.isNotEmpty(list1)) {
                if (a > i) {
                    vo.setUserIdOne(list1.get(i).getUserId());
                    vo.setUserNameOne(list1.get(i).getUserName());
                }
            }
            if (ObjectUtils.isNotEmpty(list2)) {
                if (b > i) {
                    vo.setUserIdTwo(list2.get(i).getUserId());
                    vo.setUserNameTwo(list2.get(i).getUserName());
                }
            }
            if (ObjectUtils.isNotEmpty(list3)) {
                if (c > i) {
                    vo.setUserIdThree(list3.get(i).getUserId());
                    vo.setUserNameThree(list3.get(i).getUserName());
                }
            }
            list.add(vo);
        }
        return list;
    }

    private int getMax(int size1, int size2, int size3) {
        int i = (size1 > size2) ? size1 : size2;
        int max = i > size3 ? i : size3;
        return max;
    }

    /**
     * @param followMap
     * @param followLevelVO1
     * @param followLevelVO2
     * @param followLevelVO3
     * @param followLevelVO4
     * @param effPeople
     * @param reportData
     * @param type           1:??????  2:??????
     * @author hzl
     * @date 2022/8/17 13:42
     */
    private void getLevelRate(Map<Integer, List<FollowWarn>> followMap, FollowLevelVO followLevelVO1, FollowLevelVO followLevelVO2, FollowLevelVO followLevelVO3, FollowLevelVO followLevelVO4, long effPeople, ClassReportData reportData, Integer type) {
        DecimalFormat df = new DecimalFormat("#0.0");
        // ??????????????????
        followMap.forEach((integer, followWarns) -> {
            int people = followWarns.size();
            // ??????
            double v = 0;
            if (effPeople != 0) {
                v = Func.toDouble(people) / Func.toDouble(effPeople) * 100;
            }
            String completionRate = dealRate(v);
            if (integer == 0) {
                followLevelVO4.setPeople(people);
                followLevelVO4.setClassRatio(completionRate);
                if (type == 1) {
                    reportData.setStudentRate(v);
                } else if (type == 2) {
                    reportData.setTeacherRate(v);
                }
            } else if (integer == 1) {
                followLevelVO1.setPeople(people);
                followLevelVO1.setClassRatio(completionRate);
                if (type == 1) {
                    reportData.setStudentRateOne(v);
                } else if (type == 2) {
                    reportData.setTeacherRateOne(v);
                }
            } else if (integer == 2) {
                followLevelVO2.setPeople(people);
                followLevelVO2.setClassRatio(completionRate);
                if (type == 1) {
                    reportData.setStudentRateTwo(v);
                } else if (type == 2) {
                    reportData.setTeacherRateTwo(v);
                }
            } else if (integer == 3) {
                followLevelVO3.setPeople(people);
                followLevelVO3.setClassRatio(completionRate);
                if (type == 1) {
                    reportData.setStudentRateThree(v);
                } else if (type == 2) {
                    reportData.setTeacherRateThree(v);
                }
            }

        });
    }

    // ???????????????
    public String dealRate(double score) {
        if (Double.isNaN(score)) {
            return "0%";
        }
        if (score == 0) {
            return "0%";
        } else if (score == 1) {
            return "100%";
        } else {
            return String.format("%.1f", score) + "%";
        }
    }

    // ???????????????
    public Double dealRate2(double score) {
        if (score == 0 || Double.isNaN(score)) {
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

    /**
     * ?????????????????? 1??????  2????????????  3??????
     *
     * @param classAvgScore
     * @param gradeAvgScore
     * @param classScore
     * @param gradeScore
     * @param classPeople
     * @param gradePeople
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @author hzl
     * @date 2022/8/18 19:01
     */
    private Map<String, String> diffResult(double classAvgScore, double gradeAvgScore, double classScore, double gradeScore, int classPeople, int gradePeople) {
        Map<String, String> map = new HashMap<>();
        double v = (classScore / classPeople) + (gradeScore / gradePeople);
        double score;
        if (v == 0 || Double.isNaN(v)) {
            score = 0;
        } else {
            score = (classAvgScore - gradeAvgScore) / (Math.sqrt(v));
        }
        if (Double.isNaN(score)) {
            score = 0;
        }
        DecimalFormat df = new DecimalFormat("#0.0");
        String format = df.format(score);
        map.put("diff", format);
        if (score >= 1.96) {
            map.put("result", "1");
            map.put("explain", format + "(??????)");
            map.put("classDiff", "2");
        } else if (score < 1.96 && score > -1.96) {
            map.put("result", "2");
            map.put("explain", format + "(?????????)");
            map.put("classDiff", "1");
        } else {
            map.put("result", "3");
            map.put("explain", format + "(??????)");
            map.put("classDiff", "2");
        }

        return map;
    }
}
