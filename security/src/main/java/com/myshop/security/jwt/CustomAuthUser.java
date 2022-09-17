package com.myshop.security.jwt;

import java.security.Principal;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

public class CustomAuthUser implements Principal {

    private final String userId;
    private final Collection<GrantedAuthority> authorities;

    public CustomAuthUser(String userId, Collection<GrantedAuthority> authorities) {
        this.userId = userId;
        this.authorities = authorities;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return "";
    }

    public String getUserId() {
        return userId;
    }

    public static Mono<CustomAuthUser> getUser() {
        return ReactiveSecurityContextHolder.getContext().map(securityContext -> (CustomAuthUser) securityContext.getAuthentication().getPrincipal());
    }

    public static Mono<JWTAuthenticationToken> getToken() {
        return ReactiveSecurityContextHolder.getContext().map(securityContext -> ((JWTAuthenticationToken) securityContext.getAuthentication()));
    }
}
