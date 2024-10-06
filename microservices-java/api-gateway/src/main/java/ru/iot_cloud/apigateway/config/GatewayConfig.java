package ru.iot_cloud.apigateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.iot_cloud.apigateway.config.constant.ConfigurationConstants;
import ru.iot_cloud.apigateway.security.JwtAuthenticationFilter;

import java.util.function.Function;

import static ru.iot_cloud.apigateway.config.constant.ConfigurationConstants.API_V1;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class GatewayConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Value("${ms.auth.root}")
    private String msAuthRoot;


    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes()
                .route(
                        ConfigurationConstants.AUTH_SERVICE_ID,
                        getRoute(ConfigurationConstants.AUTH_SERVICE_ROOT, msAuthRoot))
                .build();
    }

    private Function<PredicateSpec, Buildable<Route>> getRoute(String root, String uri) {
        return r ->
                r.path(root.concat("/**"))
                        .filters(filterSpec -> getGatewayFilterSpec(filterSpec, root))
                        .uri(uri);
    }

    private GatewayFilterSpec getGatewayFilterSpec(GatewayFilterSpec f, String serviceUri) {
        return f.rewritePath(
                        serviceUri.concat("(?<segment>.*)"), API_V1.concat(serviceUri).concat("${segment}"))
                .filter(jwtAuthenticationFilter);
    }
}
