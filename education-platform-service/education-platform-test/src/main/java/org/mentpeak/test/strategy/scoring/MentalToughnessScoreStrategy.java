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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 心理韧性 维度计分
 *
 * @author demain_lee
 * @since 2022-08-11
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MentalToughnessScoreStrategy implements DimensionStrategy {

    private final TestPaperQuestionMapper paperQuestionMapper;

    private final TestDimensionIndexConclusionMapper dimensionIndexConclusionMapper;

    @Override
    public Long type() {
        return 1546789344491134978L;
    }

    @Override
    public DimensionIndexData dimensionScore(Long paperId) {

        /**
         *  指标计分
         *
         *  1546789344952508417	情绪管理  原始分÷3×25
         *  1546789349885009922	目标激励  原始分÷3×25
         *  1546789354800734210	积极关注  原始分÷3×25
         *  1546789359766790145	学校支持  原始分÷3×25
         *  1546789364607016961	人际支持  原始分÷3×25
         *  1546789369476603906	家庭支持  原始分÷3×25
         */

        //  情绪管理
        // 原始分
        Integer emotionManagementScore = paperQuestionMapper.indexTotalScore(1546789344952508417L, paperId);
        String emotion = MathUtil.divideSales(emotionManagementScore.toString(), "3", 1);
        String emotionScore = MathUtil.multiply(emotion, "25");


        //  目标激励
        // 原始分
        Integer goalMotivationScore = paperQuestionMapper.indexTotalScore(1546789349885009922L, paperId);
        String goal = MathUtil.divideSales(goalMotivationScore.toString(), "3", 1);
        String goalScore = MathUtil.multiply(goal, "25");

        //  积极关注
        // 原始分
        Integer positiveAttentionScore = paperQuestionMapper.indexTotalScore(1546789354800734210L, paperId);
        String positive = MathUtil.divideSales(positiveAttentionScore.toString(), "3", 1);
        String positiveScore = MathUtil.multiply(positive, "25");

        //  学校支持
        // 原始分
        Integer schoolSupportScore = paperQuestionMapper.indexTotalScore(1546789359766790145L, paperId);
        String school = MathUtil.divideSales(schoolSupportScore.toString(), "3", 1);
        String schoolScore = MathUtil.multiply(school, "25");


        //  人际支持
        // 原始分
        Integer interpersonalSupportScore = paperQuestionMapper.indexTotalScore(1546789364607016961L, paperId);
        String interpersonal = MathUtil.divideSales(interpersonalSupportScore.toString(), "3", 1);
        String interpersonalScore = MathUtil.multiply(interpersonal, "25");

        //  家庭支持
        // 原始分
        Integer familySupportScore = paperQuestionMapper.indexTotalScore(1546789369476603906L, paperId);
        String family = MathUtil.divideSales(familySupportScore.toString(), "3", 1);
        String familyScore = MathUtil.multiply(family, "25");





        Map<Integer, String> indexMap = new ConcurrentHashMap<>(6);
        TreeSet<Integer> indexSet = new TreeSet<>();

        // 维度等级1

        // 情绪管理 数据
        PersonalReport.ChartData emotionManagementChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> emotionManagementScoreList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789344952508417L));
        emotionManagementScoreList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(emotionScore, dimensionIndex.getIndexScope());
            if (result) {
                emotionManagementChartData.setTitle(dimensionIndex.getName());
                emotionManagementChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                emotionManagementChartData.setResult(dimensionIndex.getRiskResult());
                emotionManagementChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                emotionManagementChartData.setChartColor(ColorEnum.BLUE.getValue());
                emotionManagementChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        emotionManagementChartData.setScore(emotionScore);


        // 目标激励 数据
        PersonalReport.ChartData goalMotivationChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> goalMotivationDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789349885009922L));
        goalMotivationDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(goalScore, dimensionIndex.getIndexScope());
            if (result) {
                goalMotivationChartData.setTitle(dimensionIndex.getName());
                goalMotivationChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                goalMotivationChartData.setResult(dimensionIndex.getRiskResult());
                goalMotivationChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                goalMotivationChartData.setChartColor(ColorEnum.BLUE.getValue());
                goalMotivationChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        goalMotivationChartData.setScore(goalScore);


        // 积极关注 数据
        PersonalReport.ChartData positiveAttentionChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> positiveAttentionDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789354800734210L));
        positiveAttentionDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(positiveScore, dimensionIndex.getIndexScope());
            if (result) {
                positiveAttentionChartData.setTitle(dimensionIndex.getName());
                positiveAttentionChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                positiveAttentionChartData.setResult(dimensionIndex.getRiskResult());
                positiveAttentionChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                positiveAttentionChartData.setChartColor(ColorEnum.BLUE.getValue());
                positiveAttentionChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        positiveAttentionChartData.setScore(positiveScore);


        // 学校支持 数据
        PersonalReport.ChartData schoolSupportChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> schoolSupportDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789359766790145L));
        schoolSupportDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(schoolScore, dimensionIndex.getIndexScope());
            if (result) {
                schoolSupportChartData.setTitle(dimensionIndex.getName());
                schoolSupportChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                schoolSupportChartData.setResult(dimensionIndex.getRiskResult());
                schoolSupportChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                schoolSupportChartData.setChartColor(ColorEnum.BLUE.getValue());
                schoolSupportChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        schoolSupportChartData.setScore(schoolScore);


        // 人际支持 数据
        PersonalReport.ChartData interpersonalSupportChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> interpersonalDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789364607016961L));
        interpersonalDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(interpersonalScore, dimensionIndex.getIndexScope());
            if (result) {
                interpersonalSupportChartData.setTitle(dimensionIndex.getName());
                interpersonalSupportChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                interpersonalSupportChartData.setResult(dimensionIndex.getRiskResult());
                interpersonalSupportChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                interpersonalSupportChartData.setChartColor(ColorEnum.BLUE.getValue());
                interpersonalSupportChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        interpersonalSupportChartData.setScore(interpersonalScore);


        // 人际支持 数据
        PersonalReport.ChartData familySupportSupportChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> familySupportDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789369476603906L));
        familySupportDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(familyScore, dimensionIndex.getIndexScope());
            if (result) {
                familySupportSupportChartData.setTitle(dimensionIndex.getName());
                familySupportSupportChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                familySupportSupportChartData.setResult(dimensionIndex.getRiskResult());
                familySupportSupportChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                familySupportSupportChartData.setChartColor(ColorEnum.BLUE.getValue());
                familySupportSupportChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        familySupportSupportChartData.setScore(familyScore);

        Map<Long, PersonalReport.ChartData> map = new HashMap<>(6);

        map.put(1546789344952508417L, emotionManagementChartData);
        map.put(1546789349885009922L, goalMotivationChartData);
        map.put(1546789354800734210L, positiveAttentionChartData);
        map.put(1546789359766790145L, schoolSupportChartData);
        map.put(1546789364607016961L, interpersonalSupportChartData);
        map.put(1546789369476603906L, familySupportSupportChartData);

        log.info("试卷{},{}维度下数据{}", paperId, type(), map);


        /**
         * 维度计分
         * （情绪管理+目标激励+积极关注+学校支持+人际支持+家庭支持）÷6
         *
         * 维度等级判定逻辑1   该维度所有指标取最高等级（很差＞较差＞一般＞良好）
         * 维度等级判定逻辑2   对应数据库
         *
         * 维度最终等级 从判定逻辑1和判定逻辑2中选取最高的等级（很差＞较差＞一般＞良好）
         */

        // 维度分
        String d1 = MathUtil.add(emotionScore, goalScore);
        String d2 = MathUtil.add(positiveScore, schoolScore);
        String d3 = MathUtil.add(interpersonalScore, familyScore);
        String d4 = MathUtil.add(d1, d2);
        String d5 = MathUtil.add(d3, d4);

        String mentalToughnessDimensScore = MathUtil.divide(d5, "6", 1);


        // 维度等级2
        List<TestDimensionIndexConclusion> dimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery()
                .eq(TestDimensionIndexConclusion::getDimensionId, type())
                .isNull(TestDimensionIndexConclusion::getIndexId));

        PersonalReport.TotalResult totalResult = new PersonalReport.TotalResult();

        dimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(mentalToughnessDimensScore, dimensionIndex.getDimensionScope());
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
        totalResult.setResult(riskResult);
        totalResult.setRiskIndex(riskIndex);
        totalResult.setScore(mentalToughnessDimensScore);
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
