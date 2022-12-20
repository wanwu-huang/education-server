package org.mentpeak.user.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.user.entity.Tenant;

/**
 * 平台-租户数据传输对象实体类
 *
 * @author lxp
 * @since 2022-09-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantDTO extends Tenant {
	private static final long serialVersionUID = 1L;

}
