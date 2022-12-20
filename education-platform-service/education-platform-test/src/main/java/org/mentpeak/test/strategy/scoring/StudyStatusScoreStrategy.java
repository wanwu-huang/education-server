package org.mentpeak.test.strategy.scoring;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mentpeak.common.util.IntervalUtil;
import org.mentpeak.common.util.MathUtil;
import org.mentpeak.test.entity.TestDimensionIndexConclusion;
import org.mentpeak.test.entity.mongo.PersonalReport;
import org.mentpeak.test.mapper.TestDimensionIndexConclusionMapper;
import org.mentpeak.test.mapper.TestPaperQuestionMapper;
import org.mentpeak.test.vo.DimensionIndexData;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 学习状态 维度计分
 *
 * @author demain_lee
 * @since 2022-08-09
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudyStatusScoreStrategy implements DimensionStrategy {

    private final TestPaperQuestionMapper paperQuestionMapper;

    private final TestDimensionIndexConclusionMapper dimensionIndexConclusionMapper;

    @Override
    public Long type() {
        return 1546788164255932417L;
    }

    @Override
    public DimensionIndexData dimensionScore(Long paperId) {

        /**
         *  指标计分
         *
         *  1546788164788609025 学习态度  原始分÷6×25
         *  1546788174330650626 时间管理  原始分×2.5
         *  1546788189962821634 学习倦怠  原始分÷6×25
         */

        //  学习态度
        // 原始分
        Integer learningAttitudeTotalScore = paperQuestionMapper.indexTotalScore(1546788164788609025L, paperId);
        String latt = MathUtil.divideSales(learningAttitudeTotalScore.toString(), "6", 1);
        String learningAttitudeScore = MathUtil.multiply(latt, "25");


        //  时间管理
        // 原始分
        Integer timeManagementTotalScore = paperQuestionMapper.indexTotalScore(1546788174330650626L, paperId);
        String timeManagementScore = MathUtil.multiply(timeManagementTotalScore.toString(), "2.5");

        //  学习倦怠
        // 原始分
        Integer learningBurnoutTotalScore = paperQuestionMapper.indexTotalScore(1546788189962821634L, paperId);
        String lburn = MathUtil.divideSales(learningBurnoutTotalScore.toString(), "6", 1);
        String learningBurnoutScore = MathUtil.multiply(lburn, "25");


        Map<Integer, String> indexMap = new ConcurrentHashMap<>(3);
        TreeSet<Integer> indexSet = new TreeSet<>();

        // 维度等级1

        // 学习态度 数据
        PersonalReport.ChartData learningAttitudeChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> learningAttitudeDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546788164788609025L));
        learningAttitudeDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(learningAttitudeScore, dimensionIndex.getIndexScope());
            if (result) {
                learningAttitudeChartData.setTitle(dimensionIndex.getName());
                learningAttitudeChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                learningAttitudeChartData.setResult(dimensionIndex.getRiskResult());
                learningAttitudeChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                learningAttitudeChartData.setChartColor(ColorEnum.BLUE.getValue());
                learningAttitudeChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        learningAttitudeChartData.setScore(learningAttitudeScore);


        // 时间管理 数据
        PersonalReport.ChartData timeManagementChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> timeManagementDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546788174330650626L));
        timeManagementDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(timeManagementScore, dimensionIndex.getIndexScope());
            if (result) {
                timeManagementChartData.setTitle(dimensionIndex.getName());
                timeManagementChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                timeManagementChartData.setResult(dimensionIndex.getRiskResult());
                timeManagementChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                timeManagementChartData.setChartColor(ColorEnum.BLUE.getValue());
                timeManagementChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        timeManagementChartData.setScore(timeManagementScore);


        // 学习倦怠 数据
        PersonalReport.ChartData learningBurnoutChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> learningBurnoutDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546788189962821634L));
        learningBurnoutDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(learningBurnoutScore, dimensionIndex.getIndexScope());
            if (result) {
                learningBurnoutChartData.setTitle(dimensionIndex.getName());
                learningBurnoutChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                learningBurnoutChartData.setResult(dimensionIndex.getRiskResult());
                learningBurnoutChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                learningBurnoutChartData.setChartColor(ColorEnum.ORANGE.getValue());
                learningBurnoutChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        learningBurnoutChartData.setScore(learningBurnoutScore);


        Map<Long, PersonalReport.ChartData> map = new HashMap<>(3);

        map.put(1546788164788609025L, learningAttitudeChartData);
        map.put(1546788174330650626L, timeManagementChartData);
        map.put(1546788189962821634L, learningBurnoutChartData);

        log.info("试卷{},{}维度下数据{}", paperId, type(), map);


        /**
         * 维度计分
         * [学习态度+时间管理+（100-学习倦怠）]÷3
         *
         * 维度等级判定逻辑1   该维度所有指标取最高等级（很差＞较差＞一般＞良好）
         * 维度等级判定逻辑2   对应数据库
         *
         * 维度最终等级 从判定逻辑1和判定逻辑2中选取最高的等级（很差＞较差＞一般＞良好）
         */

        // 维度分
        String d1 = MathUtil.add(learningAttitudeScore, learningBurnoutScore);
        String d2 = MathUtil.subtract("100", learningBurnoutScore);
        String d3 = MathUtil.add(d1, d2);
        String studyDimensScore = MathUtil.divide(d3, "3", 1);


        // 维度等级2
        List<TestDimensionIndexConclusion> dimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery()
                .eq(TestDimensionIndexConclusion::getDimensionId, type())
                .isNull(TestDimensionIndexConclusion::getIndexId));

        PersonalReport.TotalResult totalResult = new PersonalReport.TotalResult();

        dimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(studyDimensScore, dimensionIndex.getDimensionScope());
            if (result) {
                totalResult.setTitle(dimensionIndex.getDimensionName());
                totalResult.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });

        // 维度最终等级
        Integer riskIndex = indexSet.last();
        String riskResult = indexMap.get(riskIndex);
        totalResult.setRiskIndex(riskIndex);
        totalResult.setResult(riskResult);
        totalResult.setScore(studyDimensScore);
        totalResult.setFontColor(getFontColor(riskIndex));

        DimensionIndexData dimensionIndexData = DimensionIndexData.builder()
                .indexData(map)
                .totalData(totalResult)
                .build();

        return dimensionIndexData;
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
