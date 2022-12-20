package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestParentConclusion;
import org.mentpeak.test.vo.TestParentConclusionVO;

/**
 * 家长他评结论包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestParentConclusionWrapper extends BaseEntityWrapper<TestParentConclusion, TestParentConclusionVO>  {


	@Override
	public TestParentConclusionVO entityVO(TestParentConclusion testParentConclusion) {
		TestParentConclusionVO testParentConclusionVO = BeanUtil.copy(testParentConclusion, TestParentConclusionVO.class);


		return testParentConclusionVO;
	}

}
