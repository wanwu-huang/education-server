package org.mentpeak.user.entity;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户信息
 *
 * @author mp
 */
@Data
@ApiModel ( description = "用户信息" )
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户基础信息
     */
    @ApiModelProperty ( value = "用户" )
    private User user;

    /**
     * 权限标识集合
     */
    @ApiModelProperty ( value = "权限集合" )
    private List < String > permissions;

    /**
     * 角色集合
     */
    @ApiModelProperty ( value = "角色集合" )
    private List < String > roles;

}
