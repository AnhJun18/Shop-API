package com.myshop.security.filter;

import com.myshop.security.jwt.JWTAuthenticationToken;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.util.Objects;

public class HeaderFilter implements WebFilter {
    private final String HEADER_TIMEZONE = "timezone";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JWTAuthenticationToken) {
            JWTAuthenticationToken token = (JWTAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            if (headers.containsKey(HttpHeaders.ACCEPT_LANGUAGE)) {
                token.setLanguage(headers.getFirst(HttpHeaders.ACCEPT_LANGUAGE));
            }

            if (headers.containsKey(HEADER_TIMEZONE) && !StringUtils.isEmpty(headers.getFirst(HEADER_TIMEZONE))) {
                token.setTimeZone(ZoneId.of(Objects.requireNonNull(headers.getFirst(HEADER_TIMEZONE))));
            }
        }

        return chain.filter(exchange);
    }
}
