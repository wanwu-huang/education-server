package org.mentpeak.user.vo;


import org.mentpeak.user.entity.User;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视图实体类
 *
 * @author mp
 */
@Data
@EqualsAndHashCode ( callSuper = true )
@ApiModel ( value = "UserVO对象", description = "UserVO对象" )
public class UserVO extends User {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty ( "角色名" )
    private String roleName;

    @ApiModelProperty ( "部门名" )
    private String deptName;

    @ApiModelProperty ( "性别" )
    private String sexName;
}
