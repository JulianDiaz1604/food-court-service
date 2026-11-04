package com.pragma.foodcourt.infrastructure.configuration.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String ADMINISTRATOR_ROLE = "ADMINISTRATOR";
    private static final String OWNER_ROLE = "OWNER";
    private static final String CLIENT_ROLE = "CLIENT";
    private static final String RESTAURANTS_ENDPOINT = "/api/v1/restaurants";
    private static final String EMPLOYEES_ENDPOINT = "/api/v1/restaurants/*/employees";
    private static final String DISHES_ENDPOINT = "/api/v1/dishes";
    private static final String DISHES_ITEM_ENDPOINT = "/api/v1/dishes/**";
    private static final String[] PUBLIC_ENDPOINTS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    };

    @Bean
    public JwtTokenValidator jwtTokenValidator(@Value("${jwt.secret}") String jwtSecret) {
        return new JwtTokenValidator(jwtSecret);
    }

    // CSRF is disabled on purpose: this is a stateless REST API authenticated with a JWT Bearer
    // token (no cookies, no server-side session), so CSRF — which exploits ambient cookie
    // credentials the browser sends automatically — does not apply here.
    @SuppressWarnings("java:S4502")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtTokenValidator jwtTokenValidator)
            throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(jwtTokenValidator);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.POST, RESTAURANTS_ENDPOINT).hasRole(ADMINISTRATOR_ROLE)
                        .requestMatchers(HttpMethod.GET, RESTAURANTS_ENDPOINT).hasRole(CLIENT_ROLE)
                        .requestMatchers(HttpMethod.POST, EMPLOYEES_ENDPOINT).hasRole(OWNER_ROLE)
                        .requestMatchers(HttpMethod.POST, DISHES_ENDPOINT).hasRole(OWNER_ROLE)
                        .requestMatchers(HttpMethod.PATCH, DISHES_ITEM_ENDPOINT).hasRole(OWNER_ROLE)
                        .requestMatchers(HttpMethod.GET, DISHES_ENDPOINT).hasRole(CLIENT_ROLE)
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
