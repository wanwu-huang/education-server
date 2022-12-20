package org.mentpeak.user.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.user.entity.Menu;
import org.mentpeak.user.vo.MenuVO;

/**
 * 平台-菜单路由表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-21
 */
@AllArgsConstructor
public class MenuWrapper extends BaseEntityWrapper<Menu, MenuVO>  {


	@Override
	public MenuVO entityVO(Menu menu) {
		MenuVO menuVO = BeanUtil.copy(menu, MenuVO.class);


		return menuVO;
	}

}
