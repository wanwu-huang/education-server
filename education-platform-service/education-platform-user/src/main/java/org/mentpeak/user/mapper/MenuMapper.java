package org.mentpeak.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.user.entity.Menu;
import org.mentpeak.user.vo.Children2VO;
import org.mentpeak.user.vo.MenuVO;

import java.util.List;

/**
 * 平台-菜单路由表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-21
 */
public interface MenuMapper extends BaseMapper<Menu> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param menu
	 * @return
	 */
	List<MenuVO> selectMenuPage(IPage page, MenuVO menu);

	List<Children2VO> getMenuById(@Param("parentId") Long parentId);

}
