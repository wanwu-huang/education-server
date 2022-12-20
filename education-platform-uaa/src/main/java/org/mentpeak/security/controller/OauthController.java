package org.mentpeak.security.controller;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import io.swagger.annotations.*;
import org.mentpeak.core.log.annotation.ApiLog;
import org.mentpeak.core.tool.api.Result;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;

import lombok.AllArgsConstructor;

/**
 * 自定义Oauth2自定义返回格式
 *
 * @author pangu
 * @link https://segmentfault.com/a/1190000020317220?utm_source=tag-newest
 */
@RestController
@RequestMapping("/oauth")
@AllArgsConstructor
@Api(tags = "Oauth2管理")
public class OauthController {

	private final TokenEndpoint tokenEndpoint;

	private final RedisTemplate redisTemplate;

//	@ApiLog(value = "用户登录")
//	@GetMapping("/token")
//	@ApiOperation(value = "用户登录Get", notes = "用户登录Get")
//	public Result<?> getAccessToken(Principal principal, @RequestParam Map<String, String> parameters)
//			throws HttpRequestMethodNotSupportedException {
//		return custom(tokenEndpoint.getAccessToken(principal, parameters).getBody());
//	}

	@ApiLog(value = "用户登录")
	@PostMapping("/token")
	@ApiOperation(value = "用户登录Post", notes = "用户登录Post")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "grant_type", required = true, value = "授权类型", paramType = "query"),
			@ApiImplicitParam(name = "username", required = false, value = "用户名", paramType = "query"),
			@ApiImplicitParam(name = "password", required = false, value = "密码", paramType = "query"),
			@ApiImplicitParam(name = "scope", required = true, value = "使用范围", paramType = "query"),
	})
	public Result<?> postAccessToken(Principal principal, @RequestParam Map<String, String> parameters)  {
		try {
			return custom(tokenEndpoint.postAccessToken(principal, parameters).getBody());
		} catch (HttpRequestMethodNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 自定义返回格式
	 *
	 * @param accessToken 　Token
	 * @return Result
	 */
	private Result<?> custom(OAuth2AccessToken accessToken) {
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
		Map<String, Object> data = new LinkedHashMap<>(token.getAdditionalInformation());
		data.put("accessToken", token.getValue());
		if (token.getRefreshToken() != null) {
			data.put("refreshToken", token.getRefreshToken().getValue());
		}
		return Result.data(data);
	}



	@GetMapping("/delRedis")
	@ApiOperation(value = "测试批量删除redis")
	public Result delRedis(
			@ApiParam(value = "key") @RequestParam String key) {
		if (ObjectUtils.isNotEmpty(key)) {
			// 按照条件模糊删除
			Set keys = redisTemplate.keys(key);
			redisTemplate.delete(keys);
		} else {
			// 全部删除
			List<String> list = new ArrayList<>();
			list.add("access*");
			list.add("auth*");
			list.add("refresh*");
			for (String s : list) {
				Set keys = redisTemplate.keys(s);
				Iterator it = keys.iterator();
				while (it.hasNext()) {
					Object next = it.next();
					System.out.println(next);
				}
				Long delete = redisTemplate.delete(keys);
				System.out.println(delete);
			}
		}
		return null;
	}
}
