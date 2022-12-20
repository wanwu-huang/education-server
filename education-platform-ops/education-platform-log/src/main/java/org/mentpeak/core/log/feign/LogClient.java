package org.mentpeak.core.log.feign;


import org.mentpeak.core.log.model.LogApi;
import org.mentpeak.core.log.model.LogError;
import org.mentpeak.core.log.model.LogUsual;
import org.mentpeak.core.log.service.ILogApiService;
import org.mentpeak.core.log.service.ILogErrorService;
import org.mentpeak.core.log.service.ILogUsualService;
import org.mentpeak.core.tool.api.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;


/**
 * 日志服务Feign实现类
 */
@RestController
@AllArgsConstructor
public class LogClient implements ILogClient {

    ILogUsualService usualLogService;

    ILogApiService apiLogService;

    ILogErrorService errorLogService;

    @Override
    @PostMapping ( API_PREFIX + "/saveUsualLog" )
    public Result < Boolean > saveUsualLog ( @RequestBody LogUsual log ) {
        log.setParams ( log.getParams ( )
                           .replace ( "&amp;" ,
                                      "&" ) );
        return Result.data ( usualLogService.save ( log ) );
    }

    @Override
    @PostMapping ( API_PREFIX + "/saveApiLog" )
    public Result < Boolean > saveApiLog ( @RequestBody LogApi log ) {
        log.setParams ( log.getParams ( )
                           .replace ( "&amp;" ,
                                      "&" ) );
        return Result.data ( apiLogService.save ( log ) );
    }

    @Override
    @PostMapping ( API_PREFIX + "/saveErrorLog" )
    public Result < Boolean > saveErrorLog ( @RequestBody LogError log ) {
        log.setParams ( log.getParams ( )
                           .replace ( "&amp;" ,
                                      "&" ) );
        return Result.data ( errorLogService.save ( log ) );
    }
}
