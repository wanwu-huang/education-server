package org.mentpeak.swagger.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author lxp
 * @date 2022/05/17 14:55
 **/
@Configuration
@Slf4j
public class DefaultViewConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        System.out.println("设置默认doc.html......");
        registry.addViewController("/").setViewName("redirect:doc.html");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
}
