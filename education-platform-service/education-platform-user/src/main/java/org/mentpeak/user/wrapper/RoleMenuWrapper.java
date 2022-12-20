package org.mentpeak.user.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.user.entity.RoleMenu;
import org.mentpeak.user.vo.RoleMenuVO;

/**
 * 平台-菜单-角色关系表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-21
 */
@AllArgsConstructor
public class RoleMenuWrapper extends BaseEntityWrapper<RoleMenu, RoleMenuVO>  {


	@Override
	public RoleMenuVO entityVO(RoleMenu roleMenu) {
		RoleMenuVO roleMenuVO = BeanUtil.copy(roleMenu, RoleMenuVO.class);


		return roleMenuVO;
	}

}
