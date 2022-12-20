package org.mentpeak.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class LoginAccountDTO {

    @ApiModelProperty ( value = "账号",required = true)
    @NotBlank(message = "学号或手机号不能为空")
    @Size(max = 20,message = "最大长度20个字符" )
    private String account;

    @ApiModelProperty ( value = "密码",required = true)
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$",message = "密码格式有误,仅支持字母以及数字，密码长度6-20之间")
    @NotBlank(message = "密码不能为空")
    private String passWord;
}
