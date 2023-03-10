package org.mentpeak.security.sms;

import org.mentpeak.core.tool.utils.StringUtil;
import org.mentpeak.security.constant.AuthConstant;
import org.mentpeak.security.service.PlatformUserDetails;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

public class SMSCodeTokenGranter extends AbstractTokenGranter {
    private static final String GRANT_TYPE = "sms_code";
    private final AuthenticationManager authenticationManager;
    private UserByPhoneDetailsService userByPhoneDetailsService;
    private StringRedisTemplate stringRedisTemplate;


    public SMSCodeTokenGranter (
            AuthenticationManager authenticationManager ,
            AuthorizationServerTokenServices tokenServices ,
            ClientDetailsService clientDetailsService ,
            OAuth2RequestFactory requestFactory ,
            UserByPhoneDetailsService userByPhoneDetailsService ,
            StringRedisTemplate stringRedisTemplate ) {
        this ( authenticationManager ,
               tokenServices ,
               clientDetailsService ,
               requestFactory ,
               GRANT_TYPE );
        this.userByPhoneDetailsService = userByPhoneDetailsService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    protected SMSCodeTokenGranter (
            AuthenticationManager authenticationManager ,
            AuthorizationServerTokenServices tokenServices ,
            ClientDetailsService clientDetailsService ,
            OAuth2RequestFactory requestFactory ,
            String grantType ) {
        super ( tokenServices ,
                clientDetailsService ,
                requestFactory ,
                grantType );
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication (
            ClientDetails client ,
            TokenRequest tokenRequest ) {
        Map < String, String > parameters = new LinkedHashMap < String, String > ( tokenRequest.getRequestParameters ( ) );
        //?????????????????????
        String userPhone = parameters.get ( "userPhone" );
        //???????????????????????????
        String smsCode = parameters.get ( "smsCode" );
        // ??????????????????
        PlatformUserDetails user = ( PlatformUserDetails ) userByPhoneDetailsService.loadUserByUsername ( userPhone );
        if ( user == null ) {
            throw new InvalidGrantException ( "??????????????????" );
        }

        // ???????????????
        String smsRedisKey = AuthConstant.SMS_CODE_REDIS_PREFIX + userPhone;
        String smsCodeCached = stringRedisTemplate.opsForValue ( )
                                                  .get ( smsRedisKey );
        if ( StringUtil.isBlank ( smsCodeCached ) ) {
            throw new InvalidGrantException ( "?????????????????????????????????" );
        }
        if ( ! smsCode.equals ( smsCodeCached ) ) {
            throw new InvalidGrantException ( "??????????????????" );
        }
        Authentication userAuth = new UsernamePasswordAuthenticationToken ( user ,
                                                                            null ,
                                                                            user.getAuthorities ( ) );
        // ??????user.getAuthorities(): ??????????????????????????????????????????
        // org.springframework.security.core.userdetails.UserDetails ?????????, ????????? user.getAuthorities()
        // ??????????????????null??????
        ( ( AbstractAuthenticationToken ) userAuth ).setDetails ( parameters );
        OAuth2Request storedOAuth2Request = getRequestFactory ( ).createOAuth2Request ( client ,
                                                                                        tokenRequest );
        return new OAuth2Authentication ( storedOAuth2Request ,
                                          userAuth );
    }
}
