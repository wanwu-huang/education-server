package org.mentpeak.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UpdatePhoneDTO {


    /**
     * 可能存在没有绑定手机号的情况（比如：学员端）
     * 可以为空
     */
    @ApiModelProperty ( value = "当前使用手机号",required = true)
    private String oldPhone;

    @ApiModelProperty ( value = "手机号",required = true)
    @Pattern(regexp = "^1[3456789][0-9]{9}",message = "手机号格式有误")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @ApiModelProperty ( value = "验证码",required = true)
    @NotBlank(message = "验证码不能为空")
    private String codeMsg;
}
