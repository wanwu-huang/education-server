package org.mentpeak.test.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.http.cache.HttpCacheAble;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.mybatisplus.support.Query;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.test.dto.*;
import org.mentpeak.test.entity.TestApproach;
import org.mentpeak.test.service.ITestTaskService;
import org.mentpeak.test.vo.TaskDetailVO;
import org.mentpeak.test.vo.TaskInfoVO;
import org.mentpeak.test.vo.TaskListVO;
import org.mentpeak.test.vo.TestTaskVO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * 测评任务表 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testtask")
@Api(value = "测评任务表", tags = "测评任务表接口")
@ApiSupport(order = 300)
public class TestTaskController extends PlatformController {

    private ITestTaskService testTaskService;

    @GetMapping("/list")
    @ApiOperation(value = "获取用户测评任务列表")
    @ApiOperationSupport(order = 1)
    @HttpCacheAble(value = 5, maxAge = 5)
    public Result<List<TaskListVO>> list() {
        return Result.data(testTaskService.getList());
    }

    /**
     * 添加测评任务
     */
    @ApiOperation(value = "添加测评任务")
    @ApiOperationSupport(order = 1, author = "domain_lee")
    @PostMapping("/addTestTask")
    public Result<TestApproach> addTestTask(@Valid @RequestBody TestTaskDTO testTaskDTO) {
        return data(testTaskService.addTestTask(testTaskDTO));
    }

    /**
     * 任务管理列表
     */
    @ApiOperation(value = "任务管理列表")
    @ApiOperationSupport(order = 2, author = "domain_lee")
    @GetMapping("/testTaskList")
    public Result<Page<TaskInfoVO>> testTaskList(TaskSearchDTO taskSearchDTO, Query query) {
        return data(testTaskService.testTaskList(Condition.getPage(query), taskSearchDTO));
    }

    /**
     * 根据任务ID获取任务信息
     */
    @ApiOperation(value = "根据任务ID获取任务信息")
    @ApiOperationSupport(order = 3, author = "domain_lee")
    @GetMapping("/taskInfoById")
    public Result<TestTaskVO> taskInfoById(@ApiParam(value = "任务ID", required = true) @RequestParam Long testTaskId) {
        return data(testTaskService.taskInfoById(testTaskId));
    }

    /**
     * 根据任务ID获取测评任务详情
     */
    @ApiOperation(value = "根据任务ID获取测评任务详情")
    @ApiOperationSupport(order = 4, author = "domain_lee")
    @GetMapping("/taskDetailListById")
    public Result<Page<TaskDetailVO>> taskDetailListById(@Valid TaskDetailSearchDTO taskDetailSearchDTO, Query query) {
        return data(testTaskService.taskDetailListById(Condition.getPage(query), taskDetailSearchDTO));
    }

    /**
     * 根据任务ID获取测评任务详情
     */
    @ApiOperation(value = "根据任务ID修改任务")
    @ApiOperationSupport(order = 5, author = "domain_lee")
    @PostMapping("/updateTestTaskById")
    public Result updateTestTaskById(@Valid @RequestBody TaskUpdateDTO taskUpdateDTO) {
        return status(testTaskService.updateTestTaskById(taskUpdateDTO));
    }

    /**
     * 完成、未完成情况导出
     */
    @ApiOperation(value = "完成/未完成情况导出")
    @ApiOperationSupport(order = 6, author = "domain_lee")
    @GetMapping("/exportNoCompletion")
    public void exportNoCompletion(@Valid TaskDetailSearchDTO taskDetailSearchDTO, HttpServletResponse response) {
        testTaskService.exportNoCompletion(taskDetailSearchDTO, response);
    }

    /**
     * 删除任务
     */
    @ApiOperation(value = "删除任务")
    @ApiOperationSupport(order = 7, author = "domain_lee")
    @DeleteMapping("/deleteTask")
    public Result<?> deleteTask(@ApiParam(value = "任务ID", required = true) @RequestParam Long testTaskId) {
        return status(testTaskService.deleteTask(testTaskId));
    }

    /**
     * 批量导出已完成人员原始分
     */
    @ApiOperation(value = "批量导出已完成人员原始分")
    @ApiOperationSupport(order = 8, author = "domain_lee")
    @GetMapping("/exportFinisherScore")
    public void exportFinisherScore(@Valid TaskDetailSearchDTO taskDetailSearchDTO, HttpServletRequest request, HttpServletResponse response) {
        testTaskService.exportFinisherScore(taskDetailSearchDTO, request,response);
    }

    /**
     * 导出原始分
     */
    @ApiOperation(value = "导出原始分")
    @ApiOperationSupport(order = 9, author = "domain_lee")
    @GetMapping("/exportScore")
    public void exportScore(@Valid TaskDetailExportDTO taskDetailExportDTO, HttpServletRequest request, HttpServletResponse response) {
        testTaskService.exportScore(taskDetailExportDTO, request,response);
    }

}
