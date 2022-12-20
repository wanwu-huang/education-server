package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 教师他评结论实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestTeacherConclusion对象", description = "教师他评结论")
public class TestTeacherConclusion extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 问卷ID
     */
    @ApiModelProperty(value = "问卷ID")
    private Long questionnaireId;
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
     * 区间
     */
    @ApiModelProperty(value = "区间")
    private String scope;
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
