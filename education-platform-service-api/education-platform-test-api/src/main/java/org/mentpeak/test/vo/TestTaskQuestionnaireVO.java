package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestTaskQuestionnaire;

/**
 * 测评任务问卷关联表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestTaskQuestionnaireVO对象", description = "测评任务问卷关联表")
public class TestTaskQuestionnaireVO extends TestTaskQuestionnaire {
	private static final long serialVersionUID = 1L;

}
