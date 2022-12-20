package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestDimensionIndexConclusion;
import org.mentpeak.test.vo.TestDimensionIndexConclusionVO;

/**
 * 维度指标结论包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestDimensionIndexConclusionWrapper extends BaseEntityWrapper<TestDimensionIndexConclusion, TestDimensionIndexConclusionVO>  {


	@Override
	public TestDimensionIndexConclusionVO entityVO(TestDimensionIndexConclusion testDimensionIndexConclusion) {
		TestDimensionIndexConclusionVO testDimensionIndexConclusionVO = BeanUtil.copy(testDimensionIndexConclusion, TestDimensionIndexConclusionVO.class);


		return testDimensionIndexConclusionVO;
	}

}
