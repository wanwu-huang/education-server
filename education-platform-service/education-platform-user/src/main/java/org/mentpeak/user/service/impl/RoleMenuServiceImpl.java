package org.mentpeak.user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mentpeak.user.entity.RoleMenu;
import org.mentpeak.user.mapper.RoleMenuMapper;
import org.mentpeak.user.service.IRoleMenuService;
import org.mentpeak.user.vo.RoleMenuVO;
import org.springframework.stereotype.Service;

/**
 * 平台-菜单-角色关系表 服务实现类
 *
 * @author lxp
 * @since 2022-07-21
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements IRoleMenuService {

	@Override
	public IPage<RoleMenuVO> selectRoleMenuPage(IPage<RoleMenuVO> page, RoleMenuVO roleMenu) {
		return page.setRecords(baseMapper.selectRoleMenuPage(page, roleMenu));
	}

}
