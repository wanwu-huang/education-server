package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * 维度指标题干关联表实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@ApiModel(value = "TestDimensionIndexQuestion对象", description = "维度指标题干关联表")
public class TestDimensionIndexQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 指标ID
     */
    @ApiModelProperty(value = "指标ID")
    private Long indexId;
    /**
     * 题干ID
     */
    @ApiModelProperty(value = "题干ID")
    private Long questionId;
    /**
     * 维度ID
     */
    @ApiModelProperty(value = "维度ID")
    private Long dimensionId;


}
