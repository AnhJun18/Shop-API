package com.myshop.api.modules.auth_social;

import com.myshop.api.payload.response.user.LoginResponse;
import com.myshop.api.modules.user.UserService;
import lombok.Builder;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Builder
@RestController
@RequestMapping("/api/auth/")
public class AuthSocialController {
    @Autowired
    private UserService userService;
    @Autowired
    private Environment env;
    @Transactional
    @RequestMapping(value = "/oauth2/google")
    public ResponseEntity<LoginResponse> loginGoogle(ServerHttpRequest request) throws ClientProtocolException, IOException {
        String code = request.getQueryParams().get("code").get(0);
//        System.out.println(code);
        LoginResponse loginResponse=userService.loginWithGoogle(code);
        String url= env.getProperty("RESPONSE_URI");
        url+="accessToken="+loginResponse.getAccessToken();
        url+="&refreshToken="+loginResponse.getRefreshToken();
        return  ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, url)
                .build();
    }
}
