package org.mentpeak.user.feign;


import org.mentpeak.core.launch.constant.AppConstant;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.user.entity.User;
import org.mentpeak.user.entity.UserExt;
import org.mentpeak.user.entity.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * User Feign接口类
 *
 * @author mp
 */
@FeignClient (
        value = AppConstant.APPLICATION_USER_NAME,
        fallback = IUserClientFallback.class
)
public interface IUserClient {

    String API_PREFIX = "/client";
    String USER_INFO = API_PREFIX + "/user-info";
    String USER_INFO_BY_EMAIL = API_PREFIX + "/user-info-by-email";
    String USER_INFO_BY_ID = API_PREFIX + "/user-info-by-id";
    String USER_IPHONE = API_PREFIX + "/phone";
    String USER_EXT_INFO_BY_ID = API_PREFIX + "/user-ext-info-by-id";
    String USER_INFO_BY_ACCOUNT = API_PREFIX + "/user-info-by-account";
    String DELETE_USER_INFO_BY_ID = API_PREFIX + "/delete_user-info-by-id";
    String ALL_USER_INFO_BY_ID = API_PREFIX + "/all-user-info-by-id";
    String ALL_USER_EXT_INFO_BY_ID = API_PREFIX + "/all-user-ext-info-by-id";

    /**
     * 获取用户信息
     *
     * @param userId 用户id
     * @return
     */
    @GetMapping ( USER_INFO_BY_ID )
    Result < User > userInfoById ( @RequestParam ( "userId" ) Long userId );

    /**
     * 获取用户信息 包含逻辑删除用户
     *
     * @param userId 用户id
     * @return
     */
    @GetMapping ( ALL_USER_INFO_BY_ID )
    Result < User > allUserInfoById ( @RequestParam ( "userId" ) Long userId );

    /**
     * 根据手机号获取用户信息
     *
     * @param tenantCode 租户编号
     * @param phone      手机号
     * @return
     */
    @GetMapping ( USER_IPHONE )
    Result < UserInfo > userInfoByPhone (
            @RequestParam ( "tenantCode" ) String tenantCode ,
            @RequestParam ( "phone" ) String phone );

    /**
     * 获取用户信息
     *
     * @param tenantCode 租户编号
     * @param account    账号
     * @return
     */
    @GetMapping ( USER_INFO )
    Result < UserInfo > userInfo (
            @RequestParam ( "tenantCode" ) String tenantCode ,
            @RequestParam ( "account" ) String account );

    /**
     * 获取用户信息by email
     *
     * @param email    账号
     * @return
     */
    @GetMapping ( USER_INFO_BY_EMAIL )
    Result < UserInfo > userInfoByEmail (@RequestParam ( "email" ) String email );

    /**
     * 获取用户信息by email
     *
     * @param account    账号
     * @return
     */
    @GetMapping ( USER_INFO_BY_ACCOUNT )
    Result < UserInfo > userInfoByAccount (@RequestParam ( "account" ) String account );

    /**
     * 获取用户拓展信息
     *
     * @param userId 用户id
     * @return
     */
    @GetMapping ( USER_EXT_INFO_BY_ID )
    Result < UserExt > userExtInfoById ( @RequestParam ( "userId" ) Long userId );

    /**
     * 获取用户拓展信息 包含逻辑删除用户
     *
     * @param userId 用户id
     * @return
     */
    @GetMapping ( ALL_USER_EXT_INFO_BY_ID )
    Result < UserExt > allUserExtInfoById ( @RequestParam ( "userId" ) Long userId );


    /**
     * 删除用户信息
     * @param userId 用户ID
     * @return boolean
     */
    @PostMapping( DELETE_USER_INFO_BY_ID )
    boolean deleteUserInfoById ( @RequestParam ( "userId" ) Long userId );
}
