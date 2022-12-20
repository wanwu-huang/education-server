package org.mentpeak.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年07月20日10:13
 */
@Data
public class AccountDTO implements Serializable {

    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "角色id")
    private Integer roleId;
    @ApiModelProperty(value = "状态id")
    private Integer status;

}
