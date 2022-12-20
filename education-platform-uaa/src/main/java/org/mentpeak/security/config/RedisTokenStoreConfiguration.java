
package org.mentpeak.security.config;

import org.mentpeak.core.launch.constant.TokenConstant;
import org.mentpeak.security.support.PlatformJwtTokenEnhancer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import lombok.AllArgsConstructor;

/**
 * Redis TokenStore
 *
 * @author mp
 */
@Configuration
@AllArgsConstructor
public class RedisTokenStoreConfiguration {
    /**
     * redis连接工厂
     */
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 用于存放token
     */
    @Bean
    public TokenStore redisTokenStore ( ) {
        return new RedisTokenStore ( redisConnectionFactory );
    }


    /**
     * 用于生成jwt
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter ( ) {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter ( );
        accessTokenConverter.setSigningKey ( TokenConstant.SIGN_KEY );
        return accessTokenConverter;
    }

    /**
     * 用于扩展jwt
     */
    @Bean
    public TokenEnhancer jwtTokenEnhancer ( ) {
        PlatformJwtTokenEnhancer platformJwtTokenEnhancer = new PlatformJwtTokenEnhancer ( );
        return platformJwtTokenEnhancer;
    }
}
