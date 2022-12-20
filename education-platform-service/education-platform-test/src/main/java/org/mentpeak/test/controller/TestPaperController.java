package org.mentpeak.test.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.test.aspect.PreventDuplication;
import org.mentpeak.test.dto.NextQuestionDTO;
import org.mentpeak.test.dto.QuestionDTO;
import org.mentpeak.test.service.ITestPaperService;
import org.mentpeak.test.vo.AnswerVo;
import org.mentpeak.test.vo.GenerateVO;
import org.mentpeak.test.vo.InstructionVO;
import org.mentpeak.test.vo.QuestionVO;
import org.springframework.web.bind.annotation.*;

/**
 * 用户试卷信息表 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testpaper")
@Api(value = "用户试卷信息表", tags = "用户试卷信息表接口")
public class TestPaperController extends PlatformController {

    private ITestPaperService testPaperService;

    @GetMapping("/getReminder")
    @ApiOperation(value = "获取指导语")
    @ApiOperationSupport(order = 1)
    public Result<InstructionVO> getReminder(@ApiParam(value = "模块id", required = true) @RequestParam(value = "moduleId") Long moduleId) {
        return Result.data(testPaperService.getReminder(moduleId));
    }

    @GetMapping("/generatePaper")
    @ApiOperation(value = "开始答题")
    @ApiOperationSupport(order = 2)
    public Result<QuestionVO> generatePaper(GenerateVO generateVO) {
        return Result.data(testPaperService.generatePaper(generateVO));
    }

    @GetMapping("/getNextQuestion")
    @ApiOperation(value = "获取下一题")
    @ApiOperationSupport(order = 3)
    public Result<QuestionVO> getNextQuestion(NextQuestionDTO nextQuestionDTO) {
        return Result.data(testPaperService.getNextQuestion(nextQuestionDTO));
    }

    @PostMapping("/saveResult")
    @ApiOperation(value = "保存答题")
    @ApiOperationSupport(order = 4)
//    @PreventDuplication
    public Result<AnswerVo> saveResult(@RequestBody QuestionDTO questionDTO) {
        return Result.data(testPaperService.saveResult(questionDTO));
    }


}
