package org.mentpeak.test.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.base.BaseOption;
import org.mentpeak.test.base.Question;

/**
 * 题目信息数据
 *
 * @author demain_lee
 * @since 2022-08-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionOptionVO<T extends BaseOption> extends Question<T> {


}
