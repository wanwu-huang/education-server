package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 家庭结构实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ReportFamilyStructure对象", description = "家庭结构")
public class ReportFamilyStructure extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 家庭结构
     */
    @ApiModelProperty(value = "家庭结构")
    private String name;
    /**
     * 家庭结构 对应选项ID
     */
    @ApiModelProperty(value = "家庭结构 对应选项ID")
    private Long optionId;
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
