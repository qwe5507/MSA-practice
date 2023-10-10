package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspect) throws Exception {
        http.authorizeHttpRequests(request -> request.requestMatchers(PathRequest.toH2Console()).permitAll()
                                .requestMatchers(new MvcRequestMatcher(introspect, "/users/**")).permitAll())
                                .csrf(csrf -> csrf.disable())
                                .headers(header -> header.frameOptions(
                                        frameOptionsConfig -> frameOptionsConfig.disable()
                                ));

        return http.build();
    }
}
