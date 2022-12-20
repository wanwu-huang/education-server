package org.mentpeak.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class LoginPhoneDTO {

    @ApiModelProperty ( value = "手机号",required = true)
    @Pattern(regexp = "^1[3456789][0-9]{9}",message = "手机号格式有误")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @ApiModelProperty ( value = "密码",required = true)
    @Size(min = 6,max = 20,message = "长度6-20个字符" )
    @NotBlank(message = "密码不能为空")
    private String passWord;
}
