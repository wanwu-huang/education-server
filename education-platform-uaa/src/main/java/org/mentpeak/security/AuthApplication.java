package org.mentpeak.security;


import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;

import org.mentpeak.core.cloud.feign.EnablePlatformFeign;
import org.mentpeak.core.launch.PlatformApplication;
import org.mentpeak.core.launch.constant.AppConstant;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户认证服务器
 *
 * @author mp
 */
@EnablePlatformFeign
//@ComponentScan ( basePackages = { "org.mentpeak" } )
@EnableMethodCache ( basePackages = { AppConstant.BASE_PACKAGES } )
@EnableCreateCacheAnnotation
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})

public class AuthApplication {

    public static void main ( String[] args ) {
        PlatformApplication.run ( AppConstant.APPLICATION_AUTH_NAME ,
                                  AuthApplication.class ,
                                  args );
    }


}
