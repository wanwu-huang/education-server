package org.mentpeak.gateway.filter;

import org.mentpeak.common.constant.CommonConstant;
import org.mentpeak.common.util.StringUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 打印请求和响应简要日志
 * @author MyPC
 */
@Slf4j
@Component
@AllArgsConstructor
public class RequestLogFilter implements GlobalFilter, Ordered {

	private static final String START_TIME = "startTime";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String requestUrl = exchange.getRequest().getURI().getRawPath();
		String traceId = exchange.getRequest().getHeaders().getFirst(CommonConstant.MIC_TRACE_ID);
		// 构建成一条长 日志，避免并发下日志错乱
		StringBuilder beforeReqLog = new StringBuilder(300);
		// 日志参数
		List<Object> beforeReqArgs = new ArrayList<>();
		beforeReqLog.append("\n\n================ Gateway Request Start  ================\n");
		// 打印路由
		beforeReqLog.append("===> {}: {}\n");
		// 参数
		String requestMethod = exchange.getRequest().getMethodValue();
		beforeReqArgs.add(requestMethod);
		beforeReqArgs.add(requestUrl);

		// 打印请求头
		HttpHeaders headers = exchange.getRequest().getHeaders();
		headers.forEach((headerName, headerValue) -> {
			beforeReqLog.append("===Headers===  {}: {}\n");
			beforeReqArgs.add(headerName);
			beforeReqArgs.add(StringUtil.join(headerValue));
		});
		beforeReqLog.append("================ Gateway Request End =================\n");
		// 打印执行时间
		log.info(beforeReqLog.toString(), beforeReqArgs.toArray());

		exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			ServerHttpResponse response = exchange.getResponse();
			Long startTime = exchange.getAttribute(START_TIME);
			long executeTime = 0L;
			if (startTime != null) {
				executeTime = (System.currentTimeMillis() - startTime);
			}

			// 构建成一条长 日志，避免并发下日志错乱
			StringBuilder responseLog = new StringBuilder(300);
			// 日志参数
			List<Object> responseArgs = new ArrayList<>();
			responseLog.append("\n\n================ Gateway Response Start  ================\n");
			// 打印路由 200 get: /服务名*/xxx/xxx
			responseLog.append("<=== {} {}: {}: {}\n");
			// 参数
			responseArgs.add(response.getStatusCode().value());
			responseArgs.add(requestMethod);
			responseArgs.add(requestUrl);
			responseArgs.add(executeTime + "ms");

			// 打印请求头
			HttpHeaders httpHeaders = response.getHeaders();
			httpHeaders.forEach((headerName, headerValue) -> {
				responseLog.append("===Headers===  {}: {}\n");
				responseArgs.add(headerName);
				responseArgs.add(StringUtil.join(headerValue));
			});

			responseLog.append("================  Gateway Response End  =================\n");
			// 打印执行时间
			log.info(responseLog.toString(), responseArgs.toArray());
		}));
	}

	@Override
	public int getOrder() {
		return -2000;
	}
}
