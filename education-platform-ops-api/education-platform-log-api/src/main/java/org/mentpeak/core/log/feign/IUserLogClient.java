package org.mentpeak.core.log.feign;

import org.mentpeak.core.launch.constant.AppConstant;
import org.mentpeak.core.log.entity.UserLogLogin;
import org.mentpeak.core.tool.api.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author hzl
 * @create 2021-06-22
 */
@FeignClient (
        value = AppConstant.APPLICATION_LOG_NAME,
        fallback = IUserLogClientFallback.class
)
public interface IUserLogClient {
    String API_PREFIX = "/userloglogin";
    String SAVE = API_PREFIX + "/save";

    /**
     * 新增 用户错误登录日志
     */
    @PostMapping ( SAVE )
    public Result save ( @Valid @RequestBody UserLogLogin userLogLogin );
}
