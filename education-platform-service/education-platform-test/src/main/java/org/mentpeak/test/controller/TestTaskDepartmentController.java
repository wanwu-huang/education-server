package org.mentpeak.test.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.test.service.ITestTaskDepartmentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测评任务部门关联表 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testtaskdepartment")
@Api(value = "测评任务部门关联表", tags = "测评任务部门关联表接口")
public class TestTaskDepartmentController extends PlatformController {

	private ITestTaskDepartmentService testTaskDepartmentService;



}
