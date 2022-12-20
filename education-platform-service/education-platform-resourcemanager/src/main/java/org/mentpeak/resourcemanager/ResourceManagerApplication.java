package org.mentpeak.resourcemanager;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.mentpeak.core.cloud.feign.EnablePlatformFeign;
import org.mentpeak.core.launch.PlatformApplication;
import org.mentpeak.core.launch.constant.AppConstant;
import org.springframework.beans.factory.annotation.Value;
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
public class ResourceManagerApplication {

    public static void main ( String[] args ) {
        PlatformApplication.run ( "platform-resourcemanager" ,
                                  ResourceManagerApplication.class ,
                                  args );
    }

}
