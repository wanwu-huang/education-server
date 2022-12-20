package org.mentpeak.test.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.test.entity.TestApproach;
import org.mentpeak.test.service.ITestApproachService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测评途径表 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testapproach")
@Api(value = "测评途径表", tags = "测评途径接口")
@ApiSupport(order = 100)
public class TestApproachController extends PlatformController {

	private ITestApproachService testApproachService;


	/**
	 * 测评途径列表
	 */
	@ApiOperation(value = "测评途径列表")
	@ApiOperationSupport(order = 1, author = "domain_lee")
	@GetMapping("/testApproachList")
	public Result<List<TestApproach>> testApproachList() {
		return data(testApproachService.testApproachList());
	}


	/**
	 * 根据测评途径ID获取信息
	 */
	@ApiOperation(value = "根据测评途径ID获取信息")
	@ApiOperationSupport(order = 2, author = "domain_lee")
	@PostMapping("/getTestApproachInfoById")
	public Result<TestApproach> getTestApproachInfoById(@ApiParam( value = "测评途径ID", required = true ) @RequestParam Long id){
		return data(testApproachService.getById(id));
	}

}
