package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mentpeak.user.dto.PermissionDTO;
import org.mentpeak.user.dto.UserAccountDTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author hzl
 * @data 2022年07月22日9:41
 */
@Data
public class UserAccountVO implements Serializable {

    @ApiModelProperty(value = "姓名", required = true)
    private String name;
    @ApiModelProperty(value = "账号", required = true)
    private String account;
    @ApiModelProperty(value = "角色id", required = true)
    private String roleId;
    @ApiModelProperty(value = "状态id 1开始 0关闭", required = true)
    private Integer status;
    @ApiModelProperty(value = "用户id")
    private Long userId;
    @ApiModelProperty(value = "权限")
    private List<String[]> permission;
}
