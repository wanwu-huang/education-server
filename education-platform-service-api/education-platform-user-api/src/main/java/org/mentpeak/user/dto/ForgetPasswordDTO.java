package org.mentpeak.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author MyPC
 */
@Data
@ApiModel(value = "忘记密码实体",description = "忘记密码实体")
public class ForgetPasswordDTO {

    @ApiModelProperty ( value = "手机号",required = true)
    @Email
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @ApiModelProperty ( value = "密码",required = true)
    @NotBlank(message = "密码不能为空")
    private String password;

    @ApiModelProperty ( value = "验证码",required = true)
    @NotBlank(message = "验证码不能为空")
    private String code;
}
