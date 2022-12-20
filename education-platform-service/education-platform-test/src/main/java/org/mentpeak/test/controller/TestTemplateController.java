package org.mentpeak.test.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.test.service.ITestTemplateService;
import org.springframework.web.bind.annotation.*;

/**
 * 导入用户模板 控制器
 *
 * @author lxp
 * @since 2022-08-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/template")
@Api(value = "导入用户模板", tags = "导入用户模板接口")
public class TestTemplateController extends PlatformController {

	private ITestTemplateService testTemplateService;


	@ApiOperation(value = "导入用户信息模板")
	@GetMapping("/getImportUserInfoTemplate")
	@ApiOperationSupport(order = 1, author = "domain_lee")
	public Result getImportUserInfoTemplate(@ApiParam(value = "类型 0 学生 1 政企用户", required = true) @RequestParam Integer type) {
		return Result.data(testTemplateService.getImportUserInfoTemplate(type));
	}

}
