package org.mentpeak.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UpdatePasswordDTO {

    @ApiModelProperty ( value = "学号",required = true)
    @NotBlank(message = "学号不能为空")
    private String account;

    @ApiModelProperty ( value = "当前密码",required = true)
    @NotBlank(message = "当前密码不能为空")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$",message = "密码格式有误,仅支持字母以及数字，密码长度6-20之间")
    private String oldPassWord;

    @ApiModelProperty ( value = "新密码",required = true)
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$",message = "密码格式有误,仅支持字母以及数字，密码长度6-20之间")
    private String newPassWord;
}
