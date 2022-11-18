package com.myshop.security.jwt;

import com.myshop.common.http.filter.OptionFilter;
import com.myshop.security.filter.HeaderFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;


@Slf4j
@Configuration
@EnableWebFluxSecurity
public class JWTSecurityConfig {

    @Value("${jwkPublicFile}")
    private Resource jwkPublicFile;

    @Bean
    public ReactiveAuthenticationManager authenticationManager( ) throws Exception {
        return new JWTAuthenticationManager(jwkPublicFile);
    }

    public AuthenticationWebFilter authenticationWebFilter(ReactiveAuthenticationManager authenticationManager) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager);
        filter.setServerAuthenticationConverter(exchange -> {
            ServerHttpRequest request = exchange.getRequest();
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String authToken = authHeader.substring(7);
                Authentication auth = new PreAuthenticatedAuthenticationToken(authToken,"");
                return Mono.just(auth);
            }

            return Mono.empty();
        });
        return filter;
    }

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http, ReactiveAuthenticationManager authenticationManager) throws Exception {
        return http
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> {
                    swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                })).accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> {
                    swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                }))
                .and()
                .cors()
                .configurationSource(corsConfigurationSource()).and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .requestCache().requestCache(NoOpServerRequestCache.getInstance()).and()
                .addFilterBefore(new OptionFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAfter(new HeaderFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(authenticationWebFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION) // TODO: config here we got twice call authentication
                .headers().cache().disable()
                .and()
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/api/auth/**","/api/report/**","/api/**/**", "/api/address/**", "/api/common/**", "/swagger-ui.html", "/webjars/**", "/api-docs/**", "/uploads/**", "/api/upload/**").permitAll()
                .pathMatchers("/api/product/**").hasAuthority("ROLE_ADMIN")
                .anyExchange().authenticated()
                .and().build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Access- Control-Allow-Headers","Access-Control-Allow-Origin","Access-Control-Request-Method", "Access-Control-Request-Headers","Origin","Cache-Control", "Content-Type", "Authorization"));
        configuration.setAllowedMethods(Arrays.asList("DELETE", "GET", "POST", "PATCH", "PUT"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
