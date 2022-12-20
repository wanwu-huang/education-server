package org.mentpeak.user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mentpeak.user.entity.Menu;
import org.mentpeak.user.mapper.MenuMapper;
import org.mentpeak.user.service.IMenuService;
import org.mentpeak.user.vo.MenuVO;
import org.springframework.stereotype.Service;

/**
 * 平台-菜单路由表 服务实现类
 *
 * @author lxp
 * @since 2022-07-21
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

	@Override
	public IPage<MenuVO> selectMenuPage(IPage<MenuVO> page, MenuVO menu) {
		return page.setRecords(baseMapper.selectMenuPage(page, menu));
	}

}
