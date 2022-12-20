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
import org.mentpeak.resourcemanager.entity.ParentQuestion;
import org.mentpeak.resourcemanager.service.IParentQuestionService;
import org.mentpeak.resourcemanager.vo.ParentQuestionVO;
import org.mentpeak.resourcemanager.wrapper.ParentQuestionWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 家长他评问卷题目信息表 控制器
 *
 * @author lxp
 * @since 2022-06-17
 */
@RestController
@AllArgsConstructor
@RequestMapping("/parentquestion")
@Api(value = "家长他评问卷题目信息表", tags = "家长他评问卷题目信息表接口")
public class ParentQuestionController extends PlatformController {

	private IParentQuestionService parentQuestionService;


	/**
	* 详情
	*/
	@GetMapping("/detail")
	@ApiOperation(value = "详情", notes = "传入parentQuestion", position = 1)
	public Result<ParentQuestionVO> detail(ParentQuestion parentQuestion) {
		ParentQuestion detail = parentQuestionService.getOne(Condition.getQueryWrapper(parentQuestion));
		ParentQuestionWrapper parentQuestionWrapper = new ParentQuestionWrapper();
		return Result.data(parentQuestionWrapper.entityVO(detail));
	}

	/**
	* 分页 家长他评问卷题目信息表
	*/
	@GetMapping("/list")
	@ApiOperation(value = "分页", notes = "传入parentQuestion", position = 2)
	public Result<IPage<ParentQuestionVO>> list(ParentQuestion parentQuestion, Query query) {
		IPage<ParentQuestion> pages = parentQuestionService.page(Condition.getPage(query), Condition.getQueryWrapper(parentQuestion));
		ParentQuestionWrapper parentQuestionWrapper = new ParentQuestionWrapper();
		return Result.data(parentQuestionWrapper.pageVO(pages));
	}

	/**
	* 自定义分页 家长他评问卷题目信息表
	*/
	@GetMapping("/page")
	@ApiOperation(value = "分页", notes = "传入parentQuestion", position = 3)
	public Result<IPage<ParentQuestionVO>> page(ParentQuestionVO parentQuestion, Query query) {
		IPage<ParentQuestionVO> pages = parentQuestionService.selectParentQuestionPage(Condition.getPage(query), parentQuestion);
		return Result.data(pages);
	}

	/**
	* 新增 家长他评问卷题目信息表
	*/
	@PostMapping("/save")
	@ApiOperation(value = "新增", notes = "传入parentQuestion", position = 4)
	public Result save(@Valid @RequestBody ParentQuestion parentQuestion) {
		return Result.status(parentQuestionService.save(parentQuestion));
	}

	/**
	* 修改 家长他评问卷题目信息表
	*/
	@PostMapping("/update")
	@ApiOperation(value = "修改", notes = "传入parentQuestion", position = 5)
	public Result update(@Valid @RequestBody ParentQuestion parentQuestion) {
		return Result.status(parentQuestionService.updateById(parentQuestion));
	}

	/**
	* 新增或修改 家长他评问卷题目信息表
	*/
	@PostMapping("/submit")
	@ApiOperation(value = "新增或修改", notes = "传入parentQuestion", position = 6)
	public Result submit(@Valid @RequestBody ParentQuestion parentQuestion) {
		return Result.status(parentQuestionService.saveOrUpdate(parentQuestion));
	}

	
	/**
	* 删除 家长他评问卷题目信息表
	*/
	@PostMapping("/remove")
	@ApiOperation(value = "逻辑删除", notes = "传入ids", position = 7)
	public Result remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return Result.status(parentQuestionService.deleteLogic(Func.toIntList(ids)));
	}

	
}
