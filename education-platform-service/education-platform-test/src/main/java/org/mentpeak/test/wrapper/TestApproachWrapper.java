package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestApproach;
import org.mentpeak.test.vo.TestApproachVO;

/**
 * 测评途径表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestApproachWrapper extends BaseEntityWrapper<TestApproach, TestApproachVO>  {


	@Override
	public TestApproachVO entityVO(TestApproach testApproach) {
		TestApproachVO testApproachVO = BeanUtil.copy(testApproach, TestApproachVO.class);


		return testApproachVO;
	}

}
