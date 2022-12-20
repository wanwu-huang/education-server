package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestTeacherQuestion;
import org.mentpeak.test.vo.TestTeacherQuestionVO;

/**
 * 教师评定题干信息表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-08-15
 */
@AllArgsConstructor
public class TestTeacherQuestionWrapper extends BaseEntityWrapper<TestTeacherQuestion, TestTeacherQuestionVO>  {


	@Override
	public TestTeacherQuestionVO entityVO(TestTeacherQuestion testTeacherQuestion) {
		TestTeacherQuestionVO testTeacherQuestionVO = BeanUtil.copy(testTeacherQuestion, TestTeacherQuestionVO.class);


		return testTeacherQuestionVO;
	}

}
