
package org.mentpeak.security.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.mentpeak.security.service.PlatformClientDetailsServiceImpl;
import org.mentpeak.security.service.SingleLoginTokenServices;
import org.mentpeak.security.sms.SMSAccountTokenGranter;
import org.mentpeak.security.sms.SMSCodeTokenGranter;
import org.mentpeak.security.sms.UserByAccountDetailsService;
import org.mentpeak.security.sms.UserByPhoneDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * ?????????????????????
 *
 * @author mp
 */
@Order(2)
@Configuration
@RequiredArgsConstructor
@EnableAuthorizationServer
public class PlatformAuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {


    private final PlatformClientDetailsServiceImpl platformClientDetailsServiceImpl;
    private final TokenStore redisTokenStore;
    private final JwtAccessTokenConverter jwtAccessTokenConverter;

    private final TokenEnhancer jwtTokenEnhancer;

    private final UserByPhoneDetailsService userByPhoneDetailsService;
    private final UserByAccountDetailsService userByAccountDetailsService;
    private final StringRedisTemplate redisTemplate;
    private final UserDetailsService userDetailsService;

    @Value("${platform.uaa.isSingleLogin}")
    private boolean isSingleLogin;
    private final AuthenticationManager authenticationManager;
    private final DataSource dataSource;
    private final RedisTemplate<String, Object> redisObjectTemplate;

    @Bean
    @Primary
    public PlatformClientDetailsServiceImpl clientDetailService() {
        PlatformClientDetailsServiceImpl clientDetailsService = new PlatformClientDetailsServiceImpl(dataSource);
        clientDetailsService.setRedisTemplate(redisObjectTemplate);
        return clientDetailsService;
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        DefaultTokenServices tokenServices = createDefaultTokenServices();

        // token?????????
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
// ???jwt????????????????????????????????????????????????
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtTokenEnhancer,
                jwtAccessTokenConverter));
        tokenServices.setTokenEnhancer(tokenEnhancerChain);

        endpoints.tokenStore(redisTokenStore)
                .tokenGranter(tokenGranter(endpoints.getClientDetailsService(),
                        tokenServices,
                        endpoints.getAuthorizationCodeServices(),
                        endpoints.getOAuth2RequestFactory()))
                .tokenServices(tokenServices)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .accessTokenConverter(jwtAccessTokenConverter);
    }


    /**
     * ???????????????tokenServices
     *
     * @return DefaultTokenServices
     */
    private DefaultTokenServices createDefaultTokenServices() {
        DefaultTokenServices tokenServices = new SingleLoginTokenServices(isSingleLogin,
                redisTokenStore,
                jwtTokenEnhancer);
        tokenServices.setTokenStore(redisTokenStore);
        // ????????????Token
        tokenServices.setSupportRefreshToken(Boolean.TRUE);
        tokenServices.setReuseRefreshToken(Boolean.FALSE);
        tokenServices.setClientDetailsService(platformClientDetailsServiceImpl);
        addUserDetailsService(tokenServices);
        return tokenServices;
    }

    private void addUserDetailsService(DefaultTokenServices tokenServices) {
        if (userDetailsService != null) {
            PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
            provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(userDetailsService));
            tokenServices.setAuthenticationManager(new ProviderManager(Collections.singletonList(provider)));
        }
    }


    /**
     * ?????????????????????
     */
    @Override
    @SneakyThrows
    public void configure(ClientDetailsServiceConfigurer clients) {
        clients.withClientDetails(platformClientDetailsServiceImpl);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer
                .allowFormAuthenticationForClients()
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }


    /**
     * ?????????spring AuthorizationServerEndpointsConfigurer???????????? copy?????????,
     * ??????????????? TokenGranter, ??????????????????????????????????????????,?????????????????????
     */
    private List<TokenGranter> getDefaultTokenGranters(
            ClientDetailsService clientDetails,
            AuthorizationServerTokenServices tokenServices,
            AuthorizationCodeServices authorizationCodeServices,
            OAuth2RequestFactory requestFactory) {
        List<TokenGranter> tokenGranters = new ArrayList<TokenGranter>();
        tokenGranters.add(new AuthorizationCodeTokenGranter(tokenServices,
                authorizationCodeServices,
                clientDetails,
                requestFactory));
        tokenGranters.add(new RefreshTokenGranter(tokenServices,
                clientDetails,
                requestFactory));
        ImplicitTokenGranter implicit = new ImplicitTokenGranter(tokenServices,
                clientDetails,
                requestFactory);
        tokenGranters.add(implicit);
        tokenGranters.add(
                new ClientCredentialsTokenGranter(tokenServices,
                        clientDetails,
                        requestFactory));
        if (authenticationManager != null) {
            tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager,
                    tokenServices,
                    clientDetails,
                    requestFactory));
        }

        // ??????????????????????????????????????????????????????????????????
        tokenGranters.add(new SMSCodeTokenGranter(authenticationManager,
                tokenServices,
                clientDetails,
                requestFactory,
                userByPhoneDetailsService,
                redisTemplate));

        // ????????????
        tokenGranters.add(new SMSAccountTokenGranter(authenticationManager,
                tokenServices,
                clientDetails,
                requestFactory,
                userByAccountDetailsService,
                redisTemplate));
        return tokenGranters;
    }


    /**
     * ?????? tokenGranter ????????????????????????
     */
    private TokenGranter tokenGranter(
            ClientDetailsService clientDetails,
            AuthorizationServerTokenServices tokenServices,
            AuthorizationCodeServices authorizationCodeServices,
            OAuth2RequestFactory requestFactory) {
        TokenGranter tokenGranter = new TokenGranter() {
            private CompositeTokenGranter delegate;

            @Override
            public OAuth2AccessToken grant(
                    String grantType,
                    TokenRequest tokenRequest) {
                if (delegate == null) {
                    delegate = new CompositeTokenGranter(getDefaultTokenGranters(clientDetails,
                            tokenServices,
                            authorizationCodeServices,
                            requestFactory));
                }
                return delegate.grant(grantType,
                        tokenRequest);
            }
        };

        return tokenGranter;
    }

}
