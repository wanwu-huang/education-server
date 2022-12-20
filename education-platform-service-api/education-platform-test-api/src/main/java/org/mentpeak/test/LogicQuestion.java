package org.mentpeak.test;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.base.BaseOption;
import org.mentpeak.test.base.Question;

import java.util.List;

/**
 * @author lxp
 * @date 2022/06/16 17:36
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class LogicQuestion<T> extends Question<T> {
	private String explain;
	private String isJump;
	private List<LogicQuestion<T>> subQuestions;
}
