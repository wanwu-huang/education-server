package org.mentpeak.security.sms;

import org.mentpeak.core.log.exception.PlatformApiException;
import org.mentpeak.security.service.PlatformUserDetails;
import org.mentpeak.security.support.PlatformPasswordEncoder;
import org.mentpeak.security.utils.TokenUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author hzl
 * @data 2022年07月06日17:57
 */
public class SMSAccountTokenGranter extends AbstractTokenGranter {
    private static final String GRANT_TYPE = "account";
    private final AuthenticationManager authenticationManager;
    private UserByAccountDetailsService userByPhoneDetailsService;
    private StringRedisTemplate stringRedisTemplate;

    public SMSAccountTokenGranter(
            AuthenticationManager authenticationManager,
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory,
            UserByAccountDetailsService userByPhoneDetailsService,
            StringRedisTemplate stringRedisTemplate) {
        this(authenticationManager,
                tokenServices,
                clientDetailsService,
                requestFactory,
                GRANT_TYPE);
        this.userByPhoneDetailsService = userByPhoneDetailsService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    protected SMSAccountTokenGranter(
            AuthenticationManager authenticationManager,
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory,
            String grantType) {
        super(tokenServices,
                clientDetailsService,
                requestFactory,
                grantType);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<String, String>(tokenRequest.getRequestParameters());
        //参数中的账号
        String account = parameters.get("username");
        String password = parameters.get("password");
        PlatformPasswordEncoder passwordEncoder = new PlatformPasswordEncoder();
        // 从库里查用户
        PlatformUserDetails user = (PlatformUserDetails) userByPhoneDetailsService.loadUserByUsername(account);
        if (user == null) {
            throw new InvalidGrantException("手机号不存在");
        }
        int start = user.getPassword().indexOf("}");
        if (!passwordEncoder.encode(password).matches(user.getPassword().substring(start + 1))) {
//            throw new UsernameNotFoundException(TokenUtil.USER_NOT_FOUND);
            throw new PlatformApiException(TokenUtil.USER_NOT_FOUND);
        }
        Authentication userAuth = new UsernamePasswordAuthenticationToken(user,
                null,
                user.getAuthorities());
        // 关于user.getAuthorities(): 我们的自定义用户实体是实现了
        // org.springframework.security.core.userdetails.UserDetails 接口的, 所以有 user.getAuthorities()
        // 当然该参数传null也行
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client,
                tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request,
                userAuth);
    }
}
