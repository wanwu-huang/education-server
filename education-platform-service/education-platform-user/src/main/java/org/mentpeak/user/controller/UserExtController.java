package org.mentpeak.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.mybatisplus.support.Query;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.user.entity.SysCity;
import org.mentpeak.user.entity.SysProvince;
import org.mentpeak.user.entity.UserExt;
import org.mentpeak.user.service.IUserExtService;
import org.mentpeak.user.vo.SchoolVO;
import org.mentpeak.user.vo.UserExtVO;
import org.mentpeak.user.wrapper.UserExtWrapper;
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
 * 用户信息扩展表 控制器
 *
 * @author lxp
 * @since 2021-03-27
 */
@RestController
@AllArgsConstructor
@RequestMapping ( "/userext" )
@Api ( value = "用户信息扩展表", tags = "用户信息扩展表接口" )
public class UserExtController extends PlatformController {

    private IUserExtService userExtService;


//    /**
//     * 详情
//     */
//    @GetMapping ( "/detail" )
//    @ApiOperation ( value = "详情", notes = "传入userExt", position = 1 )
//    public Result < SchoolVO > detail ( ) {
//        return Result.data ( userExtService.detail ( ) );
//    }

//    /**
//     * 分页 用户信息扩展表
//     */
//    @GetMapping ( "/list" )
//    @ApiOperation ( value = "分页", notes = "传入userExt", position = 2 )
//    public Result < IPage < UserExtVO > > list (
//            UserExt userExt ,
//            Query query ) {
//        IPage < UserExt > pages = userExtService.page ( Condition.getPage ( query ) ,
//                                                        Condition.getQueryWrapper ( userExt ) );
//        UserExtWrapper userExtWrapper = new UserExtWrapper ( );
//        return Result.data ( userExtWrapper.pageVO ( pages ) );
//    }
//
//    /**
//     * 自定义分页 用户信息扩展表
//     */
//    @GetMapping ( "/page" )
//    @ApiOperation ( value = "分页", notes = "传入userExt", position = 3 )
//    public Result < IPage < UserExtVO > > page (
//            UserExtVO userExt ,
//            Query query ) {
//        IPage < UserExtVO > pages = userExtService.selectUserExtPage ( Condition.getPage ( query ) ,
//                                                                       userExt );
//        return Result.data ( pages );
//    }
//
//    /**
//     * 新增 用户信息扩展表
//     */
//    @PostMapping ( "/save" )
//    @ApiOperation ( value = "新增", notes = "传入userExt", position = 4 )
//    public Result save ( @Valid @RequestBody UserExt userExt ) {
//        return Result.status ( userExtService.save ( userExt ) );
//    }
//
//    /**
//     * 修改 用户信息扩展表
//     */
//    @PostMapping ( "/update" )
//    @ApiOperation ( value = "修改", notes = "传入userExt", position = 5 )
//    public Result update ( @Valid @RequestBody UserExt userExt ) {
//        return Result.status ( userExtService.updateById ( userExt ) );
//    }
//
//    /**
//     * 新增或修改 用户信息扩展表
//     */
//    @PostMapping ( "/submit" )
//    @ApiOperation ( value = "新增或修改", notes = "传入userExt", position = 6 )
//    public Result submit ( @Valid @RequestBody UserExt userExt ) {
//        return Result.status ( userExtService.saveOrUpdate ( userExt ) );
//    }
//
//
//    /**
//     * 删除 用户信息扩展表
//     */
//    @PostMapping ( "/remove" )
//    @ApiOperation ( value = "删除", notes = "传入ids", position = 7 )
//    public Result remove ( @ApiParam ( value = "主键集合", required = true ) @RequestParam String ids ) {
//        return Result.status ( userExtService.removeByIds ( Func.toIntList ( ids ) ) );
//    }
//
//    /**
//     * 查找省
//     *
//     * @return null
//     * @author hzl
//     * @date 2021/4/7 15:35
//     */
//    @GetMapping ( "/getProvince" )
//    @ApiOperation ( value = "查找省份", notes = "", position = 2 )
//    public Result getProvince ( ) {
//        return userExtService.findPrivince ( );
//    }
//
//    /**
//     * 查找市
//     *
//     * @return null
//     * @author hzl
//     * @date 2021/4/7 15:35
//     */
//    @GetMapping ( "/getCity" )
//    @ApiOperation ( value = "查找市级", notes = "", position = 2 )
//    public Result getCity ( SysProvince province ) {
//        return userExtService.findCity ( province );
//    }
//
//    /**
//     * 查找区
//     *
//     * @return null
//     * @author hzl
//     * @date 2021/4/7 15:35
//     */
//    @GetMapping ( "/getArea" )
//    @ApiOperation ( value = "查找区级", notes = "", position = 2 )
//    public Result getArea ( SysCity city ) {
//        return userExtService.findArea ( city );
//    }
//
//    /**
//     * 查找头像图片
//     *
//     * @return null
//     * @author hzl
//     * @date 2021/4/7 15:35
//     */
//    @GetMapping ( "/getImgUrl" )
//    @ApiOperation ( value = "查找头像图片", notes = "", position = 2 )
//    public Result getImgUrl ( ) {
//        return userExtService.findImgUrl ( );
//    }
//
//    /**
//     * 查找省市
//     *
//     * @return null
//     * @author hzl
//     * @date 2021/4/7 15:35
//     */
//    @GetMapping ( "/getAddress" )
//    @ApiOperation ( value = "查找省市", notes = "", position = 2 )
//    public Result getAddress ( ) {
//        return userExtService.getAddress ( );
//    }

}
