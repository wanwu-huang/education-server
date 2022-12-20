package org.mentpeak.gateway.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 权限过滤
 *
 * @author lxp
 */
@Data
@RefreshScope
@ConfigurationProperties("platform.secure.url")
@Component
public class AuthProperties {

    /**
     * 放行API集合
     */
    private final List<String> excludePatterns = new ArrayList<>();

}
