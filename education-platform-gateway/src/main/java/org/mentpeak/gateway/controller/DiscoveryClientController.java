package org.mentpeak.gateway.controller;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务发现控制器
 *
 * @author mp
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/discovery")
public class DiscoveryClientController {

    private final DiscoveryClient discoveryClient;

    /**
     * 获取服务实例
     */
    @GetMapping("/instances")
    public Map<String, List<ServiceInstance>> instances() {
        Map<String, List<ServiceInstance>> instances = new HashMap<>(16);
        List<String> services = discoveryClient.getServices();
        services.forEach(s -> {
            List<ServiceInstance> list = discoveryClient.getInstances(s);
            instances.put(s,
                    list);
        });
        return instances;
    }

}
