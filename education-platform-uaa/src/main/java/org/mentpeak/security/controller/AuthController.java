package org.mentpeak.security.controller;


import io.swagger.annotations.*;
import org.mentpeak.core.auth.PlatformUser;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.log.annotation.ApiLog;
import org.mentpeak.core.redis.core.RedisService;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.StringUtil;
import org.mentpeak.security.dto.CodeDTO;
import org.mentpeak.security.service.ValidateService;
import org.mentpeak.user.entity.UserInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 认证控制类
 *
 * @author pangu
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@Api(tags = "认证管理")
public class AuthController {

	@Qualifier("consumerTokenServices")
	private final ConsumerTokenServices consumerTokenServices;

	private final ValidateService validateService;

	private final RedisService redisService;

	@ApiLog(value = "用户信息")
	@GetMapping("/get/user")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Platform-Auth", required = true, value = "授权类型", paramType = "header")
	})
	@ApiOperation(value = "用户信息", notes = "用户信息")
	public Result<?> getUser(HttpServletRequest request) {

		PlatformUser loginUser = SecureUtil.getUser(request);
		UserInfo userInfo = null;
		return Result.data(loginUser);
	}

	@ApiLog(value = "验证码获取")
	@GetMapping("/code")
	@ApiOperation(value = "验证码获取", notes = "验证码获取")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", required = true, value = "授权类型", paramType = "header")
	})
	public Result<?> authCode() {
		return validateService.getCode();
	}

	@ApiLog(value = "退出登录")
	@PostMapping("/logout")
	@ApiOperation(value = "退出登录", notes = "退出登录")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Platform-Auth", required = true, value = "授权类型", paramType = "header")
	})
	public Result<?> logout(HttpServletRequest request) {
		if (StringUtil.isNotBlank(SecureUtil.getToken(request))) {
			consumerTokenServices.revokeToken(SecureUtil.getToken(request));
		}
		return Result.success("操作成功");
	}

	/**
	 * 验证码下发
	 *
	 * @param codeDTO 手机号码
	 * @return Result
	 */
	@ApiLog(value = "邮箱验证码下发")
	@ApiOperation(value = "邮箱验证码下发", notes = "邮箱验证码下发")
	@PostMapping("/mail-code")
	public Result<?> emailCode(@ApiParam(value = "邮箱数据传输对象",name = "codeDTO",required = true)@Valid @RequestBody CodeDTO codeDTO) throws Exception {
		return validateService.getEmailCode(codeDTO.getKey(),codeDTO.getKeyType());
	}

}

