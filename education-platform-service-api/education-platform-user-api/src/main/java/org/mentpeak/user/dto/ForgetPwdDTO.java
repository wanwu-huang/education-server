package org.mentpeak.user.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年07月21日15:04
 */
@Data
public class ForgetPwdDTO implements Serializable {

    @ApiModelProperty(value = "原密码", required = true)
    private String oldPassword;
    @ApiModelProperty(value = "新密码", required = true)
    @NotNull(message = "请输入用户名")
    private String newPassword;
    @ApiModelProperty(value = "确认密码", required = true)
    private String checkPassword;
}
