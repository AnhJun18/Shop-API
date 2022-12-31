package com.myshop.api.service.user;

import com.google.api.client.util.DateTime;
import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.config.NullAwareBeanUtilsBean;
import com.myshop.api.payload.request.user.*;
import com.myshop.api.payload.response.user.LoginResponse;
import com.myshop.api.payload.response.user.PasswordResponse;
import com.myshop.api.payload.response.user.UserResponse;
import com.myshop.api.service.email.EmailSenderService;
import com.myshop.common.Constants;
import com.myshop.common.http.ApiResponse;
import com.myshop.common.http.CodeStatus;
import com.myshop.common.http.ServiceException;
import com.myshop.common.utils.RandomUtils;
import com.myshop.common.utils.Utils;
import com.myshop.repositories.user.entities.*;
import com.myshop.repositories.user.repos.*;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Transactional
@Service
public class UserServiceImpl extends CRUDBaseServiceImpl<UserInfo, UserRequest, UserInfo, Long> implements UserService {

    private final long expireIn = Duration.ofHours(1).toSeconds();
    private final long expireInRefresh = Duration.ofHours(10).toMillis();

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Value("${jwkFile}")
    private Resource jwkFile;

    public UserServiceImpl(UserRepository userRepository) {
        super(UserInfo.class, UserRequest.class, UserInfo.class, userRepository);
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


    private LoginResponse buildTokenResponse(UserInfo userInfo) {
        String jti = UUID.randomUUID().toString();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userInfo.getId().toString())
                .jwtID(jti)
                .claim("authorities", userInfo.getAccount().getRole().getName())
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
        tokenRepository.save(Token.builder().userId(userInfo.getId()).tokenId(jti).expiredTime(System.currentTimeMillis() + expireInRefresh).build());
        return LoginResponse.builder().userInfo(userInfo).accessToken(accessToken).expiresIn(System.currentTimeMillis() +expireInRefresh).refreshToken(jti).status(true).build();
    }

