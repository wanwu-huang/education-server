package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.user.entity.RoleMenu;

/**
 * 平台-菜单-角色关系表视图实体类
 *
 * @author lxp
 * @since 2022-07-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RoleMenuVO对象", description = "平台-菜单-角色关系表")
public class RoleMenuVO extends RoleMenu {
	private static final long serialVersionUID = 1L;

}
