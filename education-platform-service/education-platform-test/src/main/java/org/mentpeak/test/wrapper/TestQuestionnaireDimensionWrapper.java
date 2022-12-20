package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestQuestionnaireDimension;
import org.mentpeak.test.vo.TestQuestionnaireDimensionVO;

/**
 * 问卷维度关联表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestQuestionnaireDimensionWrapper extends BaseEntityWrapper<TestQuestionnaireDimension, TestQuestionnaireDimensionVO>  {


	@Override
	public TestQuestionnaireDimensionVO entityVO(TestQuestionnaireDimension testQuestionnaireDimension) {
		TestQuestionnaireDimensionVO testQuestionnaireDimensionVO = BeanUtil.copy(testQuestionnaireDimension, TestQuestionnaireDimensionVO.class);


		return testQuestionnaireDimensionVO;
	}

}
