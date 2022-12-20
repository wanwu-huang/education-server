package org.mentpeak.test.enums;

import lombok.Getter;

/**
 * Option类型
 * @author lxp
 */
@Getter
public enum OptionEnum {
	/**
	 * 输入框
	 */
	INPUT_BOX("input_box", "输入框","1"),
	/**
	 * 单选框
	 */
	RADIO_BOX("radio_box", "单选框","2");

	private final String code;

	private final String message;

	private final String key;


	OptionEnum(String code, String message, String key) {
		this.code = code;
		this.message = message;
		this.key = key;
	}

	/**
	 * 根据code获取枚举
	 */
	public static OptionEnum codeToEnum(String code) {
		if (null != code) {
			for (OptionEnum e : OptionEnum.values()) {
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
			for (OptionEnum e : OptionEnum.values()) {
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
			for (OptionEnum e : OptionEnum.values()) {
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
			for (OptionEnum e : OptionEnum.values()) {
				if (e.getKey().equals(key)) {
					return e.getMessage();
				}
			}
		}
		return "未知";
	}
}
