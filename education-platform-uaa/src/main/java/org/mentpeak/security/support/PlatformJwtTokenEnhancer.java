
package org.mentpeak.security.support;

import org.mentpeak.security.service.PlatformUserDetails;
import org.mentpeak.security.utils.TokenUtil;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * jwt返回参数增强
 *
 * @author mp
 */
public class PlatformJwtTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance (
            OAuth2AccessToken accessToken ,
            OAuth2Authentication authentication ) {
        PlatformUserDetails principal = ( PlatformUserDetails ) authentication.getUserAuthentication ( )
                                                                              .getPrincipal ( );
        // 对于客户端鉴权模式，直接返回token
        if ( authentication.getUserAuthentication ( ) == null ) {
            return accessToken;
        }
        Map < String, Object > info = new HashMap <> ( 16 );
        info.put ( TokenUtil.CLIENT_ID ,
                   TokenUtil.getClientIdFromHeader ( ) );
        info.put ( TokenUtil.USER_ID ,
                   principal.getUserId ( ) );
        info.put ( TokenUtil.ROLE_ID ,
                   principal.getRoleId ( ) );
        info.put ( TokenUtil.TENANT_CODE ,
                   principal.getTenantCode ( ) );
        info.put ( TokenUtil.ACCOUNT ,
                   principal.getAccount ( ) );
        info.put ( TokenUtil.USER_NAME ,
                   principal.getName ( ) );
        info.put ( TokenUtil.ROLE_NAME ,
                   principal.getRoleName ( ) );
        info.put ( TokenUtil.AVATAR ,
                   principal.getAvatar ( ) );
        info.put ( TokenUtil.REAL_NAME ,
                principal.getRealName ( ) );
        info.put ( TokenUtil.LICENSE ,
                   TokenUtil.LICENSE_NAME );
        ( ( DefaultOAuth2AccessToken ) accessToken ).setAdditionalInformation ( info );
        return accessToken;
    }
}
