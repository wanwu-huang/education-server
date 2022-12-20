package org.mentpeak.test.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.mybatisplus.support.Query;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.test.dto.TeacherPaperDTO;
import org.mentpeak.test.service.ITestTeacherPaperService;
import org.mentpeak.test.entity.mongo.TeacherRating;
import org.mentpeak.test.vo.TeacherPaperVO;
import org.springframework.web.bind.annotation.*;

/**
 * 教师评定试卷信息表 控制器
 *
 * @author lxp
 * @since 2022-08-15
 */
@RestController
@AllArgsConstructor
@RequestMapping("/teacher")
@Api(value = "教师评定试卷信息表", tags = "教师评定试卷信息表接口")
@ApiSupport(order = 3000)
public class TestTeacherPaperController extends PlatformController {

	private ITestTeacherPaperService testTeacherPaperService;


	/**
	 * 题库数据存放到缓存
	 */
	@ApiOperation(value = "题库数据存放到缓存")
	@ApiOperationSupport(order = 1, author = "demain_lee")
	@PostMapping("/dataToCache")
	public Result<?> dataToCache(){
		return status(testTeacherPaperService.dataToCache());
	}


	@ApiOperation(value = "教师评定")
	@ApiOperationSupport(order = 2, author = "domain_lee")
	@GetMapping("/generateTeacherRating")
	public Result<TeacherRating> generateTeacherRating(@ApiParam(value = "年级",required = true) @RequestParam Long gradeId, @ApiParam(value = "班级",required = true) @RequestParam Long classId){
		return data(testTeacherPaperService.generateTeacherRating(gradeId,classId));
	}

	@ApiOperation(value = "保存暂不提交")
	@ApiOperationSupport(order = 3, author = "domain_lee")
	@PostMapping("/saveNotCommit")
	public Result<?> saveNotCommit(@RequestBody TeacherRating teacherRating){
		return data(testTeacherPaperService.saveNotCommit(teacherRating));
	}

	@ApiOperation(value = "保存提交")
	@ApiOperationSupport(order = 4, author = "domain_lee")
	@PostMapping("/saveCommit")
	public Result<?> saveCommit(@RequestBody TeacherRating teacherRating){
		return data(testTeacherPaperService.saveCommit(teacherRating));
	}

	@ApiOperation(value = "教师评定列表")
	@ApiOperationSupport(order = 5, author = "domain_lee")
	@GetMapping("/teacherRatingList")
	public Result<Page<TeacherPaperVO>> teacherRatingList(@Valid TeacherPaperDTO teacherPaperDTO,
			Query query){
		return data(testTeacherPaperService.teacherRatingList(Condition.getPage(query),teacherPaperDTO));
	}
}
