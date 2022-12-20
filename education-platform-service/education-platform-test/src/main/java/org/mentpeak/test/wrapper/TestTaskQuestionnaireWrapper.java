package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestTaskQuestionnaire;
import org.mentpeak.test.vo.TestTaskQuestionnaireVO;

/**
 * 测评任务问卷关联表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestTaskQuestionnaireWrapper extends BaseEntityWrapper<TestTaskQuestionnaire, TestTaskQuestionnaireVO>  {


	@Override
	public TestTaskQuestionnaireVO entityVO(TestTaskQuestionnaire testTaskQuestionnaire) {
		TestTaskQuestionnaireVO testTaskQuestionnaireVO = BeanUtil.copy(testTaskQuestionnaire, TestTaskQuestionnaireVO.class);


		return testTaskQuestionnaireVO;
	}

}
