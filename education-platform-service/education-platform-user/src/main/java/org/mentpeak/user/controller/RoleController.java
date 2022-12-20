package org.mentpeak.user.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.http.cache.HttpCacheAble;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.user.service.IRoleService;
import org.mentpeak.user.vo.PermissionVO;
import org.mentpeak.user.vo.RoleVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 平台-角色表 控制器
 *
 * @author lxp
 * @since 2022-07-19
 */
@RestController
@AllArgsConstructor
@RequestMapping("/")
@Api(value = "平台-角色表", tags = "平台-角色表接口")
public class RoleController extends PlatformController {

	private IRoleService roleService;

	@GetMapping("/roleList")
	@ApiOperation(value = "获取角色列表")
	@ApiOperationSupport(order = 1)
//	@HttpCacheAble(value = 10, maxAge = 10)
	public Result<List<RoleVO>> roleList() {
		return Result.data(roleService.roleList());
	}

	@GetMapping("/roleList2")
	@ApiOperation(value = "账号管理-角色下拉框")
	@ApiOperationSupport(order = 2)
	public Result<List<RoleVO>> roleList2() {
		return Result.data(roleService.roleList2());
	}


	@GetMapping("/getPermission")
	@ApiOperation(value = "根据角色id获取权限列表")
	@ApiOperationSupport(order = 3)
	public Result<List<PermissionVO>> getPermission(@ApiParam(value = "角色id", required = true) @RequestParam Integer roleId) {
		return Result.data(roleService.getPermission(roleId));
	}



}
