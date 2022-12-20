package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestTaskUser;
import org.mentpeak.test.vo.TestTaskUserVO;

/**
 * 测评任务用户关联表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestTaskUserWrapper extends BaseEntityWrapper<TestTaskUser, TestTaskUserVO>  {


	@Override
	public TestTaskUserVO entityVO(TestTaskUser testTaskUser) {
		TestTaskUserVO testTaskUserVO = BeanUtil.copy(testTaskUser, TestTaskUserVO.class);


		return testTaskUserVO;
	}

}
