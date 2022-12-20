package org.mentpeak.test.enums;

import lombok.Getter;

/**
 * Question类型
 *
 * @author lxp
 */
@Getter
public enum QuestionEnum {
	/**
	 * Single_choice 单选
	 */
	SINGLE_CHOICE("single_choice", "单选", "1"),
	/**
	 * Multiple_choice 多选
	 */
	MULTIPLE_CHOICE("multiple_choice", "多选", "2"),
	/**
	 * Drop_down 下拉
	 */
	DROP_DOWN("drop_down", "下拉", "3"),
	/**
	 * Matrix 矩阵
	 */
	MATRIX("matrix", "矩阵", "4"),
	/**
	 * Fill_in_the_blanks 填空
	 */
	FILL_IN_THE_BLANKS("fill_in_the_blanks", "填空", "5"),
	/**
	 * Parent_child_drop_down 父子题（下拉类型）
	 */
	PARENT_CHILD_DROP_DOWN("parent_child_drop_down", "父子题（下拉类型）", "6");

	private final String code;

	private final String message;

	private final String key;


	QuestionEnum(String code, String message, String key) {
		this.code = code;
		this.message = message;
		this.key = key;
	}

	/**
	 * 根据code获取枚举
	 */
	public static QuestionEnum codeToEnum(String code) {
		if (null != code) {
			for (QuestionEnum e : QuestionEnum.values()) {
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
			for (QuestionEnum e : QuestionEnum.values()) {
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
			for (QuestionEnum e : QuestionEnum.values()) {
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
			for (QuestionEnum e : QuestionEnum.values()) {
				if (e.getKey().equals(key)) {
					return e.getMessage();
				}
			}
		}
		return "未知";
	}
}
