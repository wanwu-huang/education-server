package org.mentpeak.user.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.user.entity.User;
import org.mentpeak.user.vo.AccountVO;
import org.mentpeak.user.vo.StudentInfoVO;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author mp
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 自定义分页
     *
     * @param page
     * @param user
     * @return
     */
    List<User> selectUserPage(
            IPage page,
            User user);

    /**
     * 获取用户
     * 根据账号或者手机号获取用户
     *
     * @param tenantCode
     * @param account
     * @return
     */
    User getUser(
            String tenantCode,
            String account);

    /**
     * 获取用户
     * 根据账号或者手机号获取用户
     * 无租户
     *
     * @param account
     * @return
     */
    User getUserNoTenantCode(String account);

    /**
     * 获取用户
     * 根据账号或者手机号获取用户
     * 无租户
     *
     * @param email
     * @return
     */
    User getUserByEmail(String email);

    /**
     * 获取用户
     * 根据账号获取用户
     * 无租户
     *
     * @param account
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    User getUserByAccount(String account);

    /**
     * 通过手机号获取用户
     *
     * @param tenantCode
     * @param phone
     * @return
     */
    User getUserByPhone(
            String tenantCode,
            String phone);

    /**
     * 获取角色名
     *
     * @param ids
     * @return
     */
    List<String> getRoleName(String[] ids);

    /**
     * 获取角色别名
     *
     * @param ids
     * @return
     */
    List<String> getRoleAlias(String[] ids);

    /**
     * 获取部门名
     *
     * @param ids
     * @return
     */
    List<String> getDeptName(String[] ids);


    /**
     * 辅导员信息列表
     *
     * @return
     */
    List<User> teachList();


    /**
     * 查询手机号是否存在
     *
     * @param phone
     * @return
     */
    @InterceptorIgnore(tenantLine = "1")
    Long selectCountByPhone(String phone);

    /**
     * 查询用户信息
     *
     * @param userId
     * @return org.mentpeak.user.vo.StudentInfoVO
     * @author hzl
     * @date 2022/7/12 19:15
     */
    StudentInfoVO getUserInfo(@Param("userId") Long userId);

    Page<AccountVO> accountList(IPage<AccountVO> page,@Param("name") String name,@Param("roleId") Integer roleId,@Param("status") Integer status);

    /**
     * 保存用户班级
     * @param tenantCode
     * @param classId
     * @param userId
     * @return
     */
    @Deprecated
    Integer saveUserClass(@Param("tenantCode") String tenantCode,@Param("classId") Long classId,@Param("userId") Long userId);

    /**
     * 保存老师班级
     * @param tenantCode
     * @param classId
     * @param userId
     * @return
     */
    Integer saveTeachClass(@Param("tenantCode") String tenantCode,@Param("gradeId") Long gradeId,@Param("classId") Long classId,@Param("userId") Long userId);

    /**
     * 老师班级数据
     * @param classId
     * @param userId
     * @return
     */
    Integer teacherClassCount(@Param("gradeId") Long gradeId,@Param("classId") Long classId,@Param("userId") Long userId);

    /**
     * 删除老师班级数据
     * @param userId
     * @return
     */
    Integer deleteTeacherClass(@Param("userId") Long userId);

    /**
     * 根据编号查询已删除数据
     * @param account
     * @param tenantCode
     * @return
     */
    List<User> userListByAccount(@Param("account") String account,@Param("tenantCode") String tenantCode);

    /**
     * 根据手机号查询已删除数据
     * @param phone
     * @param tenantCode
     * @return
     */
    User userByPhone(@Param("phone") String phone,@Param("tenantCode") String tenantCode);


    void updateUser(@Param("id") Long id,@Param("realName") String realName,@Param("sex") Integer sex,
        @Param("updateUser") Long updateUser,@Param("updateTime") LocalDateTime updateTime);

    void updateTeacherUser(@Param("id") Long id,@Param("updateUser") Long updateUser,@Param("updateTime") LocalDateTime updateTime);

    /**
     * 查询用户信息 包含逻辑删除
     * @param userId
     * @return
     */
    User userInfoById(@Param("userId") Long userId);

}
