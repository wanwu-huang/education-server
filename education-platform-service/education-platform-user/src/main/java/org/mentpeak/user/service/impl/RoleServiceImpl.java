package org.mentpeak.user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.user.entity.Menu;
import org.mentpeak.user.entity.Role;
import org.mentpeak.user.entity.RoleMenu;
import org.mentpeak.user.mapper.MenuMapper;
import org.mentpeak.user.mapper.RoleMapper;
import org.mentpeak.user.mapper.RoleMenuMapper;
import org.mentpeak.user.service.IRoleService;
import org.mentpeak.user.vo.ChildrenVO;
import org.mentpeak.user.vo.PermissionVO;
import org.mentpeak.user.vo.RoleVO;
import org.mentpeak.user.wrapper.RoleWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 平台-角色表 服务实现类
 *
 * @author lxp
 * @since 2022-07-19
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    private final RoleMenuMapper roleMenuMapper;

    private final MenuMapper menuMapper;

    @Override
    public IPage<RoleVO> selectRolePage(IPage<RoleVO> page, RoleVO role) {
        return page.setRecords(baseMapper.selectRolePage(page, role));
    }

    @Override
    public List<RoleVO> roleList() {
        // 排除学生
        List<Role> roles = baseMapper.selectList(Wrappers.<Role>lambdaQuery()
                .notIn(Role::getId, 3, 4, 5));
        RoleWrapper wrapper = new RoleWrapper();
        return wrapper.listVO(roles);
    }

    @Override
    public List<RoleVO> roleList2() {
        // 排除学生
        List<Role> roles = baseMapper.selectList(Wrappers.<Role>lambdaQuery()
                .notIn(Role::getId, 3, 4));
        RoleWrapper wrapper = new RoleWrapper();
        return wrapper.listVO(roles);
    }

    @Override
    public List<PermissionVO> getPermission(Integer roleId) {
        // 1系统管理员  2普通管理员  5班主任
        //系统管理员可对各个模块进行权限分配管理，普通管理员可对1任务管理、3报告管理、5预警管理中的各个任务进行权限分配
        List<RoleMenu> roleMenuList = roleMenuMapper.selectList(Wrappers.<RoleMenu>lambdaQuery()
                .eq(RoleMenu::getRoleId, roleId));
        List<Long> collect = roleMenuList.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(collect)) {
            return null;
        }
        List<Menu> menus = menuMapper.selectBatchIds(collect);
        List<PermissionVO> voList = BeanUtil.copyProperties(menus, PermissionVO.class);
        if (roleId == 2) {
            // 筛选
//            voList = voList.stream().filter(menu -> menu.getId() == 1 || menu.getId() == 8 || menu.getId() == 12).collect(Collectors.toList());
            // 添加子菜单
            List<ChildrenVO> children = baseMapper.getAllTask();
            voList.stream().forEach(permissionVO -> {
                // 普通管理员在任务管理、报告管理、预警管理中拥有查看任务的权限
                if (permissionVO.getId() == 1 || permissionVO.getId() == 8 || permissionVO.getId() == 12) {
                    permissionVO.setChildren(children);
                }

            });
        }
        return voList;
    }

}
