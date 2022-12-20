package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestOption;
import org.mentpeak.test.vo.TestOptionVO;

/**
 * 题支信息表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestOptionWrapper extends BaseEntityWrapper<TestOption, TestOptionVO>  {


	@Override
	public TestOptionVO entityVO(TestOption testOption) {
		TestOptionVO testOptionVO = BeanUtil.copy(testOption, TestOptionVO.class);


		return testOptionVO;
	}

}
