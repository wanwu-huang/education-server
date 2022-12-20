package org.mentpeak.test.dto;

import org.mentpeak.test.entity.TestQuestionnaire;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 问卷表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestQuestionnaireDTO extends TestQuestionnaire {
	private static final long serialVersionUID = 1L;

}
