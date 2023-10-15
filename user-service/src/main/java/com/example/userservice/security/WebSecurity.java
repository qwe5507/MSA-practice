package com.example.userservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {
    private final Environment env;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspect) throws Exception {

        AuthenticationManager authenticationManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));

        http.authorizeHttpRequests(request ->
                                    request.requestMatchers(PathRequest.toH2Console()).permitAll()
                                            .requestMatchers(new MvcRequestMatcher(introspect, "/**")).permitAll()
                                            .requestMatchers(new IpAddressMatcher("localhost")).permitAll()
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
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager);
        return authenticationFilter;
    }
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }
}
