package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mentpeak.common.util.DateUtil;
import org.mentpeak.common.util.IntervalUtil;
import org.mentpeak.common.util.MathUtil;
import org.mentpeak.core.auth.PlatformUser;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.test.entity.*;
import org.mentpeak.test.entity.mongo.GradeReport;
import org.mentpeak.test.entity.mongo.GradeReportData;
import org.mentpeak.test.entity.mongo.GroupsReport;
import org.mentpeak.test.entity.mongo.GroupsReport.*;
import org.mentpeak.test.mapper.*;
import org.mentpeak.test.service.ReportGroupsService;
import org.mentpeak.test.strategy.scoring.ColorEnum;
import org.mentpeak.test.vo.GFollowUserVO;
import org.mentpeak.test.vo.GTestModuleTwoVO;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 团体报告 服务实现
 *
 * @author demain_lee
 * @since 2022-08-31
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReportGroupsServiceImpl implements ReportGroupsService {

    private final MongoTemplate mongoTemplate;

    private final RedisTemplate redisTemplate;

    private final ReportGroupsMapper reportGroupsMapper;

    private final TestDimensionIndexConclusionMapper dimensionIndexConclusionMapper;

    private final TestIndexMapper indexMapper;

    private final TestDimensionIndexQuestionMapper dimensionIndexQuestionMapper;

    private final TestTaskMapper testTaskMapper;

    private final TestQuestionMapper questionMapper;

    private final TestTaskDepartmentMapper testTaskDepartmentMapper;

    @Override
    public GroupsReport reportInfo(Long taskId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("taskId").is(taskId));
        query.with(Sort.by((Order.desc("createTime"))));
        GroupsReport groupsReport = mongoTemplate.findOne(query, GroupsReport.class);
        PlatformUser user = SecureUtil.getUser();
        if ("5".equals(user.getRoleId())) {
            groupsReport.setIsVisible(0);
        } else {
            groupsReport.setIsVisible(1);
        }
        return Func.isNotEmpty(groupsReport) ? groupsReport : null;
    }

    @Override
    public boolean generateReport(Long taskId) {

        log.info("=== 开始生成团体报告 === {}", taskId);
        GroupsReport groupsReport = GroupsReport.builder().build();

        // 测评完成情况
        getEvaluationCompletion(groupsReport, taskId);

        // 基本信息
        getBasicInformation(groupsReport, taskId);

        // 心理健康评级
        getMentalHealthRatings(groupsReport, taskId);

        // 测评概况
        getTestOverview(groupsReport, taskId);

        // 指标数据
        GradeReportData indexGradeReportData = getIndexGradeReportData(taskId);

        // 学习状态
        getLearningStatus(groupsReport, taskId, indexGradeReportData);

        // 品行表现
        getBehavior(groupsReport, taskId, indexGradeReportData);

        // 心理韧性
        getMentalToughness(groupsReport, taskId, indexGradeReportData);

        // 综合压力
        getStressIndex(groupsReport, taskId, indexGradeReportData);

        // 情绪指数
        getEmotionalIndex(groupsReport, taskId, indexGradeReportData);

        // 睡眠指数
        getSleepIndex(groupsReport, taskId, indexGradeReportData);

        // 报告名字
        getReportName(groupsReport, taskId);

        groupsReport.setTaskId(taskId);
        groupsReport.setCreateTime(DateUtil.LocalDateTimeToStringTime(LocalDateTime.now()));
        mongoTemplate.insert(groupsReport);
        log.info("=== 团体报告已生成 === {}", taskId);
        return true;
    }

    /**
     * 报告名字
     *
     * @param groupsReport
     * @param taskId
     */
    void getReportName(GroupsReport groupsReport, Long taskId) {
        TestTask testTask = testTaskMapper.getTaskById(taskId);
        groupsReport.setReportName(testTask.getTaskName() + "心理测评报告");

    }

    /**
     * 综合压力
     *
     * @param groupsReport
     * @param taskId
     */
    void getStressIndex(GroupsReport groupsReport, Long taskId, GradeReportData indexGradeReportData) {

        StressIndexData baseData = new StressIndexData();

        // 总体结果
        TotalResult totalResult = new TotalResult();
        totalResult.setTitle("总体结果");
        totalResult.setLabel("本校得分");
        Object overallStressScore = getTestPeopleAvgData(taskId, "overallStressScore");
        Double mentalScore = Func.toDouble(overallStressScore);
        totalResult.setScore(MathUtil.formatRate(mentalScore));
        TestDimensionIndexConclusion dimensionConclusion = getDimensionConclusion(
                MathUtil.formatRate(mentalScore),
                1546789419250409474L);
        Integer riskIndex = dimensionConclusion.getRiskIndex();
        String riskResult = dimensionConclusion.getRiskResult();
        totalResult.setResult(riskResult);
        totalResult.setFontColor(getFontColor(riskIndex));

        baseData.setTotalResult(totalResult);

        // 图表数据
        List<ChartResultData> chartDataList = new LinkedList<>();

        Double studyStressScore = indexGradeReportData.getStudyStressScore();
        Double interpersonalStressScore = indexGradeReportData.getInterpersonalStressScore();
        Double punishmentStressScore = indexGradeReportData.getPunishmentStressScore();
        Double lossStressScore = indexGradeReportData.getLossStressScore();
        Double adaptationStressScore = indexGradeReportData.getAdaptationStressScore();

        String studyScore = MathUtil.formatRate(studyStressScore);
        String interpersonalScore = MathUtil.formatRate(interpersonalStressScore);
        String punishmentScore = MathUtil.formatRate(punishmentStressScore);
        String lossScore = MathUtil.formatRate(lossStressScore);
        String adaptScore = MathUtil.formatRate(adaptationStressScore);

        /**
         * 1546789419690811394	学习压力
         * 1546789426871459841	人际压力
         * 1546789434203103233	受惩罚压力
         * 1546789443141165057	丧失压力
         * 1546789455623413761	适应压力
         */
        // 学习压力
        TestIndex studyIndex = indexMapper.selectById(1546789419690811394L);
        ChartResultData studyChartResultData = new ChartResultData();
        studyChartResultData.setTitle(studyIndex.getName());
        studyChartResultData.setScore(studyScore);
        studyChartResultData.setResultDes(studyIndex.getRemark());
        studyChartResultData.setFontColor(ColorEnum.ORANGE.getValue());
        studyChartResultData.setChartColor(ColorEnum.ORANGE.getValue());
        chartDataList.add(studyChartResultData);

        // 人际压力
        TestIndex interpersonalIndex = indexMapper.selectById(1546789426871459841L);
        ChartResultData interpersonalChartResultData = new ChartResultData();
        interpersonalChartResultData.setTitle(interpersonalIndex.getName());
        interpersonalChartResultData.setScore(interpersonalScore);
        interpersonalChartResultData.setResultDes(interpersonalIndex.getRemark());
        interpersonalChartResultData.setFontColor(ColorEnum.ORANGE.getValue());
        interpersonalChartResultData.setChartColor(ColorEnum.ORANGE.getValue());
        chartDataList.add(interpersonalChartResultData);

        // 受惩罚压力
        TestIndex punishmentIndex = indexMapper.selectById(1546789434203103233L);
        ChartResultData punishmentChartResultData = new ChartResultData();
        punishmentChartResultData.setTitle(punishmentIndex.getName());
        punishmentChartResultData.setScore(punishmentScore);
        punishmentChartResultData.setResultDes(punishmentIndex.getRemark());
        punishmentChartResultData.setFontColor(ColorEnum.ORANGE.getValue());
        punishmentChartResultData.setChartColor(ColorEnum.ORANGE.getValue());
        chartDataList.add(punishmentChartResultData);

        // 丧失压力
        TestIndex lossIndex = indexMapper.selectById(1546789443141165057L);
        ChartResultData lossChartResultData = new ChartResultData();
        lossChartResultData.setTitle(lossIndex.getName());
        lossChartResultData.setScore(lossScore);
        lossChartResultData.setResultDes(lossIndex.getRemark());
        lossChartResultData.setFontColor(ColorEnum.ORANGE.getValue());
        lossChartResultData.setChartColor(ColorEnum.ORANGE.getValue());
        chartDataList.add(lossChartResultData);

        // 适应压力
        TestIndex adaptIndex = indexMapper.selectById(1546789455623413761L);
        ChartResultData adaptChartResultData = new ChartResultData();
        adaptChartResultData.setTitle(adaptIndex.getName());
        adaptChartResultData.setScore(adaptScore);
        adaptChartResultData.setResultDes(adaptIndex.getRemark());
        adaptChartResultData.setFontColor(ColorEnum.ORANGE.getValue());
        adaptChartResultData.setChartColor(ColorEnum.ORANGE.getValue());
        chartDataList.add(adaptChartResultData);

        baseData.setChartData(chartDataList);

        // 图表数据表格
        List<TableData> dataSortList = getStressIndexScoreTableData(indexGradeReportData, taskId);
        baseData.setTableData(dataSortList);

        // 事件分布 图表数据
        getEventDistributionData(baseData, indexGradeReportData, taskId);

        groupsReport.setStressIndex(baseData);


    }


    /**
     * 事件分布 图表数据
     *
     * @param baseData
     * @param indexGradeReportData
     * @param taskId
     */
    void getEventDistributionData(StressIndexData baseData, GradeReportData indexGradeReportData,
                                  Long taskId) {

        /**
         *  综合压力	影响事件分布 影响事件分布是指题目未选择“0”分选项的人数÷有效人数*100%
         * 影响程度排名是按照每个事件的影响程度从大到小进行排序，取前3位输出，并在柱状图进行标注
         *
         *  1546789419250409474	综合压力
         */

        List<TestIndex> testIndexList = indexMapper.indexList(1546789419250409474L);

        Integer testPeople = indexGradeReportData.getTestPeople();
        Integer invalidPeople = indexGradeReportData.getInvalidPeople();
        Integer validCount = testPeople - invalidPeople;

        if (validCount > 0) {

            String key = "education" + ":" + "stress" + ":" + "statistic" + ":" + taskId + ":";

            List<TypeResultData> typeResultDataList = new LinkedList<>();

            List<ChartResultData> allResultDataList = new LinkedList<>();

            testIndexList.forEach(index -> {

                TypeResultData typeResultData = new TypeResultData();
                typeResultData.setTitle(index.getName());
                List resultDataList = new LinkedList<ChartResultData>();

                // 题干信息
                List<TestQuestion> questionList = questionMapper.questionList(index.getId());

                questionList.forEach(question -> {

                    ChartResultData chartResultData = new ChartResultData();
                    if(question.getId() == 1546789455841517570L){
                        chartResultData.setTitle("外部原因导致生活规律改变");
                    }else{
                        chartResultData.setTitle(question.getTitle());
                    }

                    String qKey = key + index.getId() + ":" + question.getId();
                    Long count = redisTemplate.opsForSet().size(qKey);
                    chartResultData.setScore(getStressScore(count, validCount));
                    // 默认设置蓝色
                    chartResultData.setChartColor(ColorEnum.BLUE.getValue());

                    resultDataList.add(chartResultData);

                    allResultDataList.add(chartResultData);

                });

                typeResultData.setChartData(resultDataList);

                typeResultDataList.add(typeResultData);

            });

            // 待排序
            /**
             * 所有集合 allResultDataList 按分数排序
             */

            List<ChartResultData> resultDataList = allResultDataList.stream()
                    .sorted(Comparator.comparing(ChartResultData::getScore).reversed()).collect(
                            Collectors.toList());

            List<ImpactEvents> impactEventsList = new LinkedList<>();

            for (int i = 0; i <= 2; i++) {
                ChartResultData chartResultData = resultDataList.get(i);
                ImpactEvents impactEvents = new ImpactEvents();
                impactEvents.setStressEvents(chartResultData.getTitle());
                impactEvents.setRank(Func.toStr(i + 1));
                impactEvents.setPeoplePercentage(MathUtil.dealRate(Func.toDouble(chartResultData.getScore())));
                impactEventsList.add(impactEvents);
            }

            baseData.setImpactEvents(impactEventsList);

            // 修改颜色  排序
            typeResultDataList.forEach(typeResultData -> {

                // 排序
                List<ChartResultData> sortChartResultDataList = typeResultData.getChartData().stream()
                        .sorted(Comparator.comparing(ChartResultData::getScore).reversed()).collect(
                                Collectors.toList());
                // 如果包含程度排名前三数据 修改柱状图颜色为橙色
                sortChartResultDataList.forEach(chartResultData -> {
                    Boolean result = isContains(impactEventsList, chartResultData.getTitle());
                    if (result) {
                        chartResultData.setChartColor(ColorEnum.ORANGE.getValue());
                    }

                });

                typeResultData.setChartData(sortChartResultDataList);

            });

            baseData.setChartList(typeResultDataList);
        } else {
            baseData.setChartList(new ArrayList<TypeResultData>());
        }


    }

    static Boolean isContains(List<ImpactEvents> list, String arr) {
        // 如果包含程度排名前三数据 返回 true
        AtomicBoolean result = new AtomicBoolean(false);
        list.forEach(impactEvents -> {
            if (impactEvents.getStressEvents().equals(arr)) {
                result.set(true);
            }
        });
        return result.get();

    }

    /**
     * @param p1
     * @param p2
     * @return
     */
    static String getStressScore(Long p1, Integer p2) {
        String percent = "0";
        if (p2 != 0) {
            //  分选项的人数÷有效人数*100%
            String d1 = MathUtil.divide(Func.toStr(p1), Func.toStr(p2));
            percent = MathUtil.multiply(d1, "100");
        }
        return percent;
    }

    /**
     * 睡眠指数
     *
     * @param groupsReport
     * @param taskId
     */
    void getSleepIndex(GroupsReport groupsReport, Long taskId, GradeReportData indexGradeReportData) {

        BaseData baseData = new BaseData();

        // 总体结果
        TotalResult totalResult = new TotalResult();
        totalResult.setTitle("总体结果");
        totalResult.setLabel("本校得分");
        Object sleepIndexScore = getTestPeopleAvgData(taskId, "sleepIndexScore");
        String mentalScore = MathUtil.formatRate(Func.toDouble(sleepIndexScore));
        totalResult.setScore(mentalScore);
        TestDimensionIndexConclusion dimensionConclusion = getDimensionConclusion(mentalScore,
                1546789785358622721L);
        Integer riskIndex = dimensionConclusion.getRiskIndex();
        String riskResult = dimensionConclusion.getRiskResult();
        totalResult.setResult(riskResult);
        totalResult.setFontColor(getFontColor(riskIndex));

        baseData.setTotalResult(totalResult);

        // 图表数据表格
        List<TableData> dataSortList = getSleepIndexScoreTableData(indexGradeReportData, taskId);
        baseData.setTableData(dataSortList);
        groupsReport.setSleepIndex(baseData);

    }

    /**
     * 情绪指数
     *
     * @param groupsReport
     * @param taskId
     */
    void getEmotionalIndex(GroupsReport groupsReport, Long taskId, GradeReportData indexGradeReportData) {

        BaseResultData baseData = new BaseResultData();

        // 总体结果
        TotalResult totalResult = new TotalResult();
        totalResult.setTitle("总体结果");
        totalResult.setLabel("本校得分");
        Object emotionalIndexScore = getTestPeopleAvgData(taskId, "emotionalIndexScore");
        String mentalScore = MathUtil.formatRate(Func.toDouble(emotionalIndexScore));
        totalResult.setScore(mentalScore);
        TestDimensionIndexConclusion dimensionConclusion = getDimensionConclusion(mentalScore,
                1546789515505491970L);
        Integer riskIndex = dimensionConclusion.getRiskIndex();
        String riskResult = dimensionConclusion.getRiskResult();
        totalResult.setResult(riskResult);
        totalResult.setFontColor(getFontColor(riskIndex));

        baseData.setTotalResult(totalResult);

        // 图表数据
        List<ChartResultData> chartDataList = new LinkedList<>();

        Double compulsionScore = indexGradeReportData.getCompulsionScore();
        Double paranoiaScore = indexGradeReportData.getParanoiaScore();
        Double hostilityScore = indexGradeReportData.getHostilityScore();
        Double interpersonalSensitivityScore = indexGradeReportData.getInterpersonalSensitivityScore();
        Double anxietyScore = indexGradeReportData.getAnxietyScore();
        Double depressionScore = indexGradeReportData.getDepressionScore();

        String compuScore = MathUtil.formatRate(compulsionScore);
        String parScore = MathUtil.formatRate(paranoiaScore);
        String hosScore = MathUtil.formatRate(hostilityScore);
        String interScore = MathUtil.formatRate(interpersonalSensitivityScore);
        String anxScore = MathUtil.formatRate(anxietyScore);
        String depressScore = MathUtil.formatRate(depressionScore);

        /**
         * 1546789515920728066	强迫
         * 1546789524955258882	偏执
         * 1546789530986668033	敌对
         * 1546789540155416578	人际敏感
         * 1546789547860353026	焦虑
         * 1546789556760666113	抑郁
         */
        // 强迫
        TestIndex compuIndex = indexMapper.selectById(1546789515920728066L);
        ChartResultData compuChartResultData = new ChartResultData();
        compuChartResultData.setTitle(compuIndex.getName());
        compuChartResultData.setScore(compuScore);
        compuChartResultData.setResultDes(compuIndex.getRemark());
        compuChartResultData.setFontColor(ColorEnum.ORANGE.getValue());
        compuChartResultData.setChartColor(ColorEnum.ORANGE.getValue());
        chartDataList.add(compuChartResultData);

        // 偏执
        TestIndex parIndex = indexMapper.selectById(1546789524955258882L);
        ChartResultData parChartResultData = new ChartResultData();
        parChartResultData.setTitle(parIndex.getName());
        parChartResultData.setScore(parScore);
        parChartResultData.setResultDes(parIndex.getRemark());
        parChartResultData.setFontColor(ColorEnum.ORANGE.getValue());
        parChartResultData.setChartColor(ColorEnum.ORANGE.getValue());
        chartDataList.add(parChartResultData);

        // 敌对
        TestIndex hosIndex = indexMapper.selectById(1546789530986668033L);
        ChartResultData hosChartResultData = new ChartResultData();
        hosChartResultData.setTitle(hosIndex.getName());
        hosChartResultData.setScore(hosScore);
        hosChartResultData.setResultDes(hosIndex.getRemark());
        hosChartResultData.setFontColor(ColorEnum.ORANGE.getValue());
        hosChartResultData.setChartColor(ColorEnum.ORANGE.getValue());
        chartDataList.add(hosChartResultData);

        // 人际敏感
        TestIndex interIndex = indexMapper.selectById(1546789540155416578L);
        ChartResultData interChartResultData = new ChartResultData();
        interChartResultData.setTitle(interIndex.getName());
        interChartResultData.setScore(interScore);
        interChartResultData.setResultDes(interIndex.getRemark());
        interChartResultData.setFontColor(ColorEnum.ORANGE.getValue());
        interChartResultData.setChartColor(ColorEnum.ORANGE.getValue());
        chartDataList.add(interChartResultData);

        // 焦虑
        TestIndex anxIndex = indexMapper.selectById(1546789547860353026L);
        ChartResultData anxChartResultData = new ChartResultData();
        anxChartResultData.setTitle(anxIndex.getName());
        anxChartResultData.setScore(anxScore);
        anxChartResultData.setResultDes(anxIndex.getRemark());
        anxChartResultData.setFontColor(ColorEnum.ORANGE.getValue());
        anxChartResultData.setChartColor(ColorEnum.ORANGE.getValue());
        chartDataList.add(anxChartResultData);

        // 抑郁
        TestIndex depressIndex = indexMapper.selectById(1546789556760666113L);
        ChartResultData depressChartResultData = new ChartResultData();
        depressChartResultData.setTitle(depressIndex.getName());
        depressChartResultData.setScore(depressScore);
        depressChartResultData.setResultDes(depressIndex.getRemark());
        depressChartResultData.setFontColor(ColorEnum.ORANGE.getValue());
        depressChartResultData.setChartColor(ColorEnum.ORANGE.getValue());
        chartDataList.add(depressChartResultData);

        LinkedList<TypeResultData> resultDataList = new LinkedList<>();
        TypeResultData schoolTypeResultData = new TypeResultData();
        schoolTypeResultData.setTitle("全校");
        schoolTypeResultData.setChartData(chartDataList);

        // 常模图表数据
        List<ChartResultData> modelDataList = new LinkedList<>();

        /**
         *
         * 强迫21分
         * 偏执15.3分
         * 敌对16.3分
         * 人际敏感17.5分
         * 焦虑14.3分
         * 抑郁18.9分
         */
        ChartResultData data1 = new ChartResultData();
        data1.setTitle("强迫");
        data1.setScore("21");
        ChartResultData data2 = new ChartResultData();
        data2.setTitle("偏执");
        data2.setScore("15.3");
        ChartResultData data3 = new ChartResultData();
        data3.setTitle("敌对");
        data3.setScore("16.3");
        ChartResultData data4 = new ChartResultData();
        data4.setTitle("人际敏感");
        data4.setScore("17.5");
        ChartResultData data5 = new ChartResultData();
        data5.setTitle("焦虑");
        data5.setScore("14.3");
        ChartResultData data6 = new ChartResultData();
        data6.setTitle("抑郁");
        data6.setScore("18.9");

        modelDataList.add(data1);
        modelDataList.add(data2);
        modelDataList.add(data3);
        modelDataList.add(data4);
        modelDataList.add(data5);
        modelDataList.add(data6);

        TypeResultData modelTypeResultData = new TypeResultData();
        modelTypeResultData.setTitle("常模");
        modelTypeResultData.setChartData(modelDataList);

        resultDataList.add(schoolTypeResultData);
        resultDataList.add(modelTypeResultData);
        baseData.setChartData(resultDataList);

        // 图表数据表格
        List<TableData> dataSortList = getEmotionalIndexScoreTableData(indexGradeReportData, taskId);
        baseData.setTableData(dataSortList);
        groupsReport.setEmotionalIndex(baseData);

    }

    /**
     * 心理韧性
     *
     * @param groupsReport
     * @param taskId
     */
    void getMentalToughness(GroupsReport groupsReport, Long taskId, GradeReportData indexGradeReportData) {

        BaseData baseData = new BaseData();

        // 总体结果
        TotalResult totalResult = new TotalResult();
        totalResult.setTitle("总体结果");
        totalResult.setLabel("本校得分");
        Object mentalToughnessScore = getTestPeopleAvgData(taskId, "mentalToughnessScore");
        String mentalScore = MathUtil.formatRate(Func.toDouble(mentalToughnessScore));
        totalResult.setScore(mentalScore);
        TestDimensionIndexConclusion dimensionConclusion = getDimensionConclusion(mentalScore,
                1546789344491134978L);
        Integer riskIndex = dimensionConclusion.getRiskIndex();
        String riskResult = dimensionConclusion.getRiskResult();
        totalResult.setResult(riskResult);
        totalResult.setFontColor(getFontColor(riskIndex));

        baseData.setTotalResult(totalResult);

        // 图表数据
        List<ChartResultData> chartDataList = new LinkedList<>();

        Double emotionManagementScore = indexGradeReportData.getEmotionManagementScore();
        Double goalMotivationScore = indexGradeReportData.getGoalMotivationScore();
        Double positiveAttentionScore = indexGradeReportData.getPositiveAttentionScore();
        Double schoolSupportScore = indexGradeReportData.getSchoolSupportScore();
        Double interpersonalSupportScore = indexGradeReportData.getInterpersonalSupportScore();
        Double familySupportScore = indexGradeReportData.getFamilySupportScore();

        String emotionScore = MathUtil.formatRate(emotionManagementScore);
        String goalScore = MathUtil.formatRate(goalMotivationScore);
        String positScore = MathUtil.formatRate(positiveAttentionScore);
        String schoolScore = MathUtil.formatRate(schoolSupportScore);
        String interScore = MathUtil.formatRate(interpersonalSupportScore);
        String familyScore = MathUtil.formatRate(familySupportScore);

        /**
         * 1546789344952508417	情绪管理
         * 1546789349885009922	目标激励
         * 1546789354800734210	积极关注
         * 1546789359766790145	学校支持
         * 1546789364607016961	人际支持
         * 1546789369476603906	家庭支持
         */
        // 情绪管理
        TestIndex emotionIndex = indexMapper.selectById(1546789344952508417L);
        ChartResultData emotionChartResultData = new ChartResultData();
        emotionChartResultData.setTitle(emotionIndex.getName());
        emotionChartResultData.setScore(emotionScore);
        emotionChartResultData.setResultDes(emotionIndex.getRemark());
        emotionChartResultData.setFontColor(ColorEnum.BLUE.getValue());
        emotionChartResultData.setChartColor(ColorEnum.BLUE.getValue());
        chartDataList.add(emotionChartResultData);

        // 目标激励
        TestIndex goalIndex = indexMapper.selectById(1546789349885009922L);
        ChartResultData goalChartResultData = new ChartResultData();
        goalChartResultData.setTitle(goalIndex.getName());
        goalChartResultData.setScore(goalScore);
        goalChartResultData.setResultDes(goalIndex.getRemark());
        goalChartResultData.setFontColor(ColorEnum.BLUE.getValue());
        goalChartResultData.setChartColor(ColorEnum.BLUE.getValue());
        chartDataList.add(goalChartResultData);

        // 积极关注
        TestIndex positIndex = indexMapper.selectById(1546789354800734210L);
        ChartResultData positChartResultData = new ChartResultData();
        positChartResultData.setTitle(positIndex.getName());
        positChartResultData.setScore(positScore);
        positChartResultData.setResultDes(positIndex.getRemark());
        positChartResultData.setFontColor(ColorEnum.BLUE.getValue());
        positChartResultData.setChartColor(ColorEnum.BLUE.getValue());
        chartDataList.add(positChartResultData);

        // 学校支持
        TestIndex schoolIndex = indexMapper.selectById(1546789359766790145L);
        ChartResultData schoolChartResultData = new ChartResultData();
        schoolChartResultData.setTitle(schoolIndex.getName());
        schoolChartResultData.setScore(schoolScore);
        schoolChartResultData.setResultDes(schoolIndex.getRemark());
        schoolChartResultData.setFontColor(ColorEnum.BLUE.getValue());
        schoolChartResultData.setChartColor(ColorEnum.BLUE.getValue());
        chartDataList.add(schoolChartResultData);

        // 人际支持
        TestIndex interIndex = indexMapper.selectById(1546789364607016961L);
        ChartResultData interChartResultData = new ChartResultData();
        interChartResultData.setTitle(interIndex.getName());
        interChartResultData.setScore(interScore);
        interChartResultData.setResultDes(interIndex.getRemark());
        interChartResultData.setFontColor(ColorEnum.BLUE.getValue());
        interChartResultData.setChartColor(ColorEnum.BLUE.getValue());
        chartDataList.add(interChartResultData);

        // 家庭支持
        TestIndex familyIndex = indexMapper.selectById(1546789369476603906L);
        ChartResultData familyChartResultData = new ChartResultData();
        familyChartResultData.setTitle(familyIndex.getName());
        familyChartResultData.setScore(familyScore);
        familyChartResultData.setResultDes(familyIndex.getRemark());
        familyChartResultData.setFontColor(ColorEnum.BLUE.getValue());
        familyChartResultData.setChartColor(ColorEnum.BLUE.getValue());
        chartDataList.add(familyChartResultData);

        baseData.setChartData(chartDataList);

        // 图表数据表格
        List<TableData> dataSortList = getMentalToughnessScoreTableData(indexGradeReportData, taskId);
        baseData.setTableData(dataSortList);
        groupsReport.setMentalToughness(baseData);

    }

    /**
     * 品行表现
     *
     * @param groupsReport
     * @param taskId
     */
    void getBehavior(GroupsReport groupsReport, Long taskId, GradeReportData indexGradeReportData) {

        BaseData baseData = new BaseData();

        // 总体结果
        TotalResult totalResult = new TotalResult();
        totalResult.setTitle("总体结果");
        totalResult.setLabel("本校得分");
        Object behaviorScore = getTestPeopleAvgData(taskId, "behaviorScore");
        String beScore = MathUtil.formatRate(Func.toDouble(behaviorScore));
        totalResult.setScore(beScore);
        TestDimensionIndexConclusion dimensionConclusion = getDimensionConclusion(beScore,
                1546788937710755842L);
        Integer riskIndex = dimensionConclusion.getRiskIndex();
        String riskResult = dimensionConclusion.getRiskResult();
        totalResult.setResult(riskResult);
        totalResult.setFontColor(getFontColor(riskIndex));

        baseData.setTotalResult(totalResult);

        // 图表数据
        List<ChartResultData> chartDataList = new LinkedList<>();

        Double moralScore = indexGradeReportData.getMoralScore();
        Double stabilityScore = indexGradeReportData.getStabilityScore();
        Double disciplineScore = indexGradeReportData.getDisciplineScore();
        Double otherPerformanceScore = indexGradeReportData.getOtherPerformanceScore();

        String mScore = MathUtil.formatRate(moralScore);
        String staScore = MathUtil.formatRate(stabilityScore);
        String disScore = MathUtil.formatRate(disciplineScore);
        String otherScore = MathUtil.formatRate(otherPerformanceScore);

        /**
         * 1546788938151157761	道德性
         * 1546788944375504898	稳定性
         * 1546788949077319682	纪律性
         * 1546788953720414210	其他表现
         */
        // 道德性
        TestIndex moralIndex = indexMapper.selectById(1546788938151157761L);
        ChartResultData moralChartResultData = new ChartResultData();
        moralChartResultData.setTitle(moralIndex.getName());
        moralChartResultData.setScore(mScore);
        moralChartResultData.setResultDes(moralIndex.getRemark());
        moralChartResultData.setFontColor(ColorEnum.BLUE.getValue());
        moralChartResultData.setChartColor(ColorEnum.BLUE.getValue());
        chartDataList.add(moralChartResultData);

        // 稳定性
        TestIndex stabilityIndex = indexMapper.selectById(1546788944375504898L);
        ChartResultData stabilityChartResultData = new ChartResultData();
        stabilityChartResultData.setTitle(stabilityIndex.getName());
        stabilityChartResultData.setScore(staScore);
        stabilityChartResultData.setResultDes(stabilityIndex.getRemark());
        stabilityChartResultData.setFontColor(ColorEnum.BLUE.getValue());
        stabilityChartResultData.setChartColor(ColorEnum.BLUE.getValue());
        chartDataList.add(stabilityChartResultData);

        // 纪律性
        TestIndex disciplineIndex = indexMapper.selectById(1546788949077319682L);
        ChartResultData disciplineChartResultData = new ChartResultData();
        disciplineChartResultData.setTitle(disciplineIndex.getName());
        disciplineChartResultData.setScore(disScore);
        disciplineChartResultData.setResultDes(disciplineIndex.getRemark());
        disciplineChartResultData.setFontColor(ColorEnum.BLUE.getValue());
        disciplineChartResultData.setChartColor(ColorEnum.BLUE.getValue());
        chartDataList.add(disciplineChartResultData);

        // 其他表现
        TestIndex otherIndex = indexMapper.selectById(1546788953720414210L);
        ChartResultData otherChartResultData = new ChartResultData();
        otherChartResultData.setTitle(otherIndex.getName());
        otherChartResultData.setScore(otherScore);
        otherChartResultData.setResultDes(otherIndex.getRemark());
        otherChartResultData.setFontColor(ColorEnum.BLUE.getValue());
        otherChartResultData.setChartColor(ColorEnum.BLUE.getValue());
        chartDataList.add(otherChartResultData);

        baseData.setChartData(chartDataList);

        // 图表数据表格
        List<TableData> dataSortList = getBehaviorTableData(indexGradeReportData, taskId);
        baseData.setTableData(dataSortList);
        groupsReport.setBehavior(baseData);

    }


    /**
     * 学习状态
     *
     * @param groupsReport
     * @param taskId
     */
    void getLearningStatus(GroupsReport groupsReport, Long taskId, GradeReportData indexGradeReportData) {

        BaseData baseData = new BaseData();

        // 总体结果
        TotalResult totalResult = new TotalResult();
        totalResult.setTitle("总体结果");
        totalResult.setLabel("本校得分");
        Object studyStatusScore = getTestPeopleAvgData(taskId, "studyStatusScore");
        String stuScore = MathUtil.formatRate(Func.toDouble(studyStatusScore));
        totalResult.setScore(stuScore);
        TestDimensionIndexConclusion dimensionConclusion = getDimensionConclusion(stuScore,
                1546788164255932417L);
        Integer riskIndex = dimensionConclusion.getRiskIndex();
        String riskResult = dimensionConclusion.getRiskResult();
        totalResult.setResult(riskResult);
        totalResult.setFontColor(getFontColor(riskIndex));

        baseData.setTotalResult(totalResult);

        // 图表数据
        List<ChartResultData> chartDataList = new LinkedList<>();

        Double learningAttitudeScore = indexGradeReportData.getLearningAttitudeScore();
        Double timeManagementScore = indexGradeReportData.getTimeManagementScore();
        Double learningBurnoutScore = indexGradeReportData.getLearningBurnoutScore();

        String learnScore = MathUtil.formatRate(learningAttitudeScore);
        String timeScore = MathUtil.formatRate(timeManagementScore);
        String lScore = MathUtil.formatRate(learningBurnoutScore);

        /**
         * 1546788164788609025
         * 1546788174330650626
         * 1546788189962821634
         */
        // 学习态度
        TestIndex learnIndex = indexMapper.selectById(1546788164788609025L);
        ChartResultData leChartResultData = new ChartResultData();
        leChartResultData.setTitle(learnIndex.getName());
        leChartResultData.setScore(learnScore);
        leChartResultData.setResultDes(learnIndex.getRemark());
        leChartResultData.setFontColor(ColorEnum.BLUE.getValue());
        leChartResultData.setChartColor(ColorEnum.BLUE.getValue());
        chartDataList.add(leChartResultData);

        // 时间管理
        TestIndex timeIndex = indexMapper.selectById(1546788174330650626L);
        ChartResultData timeChartResultData = new ChartResultData();
        timeChartResultData.setTitle(timeIndex.getName());
        timeChartResultData.setScore(timeScore);
        timeChartResultData.setResultDes(timeIndex.getRemark());
        timeChartResultData.setFontColor(ColorEnum.BLUE.getValue());
        timeChartResultData.setChartColor(ColorEnum.BLUE.getValue());
        chartDataList.add(timeChartResultData);

        // 学习倦怠
        TestIndex lIndex = indexMapper.selectById(1546788189962821634L);
        ChartResultData lChartResultData = new ChartResultData();
        lChartResultData.setTitle(lIndex.getName());
        lChartResultData.setScore(lScore);
        lChartResultData.setResultDes(lIndex.getRemark());
        lChartResultData.setFontColor(ColorEnum.ORANGE.getValue());
        lChartResultData.setChartColor(ColorEnum.ORANGE.getValue());
        chartDataList.add(lChartResultData);

        baseData.setChartData(chartDataList);

        // 图表数据表格
        List<TableData> dataSortList = getLearnTableData(indexGradeReportData, taskId);
        baseData.setTableData(dataSortList);
        groupsReport.setLearningStatus(baseData);

    }


    /**
     * 睡眠指数数据表格
     *
     * @param indexGradeReportData
     * @param taskId
     * @return
     */
    List<TableData> getSleepIndexScoreTableData(GradeReportData indexGradeReportData,
                                                Long taskId) {

        Integer sleepVeryBad = indexGradeReportData.getSleepVeryBad();
        Integer sleepBad = indexGradeReportData.getSleepBad();
        Integer sleepGenerally = indexGradeReportData.getSleepGenerally();
        Integer sleepGood = indexGradeReportData.getSleepGood();

        Integer sleepCount =
                sleepVeryBad + sleepBad + sleepGenerally
                        + sleepGood;

        List<TableData> tableDataList = new ArrayList<>();

        TableData sleepTableData = new TableData();

        sleepTableData.setIndex("睡眠指数");
        sleepTableData.setVeryPoor(
                sleepVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(sleepVeryBad.toString(), sleepCount.toString())), 1)
                        + ")");
        sleepTableData.setRatherPoor(
                sleepBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(sleepBad.toString(), sleepCount.toString())), 1)
                        + ")");
        sleepTableData.setGeneral(
                sleepGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(sleepGenerally.toString(), sleepCount.toString())),
                        1)
                        + ")");
        sleepTableData.setGood(
                sleepGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(sleepGood.toString(), sleepCount.toString())), 1)
                        + ")");

        tableDataList.add(sleepTableData);

        return tableDataList;
    }

    /**
     * 情绪指数数据表格
     *
     * @param indexGradeReportData
     * @param taskId
     * @return
     */
    List<TableData> getEmotionalIndexScoreTableData(GradeReportData indexGradeReportData,
                                                    Long taskId) {

        Integer compulsionScoreVeryBad = indexGradeReportData.getCompulsionScoreVeryBad();
        Integer compulsionScoreBad = indexGradeReportData.getCompulsionScoreBad();
        Integer compulsionScoreGenerally = indexGradeReportData.getCompulsionScoreGenerally();
        Integer compulsionScoreGood = indexGradeReportData.getCompulsionScoreGood();

        Integer paranoiaScoreVeryBad = indexGradeReportData.getParanoiaScoreVeryBad();
        Integer paranoiaScoreBad = indexGradeReportData.getParanoiaScoreBad();
        Integer paranoiaScoreGenerally = indexGradeReportData.getParanoiaScoreGenerally();
        Integer paranoiaScoreGood = indexGradeReportData.getParanoiaScoreGood();

        Integer hostilityScoreVeryBad = indexGradeReportData.getHostilityScoreVeryBad();
        Integer hostilityScoreBad = indexGradeReportData.getHostilityScoreBad();
        Integer hostilityScoreGenerally = indexGradeReportData.getHostilityScoreGenerally();
        Integer hostilityScoreGood = indexGradeReportData.getHostilityScoreGood();

        Integer interpersonalSensitivityScoreVeryBad = indexGradeReportData.getInterpersonalSensitivityScoreVeryBad();
        Integer interpersonalSensitivityScoreBad = indexGradeReportData.getInterpersonalSensitivityScoreBad();
        Integer interpersonalSensitivityScoreGenerally = indexGradeReportData.getInterpersonalSensitivityScoreGenerally();
        Integer interpersonalSensitivityScoreGood = indexGradeReportData.getInterpersonalSensitivityScoreGood();

        Integer anxietyScoreVeryBad = indexGradeReportData.getAnxietyScoreVeryBad();
        Integer anxietyScoreBad = indexGradeReportData.getAnxietyScoreBad();
        Integer anxietyScoreGenerally = indexGradeReportData.getAnxietyScoreGenerally();
        Integer anxietyScoreGood = indexGradeReportData.getAnxietyScoreGood();

        Integer depressionScoreVeryBad = indexGradeReportData.getDepressionScoreVeryBad();
        Integer depressionScoreBad = indexGradeReportData.getDepressionScoreBad();
        Integer depressionScoreGenerally = indexGradeReportData.getDepressionScoreGenerally();
        Integer depressionScoreGood = indexGradeReportData.getDepressionScoreGood();

        Integer compulsionCount =
                compulsionScoreVeryBad + compulsionScoreBad + compulsionScoreGenerally
                        + compulsionScoreGood;

        Integer paranoiaCount =
                paranoiaScoreVeryBad + paranoiaScoreBad + paranoiaScoreGenerally
                        + paranoiaScoreGood;

        Integer hostilityCount =
                hostilityScoreVeryBad + hostilityScoreBad + hostilityScoreGenerally
                        + hostilityScoreGood;

        Integer interCount =
                interpersonalSensitivityScoreVeryBad + interpersonalSensitivityScoreBad
                        + interpersonalSensitivityScoreGenerally
                        + interpersonalSensitivityScoreGood;

        Integer anxietyCount =
                anxietyScoreVeryBad + anxietyScoreBad
                        + anxietyScoreGenerally
                        + anxietyScoreGood;

        Integer depressionCount =
                depressionScoreVeryBad + depressionScoreBad + depressionScoreGenerally
                        + depressionScoreGood;

        List<TableData> tableDataList = new ArrayList<>();

        TableData compulsionTableData = new TableData();

        compulsionTableData.setIndex("强迫");
        compulsionTableData.setVeryPoor(
                compulsionScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(compulsionScoreVeryBad.toString(), compulsionCount.toString())), 1)
                        + ")");
        compulsionTableData.setRatherPoor(
                compulsionScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(compulsionScoreBad.toString(), compulsionCount.toString())), 1)
                        + ")");
        compulsionTableData.setGeneral(
                compulsionScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(compulsionScoreGenerally.toString(), compulsionCount.toString())),
                        1)
                        + ")");
        compulsionTableData.setGood(
                compulsionScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(compulsionScoreGood.toString(), compulsionCount.toString())), 1)
                        + ")");

        TableData paranoiaTableData = new TableData();

        paranoiaTableData.setIndex("偏执");
        paranoiaTableData.setVeryPoor(
                paranoiaScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(paranoiaScoreVeryBad.toString(), paranoiaCount.toString())), 1)
                        + ")");
        paranoiaTableData.setRatherPoor(
                paranoiaScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(paranoiaScoreBad.toString(), paranoiaCount.toString())), 1)
                        + ")");
        paranoiaTableData.setGeneral(
                paranoiaScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(paranoiaScoreGenerally.toString(), paranoiaCount.toString())), 1)
                        + ")");
        paranoiaTableData.setGood(
                paranoiaScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(paranoiaScoreGood.toString(), paranoiaCount.toString())), 1)
                        + ")");

        TableData hostilityTableData = new TableData();

        hostilityTableData.setIndex("敌对");
        hostilityTableData.setVeryPoor(
                hostilityScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(hostilityScoreVeryBad.toString(), hostilityCount.toString())), 1)
                        + ")");
        hostilityTableData.setRatherPoor(
                hostilityScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(hostilityScoreBad.toString(), hostilityCount.toString())), 1)
                        + ")");
        hostilityTableData.setGeneral(
                hostilityScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(hostilityScoreGenerally.toString(), hostilityCount.toString())),
                        1)
                        + ")");
        hostilityTableData.setGood(
                hostilityScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(hostilityScoreGood.toString(), hostilityCount.toString())), 1)
                        + ")");

        TableData interTableData = new TableData();

        interTableData.setIndex("人际敏感");
        interTableData.setVeryPoor(
                interpersonalSensitivityScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(interpersonalSensitivityScoreVeryBad.toString(),
                                interCount.toString())), 1)
                        + ")");
        interTableData.setRatherPoor(
                interpersonalSensitivityScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(interpersonalSensitivityScoreBad.toString(), interCount.toString())), 1)
                        + ")");
        interTableData.setGeneral(
                interpersonalSensitivityScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(interpersonalSensitivityScoreGenerally.toString(),
                                interCount.toString())), 1)
                        + ")");
        interTableData.setGood(
                interpersonalSensitivityScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(interpersonalSensitivityScoreGood.toString(), interCount.toString())),
                        1)
                        + ")");

        TableData anxietyTableData = new TableData();

        anxietyTableData.setIndex("焦虑");
        anxietyTableData.setVeryPoor(
                anxietyScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(anxietyScoreVeryBad.toString(), anxietyCount.toString())), 1)
                        + ")");
        anxietyTableData.setRatherPoor(
                anxietyScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(anxietyScoreBad.toString(), anxietyCount.toString())), 1)
                        + ")");
        anxietyTableData.setGeneral(
                anxietyScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(anxietyScoreGenerally.toString(), anxietyCount.toString())),
                        1)
                        + ")");
        anxietyTableData.setGood(
                anxietyScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(anxietyScoreGood.toString(), anxietyCount.toString())), 1)
                        + ")");

        TableData depressionTableData = new TableData();

        depressionTableData.setIndex("抑郁");
        depressionTableData.setVeryPoor(
                depressionScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(depressionScoreVeryBad.toString(), depressionCount.toString())), 1)
                        + ")");
        depressionTableData.setRatherPoor(
                depressionScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(depressionScoreBad.toString(), depressionCount.toString())), 1)
                        + ")");
        depressionTableData.setGeneral(
                depressionScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(depressionScoreGenerally.toString(), depressionCount.toString())), 1)
                        + ")");
        depressionTableData.setGood(
                depressionScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(depressionScoreGood.toString(), depressionCount.toString())), 1)
                        + ")");

        // 影响程度排名顺序
        /**
         * 积极指标的影响程度是指该指标所有题目未选择“4”分选项的人数÷该指标题目数÷有效人数*100%
         * 消极指标的影响程度是指该指标所有题目未选择“0”分选项的人数÷该指标题目数÷有效人数*100%
         * 影响程度排名是按照每个指标的影响程度从大到小进行排序
         */

        String compulsionKey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789515920728066L;
        String paranoiaKey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789524955258882L;
        String hostilitykey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789530986668033L;
        String interkey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789540155416578L;
        String anxietykey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789547860353026L;
        String depressionkey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789556760666113L;

        Long compulsionPeopleCount = redisTemplate.opsForSet().size(compulsionKey);
        Long paranoiaPeopleCount = redisTemplate.opsForSet().size(paranoiaKey);
        Long hostilityPeopleCount = redisTemplate.opsForSet().size(hostilitykey);
        Long interPeopleCount = redisTemplate.opsForSet().size(interkey);
        Long anxietyPeopleCount = redisTemplate.opsForSet().size(anxietykey);
        Long depressionPeopleCount = redisTemplate.opsForSet().size(depressionkey);

        Long compulsionQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789515920728066L));

        Long paranoiaQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789524955258882L));

        Long hostilityQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789530986668033L));

        Long interQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789540155416578L));

        Long anxietyQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789547860353026L));

        Long depressionQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789556760666113L));

        Integer testPeople = indexGradeReportData.getTestPeople();
        Integer invalidPeople = indexGradeReportData.getInvalidPeople();
        int validCount = testPeople - invalidPeople;

        String compulsionImpactScore = "0";
        String paranoiaImpactScore = "0";
        String hostilityImpactScore = "0";
        String interImpactScore = "0";
        String anxietyImpactScore = "0";
        String depressionImpactScore = "0";

        if (validCount > 0) {

            // 影响程度分
            compulsionImpactScore = getScore(compulsionPeopleCount, compulsionQuestionCount,
                    validCount);
            paranoiaImpactScore = getScore(paranoiaPeopleCount, paranoiaQuestionCount, validCount);
            hostilityImpactScore = getScore(hostilityPeopleCount, hostilityQuestionCount,
                    validCount);
            interImpactScore = getScore(interPeopleCount, interQuestionCount, validCount);
            anxietyImpactScore = getScore(anxietyPeopleCount, anxietyQuestionCount, validCount);
            depressionImpactScore = getScore(depressionPeopleCount, depressionQuestionCount,
                    validCount);
        }


        compulsionTableData.setRankScore(compulsionImpactScore);
        paranoiaTableData.setRankScore(paranoiaImpactScore);
        hostilityTableData.setRankScore(hostilityImpactScore);
        interTableData.setRankScore(interImpactScore);
        anxietyTableData.setRankScore(anxietyImpactScore);
        depressionTableData.setRankScore(depressionImpactScore);

        tableDataList.add(compulsionTableData);
        tableDataList.add(paranoiaTableData);
        tableDataList.add(hostilityTableData);
        tableDataList.add(interTableData);
        tableDataList.add(anxietyTableData);
        tableDataList.add(depressionTableData);

        // 影响程度排名
        List<TableData> dataSortList = tableDataList.stream()
                .sorted(Comparator.comparing(TableData::getRankScore).reversed()).collect(
                        Collectors.toList());

        dataSortList.forEach(consumerWithIndex((tableData, index) -> tableData.setRank(index + 1)));

        return dataSortList;
    }


    /**
     * 综合压力图表数据表格
     *
     * @param indexGradeReportData
     * @param taskId
     * @return
     */
    List<TableData> getStressIndexScoreTableData(GradeReportData indexGradeReportData,
                                                 Long taskId) {

        Integer studyStressScoreVeryBad = indexGradeReportData.getStudyStressScoreVeryBad();
        Integer studyStressScoreBad = indexGradeReportData.getStudyStressScoreBad();
        Integer studyStressScoreGenerally = indexGradeReportData.getStudyStressScoreGenerally();
        Integer studyStressScoreGood = indexGradeReportData.getStudyStressScoreGood();

        Integer interpersonalStressScoreVeryBad = indexGradeReportData.getInterpersonalStressScoreVeryBad();
        Integer interpersonalStressScoreBad = indexGradeReportData.getInterpersonalStressScoreBad();
        Integer interpersonalStressScoreGenerally = indexGradeReportData.getInterpersonalStressScoreGenerally();
        Integer interpersonalStressScoreGood = indexGradeReportData.getInterpersonalStressScoreGood();

        Integer punishmentStressScoreVeryBad = indexGradeReportData.getPunishmentStressScoreVeryBad();
        Integer punishmentStressScoreBad = indexGradeReportData.getPunishmentStressScoreBad();
        Integer punishmentStressScoreGenerally = indexGradeReportData.getPunishmentStressScoreGenerally();
        Integer punishmentStressScoreGood = indexGradeReportData.getPunishmentStressScoreGood();

        Integer lossStressScoreVeryBad = indexGradeReportData.getLossStressScoreVeryBad();
        Integer lossStressScoreBad = indexGradeReportData.getLossStressScoreBad();
        Integer lossStressScoreGenerally = indexGradeReportData.getLossStressScoreGenerally();
        Integer lossStressScoreGood = indexGradeReportData.getLossStressScoreGood();

        Integer adaptationStressScoreVeryBad = indexGradeReportData.getAdaptationStressScoreVeryBad();
        Integer adaptationStressScoreBad = indexGradeReportData.getAdaptationStressScoreBad();
        Integer adaptationStressScoreGenerally = indexGradeReportData.getAdaptationStressScoreGenerally();
        Integer adaptationStressScoreGood = indexGradeReportData.getAdaptationStressScoreGood();

        Integer studyCount =
                studyStressScoreVeryBad + studyStressScoreBad + studyStressScoreGenerally
                        + studyStressScoreGood;

        Integer interpersonalCount =
                interpersonalStressScoreVeryBad + interpersonalStressScoreBad
                        + interpersonalStressScoreGenerally
                        + interpersonalStressScoreGood;

        Integer punishmentCount =
                punishmentStressScoreVeryBad + punishmentStressScoreBad + punishmentStressScoreGenerally
                        + punishmentStressScoreGood;

        Integer lossCount =
                lossStressScoreVeryBad + lossStressScoreBad + lossStressScoreGenerally
                        + lossStressScoreGood;

        Integer adaptationCount =
                adaptationStressScoreVeryBad + adaptationStressScoreBad
                        + adaptationStressScoreGenerally
                        + adaptationStressScoreGood;

        List<TableData> tableDataList = new ArrayList<>();

        TableData studyTableData = new TableData();

        studyTableData.setIndex("学习压力");
        studyTableData.setVeryPoor(
                studyStressScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(studyStressScoreVeryBad.toString(), studyCount.toString())), 1)
                        + ")");
        studyTableData.setRatherPoor(
                studyStressScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(studyStressScoreBad.toString(), studyCount.toString())), 1)
                        + ")");
        studyTableData.setGeneral(
                studyStressScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(studyStressScoreGenerally.toString(), studyCount.toString())),
                        1)
                        + ")");
        studyTableData.setGood(
                studyStressScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(studyStressScoreGood.toString(), studyCount.toString())), 1)
                        + ")");

        TableData interpersonalTableData = new TableData();

        interpersonalTableData.setIndex("人际压力");
        interpersonalTableData.setVeryPoor(
                interpersonalStressScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(interpersonalStressScoreVeryBad.toString(),
                                interpersonalCount.toString())), 1)
                        + ")");
        interpersonalTableData.setRatherPoor(
                interpersonalStressScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(interpersonalStressScoreBad.toString(), interpersonalCount.toString())),
                        1)
                        + ")");
        interpersonalTableData.setGeneral(
                interpersonalStressScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(interpersonalStressScoreGenerally.toString(),
                                interpersonalCount.toString())), 1)
                        + ")");
        interpersonalTableData.setGood(
                interpersonalStressScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(interpersonalStressScoreGood.toString(),
                                interpersonalCount.toString())), 1)
                        + ")");

        TableData punishmentTableData = new TableData();

        punishmentTableData.setIndex("受惩罚压力");
        punishmentTableData.setVeryPoor(
                punishmentStressScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(punishmentStressScoreVeryBad.toString(), punishmentCount.toString())),
                        1)
                        + ")");
        punishmentTableData.setRatherPoor(
                punishmentStressScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(punishmentStressScoreBad.toString(), punishmentCount.toString())), 1)
                        + ")");
        punishmentTableData.setGeneral(
                punishmentStressScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(punishmentStressScoreGenerally.toString(), punishmentCount.toString())),
                        1)
                        + ")");
        punishmentTableData.setGood(
                punishmentStressScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(punishmentStressScoreGood.toString(), punishmentCount.toString())), 1)
                        + ")");

        TableData lossTableData = new TableData();

        lossTableData.setIndex("丧失压力");
        lossTableData.setVeryPoor(
                lossStressScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(lossStressScoreVeryBad.toString(), lossCount.toString())), 1)
                        + ")");
        lossTableData.setRatherPoor(
                lossStressScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(lossStressScoreBad.toString(), lossCount.toString())), 1)
                        + ")");
        lossTableData.setGeneral(
                lossStressScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(lossStressScoreGenerally.toString(), lossCount.toString())), 1)
                        + ")");
        lossTableData.setGood(
                lossStressScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(lossStressScoreGood.toString(), lossCount.toString())), 1)
                        + ")");

        TableData adaptationTableData = new TableData();

        adaptationTableData.setIndex("适应压力");
        adaptationTableData.setVeryPoor(
                adaptationStressScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(adaptationStressScoreVeryBad.toString(), adaptationCount.toString())),
                        1)
                        + ")");
        adaptationTableData.setRatherPoor(
                adaptationStressScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(adaptationStressScoreBad.toString(), adaptationCount.toString())), 1)
                        + ")");
        adaptationTableData.setGeneral(
                adaptationStressScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(adaptationStressScoreGenerally.toString(), adaptationCount.toString())),
                        1)
                        + ")");
        adaptationTableData.setGood(
                adaptationStressScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(adaptationStressScoreGood.toString(), adaptationCount.toString())), 1)
                        + ")");

        // 影响程度排名顺序
        /**
         * 积极指标的影响程度是指该指标所有题目未选择“4”分选项的人数÷该指标题目数÷有效人数*100%
         * 消极指标的影响程度是指该指标所有题目未选择“0”分选项的人数÷该指标题目数÷有效人数*100%
         * 影响程度排名是按照每个指标的影响程度从大到小进行排序
         */

        String studyKey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789419690811394L;
        String interpersonalKey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789426871459841L;
        String punishmentkey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789434203103233L;
        String losskey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789443141165057L;
        String adaptkey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789455623413761L;

        Long studyPeopleCount = redisTemplate.opsForSet().size(studyKey);
        Long interpersonalPeopleCount = redisTemplate.opsForSet().size(interpersonalKey);
        Long punishmentPeopleCount = redisTemplate.opsForSet().size(punishmentkey);
        Long lossPeopleCount = redisTemplate.opsForSet().size(losskey);
        Long adaptPeopleCount = redisTemplate.opsForSet().size(adaptkey);

        Long studyQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789419690811394L));

        Long interpersonalQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789426871459841L));

        Long punishmentQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789434203103233L));

        Long lossQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789443141165057L));

        Long adaptQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789455623413761L));

        Integer testPeople = indexGradeReportData.getTestPeople();
        Integer invalidPeople = indexGradeReportData.getInvalidPeople();
        int validCount = testPeople - invalidPeople;

        String studyImpactScore = "0";
        String interpersonalImpactScore = "0";
        String punishmentImpactScore = "0";
        String lossImpactScore = "0";
        String adaptImpactScore = "0";

        if (validCount > 0) {

            // 影响程度分
            studyImpactScore = getScore(studyPeopleCount, studyQuestionCount, validCount);
            interpersonalImpactScore = getScore(interpersonalPeopleCount, interpersonalQuestionCount,
                    validCount);
            punishmentImpactScore = getScore(punishmentPeopleCount, punishmentQuestionCount,
                    validCount);
            lossImpactScore = getScore(lossPeopleCount, lossQuestionCount, validCount);
            adaptImpactScore = getScore(adaptPeopleCount, adaptQuestionCount, validCount);
        }


        studyTableData.setRankScore(studyImpactScore);
        interpersonalTableData.setRankScore(interpersonalImpactScore);
        punishmentTableData.setRankScore(punishmentImpactScore);
        lossTableData.setRankScore(lossImpactScore);
        adaptationTableData.setRankScore(adaptImpactScore);

        tableDataList.add(studyTableData);
        tableDataList.add(interpersonalTableData);
        tableDataList.add(punishmentTableData);
        tableDataList.add(lossTableData);
        tableDataList.add(adaptationTableData);

        // 影响程度排名
        List<TableData> dataSortList = tableDataList.stream()
                .sorted((Comparator.comparing(TableData::getRankScore).reversed())).collect(
                        Collectors.toList());

        dataSortList.forEach(consumerWithIndex((tableData, index) -> tableData.setRank(index + 1)));

        return dataSortList;
    }

    /**
     * 心理韧性图表数据表格
     *
     * @param indexGradeReportData
     * @param taskId
     * @return
     */
    List<TableData> getMentalToughnessScoreTableData(GradeReportData indexGradeReportData,
                                                     Long taskId) {

        Integer emotionManagementScoreVeryBad = indexGradeReportData.getEmotionManagementScoreVeryBad();
        Integer emotionManagementScoreBad = indexGradeReportData.getEmotionManagementScoreBad();
        Integer emotionManagementScoreGenerally = indexGradeReportData.getEmotionManagementScoreGenerally();
        Integer emotionManagementScoreGood = indexGradeReportData.getEmotionManagementScoreGood();

        Integer goalMotivationScoreVeryBad = indexGradeReportData.getGoalMotivationScoreVeryBad();
        Integer goalMotivationScoreBad = indexGradeReportData.getGoalMotivationScoreBad();
        Integer goalMotivationScoreGenerally = indexGradeReportData.getGoalMotivationScoreGenerally();
        Integer goalMotivationScoreGood = indexGradeReportData.getGoalMotivationScoreGood();

        Integer positiveAttentionScoreVeryBad = indexGradeReportData.getPositiveAttentionScoreVeryBad();
        Integer positiveAttentionScoreBad = indexGradeReportData.getPositiveAttentionScoreBad();
        Integer positiveAttentionScoreGenerally = indexGradeReportData.getPositiveAttentionScoreGenerally();
        Integer positiveAttentionScoreGood = indexGradeReportData.getPositiveAttentionScoreGood();

        Integer schoolSupportScoreVeryBad = indexGradeReportData.getSchoolSupportScoreVeryBad();
        Integer schoolSupportScoreBad = indexGradeReportData.getSchoolSupportScoreBad();
        Integer schoolSupportScoreGenerally = indexGradeReportData.getSchoolSupportScoreGenerally();
        Integer schoolSupportScoreGood = indexGradeReportData.getSchoolSupportScoreGood();

        Integer interpersonalSupportScoreVeryBad = indexGradeReportData.getInterpersonalSupportScoreVeryBad();
        Integer interpersonalSupportScoreBad = indexGradeReportData.getInterpersonalSupportScoreBad();
        Integer interpersonalSupportScoreGenerally = indexGradeReportData.getInterpersonalSupportScoreGenerally();
        Integer interpersonalSupportScoreGood = indexGradeReportData.getInterpersonalSupportScoreGood();

        Integer familySupportScoreVeryBad = indexGradeReportData.getFamilySupportScoreVeryBad();
        Integer familySupportScoreBad = indexGradeReportData.getFamilySupportScoreBad();
        Integer familySupportScoreGenerally = indexGradeReportData.getFamilySupportScoreGenerally();
        Integer familySupportScoreGood = indexGradeReportData.getFamilySupportScoreGood();

        Integer emotionCount =
                emotionManagementScoreVeryBad + emotionManagementScoreBad + emotionManagementScoreGenerally
                        + emotionManagementScoreGood;

        Integer goalCount =
                goalMotivationScoreVeryBad + goalMotivationScoreBad + goalMotivationScoreGenerally
                        + goalMotivationScoreGood;

        Integer positiveCount =
                positiveAttentionScoreVeryBad + positiveAttentionScoreBad + positiveAttentionScoreGenerally
                        + positiveAttentionScoreGood;

        Integer schoolCount =
                schoolSupportScoreVeryBad + schoolSupportScoreBad + schoolSupportScoreGenerally
                        + schoolSupportScoreGood;

        Integer interCount =
                interpersonalSupportScoreVeryBad + interpersonalSupportScoreBad
                        + interpersonalSupportScoreGenerally
                        + interpersonalSupportScoreGood;

        Integer familyCount =
                familySupportScoreVeryBad + familySupportScoreBad + familySupportScoreGenerally
                        + familySupportScoreGood;

        List<TableData> tableDataList = new ArrayList<>();

        TableData emotionTableData = new TableData();

        emotionTableData.setIndex("情绪管理");
        emotionTableData.setVeryPoor(
                emotionManagementScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(emotionManagementScoreVeryBad.toString(), emotionCount.toString())), 1)
                        + ")");
        emotionTableData.setRatherPoor(
                emotionManagementScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(emotionManagementScoreBad.toString(), emotionCount.toString())), 1)
                        + ")");
        emotionTableData.setGeneral(
                emotionManagementScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(emotionManagementScoreGenerally.toString(), emotionCount.toString())),
                        1)
                        + ")");
        emotionTableData.setGood(
                emotionManagementScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(emotionManagementScoreGood.toString(), emotionCount.toString())), 1)
                        + ")");

        TableData goalTableData = new TableData();

        goalTableData.setIndex("目标激励");
        goalTableData.setVeryPoor(
                goalMotivationScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(goalMotivationScoreVeryBad.toString(), goalCount.toString())), 1)
                        + ")");
        goalTableData.setRatherPoor(
                goalMotivationScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(goalMotivationScoreBad.toString(), goalCount.toString())), 1)
                        + ")");
        goalTableData.setGeneral(
                goalMotivationScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(goalMotivationScoreGenerally.toString(), goalCount.toString())), 1)
                        + ")");
        goalTableData.setGood(
                goalMotivationScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(goalMotivationScoreGood.toString(), goalCount.toString())), 1)
                        + ")");

        TableData positiveTableData = new TableData();

        positiveTableData.setIndex("积极关注");
        positiveTableData.setVeryPoor(
                positiveAttentionScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(positiveAttentionScoreVeryBad.toString(), positiveCount.toString())), 1)
                        + ")");
        positiveTableData.setRatherPoor(
                positiveAttentionScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(positiveAttentionScoreBad.toString(), positiveCount.toString())), 1)
                        + ")");
        positiveTableData.setGeneral(
                positiveAttentionScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(positiveAttentionScoreGenerally.toString(), positiveCount.toString())),
                        1)
                        + ")");
        positiveTableData.setGood(
                positiveAttentionScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(positiveAttentionScoreGood.toString(), positiveCount.toString())), 1)
                        + ")");

        TableData schoolTableData = new TableData();

        schoolTableData.setIndex("学校支持");
        schoolTableData.setVeryPoor(
                schoolSupportScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(schoolSupportScoreVeryBad.toString(), schoolCount.toString())), 1)
                        + ")");
        schoolTableData.setRatherPoor(
                schoolSupportScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(schoolSupportScoreBad.toString(), schoolCount.toString())), 1)
                        + ")");
        schoolTableData.setGeneral(
                schoolSupportScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(schoolSupportScoreGenerally.toString(), schoolCount.toString())), 1)
                        + ")");
        schoolTableData.setGood(
                schoolSupportScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(schoolSupportScoreGood.toString(), schoolCount.toString())), 1)
                        + ")");

        TableData interTableData = new TableData();

        interTableData.setIndex("人际支持");
        interTableData.setVeryPoor(
                interpersonalSupportScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(interpersonalSupportScoreVeryBad.toString(), interCount.toString())), 1)
                        + ")");
        interTableData.setRatherPoor(
                interpersonalSupportScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(interpersonalSupportScoreBad.toString(), interCount.toString())), 1)
                        + ")");
        interTableData.setGeneral(
                interpersonalSupportScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(interpersonalSupportScoreGenerally.toString(), interCount.toString())),
                        1)
                        + ")");
        interTableData.setGood(
                interpersonalSupportScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(interpersonalSupportScoreGood.toString(), interCount.toString())), 1)
                        + ")");

        TableData familyTableData = new TableData();

        familyTableData.setIndex("家庭支持");
        familyTableData.setVeryPoor(
                familySupportScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(familySupportScoreVeryBad.toString(), familyCount.toString())), 1)
                        + ")");
        familyTableData.setRatherPoor(
                familySupportScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(familySupportScoreBad.toString(), familyCount.toString())), 1)
                        + ")");
        familyTableData.setGeneral(
                familySupportScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(familySupportScoreGenerally.toString(), familyCount.toString())), 1)
                        + ")");
        familyTableData.setGood(
                familySupportScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(familySupportScoreGood.toString(), familyCount.toString())), 1)
                        + ")");

        // 影响程度排名顺序
        /**
         * 积极指标的影响程度是指该指标所有题目未选择“4”分选项的人数÷该指标题目数÷有效人数*100%
         * 消极指标的影响程度是指该指标所有题目未选择“0”分选项的人数÷该指标题目数÷有效人数*100%
         * 影响程度排名是按照每个指标的影响程度从大到小进行排序
         */

        String emotionKey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789344952508417L;
        String goalKey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789349885009922L;
        String positkey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789354800734210L;
        String schoolkey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789359766790145L;
        String interkey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789364607016961L;
        String familykey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546789369476603906L;

        Long emotionPeopleCount = redisTemplate.opsForSet().size(emotionKey);
        Long goalPeopleCount = redisTemplate.opsForSet().size(goalKey);
        Long positPeopleCount = redisTemplate.opsForSet().size(positkey);
        Long schoolPeopleCount = redisTemplate.opsForSet().size(schoolkey);
        Long interPeopleCount = redisTemplate.opsForSet().size(interkey);
        Long familyPeopleCount = redisTemplate.opsForSet().size(familykey);

        Long emotionQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789344952508417L));

        Long goalQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789349885009922L));

        Long positQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789354800734210L));

        Long schoolQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789359766790145L));

        Long interQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789364607016961L));

        Long familyQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546789369476603906L));

        Integer testPeople = indexGradeReportData.getTestPeople();
        Integer invalidPeople = indexGradeReportData.getInvalidPeople();
        int validCount = testPeople - invalidPeople;

        String emotionImpactScore = "0";
        String goalImpactScore = "0";
        String positImpactScore = "0";
        String schoolImpactScore = "0";
        String interImpactScore = "0";
        String familyImpactScore = "0";

        if (validCount > 0) {

            // 影响程度分
            emotionImpactScore = getScore(emotionPeopleCount, emotionQuestionCount, validCount);
            goalImpactScore = getScore(goalPeopleCount, goalQuestionCount, validCount);
            positImpactScore = getScore(positPeopleCount, positQuestionCount, validCount);
            schoolImpactScore = getScore(schoolPeopleCount, schoolQuestionCount, validCount);
            interImpactScore = getScore(interPeopleCount, interQuestionCount, validCount);
            familyImpactScore = getScore(familyPeopleCount, familyQuestionCount, validCount);
        }


        emotionTableData.setRankScore(emotionImpactScore);
        goalTableData.setRankScore(goalImpactScore);
        positiveTableData.setRankScore(positImpactScore);
        schoolTableData.setRankScore(schoolImpactScore);
        interTableData.setRankScore(interImpactScore);
        familyTableData.setRankScore(familyImpactScore);

        tableDataList.add(emotionTableData);
        tableDataList.add(goalTableData);
        tableDataList.add(positiveTableData);
        tableDataList.add(schoolTableData);
        tableDataList.add(interTableData);
        tableDataList.add(familyTableData);

        // 影响程度排名
        List<TableData> dataSortList = tableDataList.stream()
                .sorted((Comparator.comparing(TableData::getRankScore).reversed())).collect(
                        Collectors.toList());

        dataSortList.forEach(consumerWithIndex((tableData, index) -> tableData.setRank(index + 1)));

        return dataSortList;
    }

    /**
     * 品行表现图表数据表格
     *
     * @param indexGradeReportData
     * @param taskId
     * @return
     */
    List<TableData> getBehaviorTableData(GradeReportData indexGradeReportData, Long taskId) {

        Integer moralScoreVeryBad = indexGradeReportData.getMoralScoreVeryBad();
        Integer moralScoreBad = indexGradeReportData.getMoralScoreBad();
        Integer moralScoreGenerally = indexGradeReportData.getMoralScoreGenerally();
        Integer moralScoreGood = indexGradeReportData.getMoralScoreGood();

        Integer stabilityScoreVeryBad = indexGradeReportData.getStabilityScoreVeryBad();
        Integer stabilityScoreBad = indexGradeReportData.getStabilityScoreBad();
        Integer stabilityScoreGenerally = indexGradeReportData.getStabilityScoreGenerally();
        Integer stabilityScoreGood = indexGradeReportData.getStabilityScoreGood();

        Integer disciplineScoreVeryBad = indexGradeReportData.getDisciplineScoreVeryBad();
        Integer disciplineScoreBad = indexGradeReportData.getDisciplineScoreBad();
        Integer disciplineScoreGenerally = indexGradeReportData.getDisciplineScoreGenerally();
        Integer disciplineScoreGood = indexGradeReportData.getDisciplineScoreGood();

        Integer otherPerformanceScoreVeryBad = indexGradeReportData.getOtherPerformanceScoreVeryBad();
        Integer otherPerformanceScoreBad = indexGradeReportData.getOtherPerformanceScoreBad();
        Integer otherPerformanceScoreGenerally = indexGradeReportData.getOtherPerformanceScoreGenerally();
        Integer otherPerformanceScoreGood = indexGradeReportData.getOtherPerformanceScoreGood();

        Integer moralCount =
                moralScoreVeryBad + moralScoreBad + moralScoreGenerally
                        + moralScoreGood;

        Integer stabilitCount =
                stabilityScoreVeryBad + stabilityScoreBad + stabilityScoreGenerally
                        + stabilityScoreGood;

        Integer discipCount =
                disciplineScoreVeryBad + disciplineScoreBad + disciplineScoreGenerally
                        + disciplineScoreGood;

        Integer otherCount =
                otherPerformanceScoreVeryBad + otherPerformanceScoreBad + otherPerformanceScoreGenerally
                        + otherPerformanceScoreGood;

        List<TableData> tableDataList = new ArrayList<>();

        TableData moralTableData = new TableData();

        moralTableData.setIndex("道德性");
        moralTableData.setVeryPoor(
                moralScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(moralScoreVeryBad.toString(), moralCount.toString())), 1)
                        + ")");
        moralTableData.setRatherPoor(
                moralScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(moralScoreBad.toString(), moralCount.toString())), 1)
                        + ")");
        moralTableData.setGeneral(
                moralScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(moralScoreGenerally.toString(), moralCount.toString())), 1)
                        + ")");
        moralTableData.setGood(
                moralScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(moralScoreGood.toString(), moralCount.toString())), 1)
                        + ")");

        TableData stabilitTableData = new TableData();

        stabilitTableData.setIndex("稳定性");
        stabilitTableData.setVeryPoor(
                stabilityScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(stabilityScoreVeryBad.toString(), stabilitCount.toString())), 1)
                        + ")");
        stabilitTableData.setRatherPoor(
                stabilityScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(stabilityScoreBad.toString(), stabilitCount.toString())), 1)
                        + ")");
        stabilitTableData.setGeneral(
                stabilityScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(stabilityScoreGenerally.toString(), stabilitCount.toString())), 1)
                        + ")");
        stabilitTableData.setGood(
                stabilityScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(stabilityScoreGood.toString(), stabilitCount.toString())), 1)
                        + ")");

        TableData discipTableData = new TableData();

        discipTableData.setIndex("纪律性");
        discipTableData.setVeryPoor(
                disciplineScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(disciplineScoreVeryBad.toString(), discipCount.toString())), 1)
                        + ")");
        discipTableData.setRatherPoor(
                disciplineScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(disciplineScoreBad.toString(), discipCount.toString())), 1)
                        + ")");
        discipTableData.setGeneral(
                disciplineScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(disciplineScoreGenerally.toString(), discipCount.toString())), 1)
                        + ")");
        discipTableData.setGood(
                disciplineScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(disciplineScoreGood.toString(), discipCount.toString())), 1)
                        + ")");

        TableData otherTableData = new TableData();

        otherTableData.setIndex("其他表现");
        otherTableData.setVeryPoor(
                otherPerformanceScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(otherPerformanceScoreVeryBad.toString(), otherCount.toString())), 1)
                        + ")");
        otherTableData.setRatherPoor(
                otherPerformanceScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(otherPerformanceScoreBad.toString(), otherCount.toString())), 1)
                        + ")");
        otherTableData.setGeneral(
                otherPerformanceScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(otherPerformanceScoreGenerally.toString(), otherCount.toString())), 1)
                        + ")");
        otherTableData.setGood(
                otherPerformanceScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(otherPerformanceScoreGood.toString(), otherCount.toString())), 1)
                        + ")");

        // 影响程度排名顺序
        /**
         * 积极指标的影响程度是指该指标所有题目未选择“4”分选项的人数÷该指标题目数÷有效人数*100%
         * 消极指标的影响程度是指该指标所有题目未选择“0”分选项的人数÷该指标题目数÷有效人数*100%
         * 影响程度排名是按照每个指标的影响程度从大到小进行排序
         */

        String moralKey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546788938151157761L;
        String stabilitKey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546788944375504898L;
        String discipkey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546788949077319682L;
        String otherkey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546788953720414210L;

        Long moralPeopleCount = redisTemplate.opsForSet().size(moralKey);
        Long stabilitPeopleCount = redisTemplate.opsForSet().size(stabilitKey);
        Long discipPeopleCount = redisTemplate.opsForSet().size(discipkey);
        Long otherPeopleCount = redisTemplate.opsForSet().size(otherkey);

        Long moralQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546788938151157761L));

        Long stabilitQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546788944375504898L));

        Long discipQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546788949077319682L));

        Long otherQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546788953720414210L));

        Integer testPeople = indexGradeReportData.getTestPeople();
        Integer invalidPeople = indexGradeReportData.getInvalidPeople();
        int validCount = testPeople - invalidPeople;

        String moralImpactScore = "0";
        String stabilitImpactScore = "0";
        String discipImpactScore = "0";
        String otherImpactScore = "0";

        if (validCount > 0) {
            // 影响程度分
            moralImpactScore = getScore(moralPeopleCount, moralQuestionCount, validCount);
            stabilitImpactScore = getScore(stabilitPeopleCount, stabilitQuestionCount, validCount);
            discipImpactScore = getScore(discipPeopleCount, discipQuestionCount, validCount);
            otherImpactScore = getScore(otherPeopleCount, otherQuestionCount, validCount);
        }


        moralTableData.setRankScore(moralImpactScore);
        stabilitTableData.setRankScore(stabilitImpactScore);
        discipTableData.setRankScore(discipImpactScore);
        otherTableData.setRankScore(otherImpactScore);

        tableDataList.add(moralTableData);
        tableDataList.add(stabilitTableData);
        tableDataList.add(discipTableData);
        tableDataList.add(otherTableData);

        // 影响程度排名
        List<TableData> dataSortList = tableDataList.stream()
                .sorted((Comparator.comparing(TableData::getRankScore).reversed())).collect(
                        Collectors.toList());

        dataSortList.forEach(consumerWithIndex((tableData, index) -> tableData.setRank(index + 1)));

        return dataSortList;
    }

    /**
     * 学习状态图表数据表格
     *
     * @param indexGradeReportData
     * @param taskId
     * @return
     */
    List<TableData> getLearnTableData(GradeReportData indexGradeReportData, Long taskId) {

        Integer learningAttitudeScoreVeryBad = indexGradeReportData.getLearningAttitudeScoreVeryBad();
        Integer learningAttitudeScoreBad = indexGradeReportData.getLearningAttitudeScoreBad();
        Integer learningAttitudeScoreGenerally = indexGradeReportData.getLearningAttitudeScoreGenerally();
        Integer learningAttitudeScoreGood = indexGradeReportData.getLearningAttitudeScoreGood();

        Integer timeManagementScoreVeryBad = indexGradeReportData.getTimeManagementScoreVeryBad();
        Integer timeManagementScoreBad = indexGradeReportData.getTimeManagementScoreBad();
        Integer timeManagementScoreGenerally = indexGradeReportData.getTimeManagementScoreGenerally();
        Integer timeManagementScoreGood = indexGradeReportData.getTimeManagementScoreGood();

        Integer learningBurnoutScoreVeryBad = indexGradeReportData.getLearningBurnoutScoreVeryBad();
        Integer learningBurnoutScoreBad = indexGradeReportData.getLearningBurnoutScoreBad();
        Integer learningBurnoutScoreGenerally = indexGradeReportData.getLearningBurnoutScoreGenerally();
        Integer learningBurnoutScoreGood = indexGradeReportData.getLearningBurnoutScoreGood();

        Integer learnCount =
                learningAttitudeScoreVeryBad + learningAttitudeScoreBad + learningAttitudeScoreGenerally
                        + learningAttitudeScoreGood;

        Integer timeCount =
                timeManagementScoreVeryBad + timeManagementScoreBad + timeManagementScoreGenerally
                        + timeManagementScoreGood;

        Integer lCount =
                learningBurnoutScoreVeryBad + learningBurnoutScoreBad + learningBurnoutScoreGenerally
                        + learningBurnoutScoreGood;

        List<TableData> tableDataList = new ArrayList<>();

        TableData learnTableData = new TableData();

        learnTableData.setIndex("学习态度");
        learnTableData.setVeryPoor(
                learningAttitudeScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(learningAttitudeScoreVeryBad.toString(), learnCount.toString())), 1)
                        + ")");
        learnTableData.setRatherPoor(
                learningAttitudeScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(learningAttitudeScoreBad.toString(), learnCount.toString())), 1)
                        + ")");
        learnTableData.setGeneral(
                learningAttitudeScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(learningAttitudeScoreGenerally.toString(), learnCount.toString())), 1)
                        + ")");
        learnTableData.setGood(
                learningAttitudeScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(learningAttitudeScoreGood.toString(), learnCount.toString())), 1)
                        + ")");

        TableData TimeTableData = new TableData();

        TimeTableData.setIndex("时间管理");
        TimeTableData.setVeryPoor(
                timeManagementScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(timeManagementScoreVeryBad.toString(), timeCount.toString())), 1)
                        + ")");
        TimeTableData.setRatherPoor(
                learningAttitudeScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(timeManagementScoreBad.toString(), timeCount.toString())), 1)
                        + ")");
        TimeTableData.setGeneral(
                learningAttitudeScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(timeManagementScoreGenerally.toString(), timeCount.toString())), 1)
                        + ")");
        TimeTableData.setGood(
                learningAttitudeScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(timeManagementScoreGood.toString(), timeCount.toString())), 1)
                        + ")");

        TableData lTableData = new TableData();

        lTableData.setIndex("学习倦怠");
        lTableData.setVeryPoor(
                learningBurnoutScoreVeryBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(learningBurnoutScoreVeryBad.toString(), lCount.toString())), 1)
                        + ")");
        lTableData.setRatherPoor(
                learningBurnoutScoreBad + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(learningBurnoutScoreBad.toString(), lCount.toString())), 1)
                        + ")");
        lTableData.setGeneral(
                learningBurnoutScoreGenerally + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(learningBurnoutScoreGenerally.toString(), lCount.toString())), 1)
                        + ")");
        lTableData.setGood(
                learningBurnoutScoreGood + "人" + "("
                        + MathUtil.getPercent(Func.toDouble(
                        getDivideData(learningBurnoutScoreGood.toString(), lCount.toString())), 1)
                        + ")");

        // 影响程度排名顺序
        /**
         * 积极指标的影响程度是指该指标所有题目未选择“4”分选项的人数÷该指标题目数÷有效人数*100%
         * 消极指标的影响程度是指该指标所有题目未选择“0”分选项的人数÷该指标题目数÷有效人数*100%
         * 影响程度排名是按照每个指标的影响程度从大到小进行排序
         */

        String learnKey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546788164788609025L;
        String timeKey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546788174330650626L;
        String lkey =
                "education" + ":" + "index" + ":" + "statistic" + ":" + taskId + ":" + 1546788189962821634L;
        Long learnPeopleCount = redisTemplate.opsForSet().size(learnKey);
        Long timePeopleCount = redisTemplate.opsForSet().size(timeKey);
        Long lPeopleCount = redisTemplate.opsForSet().size(lkey);

        Long learnQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546788164788609025L));

        Long timeQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546788174330650626L));

        Long lQuestionCount = dimensionIndexQuestionMapper.selectCount(
                Wrappers.<TestDimensionIndexQuestion>lambdaQuery()
                        .eq(TestDimensionIndexQuestion::getIndexId, 1546788189962821634L));

        Integer testPeople = indexGradeReportData.getTestPeople();
        Integer invalidPeople = indexGradeReportData.getInvalidPeople();
        int validCount = testPeople - invalidPeople;

        String learnImpactScore = "0";
        String timeImpactScore = "0";
        String lImpactScore = "0";
        if (validCount > 0) {
            // 影响程度分
            learnImpactScore = getScore(learnPeopleCount, learnQuestionCount, validCount);
            timeImpactScore = getScore(timePeopleCount, timeQuestionCount, validCount);
            lImpactScore = getScore(lPeopleCount, lQuestionCount, validCount);
        }

        learnTableData.setRankScore(learnImpactScore);
        TimeTableData.setRankScore(timeImpactScore);
        lTableData.setRankScore(lImpactScore);

        tableDataList.add(learnTableData);
        tableDataList.add(TimeTableData);
        tableDataList.add(lTableData);

        // 影响程度排名
        List<TableData> dataSortList = tableDataList.stream()
                .sorted((Comparator.comparing(TableData::getRankScore).reversed())).collect(
                        Collectors.toList());

        dataSortList.forEach(consumerWithIndex((tableData, index) -> tableData.setRank(index + 1)));

        return dataSortList;
    }

    /**
     * 带下标遍历
     *
     * @param consumer
     * @param <T>
     * @return
     */
    static <T> Consumer<T> consumerWithIndex(BiConsumer<T, Integer> consumer) {
        class Obj {

            int i;
        }
        Obj obj = new Obj();
        return t -> {
            int index = obj.i++;
            consumer.accept(t, index);
        };
    }


    /**
     * 分数
     *
     * @param peopleCount   人数
     * @param questionCount 题目数
     * @param validCount    有效人数
     * @return
     */
    static String getScore(Long peopleCount, Long questionCount, Integer validCount) {
        // 人数÷该指标题目数÷有效人数
        String d1 = getDivideData(Func.toStr(peopleCount), Func.toStr(questionCount));
        String d2 = "";
        d2 = getDivideData(d1, Func.toStr(validCount));
        return d2;
    }

    /**
     * 测评概况
     *
     * @param groupsReport
     * @param taskId
     */
    void getTestOverview(GroupsReport groupsReport, Long taskId) {

        TestOverview testOverview = new TestOverview();

        // 积极
        List activeList = new LinkedList<OverviewData>();

        // 学习状态 品行表现 心理韧性
        Object studyStatusScore = getTestPeopleAvgData(taskId, "studyStatusScore");
        Object behaviorScore = getTestPeopleAvgData(taskId, "behaviorScore");
        Object mentalToughnessScore = getTestPeopleAvgData(taskId, "mentalToughnessScore");

        OverviewData studyOverviewData = new OverviewData();
        studyOverviewData.setTitle("学习状态");
        String studyScore = Func.toStr(studyStatusScore);
        studyOverviewData.setScore(MathUtil.formatRate(Func.toDouble(studyScore)));
        TestDimensionIndexConclusion stuDimensionConclusion = getDimensionConclusion(studyScore,
                1546788164255932417L);
        Integer stuRiskIndex = stuDimensionConclusion.getRiskIndex();
        String stuFontColor = getFontColor(stuRiskIndex);
        studyOverviewData.setFontColor(stuFontColor);
        studyOverviewData.setResult(stuDimensionConclusion.getRiskResult());

        OverviewData beOverviewData = new OverviewData();
        String beScore = Func.toStr(behaviorScore);
        beOverviewData.setTitle("品行表现");
        beOverviewData.setScore(MathUtil.formatRate(Func.toDouble(beScore)));
        TestDimensionIndexConclusion beDimensionConclusion = getDimensionConclusion(beScore,
                1546788937710755842L);
        Integer beRiskIndex = beDimensionConclusion.getRiskIndex();
        String beFontColor = getFontColor(beRiskIndex);
        beOverviewData.setFontColor(beFontColor);
        beOverviewData.setResult(beDimensionConclusion.getRiskResult());

        OverviewData meOverviewData = new OverviewData();
        String meScore = Func.toStr(mentalToughnessScore);
        meOverviewData.setTitle("心理韧性");
        meOverviewData.setScore(MathUtil.formatRate(Func.toDouble(meScore)));
        TestDimensionIndexConclusion meDimensionConclusion = getDimensionConclusion(meScore,
                1546789344491134978L);
        Integer meRiskIndex = meDimensionConclusion.getRiskIndex();
        String meFontColor = getFontColor(meRiskIndex);
        meOverviewData.setFontColor(meFontColor);
        meOverviewData.setResult(meDimensionConclusion.getRiskResult());

        activeList.add(studyOverviewData);
        activeList.add(beOverviewData);
        activeList.add(meOverviewData);

        testOverview.setActive(activeList);

        // 消极
        List negativeList = new ArrayList<OverviewData>();

        // 综合压力 情绪指数 睡眠指数
        Object overallStressScore = getTestPeopleAvgData(taskId, "overallStressScore");
        Object emotionalIndexScore = getTestPeopleAvgData(taskId, "emotionalIndexScore");
        Object sleepIndexScore = getTestPeopleAvgData(taskId, "sleepIndexScore");

        OverviewData ovOverviewData = new OverviewData();
        ovOverviewData.setTitle("综合压力");
        String ovScore = Func.toStr(overallStressScore);
        ovOverviewData.setScore(MathUtil.formatRate(Func.toDouble(ovScore)));
        TestDimensionIndexConclusion ovDimensionConclusion = getDimensionConclusion(ovScore,
                1546789419250409474L);
        Integer ovRiskIndex = ovDimensionConclusion.getRiskIndex();
        String ovFontColor = getFontColor(ovRiskIndex);
        ovOverviewData.setFontColor(ovFontColor);
        ovOverviewData.setResult(ovDimensionConclusion.getRiskResult());

        OverviewData emOverviewData = new OverviewData();
        String emScore = Func.toStr(emotionalIndexScore);
        emOverviewData.setTitle("情绪指数");
        emOverviewData.setScore(MathUtil.formatRate(Func.toDouble(emScore)));
        TestDimensionIndexConclusion emDimensionConclusion = getDimensionConclusion(emScore,
                1546789515505491970L);
        Integer emRiskIndex = emDimensionConclusion.getRiskIndex();
        String emFontColor = getFontColor(emRiskIndex);
        emOverviewData.setFontColor(emFontColor);
        emOverviewData.setResult(emDimensionConclusion.getRiskResult());

        OverviewData slOverviewData = new OverviewData();
        String slScore = Func.toStr(sleepIndexScore);
        slOverviewData.setTitle("睡眠指数");
        slOverviewData.setScore(MathUtil.formatRate(Func.toDouble(slScore)));
        TestDimensionIndexConclusion slDimensionConclusion = getDimensionConclusion(slScore,
                1546789785358622721L);
        Integer slRiskIndex = slDimensionConclusion.getRiskIndex();
        String slFontColor = getFontColor(slRiskIndex);
        slOverviewData.setFontColor(slFontColor);
        slOverviewData.setResult(slDimensionConclusion.getRiskResult());

        negativeList.add(ovOverviewData);
        negativeList.add(emOverviewData);
        negativeList.add(slOverviewData);

        testOverview.setNegative(negativeList);

        groupsReport.setTestOverview(testOverview);
    }


    /**
     * 获取维度对应等级
     *
     * @param score
     * @param dimensionId
     * @return
     */
    TestDimensionIndexConclusion getDimensionConclusion(String score, Long dimensionId) {

        List<TestDimensionIndexConclusion> dimensionIndexConclusionList = dimensionIndexConclusionMapper.dimensionIndexConclusionList(dimensionId);

        AtomicReference<TestDimensionIndexConclusion> testDimensionIndexConclusion = new AtomicReference<>(
                new TestDimensionIndexConclusion());

        dimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(score, dimensionIndex.getDimensionScope());
            if (result) {
                testDimensionIndexConclusion.set(dimensionIndex);
            }
        });

        return testDimensionIndexConclusion.get();
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


    /**
     * 心理健康评级
     *
     * @param groupsReport
     * @param taskId
     */
    void getMentalHealthRatings(GroupsReport groupsReport, Long taskId) {

        MentalHealthRatings mentalHealthRatings = new MentalHealthRatings();

        // 建议关注等级
        List<TypeData> attentionLevel = getAttentionLevel(taskId);

        // 关注等级分布表格数据
        List<LevelTable> attentionLevelTable = getAttentionLevelTable(taskId);

        // 年级建议关注等级分布
        List<AttentionLevelDistribution> attentionLevelDistribution = getAttentionLevelDistribution(
                taskId);

        // 建议关注学生名单
        List<GFollowUserVO> attentionStudent = getAttentionStudent(taskId);

        // 心理危机预警
        TypeData warningLevel = getWarningLevel(taskId);

        // 预警等级分布表格数据
        List<LevelTable> warningLevelTable = getWarningLevelTable(taskId);

        // 年级危机预警等级分布
        List<WarningLevelDistribution> warningLevelDistribution = getWarningLevelDistribution(taskId);

        // 危机预警学生名单
        List<GFollowUserVO> warningStudent = getWarningStudent(taskId);

        mentalHealthRatings.setAttentionLevel(attentionLevel);
        mentalHealthRatings.setAttentionLevelTable(attentionLevelTable);
        mentalHealthRatings.setAttentionLevelDistribution(attentionLevelDistribution);

        mentalHealthRatings.setAttentionStudent(attentionStudent);
        mentalHealthRatings.setWarningLevel(warningLevel);
        mentalHealthRatings.setWarningLevelTable(warningLevelTable);
        mentalHealthRatings.setWarningLevelDistribution(warningLevelDistribution);
        mentalHealthRatings.setWarningStudent(warningStudent);

        groupsReport.setMentalHealthRatings(mentalHealthRatings);


    }

    /**
     * 危机预警学生名单
     *
     * @param taskId
     * @return
     */
    List<GFollowUserVO> getWarningStudent(Long taskId) {
        List<GFollowUserVO> list = getWarnStudentGrade(taskId);
        return list;
    }

    /**
     * 获取任务下年级ID
     *
     * @return
     */
    List<Integer> getGradeList(Long taskId) {
//    List<Integer> list = Lists.newArrayList(31, 32, 33, 41, 42, 43);
        // 动态获取任务下年级数据
        List<Integer> list = testTaskDepartmentMapper.getDepartmentIdListByTaskId(
                taskId);
        return list;
    }

    /**
     * 年级建议关注等级分布
     *
     * @param taskId
     * @return
     */
    List<WarningLevelDistribution> getWarningLevelDistribution(Long taskId) {

        List<WarningLevelDistribution> warningLevelDistributionList = new ArrayList<>();

        List<Integer> list = getGradeList(taskId);

        list.forEach(v -> {
            WarningLevelDistribution warningLevelDistribution = getWarningLevelDistributionGrade(
                    taskId, v);
            String gradeName = getGradeName(v);
            warningLevelDistribution.setGradeId(v);
            warningLevelDistribution.setGradeName(gradeName);
            warningLevelDistribution.setThreeWarningLevel(
                    warningLevelDistribution.getThreeWarningLevelCount() + "人 " + "("
                            + warningLevelDistribution.getThreeWarningLevel() + ")");
            warningLevelDistribution.setTwoWarningLevel(
                    warningLevelDistribution.getTwoWarningLevelCount() + "人 " + "("
                            + warningLevelDistribution.getTwoWarningLevel() + ")");
            warningLevelDistribution.setOneWarningLevel(
                    warningLevelDistribution.getOneWarningLevelCount() + "人 " + "("
                            + warningLevelDistribution.getOneWarningLevel() + ")");
            warningLevelDistribution.setZeroWarningLevel(
                    warningLevelDistribution.getZeroWarningLevelCount() + "人 " + "("
                            + warningLevelDistribution.getZeroWarningLevel() + ")");
            warningLevelDistributionList.add(warningLevelDistribution);
        });

        return warningLevelDistributionList;
    }


    /**
     * 预警等级分布表格数据
     *
     * @param taskId
     * @return
     */
    List<LevelTable> getWarningLevelTable(Long taskId) {

        List list = new LinkedList<LevelTable>();

        LevelTable threeLevelTable = new LevelTable();
        threeLevelTable.setAttentionLevel("3级预警");
        Integer warnPeopleThree = getTestPeopleData(taskId, "warnPeopleThree");
        Object warnRateThree = getTestPeopleAvgData(taskId, "warnRateThree");
        threeLevelTable.setPeopleCount(warnPeopleThree.toString());
        threeLevelTable.setUniversityPercentage(MathUtil.dealRate(Func.toDouble(warnRateThree)));

        LevelTable twoLevelTable = new LevelTable();
        twoLevelTable.setAttentionLevel("2级预警");
        Integer warnPeopleTwo = getTestPeopleData(taskId, "warnPeopleTwo");
        Object warnRateTwo = getTestPeopleAvgData(taskId, "warnRateTwo");
        twoLevelTable.setPeopleCount(warnPeopleTwo.toString());
        twoLevelTable.setUniversityPercentage(MathUtil.dealRate(Func.toDouble(warnRateTwo)));

        LevelTable oneLevelTable = new LevelTable();
        oneLevelTable.setAttentionLevel("1级预警");
        Integer warnPeopleOne = getTestPeopleData(taskId, "warnPeopleOne");
        Object warnRateOne = getTestPeopleAvgData(taskId, "warnRateOne");
        oneLevelTable.setPeopleCount(warnPeopleOne.toString());
        oneLevelTable.setUniversityPercentage(MathUtil.dealRate(Func.toDouble(warnRateOne)));

        LevelTable goodLevelTable = new LevelTable();
        goodLevelTable.setAttentionLevel("未发现");
        Integer followPeople = getTestPeopleData(taskId, "warnPeople");
        Object followRate = getTestPeopleAvgData(taskId, "warnRate");
        goodLevelTable.setPeopleCount(followPeople.toString());
        goodLevelTable.setUniversityPercentage(MathUtil.dealRate(Func.toDouble(followRate)));

        list.add(threeLevelTable);
        list.add(twoLevelTable);
        list.add(oneLevelTable);
        list.add(goodLevelTable);

        return list;
    }

    /**
     * 心理危机预警
     */
    TypeData getWarningLevel(Long taskId) {

        // 危机预警等级图表
        TypeData warnTypeData = new TypeData();

        List<ChartData> warnChartDataList = new ArrayList<>();

        ChartData wThreeData = new ChartData();
        wThreeData.setName("3级预警");
        Integer warnPeopleThreeCount = getTestPeopleData(taskId, "warnPeopleThree");
        wThreeData.setValue(Func.toDouble(warnPeopleThreeCount));
        wThreeData.setChartColor(getFontColor(3));

        ChartData wTwoData = new ChartData();
        wTwoData.setName("2级预警");
        Integer warnPeopleTwoCount = getTestPeopleData(taskId, "warnPeopleTwo");
        wTwoData.setValue(Func.toDouble(warnPeopleTwoCount));
        wTwoData.setChartColor(getFontColor(2));

        ChartData wOneData = new ChartData();
        wOneData.setName("1级预警");
        Integer warnPeopleOneCount = getTestPeopleData(taskId, "warnPeopleOne");
        wOneData.setValue(Func.toDouble(warnPeopleOneCount));
        wOneData.setChartColor(getFontColor(1));


        ChartData wGoodData = new ChartData();
        wGoodData.setName("未发现");
        Integer warnPeopleCount = getTestPeopleData(taskId, "warnPeople");
        wGoodData.setValue(Func.toDouble(warnPeopleCount));
        wGoodData.setChartColor(getFontColor(0));


        warnChartDataList.add(wThreeData);
        warnChartDataList.add(wTwoData);
        warnChartDataList.add(wOneData);
        warnChartDataList.add(wGoodData);
        warnTypeData.setChartData(warnChartDataList);

        return warnTypeData;

    }


    /**
     * 建议关注学生名单
     *
     * @param taskId
     * @return
     */
    List<GFollowUserVO> getAttentionStudent(Long taskId) {
        List<GFollowUserVO> list = getAttentionStudentGrade(taskId);
        return list;
    }

    /**
     * 年级建议关注等级分布
     *
     * @param taskId
     * @return
     */
    List<AttentionLevelDistribution> getAttentionLevelDistribution(Long taskId) {

        List<AttentionLevelDistribution> attentionLevelDistributionList = new ArrayList<>();

        List<Integer> list = getGradeList(taskId);

        list.forEach(v -> {
            AttentionLevelDistribution attentionLevelDistribution = getAttentionLevelDistributionGrade(
                    taskId, v);
            String gradeName = getGradeName(v);
            attentionLevelDistribution.setGradeId(v);
            attentionLevelDistribution.setGradeName(gradeName);
            attentionLevelDistribution.setThreeAttentionLevel(
                    attentionLevelDistribution.getThreeAttentionLevelCount() + "人 " + "("
                            + attentionLevelDistribution.getThreeAttentionLevel() + ")");
            attentionLevelDistribution.setTwoAttentionLevel(
                    attentionLevelDistribution.getTwoAttentionLevelCount() + "人 " + "("
                            + attentionLevelDistribution.getTwoAttentionLevel() + ")");
            attentionLevelDistribution.setOneAttentionLevel(
                    attentionLevelDistribution.getOneAttentionLevelCount() + "人 " + "("
                            + attentionLevelDistribution.getOneAttentionLevel() + ")");
            attentionLevelDistribution.setZeroAttentionLevel(
                    attentionLevelDistribution.getZeroAttentionLevelCount() + "人 " + "("
                            + attentionLevelDistribution.getZeroAttentionLevel() + ")");
            attentionLevelDistributionList.add(attentionLevelDistribution);
        });

        return attentionLevelDistributionList;
    }

    /**
     * 关注等级分布表格数据
     *
     * @param taskId
     * @return
     */
    List<LevelTable> getAttentionLevelTable(Long taskId) {

        List list = new LinkedList<LevelTable>();

        LevelTable threeLevelTable = new LevelTable();
        threeLevelTable.setAttentionLevel("3级关注");
        Integer followPeopleThree = getTestPeopleData(taskId, "followPeopleThree");
        Object followRateThree = getTestPeopleAvgData(taskId, "followRateThree");
        threeLevelTable.setPeopleCount(followPeopleThree.toString());
        threeLevelTable.setUniversityPercentage(MathUtil.dealRate(Func.toDouble(followRateThree)));

        LevelTable twoLevelTable = new LevelTable();
        twoLevelTable.setAttentionLevel("2级关注");
        Integer followPeopleTwo = getTestPeopleData(taskId, "followPeopleTwo");
        Object followRateTwo = getTestPeopleAvgData(taskId, "followRateTwo");
        twoLevelTable.setPeopleCount(followPeopleTwo.toString());
        twoLevelTable.setUniversityPercentage(MathUtil.dealRate(Func.toDouble(followRateTwo)));

        LevelTable oneLevelTable = new LevelTable();
        oneLevelTable.setAttentionLevel("1级关注");
        Integer followPeopleOne = getTestPeopleData(taskId, "followPeopleOne");
        Object followRateOne = getTestPeopleAvgData(taskId, "followRateOne");
        oneLevelTable.setPeopleCount(followPeopleOne.toString());
        oneLevelTable.setUniversityPercentage(MathUtil.dealRate(Func.toDouble(followRateOne)));

        LevelTable goodLevelTable = new LevelTable();
        goodLevelTable.setAttentionLevel("良好");
        Integer followPeople = getTestPeopleData(taskId, "followPeople");
        Object followRate = getTestPeopleAvgData(taskId, "followRate");
        goodLevelTable.setPeopleCount(followPeople.toString());
        goodLevelTable.setUniversityPercentage(MathUtil.dealRate(Func.toDouble(followRate)));

        list.add(threeLevelTable);
        list.add(twoLevelTable);
        list.add(oneLevelTable);
        list.add(goodLevelTable);

        return list;
    }


    /**
     * 建议关注等级
     */
    List<TypeData> getAttentionLevel(Long taskId) {

        List<TypeData> list = new ArrayList<>();

        // 建议关注等级
        TypeData sTypeData = new TypeData();

        List<ChartData> sChartDataList = new ArrayList<>();

        ChartData sThreeData = new ChartData();
        sThreeData.setName("3级关注");
        Integer followPeopleThreeCount = getTestPeopleData(taskId, "followPeopleThree");
        sThreeData.setValue(Func.toDouble(followPeopleThreeCount));
        sThreeData.setChartColor(getFontColor(3));

        ChartData sTwoData = new ChartData();
        sTwoData.setName("2级关注");
        Integer followPeopleTwoCount = getTestPeopleData(taskId, "followPeopleTwo");
        sTwoData.setValue(Func.toDouble(followPeopleTwoCount));
        sTwoData.setChartColor(getFontColor(2));

        ChartData sOneData = new ChartData();
        sOneData.setName("1级关注");
        Integer followPeopleOneCount = getTestPeopleData(taskId, "followPeopleOne");
        sOneData.setValue(Func.toDouble(followPeopleOneCount));
        sOneData.setChartColor(getFontColor(1));

        ChartData sGoodData = new ChartData();
        sGoodData.setName("良好");
        Integer followPeopleCount = getTestPeopleData(taskId, "followPeople");
        sGoodData.setValue(Func.toDouble(followPeopleCount));
        sGoodData.setChartColor(getFontColor(0));


        sChartDataList.add(sThreeData);
        sChartDataList.add(sTwoData);
        sChartDataList.add(sOneData);
        sChartDataList.add(sGoodData);
        sTypeData.setChartData(sChartDataList);

        list.add(sTypeData);

        // 学生评定等级
        TypeData stuTypeData = new TypeData();

        List<ChartData> stuChartDataList = new ArrayList<>();

        ChartData stuThreeData = new ChartData();
        stuThreeData.setName("3级");
        Object studentPeopleThreeCount = getTestPeopleAvgData(taskId, "studentRateThree");
        stuThreeData.setValue(Func.toDouble(MathUtil.formatRate(Func.toDouble(studentPeopleThreeCount))));
        stuThreeData.setChartColor(getFontColor(3));

        ChartData stuTwoData = new ChartData();
        stuTwoData.setName("2级");
        Object studentPeopleTwoCount = getTestPeopleAvgData(taskId, "studentRateTwo");
        stuTwoData.setValue(Func.toDouble(MathUtil.formatRate(Func.toDouble(studentPeopleTwoCount))));
        stuTwoData.setChartColor(getFontColor(2));


        ChartData stuOneData = new ChartData();
        stuOneData.setName("1级");
        Object studentPeopleOneCount = getTestPeopleAvgData(taskId, "studentRateOne");
        stuOneData.setValue(Func.toDouble(MathUtil.formatRate(Func.toDouble(studentPeopleOneCount))));
        stuOneData.setChartColor(getFontColor(1));


        ChartData stuGoodData = new ChartData();
        stuGoodData.setName("良好");
        Object studentPeopleCount = getTestPeopleAvgData(taskId, "studentRate");
        stuGoodData.setValue(Func.toDouble(MathUtil.formatRate(Func.toDouble(studentPeopleCount))));
        stuGoodData.setChartColor(getFontColor(0));


        stuChartDataList.add(stuThreeData);
        stuChartDataList.add(stuTwoData);
        stuChartDataList.add(stuOneData);
        stuChartDataList.add(stuGoodData);
        stuTypeData.setTitle("学生评定等级");
        stuTypeData.setChartData(stuChartDataList);

        // 教师评定等级
        TypeData tTypeData = new TypeData();

        List<ChartData> tTChartDataList = new ArrayList<>();

        ChartData tThreeData = new ChartData();
        tThreeData.setName("3级");
        Object teacherPeopleThreeCount = getTestPeopleAvgData(taskId, "teacherRateThree");
        tThreeData.setValue(Func.toDouble(MathUtil.formatRate(Func.toDouble(teacherPeopleThreeCount))));
        tThreeData.setChartColor(getFontColor(3));


        ChartData tTwoData = new ChartData();
        tTwoData.setName("2级");
        Object teacherPeopleTwoCount = getTestPeopleAvgData(taskId, "teacherRateTwo");
        tTwoData.setValue(Func.toDouble(MathUtil.formatRate(Func.toDouble(teacherPeopleTwoCount))));
        tTwoData.setChartColor(getFontColor(2));


        ChartData tOneData = new ChartData();
        tOneData.setName("1级");
        Object teacherPeopleOneCount = getTestPeopleAvgData(taskId, "teacherRateOne");
        tOneData.setValue(Func.toDouble(MathUtil.formatRate(Func.toDouble(teacherPeopleOneCount))));
        tOneData.setChartColor(getFontColor(1));


        ChartData tGoodData = new ChartData();
        tGoodData.setName("良好");
        Object teacherPeopleCount = getTestPeopleAvgData(taskId, "teacherRate");
        tGoodData.setValue(Func.toDouble(MathUtil.formatRate(Func.toDouble(teacherPeopleCount))));
        tGoodData.setChartColor(getFontColor(0));


        tTChartDataList.add(tThreeData);
        tTChartDataList.add(tTwoData);
        tTChartDataList.add(tOneData);
        tTChartDataList.add(tGoodData);
        tTypeData.setTitle("教师评定等级");
        tTypeData.setChartData(tTChartDataList);

        // 家长评定等级
        TypeData ptuTypeData = new TypeData();

        List<ChartData> pTChartDataList = new ArrayList<>();

        ChartData pOneData = new ChartData();
        pOneData.setName("1级");
        Object parentPeopleOneCount = getTestPeopleAvgData(taskId, "parentRateOne");
        pOneData.setValue(Func.toDouble(MathUtil.formatRate(Func.toDouble(parentPeopleOneCount))));
        pOneData.setChartColor(getFontColor(1));


        ChartData pGoodData = new ChartData();
        pGoodData.setName("良好");
        Object parentPeopleCount = getTestPeopleAvgData(taskId, "parentRate");
        pGoodData.setValue(Func.toDouble(MathUtil.formatRate(Func.toDouble(parentPeopleCount))));
        pGoodData.setChartColor(getFontColor(0));

        pTChartDataList.add(pOneData);
        pTChartDataList.add(pGoodData);
        ptuTypeData.setTitle("家长评定等级");
        ptuTypeData.setChartData(pTChartDataList);

        list.add(stuTypeData);
        list.add(tTypeData);
        list.add(ptuTypeData);
        return list;

    }


    /**
     * 测评完成情况
     *
     * @param groupsReport
     */
    void getEvaluationCompletion(GroupsReport groupsReport, Long taskId) {

        EvaluationCompletion evaluationCompletion = new EvaluationCompletion();

        // 已测人数
        Integer testPeopleCount = getTestPeopleData(taskId, "testPeople");
        evaluationCompletion.setTotalTestPeople(testPeopleCount);

        // 未测人数
        Integer noTestPeopleCount = getTestPeopleData(taskId, "noTestPeople");
        evaluationCompletion.setTotalNoTestPeople(noTestPeopleCount);

        // 无效人数
        Integer invalidPeopleCount = getTestPeopleData(taskId, "invalidPeople");
        evaluationCompletion.setTotalInvalidPeople(invalidPeopleCount);

        // 完成率
        Object completionRate = getTestPeopleAvgData(taskId, "completionRate");
        Double dCompletionRate = Func.toDouble(completionRate);
        // 数据处理 %
        evaluationCompletion.setTotalCompletionRate(MathUtil.dealRate(dCompletionRate));

        // 测评完成情况 表格数据
        List<ParticipatingGrade> participatingGradeList = getParticipatingGradeList(taskId);
        evaluationCompletion.setParticipatingGrade(participatingGradeList);

        groupsReport.setEvaluationCompletion(evaluationCompletion);
    }


    /**
     * 测评完成情况 表格数据
     *
     * @param taskId
     * @return
     */
    List<ParticipatingGrade> getParticipatingGradeList(Long taskId) {

        List participatingGradeList = new ArrayList<ParticipatingGrade>();

        List<Integer> list = getGradeList(taskId);

        list.forEach(v -> {
            ParticipatingGrade participatingGrade = getParticipatingGrade(taskId, v);
            String gradeName = getGradeName(v);
            participatingGrade.setGradeName(gradeName);
            Double cr = Func.toDouble(participatingGrade.getCompletionRate());
            participatingGrade.setCompletionRate(MathUtil.dealRate(cr));
            participatingGradeList.add(participatingGrade);
        });

        return participatingGradeList;

    }

    /**
     * 获取班级名字
     *
     * @param gradeId
     * @return
     */
    String getGradeName(Integer gradeId) {
        String gradeKey = "education" + ":" + "grade_name" + ":" + gradeId;
        Object o = redisTemplate.opsForValue().get(gradeKey);
        String gradeName = "";
        if (Func.isEmpty(o)) {
            gradeName = reportGroupsMapper.gradeNameById(gradeId);
            redisTemplate.opsForValue().set(gradeKey, gradeName);
        } else {
            gradeName = o.toString();
        }

        return gradeName;
    }

    /**
     * 基本信息
     *
     * @param groupsReport
     */
    void getBasicInformation(GroupsReport groupsReport, Long taskId) {

        BasicInformation basicInformation = new BasicInformation();

        // 参与年级
        TypeData gradeTypeData = getGrade(taskId);
        basicInformation.setGrade(gradeTypeData);

        // 性别
        TypeData sexTypeData = getSex(taskId);
        basicInformation.setSex(sexTypeData);

        // 家庭结构数据
        TypeData fTypeData = getFamilyStructure(taskId);
        basicInformation.setFamilyStructure(fTypeData);

        // 是否与父母生活
        TypeData lTypeData = getLivingWithParents(taskId);
        basicInformation.setLivingWithParents(lTypeData);

        groupsReport.setBasicInformation(basicInformation);

    }

    /**
     * 参与年级
     *
     * @param taskId
     * @return
     */
    TypeData getGrade(Long taskId) {

        TypeData gradeTypeData = new TypeData();

        gradeTypeData.setTitle("年级");

        List gradeChartDataList = new ArrayList<ChartData>();

        List<Integer> list = getGradeList(taskId);

        list.forEach(v -> {
            Integer gradeData = getGradeData(taskId, v);
            ChartData chartData = new ChartData();
            String gradeName = getGradeName(v);
            chartData.setName(gradeName);
            chartData.setValue(Func.toDouble(gradeData));
            gradeChartDataList.add(chartData);
        });

        gradeTypeData.setChartData(gradeChartDataList);

        return gradeTypeData;
    }

    /**
     * 性别
     *
     * @param taskId
     * @return
     */
    TypeData getSex(Long taskId) {

        TypeData sexTypeData = new TypeData();

        sexTypeData.setTitle("性别");

        List sexChartDataList = new ArrayList<ChartData>();

        // 统计男女数量 0 男 1 女
        Integer mCount = reportGroupsMapper.statisticsCount(0, taskId);
        Integer wCount = reportGroupsMapper.statisticsCount(1, taskId);

        ChartData mChartData = new ChartData();
        mChartData.setName("男");
        mChartData.setValue(Func.toDouble(mCount));

        ChartData wChartData = new ChartData();
        wChartData.setName("女");
        wChartData.setValue(Func.toDouble(wCount));

        sexChartDataList.add(mChartData);
        sexChartDataList.add(wChartData);

        sexTypeData.setChartData(sexChartDataList);

        return sexTypeData;
    }

    /**
     * 家庭结构数据
     *
     * @return
     */
    TypeData getFamilyStructure(Long taskId) {

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
        String dqKey = "education" + ":" + "family_structure" + ":" + "statistic" + ":" + taskId + ":"
                + 2135703723;
        String sqKey = "education" + ":" + "family_structure" + ":" + "statistic" + ":" + taskId + ":"
                + 2135703722;
        String czKey = "education" + ":" + "family_structure" + ":" + "statistic" + ":" + taskId + ":"
                + 2135703724;
        String qtrKey = "education" + ":" + "family_structure" + ":" + "statistic" + ":" + taskId + ":"
                + 2135703725;
        String dzKey = "education" + ":" + "family_structure" + ":" + "statistic" + ":" + taskId + ":"
                + 2135703726;
        String qtKey = "education" + ":" + "family_structure" + ":" + "statistic" + ":" + taskId + ":"
                + 2135703727;

        Long dqKeyCount = redisTemplate.opsForSet().size(dqKey);
        Long sqKeyCount = redisTemplate.opsForSet().size(sqKey);
        Long czKeyCount = redisTemplate.opsForSet().size(czKey);
        Long qtrKeyCount = redisTemplate.opsForSet().size(qtrKey);
        Long dzKeyCount = redisTemplate.opsForSet().size(dzKey);
        Long qtKeyCount = redisTemplate.opsForSet().size(qtKey);

        // 家庭结果
        TypeData fTypeData = new TypeData();
        fTypeData.setTitle("家庭结构");
        List chartDataList = new ArrayList<ChartData>();
        ChartData dqChartData = new ChartData();
        dqChartData.setName("单亲");
        dqChartData.setValue(Func.toDouble(dqKeyCount));

        ChartData sqChartData = new ChartData();
        sqChartData.setName("双亲");
        sqChartData.setValue(Func.toDouble(sqKeyCount));

        ChartData czChartData = new ChartData();
        czChartData.setName("重组");
        czChartData.setValue(Func.toDouble(czKeyCount));

        ChartData qtrChartData = new ChartData();
        qtrChartData.setName("其他监护人");
        qtrChartData.setValue(Func.toDouble(qtrKeyCount));

        ChartData dzChartData = new ChartData();
        dzChartData.setName("独自");
        dzChartData.setValue(Func.toDouble(dzKeyCount));

        ChartData qtChartData = new ChartData();
        qtChartData.setName("其他");
        qtChartData.setValue(Func.toDouble(qtKeyCount));

        chartDataList.add(dqChartData);
        chartDataList.add(sqChartData);
        chartDataList.add(czChartData);
        chartDataList.add(qtrChartData);
        chartDataList.add(dzChartData);
        chartDataList.add(qtChartData);
        fTypeData.setChartData(chartDataList);

        return fTypeData;

    }

    /**
     * 是否与父母生活
     */
    TypeData getLivingWithParents(Long taskId) {

        /**
         * 是否与父母生活
         *
         * 2135703729	 与父母双方或一方生活在一起
         * 2135703730	 与（外）祖父母生活在一起
         * 2135703731	 与其他亲戚（朋友）生活在一起
         * 2135703732	 独自生活
         * 2135703733  其他
         */
        String t1Key =
                "education" + ":" + "is_alone" + ":" + "statistic" + ":" + taskId + ":" + 2135703729;
        String f1Key =
                "education" + ":" + "is_alone" + ":" + "statistic" + ":" + taskId + ":" + 2135703730;
        String f2Key =
                "education" + ":" + "is_alone" + ":" + "statistic" + ":" + taskId + ":" + 2135703731;
        String f3Key =
                "education" + ":" + "is_alone" + ":" + "statistic" + ":" + taskId + ":" + 2135703732;
        String f4Key =
                "education" + ":" + "is_alone" + ":" + "statistic" + ":" + taskId + ":" + 2135703733;

        Long t1KeyCount = redisTemplate.opsForSet().size(t1Key);
        Long f1KeyCount = redisTemplate.opsForSet().size(f1Key);
        Long f2KeyCount = redisTemplate.opsForSet().size(f2Key);
        Long f3KeyCount = redisTemplate.opsForSet().size(f3Key);
        Long f4KeyCount = redisTemplate.opsForSet().size(f4Key);

        Integer f1Count =
                Func.toInt(f1KeyCount) + Func.toInt(f2KeyCount) + Func.toInt(f3KeyCount) + Func.toInt(
                        f4KeyCount);

        TypeData lTypeData = new TypeData();
        lTypeData.setTitle("是否与父母生活");

        List lChartDataList = new ArrayList<ChartData>();
        ChartData tChartData = new ChartData();
        tChartData.setName("是");
        tChartData.setValue(Func.toDouble(t1KeyCount));

        ChartData fChartData = new ChartData();
        fChartData.setName("否");
        fChartData.setValue(Func.toDouble(f1Count));

        lChartDataList.add(tChartData);
        lChartDataList.add(fChartData);
        lTypeData.setChartData(lChartDataList);

        return lTypeData;
    }

    /**
     * 年级数据统计
     *
     * @param taskId
     * @param gradeId
     * @return
     */
    Integer getGradeData(Long taskId, Integer gradeId) {

        Criteria tCriteria = Criteria.where("taskId").is(taskId);
        Criteria gCriteria = Criteria.where("gradeId").is(gradeId);
        TypedAggregation<GradeReportData> aggregation = Aggregation.newAggregation(
                GradeReportData.class,
                Aggregation.match(tCriteria),
                Aggregation.match(gCriteria),
                Aggregation.group("gradeId").sum("totalPeople").as("count"),
                Aggregation.project("_id", "count").and("_id").as("grade").andExclude("_id")
        );

        AggregationResults<GroupData> results = mongoTemplate.aggregate(aggregation, GroupData.class);
        List<GroupData> mapList = results.getMappedResults();
        if (Func.isNotEmpty(mapList)) {
            GroupData groupData = mapList.get(0);
            Integer count = groupData.getCount();
            int value = count;
            return value;
        }
        return 0;
    }

    /**
     * 数据统计
     */
    @Data
    class GroupData {
        private Integer grade;
        private Integer count;
    }


    /**
     * 字段数据求和统计
     *
     * @param taskId
     * @return
     */
    Integer getTestPeopleData(Long taskId, String name) {

        Criteria tCriteria = Criteria.where("taskId").is(taskId);
        TypedAggregation<GradeReportData> aggregation = Aggregation.newAggregation(
                GradeReportData.class,
                Aggregation.match(tCriteria),
                Aggregation.group("null").sum(name).as("count"),
                Aggregation.project("count").andExclude("_id")
        );
        AggregationResults<GroupData> results = mongoTemplate.aggregate(aggregation, GroupData.class);
        List<GroupData> mapList = results.getMappedResults();
        if (Func.isNotEmpty(mapList)) {
            GroupData groupData = mapList.get(0);
            Integer count = groupData.getCount();
            int value = count;
            return value;
        }
        return 0;
    }

    /**
     * 平均值
     *
     * @param taskId
     * @param name
     * @return
     */
    Object getTestPeopleAvgData(Long taskId, String name) {

        Criteria tCriteria = Criteria.where("taskId").is(taskId);
        TypedAggregation<GradeReportData> aggregation = Aggregation.newAggregation(
                GradeReportData.class, Arrays.asList(
                        Aggregation.match(tCriteria),
                        Aggregation.group("null").avg(name).as("count"))
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, Map.class);
        List<Map> mapList = results.getMappedResults();
        if (Func.isNotEmpty(mapList)) {
            Map map = mapList.get(0);
            Object value = map.get("count");
            return value;
        }
        return 0;
    }


    /**
     * 年级数据统计
     *
     * @param taskId
     * @param gradeId
     * @return
     */
    ParticipatingGrade getParticipatingGrade(Long taskId, Integer gradeId) {

        Criteria tCriteria = Criteria.where("taskId").is(taskId);
        Criteria gCriteria = Criteria.where("gradeId").is(gradeId);
        TypedAggregation<GradeReportData> aggregation = Aggregation.newAggregation(
                GradeReportData.class, Arrays.asList(
                        Aggregation.match(tCriteria),
                        Aggregation.match(gCriteria),
                        Aggregation.group("gradeId")
                                .sum("totalPeople").as("totalPeople")
                                .sum("testPeople").as("testPeople")
                                .sum("noTestPeople").as("noTestPeople")
                                .sum("invalidPeople").as("invalidPeople")
                                .avg("completionRate").as("completionRate"))
        );

        AggregationResults<ParticipatingGrade> results = mongoTemplate.aggregate(aggregation,
                ParticipatingGrade.class);
        List<ParticipatingGrade> participatingGradeList = results.getMappedResults();
        if (Func.isNotEmpty(participatingGradeList)) {
            ParticipatingGrade participatingGrade = participatingGradeList.get(0);
            return participatingGrade;
        } else {
            return ParticipatingGrade.builder()
                    .totalPeople(0)
                    .testPeople(0)
                    .noTestPeople(0)
                    .invalidPeople(0)
                    .completionRate("0")
                    .build();
        }
    }


    /**
     * 年级数据统计
     *
     * @param taskId
     * @param gradeId
     * @return
     */
    AttentionLevelDistribution getAttentionLevelDistributionGrade(Long taskId, Integer gradeId) {

        Criteria tCriteria = Criteria.where("taskId").is(taskId);
        Criteria gCriteria = Criteria.where("gradeId").is(gradeId);
        TypedAggregation<GradeReportData> aggregation = Aggregation.newAggregation(
                GradeReportData.class, Arrays.asList(
                        Aggregation.match(tCriteria),
                        Aggregation.match(gCriteria),
                        Aggregation.group("gradeId")
                                .sum("followPeopleThree").as("threeAttentionLevelCount")
                                .avg("followRateThree").as("threeAttentionLevel")
                                .sum("followPeopleTwo").as("twoAttentionLevelCount")
                                .avg("followRateTwo").as("twoAttentionLevel")
                                .sum("followPeopleOne").as("oneAttentionLevelCount")
                                .avg("followRateOne").as("oneAttentionLevel")
                                .sum("followPeople").as("zeroAttentionLevelCount")
                                .avg("followRate").as("zeroAttentionLevel"))
        );

        AggregationResults<AttentionLevelDistribution> results = mongoTemplate.aggregate(aggregation,
                AttentionLevelDistribution.class);
        List<AttentionLevelDistribution> attentionLevelDistributionList = results.getMappedResults();
        if (Func.isNotEmpty(attentionLevelDistributionList)) {
            AttentionLevelDistribution attentionLevelDistribution = attentionLevelDistributionList.get(0);
            attentionLevelDistribution.setTaskId(taskId);

            attentionLevelDistribution.setThreeAttentionLevel(
                    MathUtil.dealRate(Func.toDouble(attentionLevelDistribution.getThreeAttentionLevel())));
            attentionLevelDistribution.setTwoAttentionLevel(
                    MathUtil.dealRate(Func.toDouble(attentionLevelDistribution.getTwoAttentionLevel())));
            attentionLevelDistribution.setOneAttentionLevel(
                    MathUtil.dealRate(Func.toDouble(attentionLevelDistribution.getOneAttentionLevel())));
            attentionLevelDistribution.setZeroAttentionLevel(
                    MathUtil.dealRate(Func.toDouble(attentionLevelDistribution.getZeroAttentionLevel())));

            return attentionLevelDistribution;
        } else {
            return AttentionLevelDistribution.builder()
                    .threeAttentionLevelCount(0)
                    .threeAttentionLevel("0%")
                    .twoAttentionLevelCount(0)
                    .twoAttentionLevel("0%")
                    .oneAttentionLevelCount(0)
                    .oneAttentionLevel("0%")
                    .zeroAttentionLevelCount(0)
                    .zeroAttentionLevel("0%")
                    .taskId(taskId)
                    .build();
        }
    }


    /**
     * 年级数据统计
     *
     * @param taskId
     * @param gradeId
     * @return
     */
    WarningLevelDistribution getWarningLevelDistributionGrade(Long taskId, Integer gradeId) {

        Criteria tCriteria = Criteria.where("taskId").is(taskId);
        Criteria gCriteria = Criteria.where("gradeId").is(gradeId);
        TypedAggregation<GradeReportData> aggregation = Aggregation.newAggregation(
                GradeReportData.class, Arrays.asList(
                        Aggregation.match(tCriteria),
                        Aggregation.match(gCriteria),
                        Aggregation.group("gradeId")
                                .sum("warnPeopleThree").as("threeWarningLevelCount")
                                .avg("warnRateThree").as("threeWarningLevel")
                                .sum("warnPeopleTwo").as("twoWarningLevelCount")
                                .avg("warnRateTwo").as("twoWarningLevel")
                                .sum("warnPeopleOne").as("oneWarningLevelCount")
                                .avg("warnRateOne").as("oneWarningLevel")
                                .sum("warnPeople").as("zeroWarningLevelCount")
                                .avg("warnRate").as("zeroWarningLevel"))
        );

        AggregationResults<WarningLevelDistribution> results = mongoTemplate.aggregate(aggregation,
                WarningLevelDistribution.class);
        List<WarningLevelDistribution> warningLevelDistributionList = results.getMappedResults();
        if (Func.isNotEmpty(warningLevelDistributionList)) {
            WarningLevelDistribution warningLevelDistribution = warningLevelDistributionList.get(0);
            warningLevelDistribution.setTaskId(taskId);

            warningLevelDistribution.setThreeWarningLevel(
                    MathUtil.dealRate(Func.toDouble(warningLevelDistribution.getThreeWarningLevel())));
            warningLevelDistribution.setTwoWarningLevel(
                    MathUtil.dealRate(Func.toDouble(warningLevelDistribution.getTwoWarningLevel())));
            warningLevelDistribution.setOneWarningLevel(
                    MathUtil.dealRate(Func.toDouble(warningLevelDistribution.getOneWarningLevel())));
            warningLevelDistribution.setZeroWarningLevel(
                    MathUtil.dealRate(Func.toDouble(warningLevelDistribution.getZeroWarningLevel())));

            return warningLevelDistribution;
        } else {
            return WarningLevelDistribution.builder()
                    .threeWarningLevelCount(0)
                    .threeWarningLevel("0%")
                    .twoWarningLevelCount(0)
                    .twoWarningLevel("0%")
                    .oneWarningLevelCount(0)
                    .oneWarningLevel("0%")
                    .zeroWarningLevelCount(0)
                    .zeroWarningLevel("0%")
                    .taskId(taskId)
                    .build();
        }
    }


    /**
     * 建议关注学生名单
     *
     * @param taskId
     * @return
     */
    List<GFollowUserVO> getAttentionStudentGrade(Long taskId) {

        Criteria tCriteria = Criteria.where("taskId").is(taskId);
        TypedAggregation<GradeReport> aggregation = Aggregation.newAggregation(
                GradeReport.class, Arrays.asList(
                        Aggregation.match(tCriteria),
                        Aggregation.project("testModuleTwoVO.attentionStudent"))
        );

        List<GFollowUserVO> allAttentionStudentList =  new ArrayList<>();
        AggregationResults<GTestModuleTwoVO> results = mongoTemplate.aggregate(aggregation,
                GTestModuleTwoVO.class);
        List<GTestModuleTwoVO> gTestModuleTwoVOList = results.getMappedResults();
        if (Func.isNotEmpty(gTestModuleTwoVOList)) {
            gTestModuleTwoVOList.forEach(gTestModuleTwoVO -> {
                List<GFollowUserVO> attentionStudentList = gTestModuleTwoVO.getAttentionStudent();
                allAttentionStudentList.addAll(attentionStudentList);
            });
            return allAttentionStudentList;
        } else {
            return new ArrayList<GFollowUserVO>();
        }

    }

    /**
     * 危机预警学生名单
     *
     * @param taskId
     * @return
     */
    List<GFollowUserVO> getWarnStudentGrade(Long taskId) {

        Criteria tCriteria = Criteria.where("taskId").is(taskId);
        TypedAggregation<GradeReport> aggregation = Aggregation.newAggregation(
                GradeReport.class, Arrays.asList(
                        Aggregation.match(tCriteria),
                        Aggregation.project("testModuleTwoVO.warningStudent"))
        );

        List<GFollowUserVO> allWarningStudentList =  new ArrayList<>();
        AggregationResults<GTestModuleTwoVO> results = mongoTemplate.aggregate(aggregation,
                GTestModuleTwoVO.class);
        List<GTestModuleTwoVO> gTestModuleTwoVOList = results.getMappedResults();
        if (Func.isNotEmpty(gTestModuleTwoVOList)) {
            gTestModuleTwoVOList.forEach(gTestModuleTwoVO -> {
                List<GFollowUserVO> warningStudentList = gTestModuleTwoVO.getWarningStudent();
                allWarningStudentList.addAll(warningStudentList);
            });
            return allWarningStudentList;
        } else {
            return new ArrayList<GFollowUserVO>();
        }

    }


    /**
     * 指标分
     *
     * @param taskId
     * @return
     */
    GradeReportData getIndexGradeReportData(Long taskId) {

        Criteria tCriteria = Criteria.where("taskId").is(taskId);

        TypedAggregation<GradeReportData> aggregation = Aggregation.newAggregation(
                GradeReportData.class, Arrays.asList(
                        Aggregation.match(tCriteria),
                        Aggregation.group("null")
                                .sum("testPeople").as("testPeople")
                                .sum("invalidPeople").as("invalidPeople")
                                .avg("learningAttitudeScore").as("learningAttitudeScore")
                                .avg("timeManagementScore").as("timeManagementScore")
                                .avg("learningBurnoutScore").as("learningBurnoutScore")

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

                                .avg("moralScore").as("moralScore")
                                .avg("stabilityScore").as("stabilityScore")
                                .avg("disciplineScore").as("disciplineScore")
                                .avg("otherPerformanceScore").as("otherPerformanceScore")

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
                                .sum("interpersonalSensitivityScoreVeryBad")
                                .as("interpersonalSensitivityScoreVeryBad")
                                .sum("interpersonalSensitivityScoreBad").as("interpersonalSensitivityScoreBad")
                                .sum("interpersonalSensitivityScoreGenerally")
                                .as("interpersonalSensitivityScoreGenerally")
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
                )
        );

        AggregationResults<GradeReportData> results = mongoTemplate.aggregate(aggregation,
                GradeReportData.class);
        List<GradeReportData> gradeReportDataList = results.getMappedResults();
        if (Func.isNotEmpty(gradeReportDataList)) {
            GradeReportData gradeReportData = gradeReportDataList.get(0);
            return gradeReportData;
        } else {
            return new GradeReportData();
        }

    }

    /**
     * 统计处理计算数据
     *
     * @param str1
     * @param str2
     * @return
     */
    static String getDivideData(String str1, String str2) {
        if (str2.equals("0")) {
            return "0";
        } else {
            return MathUtil.divide(str1, str2);
        }

    }

    @Override
    public GroupsReport generateBaseReport(Long taskId) {

        log.info("=== 开始生成团体报告 === {}", taskId);
        GroupsReport groupsReport = GroupsReport.builder().build();

        // 测评完成情况
        getEvaluationCompletion(groupsReport, taskId);

        // 基本信息
        getBasicInformation(groupsReport, taskId);

        // 心理健康评级
        getMentalHealthRatings(groupsReport, taskId);

        // 测评概况
        getTestOverview(groupsReport, taskId);

        // 指标数据
        GradeReportData indexGradeReportData = getIndexGradeReportData(taskId);

        // 学习状态
        getLearningStatus(groupsReport, taskId, indexGradeReportData);

        // 品行表现
        getBehavior(groupsReport, taskId, indexGradeReportData);

        // 心理韧性
        getMentalToughness(groupsReport, taskId, indexGradeReportData);

        // 综合压力
        getStressIndex(groupsReport, taskId, indexGradeReportData);

        // 情绪指数
        getEmotionalIndex(groupsReport, taskId, indexGradeReportData);

        // 睡眠指数
        getSleepIndex(groupsReport, taskId, indexGradeReportData);

        // 报告名字
        getReportName(groupsReport, taskId);

        groupsReport.setTaskId(taskId);
        groupsReport.setCreateTime(DateUtil.LocalDateTimeToStringTime(LocalDateTime.now()));
        mongoTemplate.insert(groupsReport);
        log.info("=== 团体报告已生成 === {}", taskId);
        return groupsReport;
    }
}
