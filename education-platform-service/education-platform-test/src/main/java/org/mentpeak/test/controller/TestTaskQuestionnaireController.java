package org.mentpeak.test.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.test.service.ITestTaskQuestionnaireService;
import org.mentpeak.test.vo.TaskQuestionnaireVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 测评任务问卷关联表 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testtaskquestionnaire")
@Api(value = "测评任务问卷关联表", tags = "测评任务问卷关联表接口")
public class TestTaskQuestionnaireController extends PlatformController {

    private ITestTaskQuestionnaireService testTaskQuestionnaireService;

    @GetMapping("/getListById")
    @ApiOperation(value = "获取用户测评列表")
    @ApiOperationSupport(order = 1)
    public Result<List<TaskQuestionnaireVO>> list(@ApiParam(value = "任务ID", required = true) @RequestParam(value = "id") Long id) {
        return Result.data(testTaskQuestionnaireService.getListById(id));
    }

}
