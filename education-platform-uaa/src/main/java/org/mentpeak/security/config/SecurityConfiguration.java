package org.mentpeak.security.config;

import org.mentpeak.security.support.PlatformPasswordEncoderFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

/**
 * Security配置
 *
 * @author mpx
 */
@Configuration
@AllArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Bean
	@Override
	@SneakyThrows
	public AuthenticationManager authenticationManagerBean() {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PlatformPasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Override
	@SneakyThrows
	protected void configure(HttpSecurity http) {
		http.httpBasic()
				.and()
				.csrf()
                .and()
                .formLogin()
				.disable();
	}

}
