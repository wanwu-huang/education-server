package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestDimensionIndexQuestion;
import org.mentpeak.test.vo.TestDimensionIndexQuestionVO;

/**
 * 维度指标题干关联表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestDimensionIndexQuestionWrapper extends BaseEntityWrapper<TestDimensionIndexQuestion, TestDimensionIndexQuestionVO>  {


	@Override
	public TestDimensionIndexQuestionVO entityVO(TestDimensionIndexQuestion testDimensionIndexQuestion) {
		TestDimensionIndexQuestionVO testDimensionIndexQuestionVO = BeanUtil.copy(testDimensionIndexQuestion, TestDimensionIndexQuestionVO.class);


		return testDimensionIndexQuestionVO;
	}

}
