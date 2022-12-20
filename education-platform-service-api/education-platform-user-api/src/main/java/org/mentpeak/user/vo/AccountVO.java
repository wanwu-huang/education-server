package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年07月20日10:07
 */
@Data
public class AccountVO implements Serializable {

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("角色")
    private String roleName;

    @ApiModelProperty("添加时间")
    private String createTime;

    @ApiModelProperty("状态 0关闭 1开启")
    private Integer status;
}
