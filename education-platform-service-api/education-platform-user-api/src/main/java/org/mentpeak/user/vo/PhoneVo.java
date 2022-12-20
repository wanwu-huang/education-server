package org.mentpeak.user.vo;/**
 * @author hzl
 * @create 2021-04-07
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hzl
 * @date 视图实体类 10:33
 */
@Data
@ApiModel ( value = "PhoneVo对象", description = "PhoneVo对象" )
public class PhoneVo {
    @ApiModelProperty ( "用户新手机号" )
    private String account;
}
