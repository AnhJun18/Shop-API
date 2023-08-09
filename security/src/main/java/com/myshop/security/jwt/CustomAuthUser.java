package com.myshop.security.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Collection;

public class CustomAuthUser implements Principal {

    private final String email;
    private final Collection<GrantedAuthority> authorities;

    public CustomAuthUser(String email, Collection<GrantedAuthority> authorities) {
        this.email = email;
        this.authorities = authorities;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return "";
    }

    public String getEmail() {
        return email;
    }

    public static Mono<CustomAuthUser> getUser() {
        return ReactiveSecurityContextHolder.getContext().map(securityContext -> (CustomAuthUser) securityContext.getAuthentication().getPrincipal());
    }

    public static Mono<JWTAuthenticationToken> getToken() {
        return ReactiveSecurityContextHolder.getContext().map(securityContext -> ((JWTAuthenticationToken) securityContext.getAuthentication()));
    }
}
