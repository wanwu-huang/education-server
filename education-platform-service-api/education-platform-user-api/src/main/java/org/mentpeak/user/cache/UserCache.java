package org.mentpeak.user.cache;


import org.mentpeak.core.cache.utils.CacheUtil;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.core.tool.utils.SpringUtil;
import org.mentpeak.user.entity.User;
import org.mentpeak.user.feign.IUserClient;

/**
 * 系统缓存
 *
 * @author mp
 */
public class UserCache {
    private static final String USER_CACHE = "platform:user";
    private static final String USER_CACHE_ID_ = "user:id";


    private static IUserClient userClient;

    static {
        userClient = SpringUtil.getBean ( IUserClient.class );
    }

    /**
     * 获取用户名
     *
     * @param userId 用户id
     * @return
     */
    public static User getUser ( Long userId ) {
        User user = CacheUtil.get ( USER_CACHE ,
                                    USER_CACHE_ID_ + userId ,
                                    User.class );
        if ( Func.isEmpty ( user ) ) {
            Result < User > result = userClient.userInfoById ( userId );
            if ( result.isSuccess ( ) ) {
                user = result.getData ( );
                if ( Func.isNotEmpty ( user ) ) {
                    CacheUtil.put ( USER_CACHE ,
                                    USER_CACHE_ID_ + userId ,
                                    user );
                }
            }
        }
        return user;
    }

}
