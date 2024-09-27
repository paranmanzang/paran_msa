package com.paranmanzang.gatewayserver.config;

import com.paranmanzang.gatewayserver.jwt.CustomReactiveAuthenticationManager;
import com.paranmanzang.gatewayserver.Filter.LoginFilter;
import com.paranmanzang.gatewayserver.Filter.LogoutFilter;
import com.paranmanzang.gatewayserver.jwt.CustomAuthenticationFailureHandler;
import com.paranmanzang.gatewayserver.jwt.CustomAuthenticationSuccessHandler;
import com.paranmanzang.gatewayserver.model.repository.UserRepository;
import com.paranmanzang.gatewayserver.oauth.CustomSuccessHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private final CustomSuccessHandler customSuccessHandler;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomAuthenticationFailureHandler failureHandler;
    private final CustomReactiveAuthenticationManager customReactiveAuthenticationManager;
    private final UserRepository userRepository;

    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler, CustomAuthenticationFailureHandler failureHandler, CustomReactiveAuthenticationManager customReactiveAuthenticationManager, CustomSuccessHandler customSuccessHandler, UserRepository userRepository) {
        this.customSuccessHandler = customSuccessHandler;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.customReactiveAuthenticationManager = customReactiveAuthenticationManager;
        this.userRepository = userRepository;
    }

    @Bean
    public AuthenticationWebFilter loginFilter(){
        return new LoginFilter(customReactiveAuthenticationManager, successHandler, failureHandler);
    }

    @Bean
    public LogoutFilter logoutFilter() {
        return new LogoutFilter();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        configureCors(http);
        disableDefaultSecurity(http);
        addLoginFilter(http);
        configureOAuth2(http);
        configureAuthorization(http);
        addLogoutFilter(http);

        return http.build();
    }

    private void configureCors(ServerHttpSecurity http) {
        http.cors(corsCustomizer -> corsCustomizer
                .configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
                    configuration.setMaxAge(3600L); // 1 hour
                    configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));
                    return configuration;
                }));
    }

    private void disableDefaultSecurity(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable) // CSRF 비활성화
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // 폼 로그인 비활성화
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable); // 기본 인증 비활성화
    }


    private void addLogoutFilter(ServerHttpSecurity http) {
       http.addFilterBefore(logoutFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
    }

    private void addLoginFilter(ServerHttpSecurity http) {
        log.info("Adding LoginFilter1");
        http.addFilterAt(loginFilter(), SecurityWebFiltersOrder.AUTHORIZATION);
        log.info("Adding LoginFilter2");
    }

    private void configureOAuth2(ServerHttpSecurity http) {
        http.oauth2Login(oauth2 -> oauth2
                .authenticationSuccessHandler(customSuccessHandler)
        );
    }

    private void configureAuthorization(ServerHttpSecurity http) {
        http.authorizeExchange(auth -> auth
                .pathMatchers("/**").permitAll() // /login 경로는 인증 없이 접근 가능
                .anyExchange().authenticated() // 그 외의 모든 경로는 인증 필요
        );
    }

}