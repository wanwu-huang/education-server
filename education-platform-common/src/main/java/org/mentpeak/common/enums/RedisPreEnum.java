package org.mentpeak.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 缓存前缀
 */
@Getter
@AllArgsConstructor
public enum RedisPreEnum {

	/**
	 * 用来存放每个用户每次测试的每个模块的结果
	 */
	CUSTOMER_PAPER_MODULES_CODE ( "CUSTOMER_PAPER_MODULES_CODE:" , "客户每个试卷的所有模块测试结果集" );

	private final String code;

	private final String desc;


}
