package org.mentpeak.test.vo;

import org.mentpeak.test.entity.TestQuestionnaire;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;

/**
 * 问卷表视图实体类
 *
 * @author lxp
 * @since 2022-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestQuestionnaireVO对象", description = "问卷表")
public class TestQuestionnaireVO extends TestQuestionnaire {
	private static final long serialVersionUID = 1L;

}
