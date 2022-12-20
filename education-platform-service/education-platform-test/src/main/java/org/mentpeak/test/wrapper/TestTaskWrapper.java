package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestTask;
import org.mentpeak.test.vo.TestTaskVO;

/**
 * 测评任务表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestTaskWrapper extends BaseEntityWrapper<TestTask, TestTaskVO>  {


	@Override
	public TestTaskVO entityVO(TestTask testTask) {
		TestTaskVO testTaskVO = BeanUtil.copy(testTask, TestTaskVO.class);


		return testTaskVO;
	}

}
