package org.mentpeak.user.mapper;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.user.entity.Role;
import org.mentpeak.user.vo.ChildrenVO;
import org.mentpeak.user.vo.RoleVO;

import java.util.List;

/**
 * 平台-角色表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-19
 */
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 自定义分页
     *
     * @param page
     * @param role
     * @return
     */
    List<RoleVO> selectRolePage(IPage page, RoleVO role);

    /**
     * 获取全部子菜单(任务)
     *
     * @return java.util.List<org.mentpeak.user.vo.ChildrenVO>
     * @author hzl
     * @date 2022/7/21 14:07
     */
    @Cached(name = "RoleMapper:getAllTask", expire = 10, cacheType = CacheType.BOTH)
    List<ChildrenVO> getAllTask();

}
