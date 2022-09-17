/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.myshop.security.jwt;

import java.security.Principal;
import java.time.ZoneId;
import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * An implementation of an {@link AbstractAuthenticationToken}
 * that represents an OAuth 2.0 {@link Authentication}.
 * <p>
 * The {@link Authentication} associates an {@link OAuth2User} {@code Principal}
 * which the End-User ({@code Principal}) granted authorization to
 * so that it can access it's protected resources at the UserInfo Endpoint.
 *
 * @author Joe Grandja
 * @since 5.0
 * @see AbstractAuthenticationToken
 * @see OAuth2User
 */
public class JWTAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    private final Principal principal;
    private final String tokenId;
    private long expireIn;
    private ZoneId timeZone = ZoneId.of("UTC+7");
    private String language = "vi-VN";

    /**
     * Constructs an {@code OAuth2AuthenticationToken} using the provided parameters.
     *
     * @param principal the user {@code Principal} registered with the OAuth 2.0 Provider
     * @param authorities the authorities granted to the user
     */
    public JWTAuthenticationToken(Principal principal,
                                  Collection<? extends GrantedAuthority> authorities, String tokenId) {
        super(authorities);
        Assert.notNull(principal, "principal cannot be null");
        this.principal = principal;
        this.tokenId = tokenId;
        this.setAuthenticated(true);
    }

    @Override
    public Principal getPrincipal() {
        return this.principal;
    }

    @Override
    public Object getCredentials() {
        // Credentials are never exposed (by the Provider) for an OAuth2 User
        return "";
    }

    public String getTokenId() {
        return tokenId;
    }

    public long getExpireIn() {
        return expireIn;
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        if (!StringUtils.isEmpty(language)) {
            this.language = language;
        }
    }
}
