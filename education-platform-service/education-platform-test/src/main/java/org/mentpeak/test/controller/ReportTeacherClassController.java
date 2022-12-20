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
import org.mentpeak.test.entity.mongo.PersonalReport;
import org.mentpeak.test.service.IReportTeacherClassService;
import org.mentpeak.test.vo.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 老师班级报告表 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/reportteacherclass")
@Api(value = "老师班级报告表", tags = "老师班级报告表接口")
public class ReportTeacherClassController extends PlatformController {

    private IReportTeacherClassService reportTeacherClassService;


    /**
     * 计算添加班级报告
     */
    @ApiOperation(value = "计算添加班级报告")
    @ApiOperationSupport(order = 1, author = "hzl")
    @GetMapping("/addClassReport")
    public Result<ClassReportVO> addClassReport(@ApiParam(value = "任务ID", required = true) Long taskId, @ApiParam(value = "年级ID", required = true) Long gradeId, @ApiParam(value = "班级ID", required = true) Long classId) {
        return Result.data(reportTeacherClassService.addClassReport(taskId, gradeId, classId,SecureUtil.getTenantCode()));
    }

    /**
     * 测试完成情况
     */
    @ApiOperation(value = "测试完成情况")
    @ApiOperationSupport(order = 2, author = "hzl")
    @ApiIgnore
    @GetMapping("/getTestPelple")
    public Result<TestModuleVO> getTestPelple(@ApiParam(value = "任务ID", required = true) Long taskId, @ApiParam(value = "年级ID", required = true) Long gradeId, @ApiParam(value = "班级ID", required = true) Long classId) {
        return Result.data(reportTeacherClassService.getTestPelple(taskId, gradeId, classId, null, null,SecureUtil.getTenantCode()));
    }

    /**
     * 心理健康评级
     */
    @ApiOperation(value = "心理健康评级")
    @ApiOperationSupport(order = 3, author = "hzl")
    @ApiIgnore
    @GetMapping("/getMentalHealthy")
    public Result<TestModuleTwoVO> getMentalHealthy(@ApiParam(value = "任务ID", required = true) Long taskId, @ApiParam(value = "年级ID", required = true) Long gradeId, @ApiParam(value = "班级ID", required = true) Long classId) {
        return Result.data(reportTeacherClassService.getMentalHealthy(taskId, gradeId, classId, null, SecureUtil.getTenantCode()));
    }

    /**
     * 测评概况
     */
    @ApiOperation(value = "测评概况")
    @ApiOperationSupport(order = 4, author = "hzl")
    @ApiIgnore
    @GetMapping("/getTestScore")
    public Result<PersonalReport.TestOverview> getTestScore(@ApiParam(value = "任务ID", required = true) Long taskId, @ApiParam(value = "年级ID", required = true) Long gradeId, @ApiParam(value = "班级ID", required = true) Long classId) {
        return Result.data(reportTeacherClassService.getTestScore(taskId, gradeId, classId, null,null));
    }

    /**
     * 学习状态
     */
    @ApiOperation(value = "学习状态")
    @ApiOperationSupport(order = 5, author = "hzl")
    @ApiIgnore
    @GetMapping("/getStudyStatus")
    public Result<List<DimensionReportVO>> getStudyStatus(@ApiParam(value = "任务ID", required = true) Long taskId, @ApiParam(value = "年级ID", required = true) Long gradeId, @ApiParam(value = "班级ID", required = true) Long classId) {
        return Result.data(reportTeacherClassService.getStudyStatus(taskId, gradeId, classId, null,null));
    }

    /**
     * 班级报告
     */
    @ApiOperation(value = "获取班级报告")
    @ApiOperationSupport(order = 6, author = "hzl")
    @GetMapping("/getClassReport")
    public Result<ClassReportVO> getClassReport(@ApiParam(value = "任务ID", required = true) Long taskId, @ApiParam(value = "年级ID", required = true) Long gradeId, @ApiParam(value = "班级ID", required = true) Long classId) {
        return Result.data(reportTeacherClassService.getClassReport(taskId, gradeId, classId));
    }

    /**
     * 班级报告列表
     */
    @ApiOperation(value = "班级报告列表")
    @ApiOperationSupport(order = 7, author = "hzl")
    @GetMapping("/getReportList")
    public Result<IPage<ReportClassListVO>> getReportList(Query query, @ApiParam(value = "gradeId") @RequestParam(required = false) Long gradeId,
                                                          @ApiParam(value = "classId") @RequestParam(required = false) Long classId,
                                                          @ApiParam(value = "任务ID", required = true) @RequestParam(required = true) Long taskId) {
        return Result.data(reportTeacherClassService.getReportList(Condition.getPage(query), taskId, gradeId, classId));
    }

    /**
     * 绑定教师
     */
    @ApiOperation(value = "绑定教师")
    @ApiOperationSupport(order = 8, author = "hzl")
    @PostMapping("/bindTeacher")
    public Result bindTeacher(@RequestBody BindTeacherDTO dto) {
        return Result.status(reportTeacherClassService.bindTeacher(dto));
    }

    /**
     * 绑定教师列表
     */
    @ApiOperation(value = "绑定教师列表")
    @ApiOperationSupport(order = 9, author = "hzl")
    @GetMapping("/bindTeacherList")
    public Result<List<BindTeacherVO>> bindTeacherList(@ApiParam(value = "任务ID", required = true) @RequestParam(required = true) Long taskId, @ApiParam(value = "年级ID", required = true) @RequestParam(required = true) Long gradeId, @ApiParam(value = "班级ID", required = true) @RequestParam(required = true) Long classId) {
        return Result.data(reportTeacherClassService.bindTeacherList(taskId, gradeId, classId));
    }

    /**
     * 解绑
     */
    @ApiOperation(value = "解绑教师")
    @ApiOperationSupport(order = 10, author = "hzl")
    @GetMapping("/removeBindTeacher")
    public Result removeBindTeacher(@ApiParam(value = "用户ID", required = true) @RequestParam(required = true) Long userId, @ApiParam(value = "任务ID", required = true) @RequestParam(required = true) Long taskId, @ApiParam(value = "年级ID", required = true) @RequestParam(required = true) Long gradeId, @ApiParam(value = "班级ID", required = true) @RequestParam(required = true) Long classId) {
        return Result.status(reportTeacherClassService.removeBindTeacher(userId,taskId  ,gradeId,classId));
    }


}
