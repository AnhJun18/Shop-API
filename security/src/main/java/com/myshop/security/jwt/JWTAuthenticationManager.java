package com.myshop.security.jwt;

import com.myshop.common.http.CodeStatus;
import com.myshop.common.http.ServiceException;
import com.myshop.security.jwt.service.TokenService;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Slf4j
public class JWTAuthenticationManager implements ReactiveAuthenticationManager {

    private final String AUTHORITIES = "authoriti";

    private final Resource jwkPublicJson;
    private final ReactiveJwtDecoder decoder;
    private final TokenService tokenService;

    public JWTAuthenticationManager(Resource jwkPublicJson, TokenService tokenService) throws Exception {
        this.jwkPublicJson = jwkPublicJson;
        RSAPublicKey rsaPublicKey = loadPublicKey();
        decoder = new NimbusReactiveJwtDecoder(rsaPublicKey);
        this.tokenService = tokenService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getPrincipal().toString();
        try {
            return getJwtDecoder().decode(authToken).map(this::extractAuthentication);
        } catch (Exception e) {
            log.error("authenticate ", e);
            throw new ServiceException(CodeStatus.UNAUTHORIZED);
        }
    }

    private JWTAuthenticationToken extractAuthentication(Jwt jwtToken) {
        if (jwtToken.containsClaim("ati")) {
            throw new ServiceException(CodeStatus.UNAUTHORIZED);
        }
        String jti = jwtToken.getClaimAsString(JwtClaimNames.JTI);
        if (!tokenService.isValidToken(jti)) {
            throw new ServiceException(CodeStatus.UNAUTHORIZED);
        }

        List<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;
        List<String> authoritiesString = jwtToken.getClaimAsStringList(AUTHORITIES);
        if (authoritiesString != null) {
            authorities = AuthorityUtils.createAuthorityList((String[])authoritiesString.toArray());
        }
        CustomAuthUser oauth2User = new CustomAuthUser(jwtToken.getSubject(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new JWTAuthenticationToken(oauth2User, oauth2User.getAuthorities(), jti));
        return new JWTAuthenticationToken(oauth2User, oauth2User.getAuthorities(), jti);
    }

    private ReactiveJwtDecoder getJwtDecoder() {
        return decoder;
    }

    private RSAPublicKey loadPublicKey() throws Exception {
        String json = new String(jwkPublicJson.getInputStream().readAllBytes());
        RSAKey rsaJWK = RSAKey.parse(json);
        return rsaJWK.toRSAPublicKey();
    }
}
