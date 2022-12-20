package org.mentpeak.test.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author lxp
 * @date 2022/06/16 17:15
 * 简单题目类，包含选项
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class Question<T> extends BaseQuestion{
	private List<T> options;
}
