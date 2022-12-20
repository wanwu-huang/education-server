package org.mentpeak.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import org.mentpeak.user.entity.Tenant;
import org.mentpeak.user.vo.TenantVO;

/**
 * 平台-租户 Mapper 接口
 *
 * @author lxp
 * @since 2022-09-27
 */
public interface TenantMapper extends BaseMapper<Tenant> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param tenant
	 * @return
	 */
	List<TenantVO> selectTenantPage(IPage page, TenantVO tenant);

}
