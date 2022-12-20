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

import java.util.List;

/**
 * 睡眠指数 维度计分
 *
 * @author demain_lee
 * @since 2022-08-11
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SleepIndexScoreStrategy implements DimensionStrategy {

    private final TestPaperQuestionMapper paperQuestionMapper;

    private final TestDimensionIndexConclusionMapper dimensionIndexConclusionMapper;

    @Override
    public Long type() {
        return 1546789785358622721L;
    }

    @Override
    public DimensionIndexData dimensionScore(Long paperId) {

        /**
         *  维度 睡眠指数 计分
         *
         *  原始分÷7×25
         */

        // 原始分
        Integer sleepIndexScore = paperQuestionMapper.dimensionTotalScore(1546789785358622721L, paperId);

        String sleep = MathUtil.divideSales(sleepIndexScore.toString(), "7", 1);
        String sleepScore = MathUtil.multiply(sleep, "25");

        // 维度等级
        List<TestDimensionIndexConclusion> dimensionIndexConclusionList = dimensionIndexConclusionMapper.selectList(Wrappers.<TestDimensionIndexConclusion>lambdaQuery()
                .eq(TestDimensionIndexConclusion::getDimensionId, type())
                .isNull(TestDimensionIndexConclusion::getIndexId));

        PersonalReport.TotalResult totalResult = new PersonalReport.TotalResult();

        dimensionIndexConclusionList.forEach(dimensionIndex -> {
            boolean result = IntervalUtil.isInTheInterval(sleepScore, dimensionIndex.getDimensionScope());
            if (result) {
                totalResult.setTitle(dimensionIndex.getDimensionName());
                totalResult.setResultDes(dimensionIndex.getExplanation() + "<br/>" + dimensionIndex.getSuggest());
                totalResult.setResult(dimensionIndex.getRiskResult());
                totalResult.setRiskIndex(dimensionIndex.getRiskIndex());
                totalResult.setFontColor(getFontColor(dimensionIndex.getRiskIndex()));

            }
        });

        // 维度最终等级
        totalResult.setScore(sleepScore);

        DimensionIndexData dimensionIndexData = DimensionIndexData.builder()
                .totalData(totalResult)
                .build();


        log.info("试卷{},{}维度下数据{}", paperId, type(), dimensionIndexData);

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
