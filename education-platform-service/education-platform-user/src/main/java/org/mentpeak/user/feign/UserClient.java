package org.mentpeak.user.feign;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.mentpeak.core.log.exception.PlatformApiException;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.user.entity.User;
import org.mentpeak.user.entity.UserExt;
import org.mentpeak.user.entity.UserInfo;
import org.mentpeak.user.mapper.UserExtMapper;
import org.mentpeak.user.mapper.UserMapper;
import org.mentpeak.user.service.IUserExtService;
import org.mentpeak.user.service.IUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

/**
 * 用户服务Feign实现类
 *
 * @author mp
 */
@RestController
@AllArgsConstructor
public class UserClient implements IUserClient {

    IUserService service;

    IUserExtService userExtService;

    private final UserMapper userMapper;

    private final UserExtMapper userExtMapper;

    @Override
    @GetMapping ( USER_INFO_BY_ID )
    public Result < User > userInfoById ( Long userId ) {
        return Result.data ( service.getById ( userId ) );
    }

    @Override
    public Result<User> allUserInfoById(Long userId) {
        return Result.data ( userMapper.userInfoById(userId) );
    }

    @Override
    @GetMapping ( USER_INFO )
    public Result < UserInfo > userInfo (
            String tenantCode ,
            String account ) {
        return Result.data ( service.userInfo ( tenantCode ,
                                                account ) );
    }

    /**
     * 获取用户信息by email
     *
     * @param email 账号
     * @return
     */
    @Override
    public Result<UserInfo> userInfoByEmail(String email) {
        return Result.data ( service.userInfoByEmail ( email) );
    }

    /**
     * 获取用户信息by account
     *
     * @param account 账号
     * @return
     */
    @Override
    public Result<UserInfo> userInfoByAccount(String account) {
        return Result.data ( service.userInfoByAccount ( account) );
    }

    @Override
    @GetMapping ( USER_EXT_INFO_BY_ID )
    public Result < UserExt > userExtInfoById ( Long userId ) {
        UserExt userExt = userExtService.getOne ( new LambdaQueryWrapper < UserExt > ( ).eq ( UserExt :: getCreateUser ,
                                                                                              userId ) );
        return Result.data ( userExt );
    }

    @Override
    public Result<UserExt> allUserExtInfoById(Long userId) {
        UserExt userExt = userExtMapper.userExtInfoById (userId);
        return Result.data ( userExt );
    }

    @Override
    public Result < UserInfo > userInfoByPhone (
            String tenantCode ,
            String phone ) {
        return Result.data ( service.userInfoByPhone ( tenantCode ,
                                                       phone ) );
    }

    @Override
    public boolean deleteUserInfoById(Long userId) {

        try {
            // 删除用户信息
            service.removeById(userId);
            // 删除用户拓展信息
            userExtService.remove(Wrappers.<UserExt>lambdaQuery().eq(UserExt::getCreateUser,userId));
        } catch (Exception e) {
            e.printStackTrace();
            throw new PlatformApiException("删除用户失败");
        }

        return true;
    }
}
