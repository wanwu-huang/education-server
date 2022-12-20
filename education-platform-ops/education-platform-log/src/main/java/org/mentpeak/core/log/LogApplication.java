package org.mentpeak.core.log;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;

import org.mentpeak.core.cloud.feign.EnablePlatformFeign;
import org.mentpeak.core.launch.PlatformApplication;
import org.mentpeak.core.launch.constant.AppConstant;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 日志服务
 *
 * @author lxp
 */
@EnablePlatformFeign
@SpringCloudApplication
@ComponentScan ( basePackages = { AppConstant.BASE_PACKAGES } )
@EnableMethodCache ( basePackages = { AppConstant.BASE_PACKAGES } )
@EnableCreateCacheAnnotation
public class LogApplication {

    public static void main ( String[] args ) {
        PlatformApplication.run ( AppConstant.APPLICATION_LOG_NAME ,
                                  LogApplication.class ,
                                  args );
    }

}
