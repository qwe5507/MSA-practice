package com.example.apigatewayservice.filter;

import io.micrometer.core.annotation.Timed;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {
    private final Environment env;

    public GlobalFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    @Override
    @Timed("gateway.apply")
    public GatewayFilter apply(final Config config) {
        // Custom Pre Filter

        return ((exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();
            final ServerHttpResponse response = exchange.getResponse();

            log.info("Global Filter baseMessage: {}", config.getBaseMessage());

            log.info("token.secret: {}", env.getProperty("token.secret"));

            if (config.isPreLogger()) {
                log.info("Global Filter Start: request id => {}", request.getId());
            }
            //Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("Global Filter End: response code => {}", response.getStatusCode());
                }
            }));
        });
    }

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
