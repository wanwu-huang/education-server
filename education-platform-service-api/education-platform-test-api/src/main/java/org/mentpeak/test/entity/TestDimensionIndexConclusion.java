package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 维度指标结论实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestDimensionIndexConclusion对象", description = "维度指标结论")
public class TestDimensionIndexConclusion extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 维度ID
     */
    @ApiModelProperty(value = "维度ID")
    private Long dimensionId;
    /**
     * 维度名称
     */
    @ApiModelProperty(value = "维度名称")
    private String dimensionName;
    /**
     * 问卷ID
     */
    @ApiModelProperty(value = "问卷ID")
    private Long questionnaireId;
    /**
     * 指标ID
     */
    @ApiModelProperty(value = "指标ID")
    private Long indexId;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;
    /**
     * 风险指数
     */
    @ApiModelProperty(value = "风险指数")
    private Integer riskIndex;
    /**
     * 风险结果
     */
    @ApiModelProperty(value = "风险结果")
    private String riskResult;
    /**
     * 维度区间
     */
    @ApiModelProperty(value = "维度区间")
    private String dimensionScope;
    /**
     * 指标区间
     */
    @ApiModelProperty(value = "指标区间")
    private String indexScope;
    /**
     * 外区间
     */
    @ApiModelProperty(value = "外区间")
    private String outerInterval;
    /**
     * 内区间
     */
    @ApiModelProperty(value = "内区间")
    private String innerInterval;
    /**
     * 说明
     */
    @ApiModelProperty(value = "说明")
    private String explanation;
    /**
     * 解析
     */
    @ApiModelProperty(value = "解析")
    private String analysis;
    /**
     * 建议
     */
    @ApiModelProperty(value = "建议")
    private String suggest;


}
