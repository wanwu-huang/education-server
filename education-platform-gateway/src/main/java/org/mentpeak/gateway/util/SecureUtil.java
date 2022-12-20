package org.mentpeak.gateway.util;

import org.mentpeak.common.util.StringUtil;
import org.mentpeak.core.launch.constant.TokenConstant;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * Secure工具类
 *
 * @author lxp
 */
public class SecureUtil {
    private static final String PLATFORM_USER_REQUEST_ATTR = "_PLATFORM_USER_REQUEST_ATTR_";

    private final static String HEADER = TokenConstant.HEADER;
    private final static String BEARER = TokenConstant.BEARER;
    private final static String ACCOUNT = TokenConstant.ACCOUNT;
    private final static String USER_ID = TokenConstant.USER_ID;
    private final static String ROLE_ID = TokenConstant.ROLE_ID;
    private final static String USER_NAME = TokenConstant.USER_NAME;
    private final static String ROLE_NAME = TokenConstant.ROLE_NAME;
    private final static String TENANT_CODE = TokenConstant.TENANT_CODE;
    private final static String CLIENT_ID = TokenConstant.CLIENT_ID;
    private final static Integer AUTH_LENGTH = TokenConstant.AUTH_LENGTH;
    private static String BASE64_SECURITY = Base64.getEncoder()
            .encodeToString(TokenConstant.SIGN_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * 获取Claims
     *
     * @param request request
     * @return Claims
     */
    public static Claims getClaims(HttpServletRequest request) {
        String auth = request.getHeader( SecureUtil.HEADER);
        if ((auth != null) && (auth.length() > AUTH_LENGTH)) {
            String headStr = auth.substring(0,
                    6)
                    .toLowerCase();
            if (headStr.compareTo( SecureUtil.BEARER) == 0) {
                auth = auth.substring(7);
                return SecureUtil.parseJWT( auth);
            }
        }
        return null;
    }

    /**
     * 从Token解析获取Claims对象
     *
     * @param token Mate-Auth获取的token
     * @return Claims
     */
    public static Claims getClaims(String token) {
        Claims claims = null;
        if (StringUtil.isNotBlank(token)) {
            try {
                claims = TokenUtil.getClaims(token);
            } catch (Exception e) {
                throw new RuntimeException("Token已过期！");
            }
        }
        return claims;
    }


    /**
     * 获取请求头
     *
     * @return header
     */
    public static String getHeader() {
        return getHeader(Objects.requireNonNull(WebUtil.getRequest()));
    }

    /**
     * 获取请求头
     *
     * @param request request
     * @return header
     */
    public static String getHeader(HttpServletRequest request) {
        return request.getHeader(HEADER);
    }

    /**
     * 解析jsonWebToken
     *
     * @param jsonWebToken jsonWebToken
     * @return Claims
     */
    public static Claims parseJWT(String jsonWebToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(Base64.getDecoder()
                            .decode(BASE64_SECURITY))
                    .parseClaimsJws(jsonWebToken)
                    .getBody();
        } catch (Exception ex) {
            return null;
        }
    }

}
