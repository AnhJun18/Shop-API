package com.myshop.api.service.user;

import com.google.api.client.util.DateTime;
import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.config.NullAwareBeanUtilsBean;
import com.myshop.api.payload.request.user.CustomerRequest;
import com.myshop.api.payload.request.user.ForgotPasswordRequest;
import com.myshop.api.payload.request.user.LoginRequest;
import com.myshop.api.payload.request.user.ResetPasswordRequest;
import com.myshop.api.payload.request.user.UpdateProfileRequest;
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
import com.myshop.repositories.common.GlobalOption;
import com.myshop.repositories.user.entities.Account;
import com.myshop.repositories.user.entities.Customer;
import com.myshop.repositories.user.entities.ForgotPassword;
import com.myshop.repositories.user.entities.Role;
import com.myshop.repositories.user.entities.Token;
import com.myshop.repositories.user.entities.User;
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
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl extends CRUDBaseServiceImpl<Customer, UserResponse, Customer, Long> implements UserService {

  private final long expireIn = Duration.ofHours(1).toSeconds();
  private final long expireInRefresh = Duration.ofHours(48).toMillis();

  @Autowired
  private CustomerRepository customerRepository;
  @Autowired
  private EmployeeRepository employeeRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private TokenRepository tokenRepository;
  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private AccountRepository accountRepository;
  @Autowired
  private ForgotPasswordRepository forgotPasswordRepository;
  @Autowired
  private EmailSenderService emailSenderService;
  private final EntityManager entityManager;


  @Value("${jwkFile}")
  private Resource jwkFile;

  public UserServiceImpl(CustomerRepository customerRepository, EntityManager entityManager) {
    super(Customer.class, UserResponse.class, Customer.class, customerRepository);
    this.entityManager = entityManager;
  }

  @Override
  public LoginResponse login(LoginRequest loginRequest) {
    Account account = accountRepository.findAccountByEmail(loginRequest.getEmail());
    if (account == null || account.getEmail() == null) {
      return LoginResponse.builder().message("Tài khoản hoặc mật khẩu không đúng").status(false).build();
    }
    if (account.isDeleted() == true) {
      return LoginResponse.builder().message("Tài khoản của bạn đã bị khóa").status(false).build();
    }
    if (passwordEncoder.matches(loginRequest.getPassword() + Constants.SALT_DEFAULT, account.getPassword())) {
      return _buildTokenResponse(account);
    } else {
      return LoginResponse.builder().message("Tài khoản hoặc mật khẩu không đúng.").status(false).build();
    }
  }

  private LoginResponse _buildTokenResponse(Account account) {
    Optional<Role> role = roleRepository.findById(account.getRoleId());
    if (!role.isPresent())
      return LoginResponse.builder().message("Tài khoản không có quyền truy cập tài khoản").status(false).build();
    User userLogin = null;
    if (role.get().getName().trim().equals("ROLE_ADMIN") || role.get().getName().trim().equals("ROLE_EMPLOYEE")  )
      userLogin = employeeRepository.findByEmail(account.getEmail()).orElse(null);
    else
      userLogin = customerRepository.findByEmail(account.getEmail()).orElse(null);
    if (userLogin == null)
      return LoginResponse.builder().message("Không có thông tin tài khoản").status(false).build();
    String jti = UUID.randomUUID().toString();
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(userLogin.getEmail())
            .jwtID(jti)
            .claim("authorities", role.get().getName())
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
    tokenRepository.save(Token.builder().account(account.getEmail()).tokenId(jti).expiredTime(System.currentTimeMillis() + expireInRefresh).build());
    return LoginResponse.builder().userInfo(userLogin).accessToken(accessToken).expiresIn(System.currentTimeMillis() + expireInRefresh).refreshToken(jti).status(true).build();
  }

  @Override
  @Transactional
  public UserResponse registerUser(CustomerRequest customerRequest) {
    boolean isExistsEmail = accountRepository.existsByEmail(customerRequest.getEmail());
    if (isExistsEmail) {
      return UserResponse.builder().status(false).message("Email đã tồn tại! Vui lòng thử lại!").build();
    }
    if (customerRepository.existsByPhone(customerRequest.getPhone())) {
      return UserResponse.builder().status(false).message("SĐT đã tồn tại! Vui lòng thử lại!").build();
    }
    Account newAccount = Account.builder()
            .roleId(roleRepository.findByName("ROLE_CUSTOMER").getId())
            .email(customerRequest.getEmail())
            .password(passwordEncoder.encode(customerRequest.getPassword() + Constants.SALT_DEFAULT))
            .build();
    accountRepository.save(newAccount);
    Customer customerInfo = Customer.builder().firstName(customerRequest.getFirstName())
            .lastName(customerRequest.getLastName())
            .gender(customerRequest.getGender())
            .phone(Utils.normalPhone(customerRequest.getPhone()))
            .email(customerRequest.getEmail())
            .build();
    customerRepository.save(customerInfo);
    return UserResponse.builder().status(true).message("Tài khoản đã tạo thành công!").data(customerInfo).build();
  }


  @Override
  public LoginResponse refreshToken(String refreshToken) {
    Token token = tokenRepository.findByTokenId(refreshToken);
    if (token == null) {
      return LoginResponse.builder().message("Refresh token không tồn tại").status(false).build();
    } else {
      if (System.currentTimeMillis() > token.getExpiredTime()) {
        return LoginResponse.builder().message("Token đã hết hạn lúc " + new DateTime(token.getExpiredTime())).status(false).build();
      }
      Account accountInfo = accountRepository.findAccountByEmail(token.getAccount());
      if (accountInfo.isDeleted()) {
        return LoginResponse.builder().message("Tài khoản đã bị khóa tạm thời, vui lòng liên hệ admin để hỗ trợ").status(false).build();
      } else {
        Optional<Role> role = roleRepository.findById(accountInfo.getRoleId());
        if (!role.isPresent())
          return LoginResponse.builder().message("Tài khoản không có quyền truy cập tài khoản").status(false).build();
        String jti = UUID.randomUUID().toString();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(accountInfo.getEmail())
                .jwtID(jti)
                .claim("authorities", role.get().getName())
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
        return LoginResponse.builder().accessToken(accessToken).status(true).refreshToken(refreshToken).build();
      }
    }
  }


  @Transactional
  @Override
  public ApiResponse<?> getOptsEmployee() {
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery("CBO_GetOption", GlobalOption.class);
    query.registerStoredProcedureParameter("TABLENAME", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("COLUMNID", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("COLUMNNAME", String.class, ParameterMode.IN);
    query.setParameter("TABLENAME", "EMPLOYEE");
    query.setParameter("COLUMNID", "EMPLOYEEID");
    query.setParameter("COLUMNNAME", "FULLNAME");

    query.execute();
    List<GlobalOption> options = query.getResultList();
    return ApiResponse.of(options);
  }

  @Override
  public UserResponse getUserProfile(String email) {
    Map<String, Object> dataUser = accountRepository.getProfileByEmail(email);
    return UserResponse.builder().status(true).data(dataUser).build();
  }


  //
//    @Override
//    public ApiResponse<Object> deleteAccountUser(long userID) {
//        Account account = accountRepository.findById(userID).orElseThrow();
//        account.setDeleteFlag(true);
//        accountRepository.save(account);
//        return ApiResponse.builder().status(200).message("Account is blocked").data(Mono.just(account)).build();
//    }
//
  @Override
  public ApiResponse<Object> updateProfile(String userID, UpdateProfileRequest updateRequest) {
    Optional<Customer> userInfo = customerRepository.findByEmail(userID);
    if (userInfo.isPresent()) {
      BeanUtilsBean notNull = new NullAwareBeanUtilsBean();
      try {
        notNull.copyProperties(userInfo.get(), updateRequest);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
      customerRepository.save(userInfo.get());
    }
    return ApiResponse.builder().status(200).data(userInfo).build();
  }

  @Override
  public Iterable<Customer> getUsers() {
    return customerRepository.findAll();
  }
//
//
    @Override
    public PasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        boolean result = false;
        String message = "";
        int errorCode = 0;
        Account account = accountRepository.findAccountByEmail(forgotPasswordRequest.getEmail());
        if (account != null && !StringUtils.isEmpty(account.getEmail())) {
            result = true;
            String verifyCode= RandomUtils.getAlphaNumericString(20);
            ForgotPassword forgotPassword = forgotPasswordRepository.findForgotPasswordByEmail(account.getEmail());
            if (forgotPassword == null || forgotPassword.getId() == null) {
                forgotPasswordRepository.save(
                        ForgotPassword.builder().
                                email(account.getEmail()).
                                verifyCode(verifyCode).
                                usedCode(false).
                                expiryDate(System.currentTimeMillis()+Duration.ofHours(8).toMillis()).
                                build());
            }else{
                forgotPassword.setVerifyCode(verifyCode);
                forgotPassword.setUsedCode(false);
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
           return PasswordResponse.builder().message("Mã xác thực  không hợp lệ").status(false).build();
        }
        else if(forgotPassword.isUsedCode() || forgotPassword.getExpiryDate()<System.currentTimeMillis()){
            return PasswordResponse.builder().message("Mã xác thực đã hết hạn hoặc được sử dụng").status(false).build();
        }
        return PasswordResponse.builder().message("Mã hợp lệ").status(true).build();
    }

    @Override
    public PasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
        boolean result = false;
        String message = "";
        int errorCode = 0;
        ForgotPassword forgotPassword = forgotPasswordRepository.findByVerifyCode(resetPasswordRequest.getVerifyCode());
        if (forgotPassword != null && forgotPassword.getId() >= 0) {
            if (forgotPassword.isUsedCode()) {
                return PasswordResponse.builder().status(false).message("Token này đã được sử dụng!").build();
            }
            Account account = accountRepository.findAccountByEmail(forgotPassword.getEmail());
            if (account != null ) {
                account.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()+ Constants.SALT_DEFAULT));
                accountRepository.save(account);
                forgotPassword.setUsedCode(true);
                forgotPasswordRepository.save(forgotPassword);
                result = true;
                message = "Mật khẩu đã được thay đổi! Vui lòng đăng nhập lại";
            } else {
                message = "Không tìm thấy email cần đổi mật khẩu, vui lòng thử laị!";
                errorCode = 31;
            }
        } else {
            message = "Mã yêu cầu không hợp lệ";
            errorCode = 32;
        }
        return PasswordResponse.builder().status(result).message(message).errorCode(errorCode).build();
    }

//
//    @Override
//    public Iterable<UserInfo> getAllUser() {
//        return userRepository.findAllByAccount_DeleteFlagAndAccount_Role_Name(false,"ROLE_USER");
//    }
}
