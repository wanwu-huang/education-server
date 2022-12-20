package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 问卷模块表实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestModules对象", description = "问卷模块表")
public class TestModules extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父模块ID
     */
    @ApiModelProperty(value = "父模块ID")
    private Long parentId;
    /**
     * 维度ID
     */
    @ApiModelProperty(value = "维度ID")
    private Long dimensionId;
    /**
     * 指标ID
     */
    @ApiModelProperty(value = "指标ID")
    private Long indexId;
    /**
     * 模块名称
     */
    @ApiModelProperty(value = "模块名称")
    private String name;
    /**
     * 指导语ID
     */
    @ApiModelProperty(value = "指导语ID")
    private Long introductioinId;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;


}
