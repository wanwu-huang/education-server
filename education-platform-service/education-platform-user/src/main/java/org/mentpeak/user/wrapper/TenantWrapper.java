package org.mentpeak.user.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.user.entity.Tenant;
import org.mentpeak.user.vo.TenantVO;

/**
 * 平台-租户包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-09-27
 */
@AllArgsConstructor
public class TenantWrapper extends BaseEntityWrapper<Tenant, TenantVO>  {


	@Override
	public TenantVO entityVO(Tenant tenant) {
		TenantVO tenantVO = BeanUtil.copy(tenant, TenantVO.class);


		return tenantVO;
	}

}
