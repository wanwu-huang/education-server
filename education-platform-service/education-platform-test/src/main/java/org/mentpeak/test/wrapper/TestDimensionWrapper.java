package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestDimension;
import org.mentpeak.test.vo.TestDimensionVO;

/**
 * 维度表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestDimensionWrapper extends BaseEntityWrapper<TestDimension, TestDimensionVO>  {


	@Override
	public TestDimensionVO entityVO(TestDimension testDimension) {
		TestDimensionVO testDimensionVO = BeanUtil.copy(testDimension, TestDimensionVO.class);


		return testDimensionVO;
	}

}
