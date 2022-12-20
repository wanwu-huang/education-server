package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 测评途径表实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestApproach对象", description = "测评途径表")
public class TestApproach extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 名字
     */
    @ApiModelProperty(value = "名字")
    private String name;
    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    private String icon;
    /**
     * 二维码 url
     */
    @ApiModelProperty(value = "二维码 url")
    private String qrCode;
    /**
     * 内容描述
     */
    @ApiModelProperty(value = "内容描述")
    private String contentDescribe;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;


}
