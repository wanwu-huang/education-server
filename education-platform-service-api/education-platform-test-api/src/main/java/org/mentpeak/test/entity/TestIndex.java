package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 指标表实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestIndex对象", description = "指标表")
public class TestIndex extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 指标名称
     */
    @ApiModelProperty(value = "指标名称")
    private String name;
    /**
     * 维度ID
     */
    @ApiModelProperty(value = "维度ID")
    private Long dimensionId;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;


}
