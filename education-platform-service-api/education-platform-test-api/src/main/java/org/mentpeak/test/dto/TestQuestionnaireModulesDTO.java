package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestQuestionnaireModules;

/**
 * 问卷模块关联表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestQuestionnaireModulesDTO extends TestQuestionnaireModules {
	private static final long serialVersionUID = 1L;

}
