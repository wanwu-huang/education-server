package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.user.entity.Menu;

/**
 * 平台-菜单路由表视图实体类
 *
 * @author lxp
 * @since 2022-07-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MenuVO对象", description = "平台-菜单路由表")
public class MenuVO extends Menu {
	private static final long serialVersionUID = 1L;

}
