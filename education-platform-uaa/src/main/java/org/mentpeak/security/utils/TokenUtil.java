package org.mentpeak.security.utils;

import org.mentpeak.common.constant.CommonConstant;
import org.mentpeak.core.launch.constant.TokenConstant;
import org.mentpeak.core.tool.utils.Charsets;
import org.mentpeak.core.tool.utils.StringPool;
import org.mentpeak.core.tool.utils.WebUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;

import java.util.Base64;
import java.util.Calendar;

import lombok.SneakyThrows;

/**
 * 认证工具类
 *
 * @author mp
 */
public class TokenUtil {

    public final static String AVATAR = TokenConstant.AVATAR;
    public final static String ACCOUNT = TokenConstant.ACCOUNT;
    public final static String USER_ID = TokenConstant.USER_ID;
    public final static String ROLE_ID = TokenConstant.ROLE_ID;
    public final static String USER_NAME = TokenConstant.USER_NAME;
    public final static String ROLE_NAME = TokenConstant.ROLE_NAME;
    public final static String TENANT_CODE = TokenConstant.TENANT_CODE;
    public final static String CLIENT_ID = TokenConstant.CLIENT_ID;
    public final static String LICENSE = TokenConstant.LICENSE;
    public final static String LICENSE_NAME = TokenConstant.LICENSE_NAME;
    public final static String REAL_NAME = "real_name";


    public final static String TENANT_HEADER_KEY = "Tenant-Code";
    public final static String DEFAULT_TENANT_CODE = "000000";
    public final static String USER_NOT_FOUND = "用户名或密码错误";
    public final static String ACCOUNT_DISABLED = "该账户已被禁用，请联系管理员!";
    public final static String HEADER_KEY = "Authorization";
    public final static String HEADER_PREFIX = "Basic ";
    public final static String DEFAULT_AVATAR = CommonConstant.DEFAULT_HEAD_URL;

    /**
     * 解码
     */
    @SneakyThrows
    public static String[] extractAndDecodeHeader ( ) {
        String header = WebUtil.getRequest ( )
                               .getHeader ( TokenUtil.HEADER_KEY );
        if ( header == null || ! header.startsWith ( TokenUtil.HEADER_PREFIX ) ) {
            throw new UnapprovedClientAuthenticationException ( "请求头中无client信息" );
        }

        byte[] base64Token = header.substring ( 6 )
                                   .getBytes ( Charsets.UTF_8_NAME );

        byte[] decoded;
        try {
            decoded = Base64.getDecoder ( )
                            .decode ( base64Token );
        } catch ( IllegalArgumentException var7 ) {
            throw new BadCredentialsException ( "Failed to decode basic authentication token" );
        }

        String token = new String ( decoded ,
                                    Charsets.UTF_8_NAME );
        int index = token.indexOf ( StringPool.COLON );
        if ( index == - 1 ) {
            throw new BadCredentialsException ( "Invalid basic authentication token" );
        } else {
            return new String[] { token.substring ( 0 ,
                                                    index ) , token.substring ( index + 1 ) };
        }
    }

    /**
     * 获取请求头中的客户端id
     */
    public static String getClientIdFromHeader ( ) {
        String[] tokens = extractAndDecodeHeader ( );
        assert tokens.length == 2;
        return tokens[ 0 ];
    }

    /**
     * 获取token过期时间(次日凌晨3点)
     *
     * @return expire
     */
    public static int getTokenValiditySecond ( ) {
        Calendar cal = Calendar.getInstance ( );
        cal.add ( Calendar.DAY_OF_YEAR ,
                  1 );
        cal.set ( Calendar.HOUR_OF_DAY ,
                  3 );
        cal.set ( Calendar.SECOND ,
                  0 );
        cal.set ( Calendar.MINUTE ,
                  0 );
        cal.set ( Calendar.MILLISECOND ,
                  0 );
        return ( int ) ( cal.getTimeInMillis ( ) - System.currentTimeMillis ( ) ) / 1000;
    }

    /**
     * 获取refreshToken过期时间
     *
     * @return expire
     */
    public static int getRefreshTokenValiditySeconds ( ) {
        return 60 * 60 * 24 * 15;
    }

}
