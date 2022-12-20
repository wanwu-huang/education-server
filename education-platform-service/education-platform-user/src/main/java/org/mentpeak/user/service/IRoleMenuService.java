package org.mentpeak.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mentpeak.user.entity.RoleMenu;
import org.mentpeak.user.vo.RoleMenuVO;

/**
 * 平台-菜单-角色关系表 服务类
 *
 * @author lxp
 * @since 2022-07-21
 */
public interface IRoleMenuService extends IService<RoleMenu> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param roleMenu
	 * @return
	 */
	IPage<RoleMenuVO> selectRoleMenuPage(IPage<RoleMenuVO> page, RoleMenuVO roleMenu);

}