    @Transactional
    @Override
    public UserResponse registerUser(UserRequest userRequest) {
        Account account = accountRepository.findAccountByUsername(userRequest.getUserName());
        if (account != null && account.getId() > 0) {
            return UserResponse.builder().status(false).message("Tài khoản đã tồn tại! Vui lòng thử lại!").build();
        }
        if (accountRepository.existsByEmail(userRequest.getEmail())) {
            return UserResponse.builder().status(false).message("Email đã tồn tại! Vui lòng thử lại!").build();
        }
        if (userRepository.existsByPhone(userRequest.getPhone())) {
            return UserResponse.builder().status(false).message("SĐT đã tồn tại! Vui lòng thử lại!").build();
        }
        Role role = roleRepository.findByName(userRequest.getRoleName());
        if (role == null) {
            return UserResponse.builder().status(false).message("Role is not exists, please check again").build();
        }
        /*if (role.getName() == "ROLE_ADMIN") {
            return UserResponse.builder().status(false).message("You do not have permission to create an admin account").build();
        }*/
        account = Account.builder().username(userRequest.getUserName()).deleteFlag(false).role(role).email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword() + Constants.SALT_DEFAULT)).build();
        UserInfo userInfo = UserInfo.builder().firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .phone(Utils.normalPhone(userRequest.getPhone()))
                .address(userRequest.getAddress())
                .account(account)
                .build();
        accountRepository.save(account);
        userRepository.save(userInfo);
        return UserResponse.builder().status(true).message("New account registration is successful").build();
    }


    @Override
    public UserResponse getUserProfile(long userId) {
        UserInfo userInfo = userRepository.findById(userId).orElseThrow();
        return UserResponse.builder().status(true)
                .userInfo(userInfo)
                .role(userInfo.getAccount().getRole())
                .build();
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        Token token = tokenRepository.findByTokenId(refreshToken);
        if (token == null || token.getId() <=0){
            return LoginResponse.builder().message("Refresh token is not exist").status(false).build();
        }
        else  {
            if(System.currentTimeMillis() > token.getExpiredTime()){
                return LoginResponse.builder().message("Jwt refresh token expired at "+new DateTime(token.getExpiredTime())).status(false).build();
            }
            UserInfo userInfo = userRepository.findById(token.getUserId()).orElseThrow();
            if (userInfo.getAccount().isDeleteFlag()) {
                return LoginResponse.builder().message("Your account has been temporarily locked, please contact us again").status(false).build();
            } else{
                return buildTokenResponse(userInfo);
            }
        }
    }

    @Override
    public ApiResponse<Object> deleteAccountUser(long userID) {
        Account account = accountRepository.findById(userID).orElseThrow();
        account.setDeleteFlag(true);
        accountRepository.save(account);
        return ApiResponse.builder().status(200).message("Account is blocked").data(Mono.just(account)).build();
    }

    @Override
    public ApiResponse<Object> updateProfile(Long userID, UpdateProfileRequest updateRequest){
        Optional<UserInfo> userInfo = userRepository.findById(userID);
        if(userInfo.isPresent()){
            BeanUtilsBean notNull=new NullAwareBeanUtilsBean();
            try {
                notNull.copyProperties(userInfo.get(),updateRequest);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            userRepository.save(userInfo.get());
        }
        return ApiResponse.builder().status(200).data(userInfo).build();
    }


    @Override
    public PasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        boolean result = false;
        String message = "";
        int errorCode = 0;
        Account account = accountRepository.findAccountByEmail(forgotPasswordRequest.getEmail());
        if (account != null && account.getId() > 0) {
            result = true;
            String verifyCode= RandomUtils.getAlphaNumericString(20);
            ForgotPassword forgotPassword = forgotPasswordRepository.findForgotPasswordByAccount(account);
            if (forgotPassword == null || forgotPassword.getId() == null) {
                forgotPasswordRepository.save(
                        ForgotPassword.builder().
                                account(account).
                                verifyCode(verifyCode).
                                useCode(true).
                                expiryDate(System.currentTimeMillis()+Duration.ofHours(8).toMillis()).
                                build());
            }else{
                forgotPassword.setVerifyCode(verifyCode);
                forgotPassword.setUseCode(true);
                forgotPassword.setExpiryDate(System.currentTimeMillis()+Duration.ofHours(8).toMillis());
                forgotPasswordRepository.save(forgotPassword);
            }
            emailSenderService.sendEmail(account,verifyCode);
            message = "Kiểm tra email để lấy lại mật khẩu!";
        } else {
            errorCode = 21;
            message = "Email không tồn tại trong hệ thống! Vui lòng thử lại";
        }
        return PasswordResponse.builder().status(result).message(message).errorCode(errorCode).build();
    }

    @Override
    public PasswordResponse verifyCode(String codeRequest) {
        ForgotPassword forgotPassword = forgotPasswordRepository.findByVerifyCode(codeRequest);
        if (forgotPassword == null || forgotPassword.getId() == null) {
           return PasswordResponse.builder().message("Verify code does not exist").status(false).build();
        }
        else if(!forgotPassword.isUseCode() || forgotPassword.getExpiryDate()<System.currentTimeMillis()){
            return PasswordResponse.builder().message("Verify code has been used or has expired").status(false).build();
        }
        return PasswordResponse.builder().message("Valid verify code").status(true).build();
    }

    @Override
    public PasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
        boolean result = false;
        String message = "";
        int errorCode = 0;
        ForgotPassword forgotPassword = forgotPasswordRepository.findByVerifyCode(resetPasswordRequest.getVerifyCode());
        if (forgotPassword != null && forgotPassword.getId() >= 0) {
            if (!forgotPassword.isUseCode()) {
                return PasswordResponse.builder().status(false).message("Please resubmit a new request, the link has been activated successfully!").build();
            }
            Account account = forgotPassword.getAccount();
            if (account != null && account.getId() > 0) {
                account.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()+ Constants.SALT_DEFAULT));
                accountRepository.save(account);
                forgotPassword.setUseCode(false);
                forgotPasswordRepository.save(forgotPassword);
                result = true;
                message = "You can now use your password to login to Shop!";
            } else {
                message = "Not find an account with your email, please check again!";
                errorCode = 31;
            }
        } else {
            message = "Reset code not valid, please check the reset password link";
            errorCode = 32;
        }
        return PasswordResponse.builder().status(result).message(message).errorCode(errorCode).build();
    }


    @Override
    public Iterable<UserInfo> getAllUser() {
        return userRepository.findAllByAccount_DeleteFlagAndAccount_Role_Name(false,"ROLE_USER");
    }
}
