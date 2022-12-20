package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 家庭结构任务用户关联表实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ReortFamilyStructureTaskUser对象", description = "家庭结构任务用户关联表")
public class ReortFamilyStructureTaskUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantCode;
    /**
     * 测评任务ID
     */
    @ApiModelProperty(value = "测评任务ID")
    private Long testTaskId;
    /**
     * 家庭结构ID
     */
    @ApiModelProperty(value = "家庭结构ID")
    private Long familyStructureId;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;


}
