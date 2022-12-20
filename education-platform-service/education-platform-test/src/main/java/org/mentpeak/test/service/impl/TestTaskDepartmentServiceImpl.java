package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mentpeak.test.entity.TestTaskDepartment;
import org.mentpeak.test.mapper.TestTaskDepartmentMapper;
import org.mentpeak.test.service.ITestTaskDepartmentService;
import org.mentpeak.test.vo.TestTaskDepartmentVO;
import org.springframework.stereotype.Service;

/**
 * 测评任务部门关联表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TestTaskDepartmentServiceImpl extends ServiceImpl<TestTaskDepartmentMapper, TestTaskDepartment> implements ITestTaskDepartmentService {

	@Override
	public IPage<TestTaskDepartmentVO> selectTestTaskDepartmentPage(IPage<TestTaskDepartmentVO> page, TestTaskDepartmentVO testTaskDepartment) {
		return page.setRecords(baseMapper.selectTestTaskDepartmentPage(page, testTaskDepartment));
	}

}
