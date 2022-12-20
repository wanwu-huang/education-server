package org.mentpeak.test.strategy.scoring;

import org.mentpeak.test.vo.DimensionIndexData;

/**
 * 维度计分策略接口
 * @author demain_lee
 * @since 2022-08-09
 */
public interface DimensionStrategy {

    /**
     * 类型
     * @return
     */
    Long type();

    /**
     * 维度分数信息
     * @param paperId 试卷ID
     * @return 指标信息
     */
    DimensionIndexData dimensionScore(Long paperId);
}
