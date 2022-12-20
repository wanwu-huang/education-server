package org.mentpeak.user.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.user.entity.Role;
import org.mentpeak.user.vo.RoleVO;

/**
 * 平台-角色表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-19
 */
@AllArgsConstructor
public class RoleWrapper extends BaseEntityWrapper<Role, RoleVO>  {


	@Override
	public RoleVO entityVO(Role role) {
		RoleVO roleVO = BeanUtil.copy(role, RoleVO.class);


		return roleVO;
	}

}
