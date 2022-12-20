package org.mentpeak.user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.user.entity.Tenant;
import org.mentpeak.user.mapper.TenantMapper;
import org.mentpeak.user.service.ITenantService;
import org.mentpeak.user.vo.TenantVO;
import org.mentpeak.user.wrapper.TenantWrapper;
import org.springframework.stereotype.Service;

/**
 * 平台-租户 服务实现类
 *
 * @author lxp
 * @since 2022-09-27
 */
@Service
public class TenantServiceImpl extends BaseServiceImpl<TenantMapper, Tenant> implements ITenantService {

	@Override
	public IPage<TenantVO> selectTenantPage(IPage<TenantVO> page, TenantVO tenant) {
		return page.setRecords(baseMapper.selectTenantPage(page, tenant));
	}

	@Override
	public TenantVO getTenantInfo() {
		String tenantCode = SecureUtil.getTenantCode();
		Tenant tenant = baseMapper.selectOne(
				Wrappers.<Tenant>lambdaQuery().eq(Tenant::getTenantCode, tenantCode));
		TenantWrapper tenantWrapper = new TenantWrapper();
		TenantVO tenantVO = tenantWrapper.entityVO(tenant);
		return tenantVO;
	}
}
