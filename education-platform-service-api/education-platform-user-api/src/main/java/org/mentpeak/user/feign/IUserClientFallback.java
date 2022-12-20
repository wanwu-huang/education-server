package org.mentpeak.user.feign;

import org.mentpeak.core.tool.api.Result;
import org.mentpeak.user.entity.User;
import org.mentpeak.user.entity.UserExt;
import org.mentpeak.user.entity.UserInfo;
import org.springframework.stereotype.Component;

/**
 * 用户远程调用失败，降级
 */
@Component
public class IUserClientFallback implements IUserClient {
    @Override
    public Result < User > userInfoById ( Long userId ) {
        return Result.fail ( "获取用户信息失败" );
    }

    @Override
    public Result<User> allUserInfoById(Long userId) {
        return Result.fail ( "获取用户信息失败" );
    }

    @Override
    public Result < UserInfo > userInfoByPhone (
            String tenantCode ,
            String phone ) {
        return Result.fail ( "通过手机号获取用户信息失败" );
    }

    @Override
    public Result < UserInfo > userInfo (
            String tenantCode ,
            String account ) {
        return Result.fail ( "获取用户信息失败" );
    }

    /**
     * 获取用户信息by email
     *
     * @param email 账号
     * @return
     */
    @Override
    public Result<UserInfo> userInfoByEmail(String email) {
        return Result.fail ( "根据email获取用户信息失败" );
    }
    @Override
    public Result<UserInfo> userInfoByAccount(String account) {
        return Result.fail ( "根据account获取用户信息失败" );
    }

    @Override
    public Result < UserExt > userExtInfoById ( Long userId ) {
        return Result.fail ( "获取用户拓展信息失败" );
    }

    @Override
    public Result<UserExt> allUserExtInfoById(Long userId) {
        return Result.fail ( "获取用户拓展信息失败" );
    }

    @Override
    public boolean deleteUserInfoById(Long userId) {
        return false;
    }
}
