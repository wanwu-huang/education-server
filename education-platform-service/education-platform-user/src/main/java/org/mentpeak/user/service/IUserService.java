package org.mentpeak.user.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.user.dto.*;
import org.mentpeak.user.entity.User;
import org.mentpeak.user.entity.UserInfo;
import org.mentpeak.user.vo.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 服务类
 *
 * @author mp
 */
public interface IUserService extends BaseService<User> {

    /**
     * 新增或修改用户
     *
     * @param user
     * @return
     */
    boolean submit(User user);

    /**
     * 自定义分页
     *
     * @param page
     * @param user
     * @return
     */
    IPage<User> selectUserPage(
            IPage<User> page,
            User user);

    /**
     * 用户信息
     *
     * @param tenantCode
     * @param account
     * @return
     */
    UserInfo userInfo(
            String tenantCode,
            String account);

    /**
     * 用户信息by email
     *
     * @param email
     * @return
     */
    UserInfo userInfoByEmail(String email);

    /**
     * 用户信息by account
     *
     * @param account
     * @return
     */
    UserInfo userInfoByAccount(String account);

    /**
     * 通过手机号获取用户信息
     *
     * @param tenantCode
     * @param phone
     * @return
     */
    UserInfo userInfoByPhone(
            String tenantCode,
            String phone);


    /**
     * 给用户设置角色
     *
     * @param userIds
     * @param roleIds
     * @return
     */
    boolean grant(
            String userIds,
            String roleIds);

    /**
     * 初始化密码
     *
     * @param resetPasswordDTO
     * @return
     */
    boolean resetPassword(ResetPasswordDTO resetPasswordDTO);

    /**
     * 获取角色名
     *
     * @param roleIds
     * @return
     */
    List<String> getRoleName(String roleIds);

    /**
     * 获取部门名
     *
     * @param deptIds
     * @return
     */
    List<String> getDeptName(String deptIds);

    // 用户注册
    boolean registered(
            String authorization,
            User user,
            String code);

    // 忘记密码
    boolean forgetPassword(
            String email,
            String password,
            String code);

    // 账号安全 修改密码
    PhoneVo updatePhone(
            UserPhoneVo user);

    // 用户个人信息修改
    boolean updateUserInfo(UserInfoVO userInfoVO);

    /**
     * 辅导员信息列表
     *
     * @return
     */
    List<User> teachList();


    /**
     * 根据 手机号 旧密码 修改密码
     *
     * @param phone
     * @param oldPassWord
     * @param newPassWord
     * @return
     */
    boolean updatePassWord(String phone, String oldPassWord, String newPassWord);

    /**
     * 根据 账号（学号） 旧密码 修改密码
     *
     * @param account
     * @param oldPassWord
     * @param newPassWord
     * @return
     */
    boolean updatePassWordByAccount(String account, String oldPassWord, String newPassWord);

    /**
     * 换绑手机号
     *
     * @param oldPhone
     * @param phone
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    boolean updateOldPhone(String oldPhone, String phone, String codeMsg);

    /**
     * 确认用户信息
     *
     * @return org.mentpeak.user.vo.StudentInfoVO
     * @author hzl
     * @date 2022/7/20 13:24
     */
    StudentInfoVO getUserInfo();

    /**
     * 账号管理-列表
     *
     * @param page
     * @param accountDTO
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.mentpeak.user.vo.AccountVO>
     * @author hzl
     * @date 2022/7/20 13:23
     */
    IPage<AccountVO> accountList(IPage<AccountVO> page, AccountDTO accountDTO);

    /**
     * 账号管理-重置密码
     *
     * @param userId
     * @return boolean
     * @author hzl
     * @date 2022/7/20 13:23
     */
    boolean resetPwd(Long userId);

    /**
     * 账号管理-删除
     *
     * @param userId
     * @return boolean
     * @author hzl
     * @date 2022/7/20 13:23
     */
    boolean remove(Long userId);

    /**
     * 导入用户信息
     *
     * @param file file
     * @param grade 一级部门ID
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    boolean importUserInfo(MultipartFile file, Long grade);

    /**
     * 账号管理-添加账号
     *
     * @param accountDTO
     * @return boolean
     * @author hzl
     * @date 2022/7/21 15:31
     */
    @Transactional(rollbackFor = Exception.class)
    boolean addAccount(UserAccount2DTO accountDTO);

    /**
     * 账号管理-修改-数据回显
     *
     * @param userId
     * @return org.mentpeak.user.vo.UserAccountVO
     * @author hzl
     * @date 2022/7/22 9:42
     */
    UserAccountVO getAccount(Long userId);

    /**
     * 账号管理-修改
     *
     * @param accountDTO
     * @return boolean
     * @author hzl
     * @date 2022/7/21 15:31
     */
    @Transactional(rollbackFor = Exception.class)
    boolean updateAccount(UpdateUserAccountDTO accountDTO);

    List<MenuListVO> getMenuList();

    boolean forgetPwd(ForgetPwdDTO dto);

    IsFirstVO firstLogin();

    boolean submitLogin();
}
