package org.mentpeak.test;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.mentpeak.core.cloud.feign.EnablePlatformFeign;
import org.mentpeak.core.launch.PlatformApplication;
import org.mentpeak.core.launch.constant.AppConstant;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * 用户启动器
 *
 * @author mp
 */
@EnablePlatformFeign
@SpringCloudApplication
@ComponentScan ( basePackages = { AppConstant.BASE_PACKAGES } )
@EnableMethodCache ( basePackages = { AppConstant.BASE_PACKAGES } )
@EnableCreateCacheAnnotation
public class TestApplication {

    public static void main ( String[] args ) {
        PlatformApplication.run ( "education-test" ,
                TestApplication.class ,
                                  args );
    }

}
