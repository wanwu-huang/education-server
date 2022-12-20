package org.mentpeak.user.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.user.entity.MenuData;
import org.mentpeak.user.vo.MenuDataVO;

/**
 * 菜单数据关联表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class MenuDataWrapper extends BaseEntityWrapper<MenuData, MenuDataVO>  {


	@Override
	public MenuDataVO entityVO(MenuData menuData) {
		MenuDataVO menuDataVO = BeanUtil.copy(menuData, MenuDataVO.class);


		return menuDataVO;
	}

}
