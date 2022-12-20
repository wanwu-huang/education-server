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
 * 情绪指数 维度计分
 *
 * @author demain_lee
 * @since 2022-08-11
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmotionalIndexScoreStrategy implements DimensionStrategy {

    private final TestPaperQuestionMapper paperQuestionMapper;

    private final TestDimensionIndexConclusionMapper dimensionIndexConclusionMapper;

    @Override
    public Long type() {
        return 1546789515505491970L;
    }

    @Override
    public DimensionIndexData dimensionScore(Long paperId) {

        /**
         *  指标计分
         *
         *  1546789515920728066L	强迫     原始分÷6×25
         *  1546789524955258882L	偏执     原始分×6.25
         *  1546789530986668033L	敌对     原始分÷6×25
         *  1546789540155416578L	人际敏感  原始分×5
         *  1546789547860353026L	焦虑     原始分÷6×25
         *  1546789556760666113L	抑郁     原始分÷27×100
         */

        //  强迫
        // 原始分
        Integer compulsionScore = paperQuestionMapper.indexTotalScore(1546789515920728066L, paperId);
        String compulsion = MathUtil.divideSales(compulsionScore.toString(), "6", 1);
        String cScore = MathUtil.multiply(compulsion, "25");


        //  偏执
        // 原始分
        Integer paranoiaScore = paperQuestionMapper.indexTotalScore(1546789524955258882L, paperId);
        String paranoia = MathUtil.multiply(paranoiaScore.toString(), "6.25");
        String pScore = getStr(paranoia);

        //  敌对
        // 原始分
        Integer hostilityScore = paperQuestionMapper.indexTotalScore(1546789530986668033L, paperId);
        String hostility = MathUtil.divideSales(hostilityScore.toString(), "6", 1);
        String hScore = MathUtil.multiply(hostility, "25");

        //  人际敏感
        // 原始分
        Integer interpersonalSensitivityScore = paperQuestionMapper.indexTotalScore(1546789540155416578L, paperId);
        String interScore = MathUtil.multiply(interpersonalSensitivityScore.toString(), "5");
        String rInterScore = getStr(interScore);

        //  焦虑
        // 原始分
        Integer anxietyScore = paperQuestionMapper.indexTotalScore(1546789547860353026L, paperId);
        String anxiety = MathUtil.divideSales(anxietyScore.toString(), "6", 1);
        String aScore = MathUtil.multiply(anxiety, "25");


        //  抑郁
        // 原始分
        Integer depressionScore = paperQuestionMapper.indexTotalScore(1546789556760666113L, paperId);
        String depression = MathUtil.divideSales(depressionScore.toString(), "27", 1);
        String dScore = MathUtil.multiply(depression, "100");


        Map<Integer, String> indexMap = new ConcurrentHashMap<>(6);
        TreeSet<Integer> indexSet = new TreeSet<>();

        // 维度等级1

        // 强迫 数据
        PersonalReport.ChartData compulsionChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> compulsionDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789515920728066L));
        compulsionDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(cScore, dimensionIndex.getIndexScope());
            if (result) {
                compulsionChartData.setTitle(dimensionIndex.getName());
                compulsionChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                compulsionChartData.setResult(dimensionIndex.getRiskResult());
                compulsionChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                compulsionChartData.setChartColor(ColorEnum.BLUE.getValue());
                compulsionChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        compulsionChartData.setScore(cScore);


        // 偏执 数据
        PersonalReport.ChartData paranoiaChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> paranoiaDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789524955258882L));
        paranoiaDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(pScore, dimensionIndex.getIndexScope());
            if (result) {
                paranoiaChartData.setTitle(dimensionIndex.getName());
                paranoiaChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                paranoiaChartData.setResult(dimensionIndex.getRiskResult());
                paranoiaChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                paranoiaChartData.setChartColor(ColorEnum.BLUE.getValue());
                paranoiaChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        paranoiaChartData.setScore(pScore);


        // 敌对 数据
        PersonalReport.ChartData hostilityChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> hostilityDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789530986668033L));
        hostilityDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(hScore, dimensionIndex.getIndexScope());
            if (result) {
                hostilityChartData.setTitle(dimensionIndex.getName());
                hostilityChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                hostilityChartData.setResult(dimensionIndex.getRiskResult());
                hostilityChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                hostilityChartData.setChartColor(ColorEnum.BLUE.getValue());
                hostilityChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        hostilityChartData.setScore(hScore);


        // 人际敏感 数据
        PersonalReport.ChartData interpersonalSensitivityChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> interpersonalSensitivityDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789540155416578L));
        interpersonalSensitivityDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(rInterScore, dimensionIndex.getIndexScope());
            if (result) {
                interpersonalSensitivityChartData.setTitle(dimensionIndex.getName());
                interpersonalSensitivityChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                interpersonalSensitivityChartData.setResult(dimensionIndex.getRiskResult());
                interpersonalSensitivityChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                interpersonalSensitivityChartData.setChartColor(ColorEnum.BLUE.getValue());
                interpersonalSensitivityChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        interpersonalSensitivityChartData.setScore(rInterScore);


        // 焦虑 数据
        PersonalReport.ChartData anxietyChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> anxietyDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789547860353026L));
        anxietyDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(aScore, dimensionIndex.getIndexScope());
            if (result) {
                anxietyChartData.setTitle(dimensionIndex.getName());
                anxietyChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                anxietyChartData.setResult(dimensionIndex.getRiskResult());
                anxietyChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                anxietyChartData.setChartColor(ColorEnum.BLUE.getValue());
                anxietyChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        anxietyChartData.setScore(aScore);



        // 抑郁 数据
        PersonalReport.ChartData depressionChartData = new PersonalReport.ChartData();
        List<TestDimensionIndexConclusion> depressionDimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery().eq(TestDimensionIndexConclusion::getIndexId, 1546789556760666113L));
        depressionDimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(dScore, dimensionIndex.getIndexScope());
            if (result) {
                depressionChartData.setTitle(dimensionIndex.getName());
                depressionChartData.setRiskIndex(dimensionIndex.getRiskIndex());
                depressionChartData.setResult(dimensionIndex.getRiskResult());
                depressionChartData.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                depressionChartData.setChartColor(ColorEnum.BLUE.getValue());
                depressionChartData.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));
                indexMap.put(dimensionIndex.getRiskIndex(), dimensionIndex.getRiskResult());
                indexSet.add(dimensionIndex.getRiskIndex());
            }
        });
        depressionChartData.setScore(dScore);


        Map<Long, PersonalReport.ChartData> map = new HashMap<>(6);

        map.put(1546789515920728066L, compulsionChartData);
        map.put(1546789524955258882L, paranoiaChartData);
        map.put(1546789530986668033L, hostilityChartData);
        map.put(1546789540155416578L, interpersonalSensitivityChartData);
        map.put(1546789547860353026L, anxietyChartData);
        map.put(1546789556760666113L, depressionChartData);

        log.info("试卷{},{}维度下数据{}", paperId, type(), map);


        /**
         * 维度计分
         * （强迫+偏执+敌对+人际敏感+焦虑＋抑郁）÷6
         *
         * 维度等级判定逻辑1   该维度所有指标取最高等级（很差＞较差＞一般＞良好）
         * 维度等级判定逻辑2   对应数据库
         *
         * 维度最终等级 从判定逻辑1和判定逻辑2中选取最高的等级（很差＞较差＞一般＞良好）
         */

        // 维度分
        String d1 = MathUtil.add(cScore, pScore);
        String d2 = MathUtil.add(hScore, rInterScore);
        String d3 = MathUtil.add(aScore, dScore);
        String d4 = MathUtil.add(d1, d2);
        String d5 = MathUtil.add(d3, d4);

        String emotionalIndexDimensScore = MathUtil.divide(d5, "6", 1);


        // 维度等级2
        List<TestDimensionIndexConclusion> dimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery()
                .eq(TestDimensionIndexConclusion::getDimensionId, type())
                .isNull(TestDimensionIndexConclusion::getIndexId));

        PersonalReport.TotalResult totalResult = new PersonalReport.TotalResult();

        dimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(emotionalIndexDimensScore, dimensionIndex.getDimensionScope());
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
        totalResult.setScore(emotionalIndexDimensScore);
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
