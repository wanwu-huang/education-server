package org.mentpeak.user.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import org.mentpeak.core.tenant.mp.TenantEntity;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实体类
 *
 * @author mp
 */
@Data
@TableName ( "platform_user" )
@EqualsAndHashCode ( callSuper = true )
public class User extends TenantEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty ( "账号" )
    private String account;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty ( "密码" )
    private String password;
    @ApiModelProperty ( "昵称" )
    private String name;
//    @TableField ( insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED )
    @ApiModelProperty ( "真名" )
    private String realName;
    @ApiModelProperty ( "邮箱" )
    private String email;
    @ApiModelProperty ( "手机" )
    private String phone;
//    @TableField ( insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED )
    @ApiModelProperty ( "生日" )
    private LocalDateTime birthday;
    @ApiModelProperty ( "性别 0男 1女" )
    private Integer sex;
    @ApiModelProperty ( "角色id" )
    private String roleId;
    @ApiModelProperty ( "部门id" )
    private String deptId;
    @ApiModelProperty ( "租户id" )
    private String tenantCode;
    @ApiModelProperty ( "是否启用" )
    private Integer status;

}
