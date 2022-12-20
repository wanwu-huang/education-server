package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestIndex;
import org.mentpeak.test.vo.TestIndexVO;

/**
 * 指标表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestIndexWrapper extends BaseEntityWrapper<TestIndex, TestIndexVO>  {


	@Override
	public TestIndexVO entityVO(TestIndex testIndex) {
		TestIndexVO testIndexVO = BeanUtil.copy(testIndex, TestIndexVO.class);


		return testIndexVO;
	}

}
