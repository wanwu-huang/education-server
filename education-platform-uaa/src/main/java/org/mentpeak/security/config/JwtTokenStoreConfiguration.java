package org.mentpeak.security.config;//package org.mentpeak.security.config;
//
//import org.mentpeak.core.launch.constant.TokenConstant;
//import org.mentpeak.security.support.PlatformJwtTokenEnhancer;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.provider.token.TokenEnhancer;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
//
///**
// * JwtTokenStore
// *
// * @author mp
// */
//@Configuration
//@ConditionalOnProperty ( prefix = "platform.security.oauth2", name = "storeType", havingValue = "jwt", matchIfMissing = true )
//public class JwtTokenStoreConfiguration {
//
//    private JwtAccessTokenConverter jwtAccessTokenConverter;
//
//    /**
//     * 使用jwtTokenStore存储token
//     */
//    @Bean
//    public TokenStore jwtTokenStore ( ) {
//        return new JwtTokenStore ( jwtAccessTokenConverter );
//    }
//
//
//}
