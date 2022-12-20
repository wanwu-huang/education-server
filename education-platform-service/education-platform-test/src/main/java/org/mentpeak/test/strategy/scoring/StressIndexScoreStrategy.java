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
 * 综合压力 维度计分
 *
 * @author demain_lee
 * @since 2022-08-11
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StressIndexScoreStrategy implements DimensionStrategy {

    private final TestPaperQuestionMapper paperQuestionMapper;

    private final TestDimensionIndexConclusionMapper dimensionIndexConclusionMapper;

    @Override
    public Long type() {
        return 1546789419250409474L;
    }

    @Override
    public DimensionIndexData dimensionScore(Long paperId) {

        /**
         *  指标计分
         *
         *  1546789419690811394L	学习压力  原始分×6.25
         *  1546789426871459841L	人际压力  原始分×6.25
         *  1546789434203103233L	受惩罚压力 原始分×5
         *  1546789443141165057L	丧失压力  原始分÷7×25
         *  1546789455623413761L	适应压力  原始分×6.25
         */

        //  学习压力
        // 原始分
        Integer studyStressScore = paperQuestionMapper.indexTotalScore(1546789419690811394L, paperId);
        String study = MathUtil.multiply(studyStressScore.toString(), "6.25");
        String studyScore = getStr(study);


        //  人际压力
        // 原始分
        Integer interpersonalStressScore = paperQuestionMapper.indexTotalScore(1546789426871459841L, paperId);
        String interpersonal = MathUtil.multiply(interpersonalStressScore.toString(), "6.25");
        String interpersonalScore = getStr(interpersonal);

        //  受惩罚压力
        // 原始分
        Integer punishmentStressScore = paperQuestionMapper.indexTotalScore(1546789434203103233L, paperId);
        String punishment = MathUtil.multiply(punishmentStressScore.toString(), "5");
        String punishmentScore = getStr(punishment);

        //  丧失压力
        // 原始分
        Integer lossStressScore = paperQuestionMapper.indexTotalScore(1546789443141165057L, paperId);
        String loss = MathUtil.divideSales(lossStressScore.toString(), "7", 1);
        String lossScore = MathUtil.multiply(loss, "25");


        //  适应压力
        // 原始分
        Integer adaptationStressScore = paperQuestionMapper.indexTotalScore(1546789455623413761L, paperId);
        String adaptation = MathUtil.multiply(adaptationStressScore.toString(), "6.25");
        String adaptationScore = getStr(adaptation);


        Map<Integer, String> indexMap = new ConcurrentHashMap<>(5);
        TreeSet<Integer> indexSet = new TreeSet<>();

        // 维度等级1

        // 学习压力 数据
        PersonalReport.ChartData studyStressChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> studyStressDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789419690811394L));
        studyStressDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(studyScore, dimensionIndex.getIndexScope());
            if (result) {
                studyStressChartData.setTitle(dimensionIndex.getName());
                studyStressChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                studyStressChartData.setResult(dimensionIndex.getRiskResult());
                studyStressChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                studyStressChartData.setChartColor(ColorEnum.BLUE.getValue());
                studyStressChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        studyStressChartData.setScore(studyScore);


        // 人际压力 数据
        PersonalReport.ChartData interpersonalStressChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> interpersonalStressDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789426871459841L));
        interpersonalStressDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(interpersonalScore, dimensionIndex.getIndexScope());
            if (result) {
                interpersonalStressChartData.setTitle(dimensionIndex.getName());
                interpersonalStressChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                interpersonalStressChartData.setResult(dimensionIndex.getRiskResult());
                interpersonalStressChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                interpersonalStressChartData.setChartColor(ColorEnum.BLUE.getValue());
                interpersonalStressChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        interpersonalStressChartData.setScore(interpersonalScore);


        // 受惩罚压力 数据
        PersonalReport.ChartData punishmentStressChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> punishmentStressDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789434203103233L));
        punishmentStressDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(punishmentScore, dimensionIndex.getIndexScope());
            if (result) {
                punishmentStressChartData.setTitle(dimensionIndex.getName());
                punishmentStressChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                punishmentStressChartData.setResult(dimensionIndex.getRiskResult());
                punishmentStressChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                punishmentStressChartData.setChartColor(ColorEnum.BLUE.getValue());
                punishmentStressChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        punishmentStressChartData.setScore(punishmentScore);


        // 丧失压力 数据
        PersonalReport.ChartData lossStressChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> lossStressDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789443141165057L));
        lossStressDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(lossScore, dimensionIndex.getIndexScope());
            if (result) {
                lossStressChartData.setTitle(dimensionIndex.getName());
                lossStressChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                lossStressChartData.setResult(dimensionIndex.getRiskResult());
                lossStressChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                lossStressChartData.setChartColor(ColorEnum.BLUE.getValue());
                lossStressChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        lossStressChartData.setScore(lossScore);


        // 适应压力 数据
        PersonalReport.ChartData adaptationStressChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> adaptationStressDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789455623413761L));
        adaptationStressDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(adaptationScore, dimensionIndex.getIndexScope());
            if (result) {
                adaptationStressChartData.setTitle(dimensionIndex.getName());
                adaptationStressChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                adaptationStressChartData.setResult(dimensionIndex.getRiskResult());
                adaptationStressChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                adaptationStressChartData.setChartColor(ColorEnum.BLUE.getValue());
                adaptationStressChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        adaptationStressChartData.setScore(adaptationScore);

        Map<Long, PersonalReport.ChartData> map = new HashMap<>(5);

        map.put(1546789419690811394L, studyStressChartData);
        map.put(1546789426871459841L, interpersonalStressChartData);
        map.put(1546789434203103233L, punishmentStressChartData);
        map.put(1546789443141165057L, lossStressChartData);
        map.put(1546789455623413761L, adaptationStressChartData);

        log.info("试卷{},{}维度下数据{}", paperId, type(), map);


        /**
         * 维度计分
         * （（学习压力+人际压力+受惩罚压力+丧失压力+适应压力）÷6
         *
         * 维度等级判定逻辑1   该维度所有指标取最高等级（很差＞较差＞一般＞良好）
         * 维度等级判定逻辑2   对应数据库
         *
         * 维度最终等级 从判定逻辑1和判定逻辑2中选取最高的等级（很差＞较差＞一般＞良好）
         */

        // 维度分
        String d1 = MathUtil.add(studyScore, interpersonalScore);
        String d2 = MathUtil.add(punishmentScore, lossScore);
        String d3 = MathUtil.add(d1, d2);
        String d4 = MathUtil.add(d3, adaptationScore);
        String overallStressDimensScore = MathUtil.divide(d4, "6", 1);


        // 维度等级2
        List<TestDimensionIndexConclusion> dimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery()
                .eq(TestDimensionIndexConclusion::getDimensionId, type())
                .isNull(TestDimensionIndexConclusion::getIndexId));

        PersonalReport.TotalResult totalResult = new PersonalReport.TotalResult();

        dimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(overallStressDimensScore, dimensionIndex.getDimensionScope());
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
        totalResult.setScore(overallStressDimensScore);
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

    /**
     * 字符串
     *
     * @param str 字符串数据            3.35
     * @return 字符串类型（保留一位小数）  3.4
     */
    static String getStr(String str) {
        double data = Double.parseDouble(str);
        //利用字符串格式化的方式实现四舍五入,保留1位小数
        String result = String.format("%.1f", data);
        //1代表小数点后面的位数, 不足补0。f代表数据是浮点类型。保留2位小数就是“%.2f”，依此累推。
        return result;
    }

}
