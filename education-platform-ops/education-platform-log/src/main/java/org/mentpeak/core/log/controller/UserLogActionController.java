package org.mentpeak.core.log.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.log.entity.UserLogAction;
import org.mentpeak.core.log.service.IUserLogActionService;
import org.mentpeak.core.log.vo.UserLogActionVO;
import org.mentpeak.core.log.wrapper.UserLogActionWrapper;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.mybatisplus.support.Query;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.Func;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

/**
 * 管理后台-操作日志表 控制器
 *
 * @author lxp
 * @since 2021-03-27
 */
@RestController
@AllArgsConstructor
@RequestMapping ( "/userlogaction" )
@Api ( value = "管理后台-操作日志表", tags = "管理后台-操作日志表接口" )
public class UserLogActionController extends PlatformController {

    private IUserLogActionService userLogActionService;


    /**
     * 详情
     */
    @GetMapping ( "/detail" )
    @ApiOperation ( value = "详情", notes = "传入userLogAction", position = 1 )
    public Result < UserLogActionVO > detail ( UserLogAction userLogAction ) {
        UserLogAction detail = userLogActionService.getOne ( Condition.getQueryWrapper ( userLogAction ) );
        UserLogActionWrapper userLogActionWrapper = new UserLogActionWrapper ( );
        return Result.data ( userLogActionWrapper.entityVO ( detail ) );
    }

    /**
     * 分页 管理后台-操作日志表
     */
    @GetMapping ( "/list" )
    @ApiOperation ( value = "分页", notes = "传入userLogAction", position = 2 )
    public Result < IPage < UserLogActionVO > > list (
            UserLogAction userLogAction ,
            Query query ) {
        IPage < UserLogAction > pages = userLogActionService.page ( Condition.getPage ( query ) ,
                                                                    Condition.getQueryWrapper ( userLogAction ) );
        UserLogActionWrapper userLogActionWrapper = new UserLogActionWrapper ( );
        return Result.data ( userLogActionWrapper.pageVO ( pages ) );
    }

    /**
     * 自定义分页 管理后台-操作日志表
     */
    @GetMapping ( "/page" )
    @ApiOperation ( value = "分页", notes = "传入userLogAction", position = 3 )
    public Result < IPage < UserLogActionVO > > page (
            UserLogActionVO userLogAction ,
            Query query ) {
        IPage < UserLogActionVO > pages = userLogActionService.selectUserLogActionPage ( Condition.getPage ( query ) ,
                                                                                         userLogAction );
        return Result.data ( pages );
    }

    /**
     * 新增 管理后台-操作日志表
     */
    @PostMapping ( "/save" )
    @ApiOperation ( value = "新增", notes = "传入userLogAction", position = 4 )
    public Result save ( @Valid @RequestBody UserLogAction userLogAction ) {
        return Result.status ( userLogActionService.save ( userLogAction ) );
    }

    /**
     * 修改 管理后台-操作日志表
     */
    @PostMapping ( "/update" )
    @ApiOperation ( value = "修改", notes = "传入userLogAction", position = 5 )
    public Result update ( @Valid @RequestBody UserLogAction userLogAction ) {
        return Result.status ( userLogActionService.updateById ( userLogAction ) );
    }

    /**
     * 新增或修改 管理后台-操作日志表
     */
    @PostMapping ( "/submit" )
    @ApiOperation ( value = "新增或修改", notes = "传入userLogAction", position = 6 )
    public Result submit ( @Valid @RequestBody UserLogAction userLogAction ) {
        return Result.status ( userLogActionService.saveOrUpdate ( userLogAction ) );
    }


    /**
     * 删除 管理后台-操作日志表
     */
    @PostMapping ( "/remove" )
    @ApiOperation ( value = "删除", notes = "传入ids", position = 7 )
    public Result remove ( @ApiParam ( value = "主键集合", required = true ) @RequestParam String ids ) {
        return Result.status ( userLogActionService.removeByIds ( Func.toIntList ( ids ) ) );
    }


}
