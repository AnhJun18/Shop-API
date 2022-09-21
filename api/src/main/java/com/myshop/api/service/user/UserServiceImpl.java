package com.myshop.api.service.user;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.user.LoginRequest;
import com.myshop.api.payload.request.user.UserRequest;
import com.myshop.api.payload.response.user.AbilityResponse;
import com.myshop.api.payload.response.user.LoginResponse;
import com.myshop.api.payload.response.user.UserResponse;
import com.myshop.common.Constants;
import com.myshop.common.http.CodeStatus;
import com.myshop.common.http.ServiceException;
import com.myshop.common.utils.Utils;
import com.myshop.repositories.user.entities.Role;
import com.myshop.repositories.user.entities.Token;
import com.myshop.repositories.user.entities.User;
import com.myshop.repositories.user.repos.RoleRepository;
import com.myshop.repositories.user.repos.TokenRepository;
import com.myshop.repositories.user.repos.UserRepository;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Transactional
@Service
public class UserServiceImpl extends CRUDBaseServiceImpl<User, UserRequest, User, Long> implements UserService {

    private final long expireIn = Duration.ofHours(8).toSeconds();

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Value("${jwkFile}")
    private Resource jwkFile;

    public UserServiceImpl(UserRepository userRepository) {
        super(User.class, UserRequest.class, User.class, userRepository);
        this.userRepository = userRepository;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        int count = 4;
        User user = userRepository.findUserByUsername(loginRequest.getUsername());
        if (user == null || user.getId() == null) {
            return LoginResponse.builder().message("Invalid email . Please try again.").errorCode(10).status(false).build();
        }
        if (user.getDeleteFlag() == 0) {
            return LoginResponse.builder().message("Your account has been temporarily locked, please contact us again").errorCode(12).status(false).build();
        }
        if (passwordEncoder.matches(loginRequest.getPassword() + user.getSalt(), user.getPassword())) {
            return buildTokenResponse(user);
        } else {
            return LoginResponse.builder().message("Invalid email or password. Please try again.").errorCount(count)
                    .errorCode(11).status(false).build();
        }
    }


    private LoginResponse buildTokenResponse(User user) {
        String jti = UUID.randomUUID().toString();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .jwtID(jti)
                .expirationTime(new Date(Instant.now().plusSeconds(expireIn).toEpochMilli()))
                .build();
        String accessToken;
        try {
            RSAKey rsaJWK = RSAKey.parse(new String(jwkFile.getInputStream().readAllBytes()));
            JWSSigner signer = new RSASSASigner(rsaJWK);
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                    claimsSet);
            signedJWT.sign(signer);
            accessToken = signedJWT.serialize();
        } catch (Exception e) {
            throw new ServiceException(CodeStatus.INTERNAL_ERROR);
        }
        tokenRepository.save(Token.builder().userId(user.getId()).tokenId(jti).expiredTime(System.currentTimeMillis() + expireIn).build());
        List<AbilityResponse> abilities = new ArrayList<>();
        if (user.getRole().getName().equals("admin")) {
            abilities.add(AbilityResponse.builder().action("manage").subject("all").build());
        } else {
            abilities.add(AbilityResponse.builder().action("view").subject("auth").build());
            abilities.add(AbilityResponse.builder().action("view").subject("dashboard").build());
        }
        return LoginResponse.builder().user(user).ability(abilities).accessToken(accessToken).expiresIn(expireIn).refreshToken(jti).status(true).build();
    }

    @Override
    public UserResponse registerUser(UserRequest userRequest) {
        User user = userRepository.findUserByUsername(userRequest.getUserName());
        if (user != null && user.getId() > 0) {
            return UserResponse.builder().status(false).message("Your admin account already exists, please check again").build();
        }
        Role role = roleRepository.findByName(userRequest.getRoleName());
        user = User.builder().role(role).username(userRequest.getUserName()).salt(Constants.SALT_DEFAULT)
                .fullName(userRequest.getFirstName() + ' ' + userRequest.getLastName())
                .phone(Utils.normalPhone(userRequest.getPhone()))
                .email(userRequest.getEmail())
                .address(userRequest.getAddress())
                .password(passwordEncoder.encode(userRequest.getPassword() + Constants.SALT_DEFAULT)).deleteFlag(1).build();
        userRepository.save(user);
        return UserResponse.builder().status(true).message("New admin account registration is successful").build();
    }


    @Override
    public UserResponse getUserProfile(long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return UserResponse.builder()
                .user(user)
                .role(user.getRole())
                .build();
    }
}
