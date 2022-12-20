package org.mentpeak.core.log.feign;/**
 * @author hzl
 * @create 2021-06-22
 */

import org.mentpeak.core.log.entity.UserLogLogin;
import org.mentpeak.core.tool.api.Result;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

/**
 * @author hzl
 * @data 2021年06月22日16:48
 */
@Component
public class IUserLogClientFallback implements IUserLogClient {

    @Override
    public Result save ( @Valid UserLogLogin userLogLogin ) {
        return Result.fail ( "保存用户登录信息失败" );
    }
}
