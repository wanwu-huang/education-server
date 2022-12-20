package org.mentpeak.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author hzl
 * @data 2022年07月21日15:04
 */
@Data
public class UpdateUserAccountDTO implements Serializable {

    @ApiModelProperty(value = "用户id", required = true)
    private Long userId;
    @ApiModelProperty(value = "角色id", required = true)
    private Integer roleId;
    @ApiModelProperty(value = "权限")
    private List<String[]> permission;
    @ApiModelProperty(value = "状态id 1开始 0关闭", required = true)
    private Integer status;
}
