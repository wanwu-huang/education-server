package org.mentpeak.test.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.mybatisplus.support.Query;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.test.dto.BindTeacherDTO;
import org.mentpeak.test.service.IReportTeacherGradeService;
import org.mentpeak.test.vo.BindTeacherVO;
import org.mentpeak.test.vo.GradeReportVO;
import org.mentpeak.test.vo.ReportListVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 老师年级报告表 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/reportteachergrade")
@Api(value = "老师年级报告表", tags = "老师年级报告表接口")
public class ReportTeacherGradeController extends PlatformController {

    private IReportTeacherGradeService reportTeacherGradeService;

    /**
     * 计算添加年级报告
     */
    @ApiOperation(value = "获取年级报告")
    @ApiOperationSupport(order = 1, author = "hzl")
    @GetMapping("/getGradeReport")
    public Result<GradeReportVO> addClassReport(@ApiParam(value = "任务ID", required = true) Long taskId, @ApiParam(value = "年级ID", required = true) Long gradeId) {
        return Result.data(reportTeacherGradeService.getGradeReport(taskId, gradeId));
    }

    /**
     * 测试完成情况
     */
    @ApiOperation(value = "计算添加年级报告")
    @ApiOperationSupport(order = 2, author = "hzl")
    @GetMapping("/addClassReport")
    public Result<GradeReportVO> getTestPelple(@ApiParam(value = "任务ID", required = true) Long taskId, @ApiParam(value = "年级ID", required = true) Long classId) {
        return Result.data(reportTeacherGradeService.addGradeReport(taskId, classId, SecureUtil.getTenantCode()));
    }

    /**
     * 年级报告列表
     */
    @ApiOperation(value = "年级报告列表")
    @ApiOperationSupport(order = 3, author = "hzl")
    @GetMapping("/getReportList")
    public Result<IPage<ReportListVO>> getReportList(Query query, @ApiParam(value = "年级ID") @RequestParam(required = false) Long gradeId, @ApiParam(value = "任务ID", required = true) @RequestParam(required = false) Long taskId) {
        return Result.data(reportTeacherGradeService.getReportList(Condition.getPage(query), gradeId, taskId));
    }

    /**
     * 绑定教师
     */
    @ApiOperation(value = "绑定教师")
    @ApiOperationSupport(order = 4, author = "hzl")
    @PostMapping("/bindTeacher")
    public Result bindTeacher(@RequestBody BindTeacherDTO dto) {
        return Result.status(reportTeacherGradeService.bindTeacher(dto));
    }

    /**
     * 绑定教师列表
     */
    @ApiOperation(value = "绑定教师列表")
    @ApiOperationSupport(order = 5, author = "hzl")
    @GetMapping("/bindTeacherList")
    public Result<List<BindTeacherVO>> bindTeacherList(@ApiParam(value = "任务ID", required = true) @RequestParam(required = true) Long taskId, @ApiParam(value = "年级ID", required = true) @RequestParam(required = true) Long gradeId) {
        return Result.data(reportTeacherGradeService.bindTeacherList(taskId, gradeId));
    }

    /**
     * 解绑
     */
    @ApiOperation(value = "解绑教师")
    @ApiOperationSupport(order = 6, author = "hzl")
    @GetMapping("/removeBindTeacher")
    public Result removeBindTeacher(@ApiParam(value = "用户ID", required = true) @RequestParam(required = true) Long userId, @ApiParam(value = "任务ID", required = true) @RequestParam(required = true) Long taskId, @ApiParam(value = "年级ID", required = true) @RequestParam(required = true) Long gradeId) {
        return Result.status(reportTeacherGradeService.removeBindTeacher(userId, taskId, gradeId));
    }


}
