package org.mentpeak.gateway;

import org.mentpeak.core.launch.PlatformApplication;
import org.mentpeak.core.launch.constant.AppConstant;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

/**
 * 项目启动
 *
 * @author mp
 */
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"org.mentpeak"})
public class GateWayApplication {

    public static void main(String[] args) {
        PlatformApplication.run(AppConstant.APPLICATION_GATEWAY_NAME,
                GateWayApplication.class,
                args);
    }

}
