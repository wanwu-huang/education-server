package org.mentpeak.core.log.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.mentpeak.core.log.model.LogApi;
import org.mentpeak.core.log.model.LogApiVo;
import org.mentpeak.core.log.service.ILogApiService;
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
@RequestMapping ( "/api" )
public class LogApiController {

    private ILogApiService logService;

    /**
     * 查询单条
     */
    @GetMapping ( "/detail" )
    public Result < LogApi > detail ( LogApi log ) {
        return Result.data ( logService.getOne ( Condition.getQueryWrapper ( log ) ) );
    }

    /**
     * 查询多条(分页)
     */
    @GetMapping ( "/list" )
    public Result < IPage < LogApiVo > > list (
            @ApiIgnore @RequestParam Map < String, Object > log ,
            Query query ) {
        IPage < LogApi > pages = logService.page ( Condition.getPage ( query.setDescs ( "create_time" ) ) ,
                                                   Condition.getQueryWrapper ( log ,
                                                                               LogApi.class ) );
        List < LogApiVo > records = pages.getRecords ( )
                                         .stream ( )
                                         .map ( logApi -> {
                                             LogApiVo vo = BeanUtil.copy ( logApi ,
                                                                           LogApiVo.class );
                                             vo.setStrId ( Func.toStr ( logApi.getId ( ) ) );
                                             return vo;
                                         } )
                                         .collect ( Collectors.toList ( ) );
        IPage < LogApiVo > pageVo = new Page <> ( pages.getCurrent ( ) ,
                                                  pages.getSize ( ) ,
                                                  pages.getTotal ( ) );
        pageVo.setRecords ( records );
        return Result.data ( pageVo );
    }

}
