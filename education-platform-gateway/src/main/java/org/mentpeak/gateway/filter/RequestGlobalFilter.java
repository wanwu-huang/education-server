package org.mentpeak.gateway.filter;

import org.mentpeak.core.redis.core.RedisService;
import org.mentpeak.gateway.props.AuthProperties;
import org.mentpeak.gateway.provider.AuthProvider;
import org.mentpeak.gateway.provider.ResponseProvider;
import org.mentpeak.gateway.util.SecureUtil;
import org.mentpeak.gateway.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;


/**
 * <p>
 * 全局拦截器，作用所有的微服务
 * <p>
 * 1. 对请求头中参数进行处理 from 参数进行清洗
 * 2. 重写StripPrefix = 1,支持全局
 *
 * @author lengleng
 */
@Slf4j
@Component
@AllArgsConstructor
public class RequestGlobalFilter implements GlobalFilter, Ordered {


    private final RedisService redisService;
    /**
     * 索引自1开头检索，跳过第一个字符就是检索的字符的问题
     */
    public static final int FROM_INDEX = 1;

    @Autowired
    private AuthProperties authProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    /**
     * Process the Web request and (optionally) delegate to the next
     * {@code WebFilter} through the given {@link GatewayFilterChain}.
     *
     * @param exchange the current server exchange
     * @param chain    provides a way to delegate to the next filter
     * @return {@code Mono<Void>} to indicate when request processing is complete
     */
    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {
        log.info("exchange:{}",exchange.getRequest().getURI());
        // 1. 清洗请求头中from 参数
        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .headers(httpHeaders -> httpHeaders.remove("X"))
                .build();

        // 2. 重写StripPrefix
        addOriginalRequestUrl(exchange,
                request.getURI());
        String rawPath = request.getURI()
                .getRawPath();
        String newPath = "/" + Arrays.stream(StringUtils.tokenizeToStringArray(rawPath,
                "/"))
                .skip(1L)
                .collect(Collectors.joining("/"));
        ServerHttpRequest newRequest = request.mutate()
                .path(newPath)
                .build();
        exchange.getAttributes()
                .put(GATEWAY_REQUEST_URL_ATTR,
                        newRequest.getURI());
        log.info("exchange:{}",exchange.getRequest().getURI());
        boolean skip = isSkip(newPath);
        if (!skip) {
            // 验证token是否有效
            ServerHttpResponse resp = exchange.getResponse();
            String headerToken = exchange.getRequest()
                    .getHeaders()
                    .getFirst("platform-auth");
            if (headerToken == null) {
                return unauthorized(resp,
                        "没有携带Token信息！");
            }
            String token = TokenUtil.getToken(headerToken);
            Claims claims = SecureUtil.getClaims( token);
            if (claims == null) {
                return unauthorized(resp,
                        "token已过期或验证不正确！");
            }

            // 判断token是否存在于redis,对于只允许一台设备场景适用。
            // 如只允许一台设备登录，需要在登录成功后，查询key是否存在，如存在，则删除此key，提供思路。
            boolean hasKey = redisService.hasKey("auth:" + token);
            log.debug("查询token是否存在: " + hasKey);
            if (!hasKey) {
                return unauthorized(resp,
                        "登录超时，请重新登录");
            }
        }

        log.info("exchange:{}",exchange.getRequest().getURI());
        return chain.filter(exchange.mutate()
                .request(newRequest.mutate()
                        .build())
                .build());
    }

    private boolean isSkip(String path) {
        List<String> excludePatterns = authProperties.getExcludePatterns();
        return ( AuthProvider.getDefaultSkipUrl().stream().anyMatch( pattern -> antPathMatcher.match( pattern, path)))
                || (excludePatterns.stream()
                .map(url -> url.replace("/**",
                        ""))
                .anyMatch(path::startsWith));
    }


    private Mono<Void> unauthorized(
            ServerHttpResponse resp,
            String msg) {
        return ResponseProvider.webFluxResponseWriter( resp,
                                                       "application/json;charset=UTF-8",
                                                       HttpStatus.UNAUTHORIZED,
                                                       msg);
    }

    /**
     * 移除模块前缀
     *
     * @param path 路径
     * @return String
     */
    private String replacePrefix(String path) {
        if (path.startsWith("platform") | path.startsWith("test")) {
            return path.substring(path.indexOf("/",
                    FROM_INDEX));
        }
        return path;
    }


    @Override
    public int getOrder() {
        return -1000;
    }

}
