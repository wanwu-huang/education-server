package org.mentpeak.user.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.user.service.ITenantService;
import org.mentpeak.user.vo.TenantVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 平台-租户 控制器
 *
 * @author lxp
 * @since 2022-09-27
 */
@RestController
@AllArgsConstructor
@RequestMapping("/tenant")
@Api(value = "平台-租户", tags = "平台-租户接口")
public class TenantController extends PlatformController {

	private ITenantService tenantService;

	@GetMapping("/tenantInfo")
	@ApiOperation(value = "租户信息")
	@ApiOperationSupport(order = 1)
	public Result<TenantVO> tenantInfo() {
		return Result.data(tenantService.getTenantInfo());
	}

}
