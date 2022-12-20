package org.mentpeak.test.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.test.entity.mongo.GroupsReport;
import org.mentpeak.test.service.ReportGroupsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 团体报告
 * @author demain_lee
 * @since 2022-08-31
 */
@RestController
@AllArgsConstructor
@RequestMapping("/report-group")
@Api(value = "团体报告", tags = "团体报告接口")
public class ReportGroupsController extends PlatformController {


	private final ReportGroupsService reportGroupsService;


	@ApiOperation(value = "生成团体报告")
	@ApiOperationSupport(order = 1, author = "domain_lee")
	@GetMapping("/generateReport")
	public Result<?> generateReport(Long taskId) {
		return status(reportGroupsService.generateReport(taskId));
	}


	@ApiOperation(value = "报告信息")
	@ApiOperationSupport(order = 2, author = "domain_lee")
	@GetMapping("/reportInfo")
	public Result<GroupsReport> reportInfo(Long taskId) {
		return data(reportGroupsService.reportInfo(taskId));
	}


	@ApiOperation(value = "生成报告数据(可查看)")
	@ApiOperationSupport(order = 3, author = "domain_lee")
	@GetMapping("/generateBaseReport")
	public Result<GroupsReport> generateBaseReport(Long taskId) {
		return data(reportGroupsService.generateBaseReport(taskId));
	}

}
