package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 生活方式（是否与父母生活）实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ReportLifeStyle对象", description = "生活方式（是否与父母生活）")
public class ReportLifeStyle extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 家庭结构
     */
    @ApiModelProperty(value = "家庭结构")
    private String name;
    /**
     * 生活方式 对应多个选项ID
     */
    @ApiModelProperty(value = "生活方式 对应多个选项ID")
    private String optionId;
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
