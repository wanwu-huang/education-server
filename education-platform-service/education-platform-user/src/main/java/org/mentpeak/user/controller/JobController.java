//package org.mentpeak.user.controller;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//
//import org.mentpeak.core.boot.ctrl.PlatformController;
//import org.mentpeak.core.mybatisplus.support.Condition;
//import org.mentpeak.core.mybatisplus.support.Query;
//import org.mentpeak.core.tool.api.Result;
//import org.mentpeak.core.tool.utils.Func;
//import org.mentpeak.user.entity.Job;
//import org.mentpeak.user.service.IJobService;
//import org.mentpeak.user.vo.JobVO;
//import org.mentpeak.user.wrapper.JobWrapper;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.validation.Valid;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import lombok.AllArgsConstructor;
//
///**
// * 职业表 控制器
// *
// * @author lxp
// * @since 2021-03-27
// */
//@RestController
//@AllArgsConstructor
//@RequestMapping ( "/job" )
//@Api ( value = "职业表", tags = "职业表接口" )
//public class JobController extends PlatformController {
//
//    private IJobService jobService;
//
//
//    /**
//     * 详情
//     */
//    @GetMapping ( "/detail" )
//    @ApiOperation ( value = "详情", notes = "传入job", position = 1 )
//    public Result < JobVO > detail ( Job job ) {
//        Job detail = jobService.getOne ( Condition.getQueryWrapper ( job ) );
//        JobWrapper jobWrapper = new JobWrapper ( );
//        return Result.data ( jobWrapper.entityVO ( detail ) );
//    }
//
//    /**
//     * 分页 职业表
//     */
//    @GetMapping ( "/list" )
//    @ApiOperation ( value = "分页", notes = "传入job", position = 2 )
//    public Result < IPage < JobVO > > list (
//            Job job ,
//            Query query ) {
//        IPage < Job > pages = jobService.page ( Condition.getPage ( query ) ,
//                                                Condition.getQueryWrapper ( job ) );
//        JobWrapper jobWrapper = new JobWrapper ( );
//        return Result.data ( jobWrapper.pageVO ( pages ) );
//    }
//
//    /**
//     * 自定义分页 职业表
//     */
//    @GetMapping ( "/page" )
//    @ApiOperation ( value = "分页", notes = "传入job", position = 3 )
//    public Result < IPage < JobVO > > page (
//            JobVO job ,
//            Query query ) {
//        IPage < JobVO > pages = jobService.selectJobPage ( Condition.getPage ( query ) ,
//                                                           job );
//        return Result.data ( pages );
//    }
//
//    /**
//     * 新增 职业表
//     */
//    @PostMapping ( "/save" )
//    @ApiOperation ( value = "新增", notes = "传入job", position = 4 )
//    public Result save ( @Valid @RequestBody Job job ) {
//        return Result.status ( jobService.save ( job ) );
//    }
//
//    /**
//     * 修改 职业表
//     */
//    @PostMapping ( "/update" )
//    @ApiOperation ( value = "修改", notes = "传入job", position = 5 )
//    public Result update ( @Valid @RequestBody Job job ) {
//        return Result.status ( jobService.updateById ( job ) );
//    }
//
//    /**
//     * 新增或修改 职业表
//     */
//    @PostMapping ( "/submit" )
//    @ApiOperation ( value = "新增或修改", notes = "传入job", position = 6 )
//    public Result submit ( @Valid @RequestBody Job job ) {
//        return Result.status ( jobService.saveOrUpdate ( job ) );
//    }
//
//
//    /**
//     * 删除 职业表
//     */
//    @PostMapping ( "/remove" )
//    @ApiOperation ( value = "删除", notes = "传入ids", position = 7 )
//    public Result remove ( @ApiParam ( value = "主键集合", required = true ) @RequestParam String ids ) {
//        return Result.status ( jobService.removeByIds ( Func.toIntList ( ids ) ) );
//    }
//
//
//}
