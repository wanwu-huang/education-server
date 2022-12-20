package org.mentpeak.security.service.impl;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mentpeak.common.constant.CommonConstant;
import org.mentpeak.common.enums.CaptchaEnum;
import org.mentpeak.core.redis.core.RedisService;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.core.tool.utils.RandomType;
import org.mentpeak.core.tool.utils.StringUtil;
import org.mentpeak.security.exception.CaptchaException;
import org.mentpeak.security.service.ValidateService;
import org.mentpeak.security.utils.MailUtil;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

/**
 * 验证码业务类
 *
 * @author pangu
 */
@Slf4j
@Service
@AllArgsConstructor
public class ValidateServiceImpl implements ValidateService {

	private final RedisService redisService;


	@Override
	public Result<?> getCode() {
		Map<String, String> data = new HashMap<>(2);
		String uuid = UUID.randomUUID().toString().replace("-", "");
		//SpecCaptcha captcha = new SpecCaptcha(120, 40);
		//String text = captcha.text();// 获取运算的结果：5
		ArithmeticCaptcha captcha = new ArithmeticCaptcha(120, 40);
		captcha.getArithmeticString();  // 获取运算的公式：3+2=?
		// 获取运算的结果：5
		String text = captcha.text();
		redisService.set(CommonConstant.CAPTCHA_KEY + uuid, text, Duration.ofMinutes(30));
		data.put("key", uuid);
		data.put("codeUrl", captcha.toBase64());
		return Result.data(data);
	}

	/**
	 * 获取短信验证码
	 *
	 * @param mobile 手机号码
	 * @return Result
	 */
	@Override
	public Result<?> getSmsCode(String mobile) {
		return null;
	}


	@Override
	public void check(String key, String code) throws CaptchaException {
		Object codeFromRedis = redisService.get(CommonConstant.CAPTCHA_KEY + key);

		if (StrUtil.isBlank(code)) {
			throw new CaptchaException("请输入验证码");
		}
		if (codeFromRedis == null) {
			throw new CaptchaException("验证码已过期");
		}
		if (!StringUtil.equalsIgnoreCase(code, codeFromRedis.toString())) {
			throw new CaptchaException("验证码不正确");
		}
		redisService.del(CommonConstant.CAPTCHA_KEY + key);
	}

	/**
	 * 获取短信验证码
	 *
	 *
	 * @param key 邮箱
	 * @param keyType 验证码类型
	 * @return Result
	 */
	@Override
	public Result<?> getEmailCode(String key, String keyType) throws Exception {
		String verifyCode = Func.random(6, RandomType.INT);
		redisService.set(CommonConstant.CAPTCHA_KEY + CaptchaEnum.keyToCode(keyType) +"." + key, verifyCode, Duration.ofMinutes(30));
		String content = "您的"+CaptchaEnum.keyToMessage(keyType)+"验证码：" + verifyCode + "，30分钟内有效，如非本人操作，请忽略！请勿回复此邮箱";
		MailUtil.sendMail("verification@mentpeak.com",key,"verification@mentpeak.com","Gkxy123456","验证码",content);
		return Result.data("发送成功，请前往邮箱查收。");
	}


}
