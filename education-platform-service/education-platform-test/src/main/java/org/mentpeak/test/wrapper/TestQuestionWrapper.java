package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestQuestion;
import org.mentpeak.test.vo.TestQuestionVO;

/**
 * 题干信息表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestQuestionWrapper extends BaseEntityWrapper<TestQuestion, TestQuestionVO>  {


	@Override
	public TestQuestionVO entityVO(TestQuestion testQuestion) {
		TestQuestionVO testQuestionVO = BeanUtil.copy(testQuestion, TestQuestionVO.class);


		return testQuestionVO;
	}

}
