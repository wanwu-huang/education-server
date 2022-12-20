package org.mentpeak.test.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.http.cache.HttpCacheAble;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.mybatisplus.support.Query;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.test.dto.TaskSearchDTO;
import org.mentpeak.test.service.ITestTaskUserService;
import org.mentpeak.test.vo.FollowVO;
import org.mentpeak.test.vo.TaskWarnVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测评任务用户关联表 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testtaskuser")
@Api(value = "测评任务用户关联表", tags = "测评任务用户关联表接口")
public class TestTaskUserController extends PlatformController {

    private ITestTaskUserService testTaskUserService;

    /**
     * 预警管理列表
     */
    @ApiOperation(value = "预警管理列表")
    @ApiOperationSupport(order = 1, author = "hzl")
    @GetMapping("/warnList")
//    @HttpCacheAble(value = 10, maxAge = 10)
    public Result<Page<TaskWarnVO>> warnList(TaskSearchDTO taskSearchDTO, Query query) {
        return Result.data(testTaskUserService.warnList(Condition.getPage(query), taskSearchDTO));
    }

    /**
     * 关注等级列表
     */
    @ApiOperation(value = "关注等级列表")
    @ApiOperationSupport(order = 2, author = "hzl")
    @GetMapping("/followList")
//    @HttpCacheAble(value = 10, maxAge = 10)
    public Result<IPage<FollowVO>> followList(Query query, @ApiParam(value = "关注等级ID", required = true) Long followId, @ApiParam(value = "任务ID", required = true) Long taskId) {
        return Result.data(testTaskUserService.followList(Condition.getPage(query), followId, taskId));
    }


}
