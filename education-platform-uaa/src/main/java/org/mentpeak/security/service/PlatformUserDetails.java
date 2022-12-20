package org.mentpeak.security.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

import lombok.Getter;

/**
 * 用户信息拓展
 *
 * @author mp
 */
@Getter
public class PlatformUserDetails extends User {

    /**
     * 注: 缓存存在增加字段前的数据 避免反序列化问题. 如不添加需要清除缓存
     */
    private static final long serialVersionUID = -4843348040955050021L;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 租户编号
     */
    private String tenantCode;
    /**
     * 昵称
     */
    private String name;
    /**
     * 账号
     */
    private String account;
    /**
     * 角色id
     */
    private String roleId;
    /**
     * 角色名
     */
    private String roleName;
    /**
     * 头像
     */
    private String avatar;

    private String realName;

    public PlatformUserDetails (
            Long userId ,
            String tenantCode ,
            String name ,
            String roleId ,
            String roleName ,
            String avatar ,
            String realName ,
            String username ,
            String password ,
            boolean enabled ,
            boolean accountNonExpired ,
            boolean credentialsNonExpired ,
            boolean accountNonLocked ,
            Collection < ? extends GrantedAuthority > authorities ) {
        super ( username ,
                password ,
                enabled ,
                accountNonExpired ,
                credentialsNonExpired ,
                accountNonLocked ,
                authorities );
        this.userId = userId;
        this.tenantCode = tenantCode;
        this.name = name;
        this.account = username;
        this.roleId = roleId;
        this.roleName = roleName;
        this.avatar = avatar;
        this.realName = realName;
    }

}
