package com.myshop.security.jwt;

import java.time.ZoneId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class SecurityContextRepository implements ServerSecurityContextRepository {
    private final String HEADER_TIMEZONE = "timezone";
    private final String DEFAULT_ZONE = "GMT+7";
    private ReactiveAuthenticationManager authenticationManager;

    public SecurityContextRepository(ReactiveAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7);
            Authentication auth = new PreAuthenticatedAuthenticationToken(authToken, "");
            return this.authenticationManager.authenticate(auth).flatMap(authentication -> {
                String timeZone = request.getHeaders().getFirst(HEADER_TIMEZONE);
                if (StringUtils.isEmpty(timeZone)) {
                    timeZone = DEFAULT_ZONE;
                }

                ((JWTAuthenticationToken) authentication).setTimeZone(ZoneId.of(timeZone));

                String lang = request.getHeaders().getFirst(HttpHeaders.ACCEPT_LANGUAGE);
                ((JWTAuthenticationToken) authentication).setLanguage(lang);
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
                return Mono.empty();
            });
        } else {
            return Mono.empty();
        }
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange swe) {
        if (SecurityContextHolder.getContext() != null) {
            return Mono.just(SecurityContextHolder.getContext());
        } else {
            return Mono.empty();
        }
    }

}