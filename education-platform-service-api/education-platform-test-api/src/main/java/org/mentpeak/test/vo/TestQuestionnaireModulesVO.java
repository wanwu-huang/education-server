package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestQuestionnaireModules;

/**
 * 问卷模块关联表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestQuestionnaireModulesVO对象", description = "问卷模块关联表")
public class TestQuestionnaireModulesVO extends TestQuestionnaireModules {
	private static final long serialVersionUID = 1L;

}
