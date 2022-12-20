package org.mentpeak.gateway.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * 链路追踪配置
 * @author lxp
 * @date 2022/05/17 16:19
 **/
@Getter
@Setter
@RefreshScope
@Component
@ConfigurationProperties("platform.request")
public class PlatformRequestProperties {
    /**
     * 是否开启日志链路追踪
     */
    private Boolean trace = false;

    /**
     * 是否启用获取IP地址
     */
    private Boolean ip = false;

    /**
     * 是否启用增强模式
     */
    private Boolean enhance = false;
}
