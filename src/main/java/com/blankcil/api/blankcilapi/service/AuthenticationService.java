package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.model.*;
import com.blankcil.api.blankcilapi.config.JwtService;
import com.blankcil.api.blankcilapi.entity.UserEntity;
import com.blankcil.api.blankcilapi.entity.TokenEntity;
import com.blankcil.api.blankcilapi.model.request.AuthenticationRequest;
import com.blankcil.api.blankcilapi.model.request.GoogleLoginRequest;
import com.blankcil.api.blankcilapi.model.request.RegisterRequest;
import com.blankcil.api.blankcilapi.model.response.AuthenticationResponse;
import com.blankcil.api.blankcilapi.model.response.RegisterResponse;
import com.blankcil.api.blankcilapi.repository.TokenRepository;
import com.blankcil.api.blankcilapi.token.TokenType;
import com.blankcil.api.blankcilapi.repository.UserRepository;
import com.blankcil.api.blankcilapi.user.Role;
import com.blankcil.api.blankcilapi.utils.EncryptionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  @Autowired
  private IEmailService emailService = new EmailServiceImpl();

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private GoogleIdTokenVerifier verifier;

  public RegisterResponse register(RegisterRequest request) throws Exception {
    if(userRepository.existsUserEntityByEmail(request.getEmail()))
      throw new RuntimeException("User with email " + request.getEmail() +" or nick name "+request.getNickName()+ " already exists.");

    String code = this.getRandom();

    var user = UserEntity.builder()
            .fullname(request.getFullname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .address(request.getAddress())
            .phone(request.getPhone())
            .birthday(request.getBirthday())
            .createDay(LocalDateTime.now())
            .avatar_url(null)
            .cover_url(null)
            .code(code)
            .isActive(false)
            .nickName(request.getNickName())
            .isLock(false).build();

    // Send verification email with encrypted code
//    String encryptedCode = EncryptionUtils.encrypt(user.getCode());
    emailService.sendVerificationMail(user.getEmail(), user.getCode());

    var savedUser = repository.save(user);

    return RegisterResponse.builder()
            .fullname(savedUser.getFullname())
            .email(savedUser.getEmail())
            .build();
  }

//  private String sendCode(String email){
////    account.setCode(code);
//    String body = "Mã xác nhận Mạng xã hội Podcast Blankcil của bạn là: "+code+" ! Nếu bạn không đăng ký" +
//            "\n Blankcil thì hãy bỏ qua email này!";
////    emailService.sendEmail("Blankcil Team",email,"Confirm email",body);
//    return code;
//  }

//  public String sendCodeToUser(String email){
//
//    var user = userRepository.findByEmail(email)
//            .orElseThrow();
//    String code = sendCode(user.getEmail());
//    user.setCode(code);
//    UserEntity savedUser = userRepository.save(user);
//    return savedUser.getEmail();
////    return new ConfirmRequest().builder().email(savedUser.getEmail()).code(savedUser.getCode()).build();
//  }

  public AuthenticationResponse confirmRegister(ConfirmRequest confirmRequest) throws Exception{
    if ("ĐÃ XÁC THỰC".equals(confirmRequest.getCode())) {
      throw new Exception("Lỗi bảo mật!");
    }

//    String decryptedCode = EncryptionUtils.decrypt(confirmRequest.getCode());
    var user = repository.findByEmailAndCode(confirmRequest.getEmail(), confirmRequest.getCode()).orElseThrow();

    user.setActive(true);
    user.setCode("ĐÃ XÁC THỰC");
    var savedUser = repository.save(user);

    emailService.sendWelcomeMessage(savedUser.getEmail(), savedUser.getFullname());

    var jwtToken = jwtService.generateToken(savedUser);
    var refreshToken = jwtService.generateRefreshToken(savedUser);
    saveUserToken(savedUser, jwtToken);

    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  // Google login
  public AuthenticationResponse loginOAuthGoogle(GoogleLoginRequest requestBody) {
    UserEntity user = verifyIDToken(requestBody.getTokenId());

    if (user == null) {
      throw new IllegalArgumentException();
    }

    user = createOauth2User(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);

    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);

    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
  }

  // Only use for Google oauth2
  @Transactional
  public UserEntity createOauth2User(UserEntity user) {
    UserEntity existingAccount = userRepository.findByEmail(user.getEmail()).orElse(null);
    if (existingAccount == null) {
      user.setEmail(user.getEmail());
      user.setFullname(user.getFullname());
      user.setRole(Role.USER);
      user.setCode("ĐÃ XÁC THỰC");
      user.setCreateDay(LocalDateTime.now());
      user.setActive(true);
      userRepository.save(user);

      // Send welcome email when it was the first login
      emailService.sendWelcomeMessage(user.getEmail(), user.getFullname());

      return user;
    }
    return existingAccount;
  }

  private void saveUserToken(UserEntity userEntity, String jwtToken) {
    var token = TokenEntity.builder()
        .userEntity(userEntity)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(UserEntity userEntity) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(userEntity.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
  public String getRandom() {
    Random rnd = new Random();
    int number = rnd.nextInt(999999);
    return String.format("%06d", number);
  }

  private UserEntity verifyIDToken(String idToken) {
    try {
      GoogleIdToken idTokenObj = verifier.verify(idToken);
      if (idTokenObj == null) {
        return null;
      }
      GoogleIdToken.Payload payload = idTokenObj.getPayload();
      String firstName = (String) payload.get("given_name");
      String lastName = (String) payload.get("family_name");
      String email = payload.getEmail();

      return new UserEntity(email, firstName + " " + lastName);
    } catch (GeneralSecurityException | IOException e) {
      return null;
    }
  }
}
