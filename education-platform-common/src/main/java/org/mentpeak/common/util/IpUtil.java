package org.mentpeak.common.util;/**
 * @author hzl
 * @create 2021-06-22
 */

import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hzl
 * @data 2021年06月22日15:28
 */
public class IpUtil {

    // 本地ip
    private static final String LOCALHOST_IP = "0:0:0:0:0:0:0:1";

    /*
     * 获取请求ip
     * @author hzl
     * @date 2021/6/22 15:29
     * @param null
     * @return null
     */
    public static String getIpAddress ( HttpServletRequest request ) {
        String ip = request.getHeader ( "x-forwarded-for" );
        System.out.println ( "x-forwarded-for ip: " + ip );
        if ( ip != null && ip.length ( ) != 0 && ! "unknown".equalsIgnoreCase ( ip ) ) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if ( ip.indexOf ( "," ) != - 1 ) {
                ip = ip.split ( "," )[ 0 ];
            }
        }
        if ( ip == null || ip.length ( ) == 0 || "unknown".equalsIgnoreCase ( ip ) ) {
            ip = request.getHeader ( "Proxy-Client-IP" );
            System.out.println ( "Proxy-Client-IP ip: " + ip );
        }
        if ( ip == null || ip.length ( ) == 0 || "unknown".equalsIgnoreCase ( ip ) ) {
            ip = request.getHeader ( "WL-Proxy-Client-IP" );
            System.out.println ( "WL-Proxy-Client-IP ip: " + ip );
        }
        if ( ip == null || ip.length ( ) == 0 || "unknown".equalsIgnoreCase ( ip ) ) {
            ip = request.getHeader ( "HTTP_CLIENT_IP" );
            System.out.println ( "HTTP_CLIENT_IP ip: " + ip );
        }
        if ( ip == null || ip.length ( ) == 0 || "unknown".equalsIgnoreCase ( ip ) ) {
            ip = request.getHeader ( "HTTP_X_FORWARDED_FOR" );
            System.out.println ( "HTTP_X_FORWARDED_FOR ip: " + ip );
        }
        if ( ip == null || ip.length ( ) == 0 || "unknown".equalsIgnoreCase ( ip ) ) {
            ip = request.getHeader ( "X-Real-IP" );
            System.out.println ( "X-Real-IP ip: " + ip );
        }
        if ( ip == null || ip.length ( ) == 0 || "unknown".equalsIgnoreCase ( ip ) ) {
            ip = request.getRemoteAddr ( );
            System.out.println ( "getRemoteAddr ip: " + ip );
        }
        //获取本地ip
        if ( LOCALHOST_IP.equals ( ip ) ) {
            try {
                ip = InetAddress.getLocalHost ( )
                                .getHostAddress ( );
            } catch ( Exception e ) {
                // TODO Auto-generated catch block
                e.printStackTrace ( );
            }
            System.out.println ( "getLocal ip: " + ip );
        }
        return ip;
    }
}
