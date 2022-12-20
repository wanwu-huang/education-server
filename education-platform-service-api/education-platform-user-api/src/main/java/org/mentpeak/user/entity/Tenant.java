package org.mentpeak.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.tenant.mp.TenantEntity;

/**
 * 平台-租户实体类
 *
 * @author lxp
 * @since 2022-09-27
 */
@Data
@TableName("platform_tenant")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Tenant对象", description = "平台-租户")
public class Tenant extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称")
      private String tenantName;
    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
      private String linkman;
    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
      private String contactNumber;
    /**
     * 联系地址
     */
    @ApiModelProperty(value = "联系地址")
      private String address;


}
