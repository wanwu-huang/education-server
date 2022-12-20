package org.mentpeak.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.user.entity.RoleMenu;
import org.mentpeak.user.vo.RoleMenuVO;

import java.util.List;

/**
 * 平台-菜单-角色关系表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-21
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param roleMenu
	 * @return
	 */
	List<RoleMenuVO> selectRoleMenuPage(IPage page, RoleMenuVO roleMenu);

}
