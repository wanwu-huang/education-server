package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestTaskQuestionnaire;

/**
 * 测评任务问卷关联表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestTaskQuestionnaireDTO extends TestTaskQuestionnaire {
	private static final long serialVersionUID = 1L;

}
