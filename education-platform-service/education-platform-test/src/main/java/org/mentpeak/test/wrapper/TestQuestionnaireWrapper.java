package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestQuestionnaire;
import org.mentpeak.test.vo.TestQuestionnaireVO;

/**
 * 问卷表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-13
 */
@AllArgsConstructor
public class TestQuestionnaireWrapper extends BaseEntityWrapper<TestQuestionnaire, TestQuestionnaireVO>  {


	@Override
	public TestQuestionnaireVO entityVO(TestQuestionnaire testQuestionnaire) {
		TestQuestionnaireVO testQuestionnaireVO = BeanUtil.copy(testQuestionnaire, TestQuestionnaireVO.class);


		return testQuestionnaireVO;
	}

}
