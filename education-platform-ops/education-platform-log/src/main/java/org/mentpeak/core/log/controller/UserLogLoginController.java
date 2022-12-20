package org.mentpeak.core.log.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.log.entity.UserLogLogin;
import org.mentpeak.core.log.service.IUserLogLoginService;
import org.mentpeak.core.log.vo.UserLogLoginVO;
import org.mentpeak.core.log.wrapper.UserLogLoginWrapper;
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
 * 用户错误登录日志 控制器
 *
 * @author lxp
 * @since 2021-03-27
 */
@RestController
@AllArgsConstructor
@RequestMapping ( "/userloglogin" )
@Api ( value = "用户错误登录日志", tags = "用户错误登录日志接口" )
public class UserLogLoginController extends PlatformController {

    private IUserLogLoginService userLogLoginService;


    /**
     * 详情
     */
    @GetMapping ( "/detail" )
    @ApiOperation ( value = "详情", notes = "传入userLogLogin", position = 1 )
    public Result < UserLogLoginVO > detail ( UserLogLogin userLogLogin ) {
        UserLogLogin detail = userLogLoginService.getOne ( Condition.getQueryWrapper ( userLogLogin ) );
        UserLogLoginWrapper userLogLoginWrapper = new UserLogLoginWrapper ( );
        return Result.data ( userLogLoginWrapper.entityVO ( detail ) );
    }

    /**
     * 分页 用户错误登录日志
     */
    @GetMapping ( "/list" )
    @ApiOperation ( value = "分页", notes = "传入userLogLogin", position = 2 )
    public Result < IPage < UserLogLoginVO > > list (
            UserLogLogin userLogLogin ,
            Query query ) {
        IPage < UserLogLogin > pages = userLogLoginService.page ( Condition.getPage ( query ) ,
                                                                  Condition.getQueryWrapper ( userLogLogin ) );
        UserLogLoginWrapper userLogLoginWrapper = new UserLogLoginWrapper ( );
        return Result.data ( userLogLoginWrapper.pageVO ( pages ) );
    }

    /**
     * 自定义分页 用户错误登录日志
     */
    @GetMapping ( "/page" )
    @ApiOperation ( value = "分页", notes = "传入userLogLogin", position = 3 )
    public Result < IPage < UserLogLoginVO > > page (
            UserLogLoginVO userLogLogin ,
            Query query ) {
        IPage < UserLogLoginVO > pages = userLogLoginService.selectUserLogLoginPage ( Condition.getPage ( query ) ,
                                                                                      userLogLogin );
        return Result.data ( pages );
    }

    /**
     * 新增 用户错误登录日志
     */
    @PostMapping ( "/save" )
    @ApiOperation ( value = "新增", notes = "传入userLogLogin", position = 4 )
    public Result save ( @Valid @RequestBody UserLogLogin userLogLogin ) {
        return Result.status ( userLogLoginService.save ( userLogLogin ) );
    }

    /**
     * 修改 用户错误登录日志
     */
    @PostMapping ( "/update" )
    @ApiOperation ( value = "修改", notes = "传入userLogLogin", position = 5 )
    public Result update ( @Valid @RequestBody UserLogLogin userLogLogin ) {
        return Result.status ( userLogLoginService.updateById ( userLogLogin ) );
    }

    /**
     * 新增或修改 用户错误登录日志
     */
    @PostMapping ( "/submit" )
    @ApiOperation ( value = "新增或修改", notes = "传入userLogLogin", position = 6 )
    public Result submit ( @Valid @RequestBody UserLogLogin userLogLogin ) {
        return Result.status ( userLogLoginService.saveOrUpdate ( userLogLogin ) );
    }


    /**
     * 删除 用户错误登录日志
     */
    @PostMapping ( "/remove" )
    @ApiOperation ( value = "删除", notes = "传入ids", position = 7 )
    public Result remove ( @ApiParam ( value = "主键集合", required = true ) @RequestParam String ids ) {
        return Result.status ( userLogLoginService.removeByIds ( Func.toIntList ( ids ) ) );
    }


}
