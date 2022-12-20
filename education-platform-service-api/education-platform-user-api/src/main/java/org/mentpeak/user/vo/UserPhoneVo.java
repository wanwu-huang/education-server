package org.mentpeak.user.vo;/**
 * @author hzl
 * @create 2021-04-07
 */

import org.mentpeak.user.entity.User;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hzl
 * @date 视图实体类 10:33
 */
@Data
@ApiModel ( value = "UserPhoneVo对象", description = "UserPhoneVo对象" )
public class UserPhoneVo extends User {
    @ApiModelProperty ( "用户新手机号" )
    private String newPhone;
    @ApiModelProperty ( "验证码" )
    private String code;
}
