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
 * 品行表现 维度计分
 *
 * @author demain_lee
 * @since 2022-08-11
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BehaviorScoreStrategy implements DimensionStrategy {

    private final TestPaperQuestionMapper paperQuestionMapper;

    private final TestDimensionIndexConclusionMapper dimensionIndexConclusionMapper;

    @Override
    public Long type() {
        return 1546788937710755842L;
    }

    @Override
    public DimensionIndexData dimensionScore(Long paperId) {

        /**
         *  指标计分
         *
         *  1546788938151157761	道德性    原始分×6.25
         *  1546788944375504898	稳定性    原始分÷3×25
         *  1546788949077319682	纪律性    原始分÷3×25
         *  1546788953720414210	其他表现  原始分×6.25
         */

        //  道德性
        // 原始分
        Integer moralScore = paperQuestionMapper.indexTotalScore(1546788938151157761L, paperId);
        String mScore = MathUtil.multiply(moralScore.toString(), "6.25");
        String rmScore = getStr(mScore);


        //  稳定性
        // 原始分
        Integer stabilityScore = paperQuestionMapper.indexTotalScore(1546788944375504898L, paperId);
        String stability = MathUtil.divideSales(stabilityScore.toString(), "3", 1);
        String stScore = MathUtil.multiply(stability, "25");

        //  纪律性
        // 原始分
        Integer disciplineScore = paperQuestionMapper.indexTotalScore(1546788949077319682L, paperId);
        String discipline = MathUtil.divideSales(disciplineScore.toString(), "3", 1);
        String dScore = MathUtil.multiply(discipline, "25");

        //  其他表现
        // 原始分
        Integer otherPerformanceScore = paperQuestionMapper.indexTotalScore(1546788953720414210L, paperId);
        String oScore = MathUtil.multiply(otherPerformanceScore.toString(), "6.25");
        String roScore = getStr(oScore);


        Map<Integer, String> indexMap = new ConcurrentHashMap<>(4);
        TreeSet<Integer> indexSet = new TreeSet<>();

        // 维度等级1

        // 道德性 数据
        PersonalReport.ChartData moralChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> moralScoreList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546788938151157761L));
        moralScoreList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(rmScore, dimensionIndex.getIndexScope());
            if (result) {
                moralChartData.setTitle(dimensionIndex.getName());
                moralChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                moralChartData.setResult(dimensionIndex.getRiskResult());
                moralChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                moralChartData.setChartColor(ColorEnum.BLUE.getValue());
                moralChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        moralChartData.setScore(rmScore);


        // 稳定性 数据
        PersonalReport.ChartData stabilityChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> stabilityDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546788944375504898L));
        stabilityDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(stScore, dimensionIndex.getIndexScope());
            if (result) {
                stabilityChartData.setTitle(dimensionIndex.getName());
                stabilityChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                stabilityChartData.setResult(dimensionIndex.getRiskResult());
                stabilityChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                stabilityChartData.setChartColor(ColorEnum.BLUE.getValue());
                stabilityChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        stabilityChartData.setScore(stScore);


        // 纪律性 数据
        PersonalReport.ChartData disciplineChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> disciplineDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546788949077319682L));
        disciplineDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(dScore, dimensionIndex.getIndexScope());
            if (result) {
                disciplineChartData.setTitle(dimensionIndex.getName());
                disciplineChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                disciplineChartData.setResult(dimensionIndex.getRiskResult());
                disciplineChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                disciplineChartData.setChartColor(ColorEnum.BLUE.getValue());
                disciplineChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        disciplineChartData.setScore(dScore);


        // 其他表现 数据
        PersonalReport.ChartData otherPerformanceChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> otherPerformanceDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546788953720414210L));
        otherPerformanceDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(roScore, dimensionIndex.getIndexScope());
            if (result) {
                otherPerformanceChartData.setTitle(dimensionIndex.getName());
                otherPerformanceChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                otherPerformanceChartData.setResult(dimensionIndex.getRiskResult());
                otherPerformanceChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                otherPerformanceChartData.setChartColor(ColorEnum.BLUE.getValue());
                otherPerformanceChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        otherPerformanceChartData.setScore(roScore);

        Map<Long, PersonalReport.ChartData> map = new HashMap<>(4);

        map.put(1546788938151157761L, moralChartData);
        map.put(1546788944375504898L, stabilityChartData);
        map.put(1546788949077319682L, disciplineChartData);
        map.put(1546788953720414210L, otherPerformanceChartData);

        log.info("试卷{},{}维度下数据{}", paperId, type(), map);


        /**
         * 维度计分
         * （道德性+稳定性+纪律性+其他表现）÷4
         *
         * 维度等级判定逻辑1   该维度所有指标取最高等级（很差＞较差＞一般＞良好）
         * 维度等级判定逻辑2   对应数据库
         *
         * 维度最终等级 从判定逻辑1和判定逻辑2中选取最高的等级（很差＞较差＞一般＞良好）
         */

        // 维度分
        String d1 = MathUtil.add(rmScore, stScore);
        String d2 = MathUtil.add(dScore, roScore);
        String d3 = MathUtil.add(d1, d2);
        String behaviorDimensScore = MathUtil.divide(d3, "4", 1);


        // 维度等级2
        List<TestDimensionIndexConclusion> dimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery()
                .eq(TestDimensionIndexConclusion::getDimensionId, type())
                .isNull(TestDimensionIndexConclusion::getIndexId));

        PersonalReport.TotalResult totalResult = new PersonalReport.TotalResult();

        dimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(behaviorDimensScore, dimensionIndex.getDimensionScope());
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
        totalResult.setScore(behaviorDimensScore);
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
