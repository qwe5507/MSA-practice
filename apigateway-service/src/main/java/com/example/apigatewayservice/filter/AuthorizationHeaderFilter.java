package com.example.apigatewayservice.filter;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final Environment env;
    private JwtParser jwtParser;
    public AuthorizationHeaderFilter(final Environment env) {
        super(AuthorizationHeaderFilter.Config.class);
        this.env = env;
        final JwtParserBuilder jwtParserBuilder = Jwts.parserBuilder()
                .setSigningKey(env.getProperty("token.secret"));
        jwtParser = jwtParserBuilder.build();
    }

    @Override
    public GatewayFilter apply(final Config config) {
        // Custom Pre Filter
        return ((exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer ", "");

            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            //Custom Post Filter
            return chain.filter(exchange);
        });
    }

    private boolean isJwtValid(final String jwt) {
        boolean returnValue = true;

        String subject = null;

        log.info("isJwtValid jwt: {}", jwt);
        try {
            subject = jwtParser.parseClaimsJws(jwt).getBody().getSubject();
            log.info("isJwtValid subject: {}", subject);
        } catch (Exception ex) {
            log.error("{}", ex);
            returnValue = false;
        }

        if (subject == null || subject.isBlank()) {
            returnValue = false;
        }

        return returnValue;
    }

    private Mono<Void> onError(final ServerWebExchange exchange, final String err, final HttpStatus httpStatus) {
        final ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }

    public static class Config {

    }
}
