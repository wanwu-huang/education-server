package org.mentpeak.gateway.provider;

import com.alibaba.fastjson.JSONObject;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.util.HashMap;
import java.util.Map;

import reactor.core.publisher.Mono;

/**
 * 请求响应返回
 */
public class ResponseProvider {

	/**
	 * 成功
	 *
	 * @param message 信息
	 * @return
	 */
	public static Map<String, Object> success(String message) {
		return response(200, message);
	}

	/**
	 * 失败
	 *
	 * @param message 信息
	 * @return
	 */
	public static Map<String, Object> fail(String message) {
		return response(400, message);
	}

	/**
	 * 未授权
	 *
	 * @param message 信息
	 * @return
	 */
	public static Map<String, Object> unAuth(String message) {
		return response(401, message);
	}

	/**
	 * 服务器异常
	 *
	 * @param message 信息
	 * @return
	 */
	public static Map<String, Object> error(String message) {
		return response(500, message);
	}

	/**
	 * 构建返回的JSON数据格式
	 *
	 * @param status  状态码
	 * @param message 信息
	 * @return
	 */
	public static Map<String, Object> response(int status, String message) {
		Map<String, Object> map = new HashMap<>(16);
		map.put("code", status);
		map.put("msg", message);
		map.put("data", null);
		return map;
	}

	/**
	 * 设置webflux模型响应
	 *
	 * @param response    ServerHttpResponse
	 * @param contentType content-type
	 * @param status      http状态码
	 * @param value       响应内容
	 * @return Mono<Void>
	 */
	public static Mono<Void> webFluxResponseWriter(
			ServerHttpResponse response,
			String contentType,
			HttpStatus status,
			Object value) {
		response.setStatusCode(status);
		response.getHeaders()
				.add(HttpHeaders.CONTENT_TYPE,
						contentType);
		Map<String, Object> result = response(status.value(),
				value.toString());
		DataBuffer dataBuffer = response.bufferFactory()
				.wrap(JSONObject.toJSONString(result)
						.getBytes());
		return response.writeWith(Mono.just(dataBuffer));
	}
}
