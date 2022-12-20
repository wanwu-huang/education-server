package org.mentpeak.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mentpeak.user.entity.Role;
import org.mentpeak.user.vo.PermissionVO;
import org.mentpeak.user.vo.RoleVO;

import java.util.List;

/**
 * 平台-角色表 服务类
 *
 * @author lxp
 * @since 2022-07-19
 */
public interface IRoleService extends IService<Role> {

    /**
     * 自定义分页
     *
     * @param page
     * @param role
     * @return
     */
    IPage<RoleVO> selectRolePage(IPage<RoleVO> page, RoleVO role);

    List<RoleVO> roleList();
    List<RoleVO> roleList2();

    /**
     * 根据角色id查询权限菜单
     * @author hzl
     * @date 2022/7/21 10:44
     * @param roleId
     * @return java.util.List<org.mentpeak.user.vo.PermissionVO>
     */
    List<PermissionVO> getPermission(Integer roleId);

}
