package org.mentpeak.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mentpeak.user.entity.Menu;
import org.mentpeak.user.vo.MenuVO;

/**
 * 平台-菜单路由表 服务类
 *
 * @author lxp
 * @since 2022-07-21
 */
public interface IMenuService extends IService<Menu> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param menu
	 * @return
	 */
	IPage<MenuVO> selectMenuPage(IPage<MenuVO> page, MenuVO menu);

}
