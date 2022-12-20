package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel ( value = "UserVO对象", description = "UserVO对象" )
public class UserAccessVo {
    @ApiModelProperty ( "用户ID" )
    private Integer id;
    @ApiModelProperty ( "用户姓名" )
    private String user_name;
    @ApiModelProperty ( "真实姓名" )
    private String real_name;
    @ApiModelProperty ( "用户Token" )
    private String token;
    @ApiModelProperty ( "用户邮箱" )
    private String email;
    @ApiModelProperty ( "用户登录账号" )
    private String account;
    @ApiModelProperty ( "用户所属租户" )
    private String tenant_code;
    @ApiModelProperty ( "角色id" )
    private String roleId;

    @ApiModelProperty ( "用户头像" )
    private String headUrl;


}
