package org.mentpeak.test.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.mybatisplus.support.Query;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.test.dto.ReportUserDTO;
import org.mentpeak.test.entity.mongo.PersonalReport;
import org.mentpeak.test.service.ReportUserService;
import org.mentpeak.test.vo.ReportUserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 个人报告
 * @author demain_lee
 * @since 2022-08-09
 */
@RestController
@AllArgsConstructor
@RequestMapping("/report-user")
@Api(value = "个人报告", tags = "个人报告接口")
public class ReportUserController extends PlatformController {


	private final ReportUserService reportUserService;


	@ApiOperation(value = "生成个人报告")
	@ApiOperationSupport(order = 1, author = "domain_lee")
	@GetMapping("/generateReport")
	public Result<?> generateReport(Long paperId) {
		return status(reportUserService.generateReport(paperId, getUser().getUserId()));
	}


	@ApiOperation(value = "报告信息")
	@ApiOperationSupport(order = 2, author = "domain_lee")
	@GetMapping("/reportInfo")
	public Result<PersonalReport> reportInfo(Long taskId,Long userId) {
		return data(reportUserService.reportInfo(taskId, userId));
	}

	@ApiOperation(value = "个体报告列表")
	@ApiOperationSupport(order = 3, author = "domain_lee")
	@GetMapping("/reportList")
	public Result<Page<ReportUserVO>> reportList(@Valid ReportUserDTO reportUserDTO, Query query) {
		return data(reportUserService.reportList(Condition.getPage(query), reportUserDTO));
	}
}
