package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestCommonIntroductioin;
import org.mentpeak.test.vo.TestCommonIntroductioinVO;

/**
 * 固定文本包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestCommonIntroductioinWrapper extends BaseEntityWrapper<TestCommonIntroductioin, TestCommonIntroductioinVO>  {


	@Override
	public TestCommonIntroductioinVO entityVO(TestCommonIntroductioin testCommonIntroductioin) {
		TestCommonIntroductioinVO testCommonIntroductioinVO = BeanUtil.copy(testCommonIntroductioin, TestCommonIntroductioinVO.class);


		return testCommonIntroductioinVO;
	}

}
