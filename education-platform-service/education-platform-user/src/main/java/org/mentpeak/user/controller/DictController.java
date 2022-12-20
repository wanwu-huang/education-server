package org.mentpeak.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.mentpeak.core.log.annotation.ApiLog;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.dict.cache.DictCache;
import org.mentpeak.dict.entity.Dict;
import org.mentpeak.dict.vo.GradeVO;
import org.mentpeak.user.dto.BindStudentDTO;
import org.mentpeak.user.service.IDictService;
import org.mentpeak.user.vo.ParentStudentVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author hzl
 * @data 2022年06月24日17:20
 */
@RestController
@RequestMapping
@AllArgsConstructor
@Api(tags = "字典表操作")
public class DictController {

    private final IDictService dictService;

    /**
     * 获取年级列表
     */
    @GetMapping("/getGradeList")
    @ApiOperation(value = "获取年级列表", notes = "", position = 1)
    @ApiLog("获取年级列表")
    public Result<List<GradeVO>> getGradeList() {
        return Result.data(dictService.getGradeList());
    }

    /**
     * 获取班级列表
     */
    @GetMapping("/getClassList")
    @ApiOperation(value = "获取班级列表", notes = "传入年级id", position = 2)
    @ApiLog("获取班级列表")
    public Result<List<GradeVO>> getClassList(@ApiParam(value = "年级id", required = true) @RequestParam Long id) {
        return Result.data(dictService.getClassList(id));
    }

    /**
     * 绑定学生信息
     */
    @PostMapping("/bindStudent")
    @ApiOperation(value = "绑定学生信息", notes = "传入学生信息", position = 3)
    @ApiLog("绑定学生信息")
    public Result bindStudent(@Validated @RequestBody BindStudentDTO bindStudentDTO) {
        return Result.status(dictService.bindStudent(bindStudentDTO));
    }

    /**
     * 已绑定学生列表
     */
    @PostMapping("/bindStudentList")
    @ApiOperation(value = "已绑定学生列表", notes = "", position = 4)
    @ApiLog("已绑定学生列表")
    public Result<List<ParentStudentVO>> bindStudentList() {
        return Result.data(dictService.bindStudentList());
    }

    /**
     * 删除已绑定学生
     */
    @GetMapping("/delBindStudent")
    @ApiOperation(value = "删除已绑定学生", notes = "传入学生id", position = 5)
    @ApiLog("删除已绑定学生")
    public Result delBindStudent(@RequestParam Integer studentId) {
        return Result.status(dictService.delBindStudent(studentId));
    }


    /**
     * 获取字典
     *
     * @return
     */
    @GetMapping("/dictionary")
    @ApiOperation(value = "获取字典", notes = "获取字典", position = 8)
    public Result<List<Dict>> dictionary(String code) {
        List<Dict> tree = dictService.getList(code);
        return Result.data(tree);
    }
}
