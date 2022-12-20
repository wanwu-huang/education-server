package org.mentpeak.common.enums;

import lombok.Getter;

/**
 * 邮箱验证码类型
 * @author lxp
 * @date 2022/06/15 15:58
 **/
@Getter
public enum  CaptchaEnum {
	/**
	 * 注册
	 */
	REGISTER("register", "注册","1"),
	/**
	 * 忘记密码
	 */
	FORGETPASSWORD("forgetpassword", "忘记密码","2"),
	/**
	 * 修改密码
	 */
	UPDATEPASSWORD("updatepassword", "修改密码","3");

	private final String code;

	private final String message;

	private final String key;

	CaptchaEnum(String code, String message, String key) {
		this.code = code;
		this.message = message;
		this.key = key;
	}

	/**
	 * 根据code获取枚举
	 */
	public static CaptchaEnum codeToEnum(String code) {
		if (null != code) {
			for (CaptchaEnum e : CaptchaEnum.values()) {
				if (e.getCode().equals(code)) {
					return e;
				}
			}
		}
		return null;
	}

	/**
	 * 编码转化成中文含义
	 */
	public static String codeToMessage(String code) {
		if (null != code) {
			for (CaptchaEnum e : CaptchaEnum.values()) {
				if (e.getCode().equals(code)) {
					return e.getMessage();
				}
			}
		}
		return "未知";
	}

	/**
	 * key转化成code
	 */
	public static String keyToCode(String key) {
		if (null != key) {
			for (CaptchaEnum e : CaptchaEnum.values()) {
				if (e.getKey().equals(key)) {
					return e.getCode();
				}
			}
		}
		return "null";
	}

	/**
	 * key转化成code
	 */
	public static String keyToMessage(String key) {
		if (null != key) {
			for (CaptchaEnum e : CaptchaEnum.values()) {
				if (e.getKey().equals(key)) {
					return e.getMessage();
				}
			}
		}
		return "未知";
	}
}
