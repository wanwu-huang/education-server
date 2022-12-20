package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 固定文本实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestCommonIntroductioin对象", description = "固定文本")
public class TestCommonIntroductioin extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父级ID
     */
    @ApiModelProperty(value = "父级ID")
    private Long parentId;
    /**
     * 说明
     */
    @ApiModelProperty(value = "说明")
    private String name;
    /**
     * 简介内容
     */
    @ApiModelProperty(value = "简介内容")
    private String introduction;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;


}
