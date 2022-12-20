package org.mentpeak.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

/**
 * 自定义登录成功配置
 *
 * @author mp
 */
@Configuration
@AllArgsConstructor
@EnableResourceServer
public class PlatformResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    /**
     * 自定义登录成功处理器
     */
    private AuthenticationSuccessHandler appLoginInSuccessHandler;

    @Override
    @SneakyThrows
    public void configure ( HttpSecurity http ) {
        http.headers ( )
            .frameOptions ( )
            .disable ( );
        http.formLogin ( )
            .successHandler ( appLoginInSuccessHandler )
            .and ( )
            .authorizeRequests ( )
            .antMatchers ( "/actuator/**" ,
                           "/token/**" ,
                           "/auth/**",
                           "/mobile/**" ,
                           "/oauth/**",
                           "/v2/api-docs" ,
                           "/v2/api-docs-ext" )
            .permitAll ( )
            .anyRequest ( )
            .authenticated ( )
            .and ( )
            .csrf ( )
            .disable ( );
    }

}
