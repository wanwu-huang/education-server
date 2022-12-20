package org.mentpeak.common.launch;

import org.mentpeak.common.constant.CommonConstant;
import org.mentpeak.core.launch.service.LauncherService;
import org.mentpeak.core.process.service.AutoService;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Properties;


/**
 * 启动参数拓展
 *
 * @author lxp
 */
@AutoService ( LauncherService.class )
public class LauncherServiceImpl implements LauncherService {

    @Override
    public void launcher (
            SpringApplicationBuilder builder ,
            String appName ,
            String profile ,
            boolean isLocalDev ) {
        Properties props = System.getProperties ( );
        props.setProperty ( "spring.cloud.nacos.discovery.server-addr" ,
                            CommonConstant.nacosAddr ( profile ) );
        props.setProperty ( "spring.cloud.nacos.config.server-addr" ,
                            CommonConstant.nacosAddr ( profile ) );
//		绑定命名空间
        props.setProperty ( "spring.cloud.nacos.discovery.namespace" ,
                            CommonConstant.NACOS_DEV_NAMESPACE );
        props.setProperty ( "spring.cloud.nacos.config.namespace" ,
                            CommonConstant.NACOS_DEV_NAMESPACE );
        props.setProperty ( "spring.cloud.sentinel.transport.dashboard" ,
                            CommonConstant.sentinelAddr ( profile ) );
        props.setProperty ( "spring.cloud.nacos.config.prefix" ,
                            "education" );
    }

}
