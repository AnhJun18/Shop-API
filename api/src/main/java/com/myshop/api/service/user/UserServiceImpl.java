package com.myshop.api.service.user;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.user.LoginRequest;
import com.myshop.api.payload.request.user.UserRequest;
import com.myshop.api.payload.response.user.LoginResponse;
import com.myshop.api.payload.response.user.UserResponse;
import com.myshop.common.Constants;
import com.myshop.common.http.ApiResponse;
import com.myshop.common.http.CodeStatus;
import com.myshop.common.http.ServiceException;
import com.myshop.common.utils.Utils;
import com.myshop.repositories.user.entities.Account;
import com.myshop.repositories.user.entities.Role;
import com.myshop.repositories.user.entities.Token;
import com.myshop.repositories.user.entities.User;
import com.myshop.repositories.user.repos.AccountRepository;
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
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
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
    @Autowired
    private AccountRepository accountRepository;

    @Value("${jwkFile}")
    private Resource jwkFile;

    public UserServiceImpl(UserRepository userRepository) {
        super(User.class, UserRequest.class, User.class, userRepository);
        this.userRepository = userRepository;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Account account = accountRepository.findAccountByUsername(loginRequest.getUsername());
        if (account == null || account.getId() == null) {
            return LoginResponse.builder().message("Invalid account . Please try again.").status(false).build();
        }
        if (account.isDeleteFlag() == true) {
            return LoginResponse.builder().message("Your account has been temporarily locked, please contact us again").status(false).build();
        }
        if (passwordEncoder.matches(loginRequest.getPassword() + Constants.SALT_DEFAULT, account.getPassword())) {
            return buildTokenResponse(userRepository.findUserByAccount(account));
        } else {
            return LoginResponse.builder().message("Invalid email or password. Please try again.").status(false).build();
        }

    }


    private LoginResponse buildTokenResponse(User user) {
        String jti = UUID.randomUUID().toString();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .jwtID(jti)
                .claim("authorities",user.getRole().getName())
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

        return LoginResponse.builder().user(user).accessToken(accessToken).expiresIn(expireIn).refreshToken(jti).status(true).build();
    }

    @Transactional
    @Override
    public UserResponse registerUser(UserRequest userRequest) {
        Account account = accountRepository.findAccountByUsername(userRequest.getUserName());
        if (account != null && account.getId() > 0) {
            return UserResponse.builder().status(false).message("Your account already exists, please check again").build();
        }
        if (accountRepository.existsByEmail(userRequest.getEmail())) {
            return UserResponse.builder().status(false).message("Email already exists, please check again").build();
        }
        if (userRepository.existsByPhone(userRequest.getPhone())) {
            return UserResponse.builder().status(false).message("Phone already exists, please check again").build();
        }
        Role role = roleRepository.findByName(userRequest.getRoleName());
        if (role == null) {
            return UserResponse.builder().status(false).message("Role is not exists, please check again").build();
        }
        account = Account.builder().username(userRequest.getUserName()).deleteFlag(false)
                .password(passwordEncoder.encode(userRequest.getPassword() + Constants.SALT_DEFAULT)).build();
        User user = User.builder().firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .phone(Utils.normalPhone(userRequest.getPhone()))
                .address(userRequest.getAddress())
                .role(role)
                .account(account)
                .build();
        accountRepository.save(account);
        userRepository.save(user);
        return UserResponse.builder().status(true).message("New account registration is successful").build();
    }


    @Override
    public UserResponse getUserProfile(long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return UserResponse.builder()
                .user(user)
                .role(user.getRole())
                .build();
    }

    @Override
    public ApiResponse<Object> deleteAccountUser(long userID) {
        Account account = accountRepository.findById(userID).orElseThrow();
        account.setDeleteFlag(true);
        accountRepository.save(account);
        return ApiResponse.builder().status(200).message("Account is blocked").data(Mono.just(account)).build();
    }

    @Override
    public Iterable<Account> getAll() {
        return accountRepository.findAllByDeleteFlag(false);
    }
}
