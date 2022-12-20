package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.user.entity.Tenant;

/**
 * 平台-租户视图实体类
 *
 * @author lxp
 * @since 2022-09-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TenantVO对象", description = "平台-租户")
public class TenantVO extends Tenant {
	private static final long serialVersionUID = 1L;

}
