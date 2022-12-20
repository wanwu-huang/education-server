package org.mentpeak.test.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.mybatisplus.support.Query;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.test.dto.GradeDetailUserDTO;
import org.mentpeak.test.service.IClassUserService;
import org.mentpeak.test.vo.GradeDetailUserVO;
import org.mentpeak.test.vo.GradeUserVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 班级用户表 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/classuser")
@Api(value = "班级用户表", tags = "班级用户表接口")
public class ClassUserController extends PlatformController {

	private IClassUserService classUserService;


	/**
	 * 用户管理列表
	 */
	@ApiOperation(value = "用户管理列表")
	@ApiOperationSupport(order = 1, author = "domain_lee")
	@GetMapping("/userManagerList")
	public Result<Page<GradeUserVO>> userManagerList(@ApiParam(value = "一级部门ID") @RequestParam(required = false) Long gradeId, Query query) {
		return data(classUserService.userManagerList(Condition.getPage(query), gradeId));
	}


	/**
	 * 一级部门详情列表
	 */
	@ApiOperation(value = "一级部门详情列表")
	@ApiOperationSupport(order = 2, author = "domain_lee")
	@GetMapping("/userManagerDetailList")
	public Result<Page<GradeDetailUserVO>> userManagerDetailList(@Valid GradeDetailUserDTO gradeDetailUserDTO, Query query) {
		return data(classUserService.userManagerDetailList(Condition.getPage(query), gradeDetailUserDTO));
	}


	/**
	 * 删除用户
	 */
	@ApiOperation(value = "删除一级部门")
	@ApiOperationSupport(order = 3, author = "domain_lee")
	@DeleteMapping("/deleteByGradeId")
	public Result<?> deleteByGradeId(@ApiParam(value = "一级部门ID",required = true) @RequestParam Long gradeId) {
		return status(classUserService.deleteByGradeId(gradeId));
	}

	/**
	 * 删除用户
	 */
	@ApiOperation(value = "删除用户")
	@ApiOperationSupport(order = 5, author = "domain_lee")
	@DeleteMapping("/deleteById")
	public Result<?> deleteById(@ApiParam(value = "用户ID",required = true) @RequestParam Long userId) {
		return status(classUserService.deleteById(userId));
	}

}
