package org.mentpeak.test.enums;

import lombok.Getter;

/**
 * @author lxp
 */
@Getter
public enum LogicTypeEnum {

	/**
	 * 仅跳题
	 */
	ONLY_SKIP("only_skip", "跳题","1"),
	/**
	 * 跳题+禁用
	 */
	SKIP_DISABLED("skip_disabled", "跳题禁用","2");

	private final String code;

	private final String message;

	private final String key;


	LogicTypeEnum(String code, String message, String key) {
		this.code = code;
		this.message = message;
		this.key = key;
	}

	/**
	 * 根据code获取枚举
	 */
	public static LogicTypeEnum codeToEnum(String code) {
		if (null != code) {
			for (LogicTypeEnum e : LogicTypeEnum.values()) {
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
			for (LogicTypeEnum e : LogicTypeEnum.values()) {
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
			for (LogicTypeEnum e : LogicTypeEnum.values()) {
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
			for (LogicTypeEnum e : LogicTypeEnum.values()) {
				if (e.getKey().equals(key)) {
					return e.getMessage();
				}
			}
		}
		return "未知";
	}
}
