package org.mentpeak.test.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.mybatisplus.support.Query;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.test.service.ITestPaperRecordService;
import org.mentpeak.test.vo.TestRecordVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户问卷测试记录表 控制器
 *
 * @author lxp
 * @since 2022-07-13
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testPaperRecord")
@Api(value = "用户问卷测试记录表", tags = "用户问卷测试记录表接口")
public class TestPaperRecordController extends PlatformController {

    private ITestPaperRecordService testPaperRecordService;

    @ApiOperation("测评记录")
    @ApiOperationSupport(order = 1)
    @GetMapping("/list")
    public Result<IPage<TestRecordVO>> list(Query query) {
        return Result.data(testPaperRecordService.getList(Condition.getPage(query)));
    }


}
