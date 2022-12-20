package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestQuestionnaireModules;
import org.mentpeak.test.vo.TestQuestionnaireModulesVO;

/**
 * 问卷模块关联表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestQuestionnaireModulesWrapper extends BaseEntityWrapper<TestQuestionnaireModules, TestQuestionnaireModulesVO>  {


	@Override
	public TestQuestionnaireModulesVO entityVO(TestQuestionnaireModules testQuestionnaireModules) {
		TestQuestionnaireModulesVO testQuestionnaireModulesVO = BeanUtil.copy(testQuestionnaireModules, TestQuestionnaireModulesVO.class);


		return testQuestionnaireModulesVO;
	}

}
