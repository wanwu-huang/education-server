package org.mentpeak.gateway.props;

import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.List;

import lombok.Data;

/**
 * 网关路由实例
 *
 * @author MyPC
 */
@Data
public class GatewayRoute {

	private static final long serialVersionUID = 1L;

	List<RouteDefinition> routes;
}
