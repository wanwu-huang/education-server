package org.mentpeak.parent.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;
import org.mentpeak.core.tenant.mp.TenantEntity;

/**
 * 家长端-匿名信表实体类
 *
 * @author lxp
 * @since 2022-06-15
 */
@Data
@TableName("parent_letters")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Letters对象", description = "家长端-匿名信表")
public class Letters extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantCode;
    /**
     * 信件信息
     */
    @ApiModelProperty(value = "信件信息",required = true)
    private String lettersContent;
    /**
     * 信件信息
     */
    @ApiModelProperty(value = "学校名称")
    private String school;


}
