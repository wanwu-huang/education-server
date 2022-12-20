package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mentpeak.test.entity.TestTaskDepartment;
import org.mentpeak.test.vo.TestTaskDepartmentVO;

/**
 * 测评任务部门关联表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestTaskDepartmentService extends IService<TestTaskDepartment> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testTaskDepartment
	 * @return
	 */
	IPage<TestTaskDepartmentVO> selectTestTaskDepartmentPage(IPage<TestTaskDepartmentVO> page, TestTaskDepartmentVO testTaskDepartment);

}
