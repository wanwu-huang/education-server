package org.mentpeak.user.vo;


import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 视图实体类
 *
 * @author mp
 */
@Data
@ApiModel ( value = "UserInfoVO对象", description = "UserInfoVO对象" )
public class UserInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty ( "id" )
    private Long id;
    @ApiModelProperty ( "账号" )
    private String account;
    @ApiModelProperty ( "密码" )
    private String password;
    @ApiModelProperty ( "昵称" )
    private String name;
    @ApiModelProperty ( "真名" )
    private String realName;
    @ApiModelProperty ( "邮箱" )
    private String email;
    @ApiModelProperty ( "手机" )
    private String phone;
    @ApiModelProperty ( "生日" )
    private String birthday;
    @ApiModelProperty ( "性别" )
    private Integer sex;
    @ApiModelProperty ( "角色id" )
    private String roleId;
    @ApiModelProperty ( "角色姓名" )
    private String roleName;
    @ApiModelProperty ( "部门id" )
    private String deptId;
    @ApiModelProperty ( "租户id" )
    private String tenantCode;
    @ApiModelProperty ( "是否启用" )
    private Integer status;
    @ApiModelProperty ( "用户头像" )
    private String headUrl;
}
