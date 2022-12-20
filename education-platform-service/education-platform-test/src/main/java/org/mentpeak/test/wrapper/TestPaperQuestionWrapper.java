package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestPaperQuestion;
import org.mentpeak.test.vo.TestPaperQuestionVO;

/**
 * 试卷和题干及选中题支关系表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-15
 */
@AllArgsConstructor
public class TestPaperQuestionWrapper extends BaseEntityWrapper<TestPaperQuestion, TestPaperQuestionVO>  {


	@Override
	public TestPaperQuestionVO entityVO(TestPaperQuestion testPaperQuestion) {
		TestPaperQuestionVO testPaperQuestionVO = BeanUtil.copy(testPaperQuestion, TestPaperQuestionVO.class);


		return testPaperQuestionVO;
	}

}
