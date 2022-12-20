package org.mentpeak.core.log.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.mentpeak.core.log.model.LogError;
import org.mentpeak.core.log.model.LogErrorVo;
import org.mentpeak.core.log.service.ILogErrorService;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.mybatisplus.support.Query;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.core.tool.utils.Func;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 控制器
 */
@RestController
@AllArgsConstructor
@RequestMapping ( "/error" )
public class LogErrorController {

    private ILogErrorService errorLogService;

    /**
     * 查询单条
     */
    @GetMapping ( "/detail" )
    public Result < LogError > detail ( LogError logError ) {
        return Result.data ( errorLogService.getOne ( Condition.getQueryWrapper ( logError ) ) );
    }

    /**
     * 查询多条(分页)
     */
    @GetMapping ( "/list" )
    public Result < IPage < LogErrorVo > > list (
            @ApiIgnore @RequestParam Map < String, Object > logError ,
            Query query ) {
        IPage < LogError > pages = errorLogService.page ( Condition.getPage ( query.setDescs ( "create_time" ) ) ,
                                                          Condition.getQueryWrapper ( logError ,
                                                                                      LogError.class ) );
        List < LogErrorVo > records = pages.getRecords ( )
                                           .stream ( )
                                           .map ( logApi -> {
                                               LogErrorVo vo = BeanUtil.copy ( logApi ,
                                                                               LogErrorVo.class );
                                               vo.setStrId ( Func.toStr ( logApi.getId ( ) ) );
                                               return vo;
                                           } )
                                           .collect ( Collectors.toList ( ) );
        IPage < LogErrorVo > pageVo = new Page <> ( pages.getCurrent ( ) ,
                                                    pages.getSize ( ) ,
                                                    pages.getTotal ( ) );
        pageVo.setRecords ( records );
        return Result.data ( pageVo );
    }

}
