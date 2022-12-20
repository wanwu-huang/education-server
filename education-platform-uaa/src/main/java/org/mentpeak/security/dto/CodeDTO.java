package org.mentpeak.security.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;


/**
 * 验证码DOT
 * @author lxp
 * @date 2022/06/15 14:58
 **/
@Data
public class CodeDTO implements Serializable {

	private static final long serialVersionUID = 2406695297368398027L;

	@ApiModelProperty( "关键信息:邮箱" )
	@Email(message = "邮箱格式不正确")
	@NotBlank(message = "邮箱不能为空")
	private String key ;

	@ApiModelProperty( "验证码类型(type):1、register=1;2、forgetpassword=2;3、updatepassword=3" )
	@NotBlank(message = "验证码类型不能为空")
	private String keyType;
}
