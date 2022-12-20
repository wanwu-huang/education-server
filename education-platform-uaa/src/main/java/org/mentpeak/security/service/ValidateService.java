package org.mentpeak.security.service;

import org.mentpeak.core.tool.api.Result;
import org.mentpeak.security.exception.CaptchaException;

/**
 * 验证码业务类
 *
 * @author pangu
 */
public interface ValidateService {

  /**
   * 获取验证码
   *
   * @return Result
   */
  Result<?> getCode();

  /**
   * 获取短信验证码
   *
   * @param mobile 手机号码
   * @return Result
   */
  Result<?> getSmsCode(String mobile);

  /**
   * 校验验证码
   *
   * @param key 　KEY
   * @param code 验证码
   */
  void check(String key, String code) throws CaptchaException;


  /**
   * 获取短信验证码
   *
   *
   * @param key 邮箱
   * @param keyType 验证码类型
   * @return Result
   */
  Result<?> getEmailCode(String key, String keyType) throws Exception;
}
