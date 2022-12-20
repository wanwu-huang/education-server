package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestTaskDepartment;
import org.mentpeak.test.vo.TestTaskDepartmentVO;

/**
 * 测评任务部门关联表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestTaskDepartmentWrapper extends BaseEntityWrapper<TestTaskDepartment, TestTaskDepartmentVO>  {


	@Override
	public TestTaskDepartmentVO entityVO(TestTaskDepartment testTaskDepartment) {
		TestTaskDepartmentVO testTaskDepartmentVO = BeanUtil.copy(testTaskDepartment, TestTaskDepartmentVO.class);


		return testTaskDepartmentVO;
	}

}
