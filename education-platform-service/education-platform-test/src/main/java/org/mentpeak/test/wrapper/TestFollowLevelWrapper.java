package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestFollowLevel;
import org.mentpeak.test.vo.TestFollowLevelVO;

/**
 * 关注等级
包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestFollowLevelWrapper extends BaseEntityWrapper<TestFollowLevel, TestFollowLevelVO>  {


	@Override
	public TestFollowLevelVO entityVO(TestFollowLevel testFollowLevel) {
		TestFollowLevelVO testFollowLevelVO = BeanUtil.copy(testFollowLevel, TestFollowLevelVO.class);


		return testFollowLevelVO;
	}

}
