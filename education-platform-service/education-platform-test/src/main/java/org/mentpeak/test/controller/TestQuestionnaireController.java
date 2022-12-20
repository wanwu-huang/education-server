package org.mentpeak.test.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.mybatisplus.support.Query;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.test.entity.TestQuestionnaire;
import org.mentpeak.test.service.ITestQuestionnaireService;
import org.mentpeak.test.vo.TestQuestionnaireVO;
import org.mentpeak.test.wrapper.TestQuestionnaireWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 问卷表 控制器
 *
 * @author lxp
 * @since 2022-07-13
 */
@RestController
@AllArgsConstructor
@RequestMapping("/questionnaire")
@Api(value = "问卷表", tags = "问卷表接口")
@ApiSupport(order = 200)
public class TestQuestionnaireController extends PlatformController {

    private ITestQuestionnaireService testQuestionnaireService;


    /**
     * 测评问卷列表
     */
    @ApiOperation(value = "测评问卷列表")
    @ApiOperationSupport(order = 1, author = "domain_lee")
    @GetMapping("/testQuestionnaireList")
    public Result<List<TestQuestionnaire>> testQuestionnaireList() {
        return data(testQuestionnaireService.testQuestionnaireList());
    }

    @GetMapping("/list")
    @ApiOperation(value = "量表管理-列表分页", notes = "传入platformDict", position = 2)
    public Result<IPage<TestQuestionnaireVO>> list(@ApiParam(value = "量表名称") @RequestParam(value = "name", required = false) String name, Query query) {
        LambdaQueryWrapper<TestQuestionnaire> wrapper = Wrappers.<TestQuestionnaire>lambdaQuery().eq(TestQuestionnaire::getIsDeleted, 0).like(Func.isNotEmpty(name), TestQuestionnaire::getName, name);
        IPage<TestQuestionnaire> pages = testQuestionnaireService.page(Condition.getPage(query), wrapper);
        TestQuestionnaireWrapper platformDictWrapper = new TestQuestionnaireWrapper();
        return Result.data(platformDictWrapper.pageVO(pages));
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改", notes = "传入platformDict", position = 5)
    public Result update(@Valid @RequestBody TestQuestionnaire platformDict) {
        return Result.status(testQuestionnaireService.updateById(platformDict));
    }

    @GetMapping("/getQuestionnaireById")
    @ApiOperation(value = "数据回显", notes = "传入id", position = 2)
    public Result<TestQuestionnaire> getQuestionnaireById(@ApiParam(value = "量表id") @RequestParam Long id) {
        TestQuestionnaire byId = testQuestionnaireService.getById(id);
        return Result.data(byId);
    }

}
