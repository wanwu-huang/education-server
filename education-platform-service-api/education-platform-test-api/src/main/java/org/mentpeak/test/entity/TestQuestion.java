package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 题干信息表实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestQuestion对象", description = "题干信息表")
public class TestQuestion extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 模块ID
     */
    @ApiModelProperty(value = "模块ID")
    private Long moduleId;
    /**
     * 题目内容
     */
    @ApiModelProperty(value = "题目内容")
    private String title;
    /**
     * 题型
     */
    @ApiModelProperty(value = "题型")
    private Long questionType;
    /**
     * 曝光量 (题目使用次数)
     */
    @ApiModelProperty(value = "曝光量 (题目使用次数)")
    private Long pv;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;


}
