package org.mentpeak.resourcemanager.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.mybatisplus.support.Query;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.resourcemanager.entity.ParentOption;
import org.mentpeak.resourcemanager.service.IParentOptionService;
import org.mentpeak.resourcemanager.vo.ParentOptionVO;
import org.mentpeak.resourcemanager.wrapper.ParentOptionWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 家长他评问卷题支信息表 控制器
 *
 * @author lxp
 * @since 2022-06-17
 */
@RestController
@AllArgsConstructor
@RequestMapping("/parentoption")
@Api(value = "家长他评问卷题支信息表", tags = "家长他评问卷题支信息表接口")
public class ParentOptionController extends PlatformController {

	private IParentOptionService parentOptionService;


	/**
	* 详情
	*/
	@GetMapping("/detail")
	@ApiOperation(value = "详情", notes = "传入parentOption", position = 1)
	public Result<ParentOptionVO> detail(ParentOption parentOption) {
		ParentOption detail = parentOptionService.getOne(Condition.getQueryWrapper(parentOption));
		ParentOptionWrapper parentOptionWrapper = new ParentOptionWrapper();
		return Result.data(parentOptionWrapper.entityVO(detail));
	}

	/**
	* 分页 家长他评问卷题支信息表
	*/
	@GetMapping("/list")
	@ApiOperation(value = "分页", notes = "传入parentOption", position = 2)
	public Result<IPage<ParentOptionVO>> list(ParentOption parentOption, Query query) {
		IPage<ParentOption> pages = parentOptionService.page(Condition.getPage(query), Condition.getQueryWrapper(parentOption));
		ParentOptionWrapper parentOptionWrapper = new ParentOptionWrapper();
		return Result.data(parentOptionWrapper.pageVO(pages));
	}

	/**
	* 自定义分页 家长他评问卷题支信息表
	*/
	@GetMapping("/page")
	@ApiOperation(value = "分页", notes = "传入parentOption", position = 3)
	public Result<IPage<ParentOptionVO>> page(ParentOptionVO parentOption, Query query) {
		IPage<ParentOptionVO> pages = parentOptionService.selectParentOptionPage(Condition.getPage(query), parentOption);
		return Result.data(pages);
	}

	/**
	* 新增 家长他评问卷题支信息表
	*/
	@PostMapping("/save")
	@ApiOperation(value = "新增", notes = "传入parentOption", position = 4)
	public Result save(@Valid @RequestBody ParentOption parentOption) {
		return Result.status(parentOptionService.save(parentOption));
	}

	/**
	* 修改 家长他评问卷题支信息表
	*/
	@PostMapping("/update")
	@ApiOperation(value = "修改", notes = "传入parentOption", position = 5)
	public Result update(@Valid @RequestBody ParentOption parentOption) {
		return Result.status(parentOptionService.updateById(parentOption));
	}

	/**
	* 新增或修改 家长他评问卷题支信息表
	*/
	@PostMapping("/submit")
	@ApiOperation(value = "新增或修改", notes = "传入parentOption", position = 6)
	public Result submit(@Valid @RequestBody ParentOption parentOption) {
		return Result.status(parentOptionService.saveOrUpdate(parentOption));
	}

	
	/**
	* 删除 家长他评问卷题支信息表
	*/
	@PostMapping("/remove")
	@ApiOperation(value = "逻辑删除", notes = "传入ids", position = 7)
	public Result remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return Result.status(parentOptionService.deleteLogic(Func.toIntList(ids)));
	}

	
}
