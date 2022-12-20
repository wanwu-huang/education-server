package org.mentpeak.test.service;

import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.dto.*;
import org.mentpeak.test.entity.TestApproach;
import org.mentpeak.test.entity.TestTask;
import org.mentpeak.test.vo.*;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 测评任务表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestTaskService extends BaseService<TestTask> {

    /**
     * 自定义分页
     *
     * @param page
     * @param testTask
     * @return
     */
    IPage<TestTaskVO> selectTestTaskPage(IPage<TestTaskVO> page, TestTaskVO testTask);

    /**
     * 获取当前用户任务列表
     *
     * @return java.util.List<org.mentpeak.test.vo.TaskListVO>
     * @author hzl
     * @date 2022/7/13 14:31
     */
    List<TaskListVO> getList();


	/**
	 * 添加测评任务
	 * @param testTaskDTO testTaskInfo
	 * @return boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	TestApproach addTestTask(TestTaskDTO testTaskDTO);


	/**
	 * 任务管理 - 任务列表
	 * @param page page
	 * @param taskSearchDTO dto
	 * @return info
	 */
	Page<TaskInfoVO> testTaskList(IPage<TestRecordVO> page,TaskSearchDTO taskSearchDTO);

	/**
	 * 根据任务ID获取任务信息
	 * @param taskId 任务ID
	 * @return 任务信息
	 */
	@Cached(name = "task:info:",key = "#taskId",expire = 60)
	TestTaskVO taskInfoById(Long taskId);

	/**
	 * 根据任务ID获取测评任务详情
	 * @param page page
	 * @param taskDetailSearchDTO dto
	 * @return detail
	 */
	Page<TaskDetailVO> taskDetailListById(IPage<TestRecordVO> page, TaskDetailSearchDTO taskDetailSearchDTO);

	/**
	 * 根据任务ID修改任务
	 * @param taskUpdateDTO dto
	 * @return true
	 */
	boolean updateTestTaskById(TaskUpdateDTO taskUpdateDTO);

	/**
	 * 未完成情况导出
	 * @param taskDetailSearchDTO dto
	 * @param response response
	 */
	void exportNoCompletion(TaskDetailSearchDTO taskDetailSearchDTO, HttpServletResponse response);

	/**
	 * 删除任务
	 * @param testTaskId
	 */
	@Transactional(rollbackFor = Exception.class)
	boolean deleteTask(Long testTaskId);

	/**
	 * 批量导出已完成人员原始分
	 * @param taskDetailSearchDTO dto
	 * @param request request
	 * @param response response
	 */
	void exportFinisherScore(TaskDetailSearchDTO taskDetailSearchDTO, HttpServletRequest request, HttpServletResponse response);

	/**
	 * 导出原始分
	 * @param taskDetailExportDTO dto
	 * @param request request
	 * @param response response
	 */
	void exportScore(TaskDetailExportDTO taskDetailExportDTO, HttpServletRequest request, HttpServletResponse response);

}
