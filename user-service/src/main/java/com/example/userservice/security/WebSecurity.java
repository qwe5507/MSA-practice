package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurity {
    private final Environment env;
    private final UserService userService;
    public static final String ALLOWED_IP_ADDRESS = "172.30.1.50";
    public static final String SUBNET = "/32";
    public static final IpAddressMatcher ALLOWED_IP_ADDRESS_MATCHER = new IpAddressMatcher(ALLOWED_IP_ADDRESS + SUBNET);

    private AuthorizationDecision hasIpAddress(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        log.info("요청 URI : object.getRequest(): {}", object.getRequest().getRemoteAddr());
        log.info("{}", ALLOWED_IP_ADDRESS_MATCHER.matches(object.getRequest()));
        log.info("{}", !(authentication.get() instanceof AnonymousAuthenticationToken));
        return new AuthorizationDecision(ALLOWED_IP_ADDRESS_MATCHER.matches(object.getRequest()));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspect) throws Exception {

        AuthenticationManager authenticationManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));

        http.authorizeHttpRequests(request ->
                                    request
                                            .requestMatchers(PathRequest.toH2Console()).permitAll()
                                            .requestMatchers(new MvcRequestMatcher(introspect, "/**")).access(this::hasIpAddress)
//                                            .anyRequest().authenticated()
                                    )
//                                .permitAll()
//                                .anyRequest().permitAll())
                                .csrf(csrf -> csrf.disable())
                                .headers(header -> header.frameOptions(
                                        frameOptionsConfig -> frameOptionsConfig.disable()
                                ))
                                .addFilter(getAuthenticationFilter(authenticationManager));

        return http.build();
    }
    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, userService, env);
        return authenticationFilter;
    }
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }
}
