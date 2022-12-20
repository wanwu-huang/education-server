package org.mentpeak.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.user.entity.Tenant;
import org.mentpeak.user.vo.TenantVO;

/**
 * 平台-租户 服务类
 *
 * @author lxp
 * @since 2022-09-27
 */
public interface ITenantService extends BaseService<Tenant> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param tenant
	 * @return
	 */
	IPage<TenantVO> selectTenantPage(IPage<TenantVO> page, TenantVO tenant);


	/**
	 * 租户信息
	 * @return
	 */
	TenantVO getTenantInfo();
}
