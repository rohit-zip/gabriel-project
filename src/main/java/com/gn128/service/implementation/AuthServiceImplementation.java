package com.gn128.service.implementation;

import com.gn128.authentication.CustomUserDetailService;
import com.gn128.authentication.GoogleTokenVerifier;
import com.gn128.authentication.UserPrincipal;
import com.gn128.constants.EnvironmentConstants;
import com.gn128.constants.ExceptionMessages;
import com.gn128.constants.ServiceConstants;
import com.gn128.dao.repository.ForgetPasswordRepository;
import com.gn128.dao.repository.RegistrationOtpRepository;
import com.gn128.dao.repository.UserAuthRepository;
import com.gn128.entity.ForgetPassword;
import com.gn128.entity.RegistrationOtp;
import com.gn128.entity.UserAuth;
import com.gn128.enums.Provider;
import com.gn128.exception.payloads.AuthenticationException;
import com.gn128.exception.payloads.BadRequestException;
import com.gn128.payloads.GoogleOauthUserInfo;
import com.gn128.payloads.record.RefreshTokenDaoValidationRecord;
import com.gn128.payloads.record.RemoteAddressResponse;
import com.gn128.payloads.request.AuthenticationRequest;
import com.gn128.payloads.request.ForgetPasswordRequest;
import com.gn128.payloads.request.GoogleLoginRequest;
import com.gn128.payloads.request.RegisterRequest;
import com.gn128.payloads.response.AuthResponse;
import com.gn128.payloads.response.ModuleResponse;
import com.gn128.processor.LogoutUserRefreshTokenValidationProcessor;
import com.gn128.processor.RefreshTokenPersistence;
import com.gn128.processor.mail.ForgetPasswordMailSendingProcessor;
import com.gn128.processor.mail.SendRegistrationOtpProcessor;
import com.gn128.service.AuthenticationService;
import com.gn128.transformer.OauthUserToUserAuthTransformer;
import com.gn128.transformer.RegisterUserRequestToUserAuthTransformer;
import com.gn128.transformer.VerifiedUserEntityTransformer;
import com.gn128.utils.*;
import com.gn128.validator.implementation.AuthenticationRequestValidator;
import com.gn128.validator.implementation.NativeRefreshTokenValidator;
import com.gn128.validator.implementation.RefreshTokenDaoValidation;
import com.gn128.validator.implementation.ResendOtpValidator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import static com.gn128.constants.ServiceConstants.ORIGIN;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.service.implementation
 * Created_on - November 10 - 2024
 * Created_at - 14:07
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthenticationService {

    private final UserAuthRepository userAuthRepository;
    private final RegisterUserRequestToUserAuthTransformer registerUserRequestToUserAuthTransformer;
    private final SendRegistrationOtpProcessor sendRegistrationOtpProcessor;
    private final AuthenticationRequestValidator authenticationRequestValidator;
    private final AuthenticationManager authenticationManager;
    private final Environment environment;
    private final RefreshTokenPersistence refreshTokenPersistence;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final OauthUserToUserAuthTransformer oauthUserToUserAuthTransformer;
    private final CustomUserDetailService customUserDetailService;
    private final RegistrationOtpRepository registrationOtpRepository;
    private final VerifiedUserEntityTransformer verifiedUserEntityTransformer;
    private final ResendOtpValidator resendOtpValidator;
    private final LogoutUserRefreshTokenValidationProcessor logoutUserRefreshTokenValidationProcessor;
    private final NativeRefreshTokenValidator nativeRefreshTokenValidator;
    private final RefreshTokenDaoValidation refreshTokenDaoValidation;
    private final JwtDecoderUtil jwtDecoderUtil;
    private final ForgetPasswordRepository forgetPasswordRepository;
    private final OtpGenerator otpGenerator;
    private final PasswordEncoder passwordEncoder;
    private final ForgetPasswordMailSendingProcessor forgetPasswordMailSendingProcessor;

    @Override
    public ModuleResponse register(RegisterRequest registerRequest, HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        boolean isExists = userAuthRepository.existsByEmail(registerRequest.getEmail());
        if (isExists) {
            throw new BadRequestException("User is already exists with given Email", HttpStatus.BAD_REQUEST);
        }
        UserAuth userEntity = registerUserRequestToUserAuthTransformer.transform(registerRequest, httpServletRequest);
        UserAuth response = userAuthRepository.save(userEntity);
        sendRegistrationOtpProcessor.process(response);
        log.info("Execution Time (Register User) -> {}ms", System.currentTimeMillis() - startTime);
        return ModuleResponse
                .builder()
                .message("User registered successfully to Server. Please check OTP on your registered mail")
                .userId(response.getUserId())
                .build();
    }

    @Override
    public AuthResponse authenticate(AuthenticationRequest authenticationRequest, HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        authenticationRequestValidator.validate(authenticationRequest);
        Authentication userAuthentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEntrypoint(),
                        authenticationRequest.getPassword()
                )
        );
        AuthResponse authResponse = getAuthResponse(httpServletRequest, userAuthentication);
        log.info("Execution Time (Login User) -> {}ms", System.currentTimeMillis() - startTime);
        return authResponse;
    }

    @Override
    public AuthResponse googleSSO(GoogleLoginRequest googleLoginRequest, HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        googleTokenVerifier.authorize(googleLoginRequest.getSecret());
        String userInfoEndpoint = "https://openidconnect.googleapis.com/v1/userinfo";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(googleLoginRequest.getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Object> response = restTemplate.exchange(
                userInfoEndpoint,
                HttpMethod.GET,
                entity,
                Object.class
        );
        if (! (response.getBody() instanceof LinkedHashMap<?, ?>)) {
            throw new BadRequestException("Casting Error Occurred for Linked Hashmap", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>)response.getBody();
        GoogleOauthUserInfo userInfo = new GoogleOauthUserInfo(body);
        Optional<UserAuth> userByEmailOptional = userAuthRepository.findByEmail(userInfo.getEmail());
        if (userByEmailOptional.isEmpty()) {
            UserAuth userEntity = oauthUserToUserAuthTransformer.transform(userInfo, httpServletRequest);
            UserAuth userEntityResponse = userAuthRepository.save(userEntity);
            log.info("Google OAuth new user registered to sever with Id : " + userEntityResponse.getUserId());
        } else {
            UserAuth userEntity = userByEmailOptional.get();
            if (!userEntity.getProvider().equals(Provider.google)) {
                throw new BadRequestException(String.format("Your account has been created using %s . Please use %s account to login", userEntity.getProvider().name(), userEntity.getProvider().name()), HttpStatus.BAD_REQUEST);
            }
        }
        UserDetails userDetails = customUserDetailService.loadUserByUsername(userInfo.getEmail());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        AuthResponse authResponse = getAuthResponse(httpServletRequest, authentication);
        log.info("Execution Time (Login Google) -> {}ms", System.currentTimeMillis() - startTime);
        return authResponse;
    }

    @Override
    public ModuleResponse verifyOtp(String otp, String userId) {
        long startTime = System.currentTimeMillis();
        ValueCheckerUtil.isValidUUID(userId);
        Optional<RegistrationOtp> byUserIdOptional = registrationOtpRepository.findByUserId(userId);
        if (byUserIdOptional.isEmpty()) {
            throw new BadRequestException("No user is present for the given OTP", HttpStatus.BAD_REQUEST);
        }
        UserAuth userEntity = userAuthRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ExceptionMessages.USER_NOT_FOUND_ID, HttpStatus.BAD_REQUEST));
        RegistrationOtp registrationOtpDocument = byUserIdOptional.get();
        if (!registrationOtpDocument.getOtp().equals(otp)) {
            throw new BadRequestException("You entered Incorrect OTP", HttpStatus.BAD_REQUEST);
        }
        if (Boolean.TRUE.equals(userEntity.isEnabled()))
            throw new BadRequestException("User email is already verified", HttpStatus.BAD_REQUEST);
        if (registrationOtpDocument.getExpiry().before(new Date()))
            throw new BadRequestException("OTP Expired", HttpStatus.BAD_REQUEST);
        UserAuth transform = verifiedUserEntityTransformer.transform(userEntity);
        UserAuth response = userAuthRepository.save(transform);
        registrationOtpRepository.deleteById(registrationOtpDocument.getOtpId());
        log.info("Execution Time (Verify OTP) -> {}ms", System.currentTimeMillis() - startTime);
        return ModuleResponse
                .builder()
                .message("User Verified successfully to server")
                .userId(userId)
                .build();
    }

    @Override
    public ModuleResponse resendOtp(String userId) {
        long startTime = System.currentTimeMillis();
        Optional<RegistrationOtp> registrationOtpOptional = registrationOtpRepository.findByUserId(userId);
        Optional<UserAuth> userAuthOptional = userAuthRepository.findById(userId);
        if (registrationOtpOptional.isEmpty()) throw new BadRequestException("OTP not present for the User", HttpStatus.BAD_REQUEST);
        if (userAuthOptional.isEmpty()) throw new BadRequestException(ExceptionMessages.USER_NOT_FOUND_ID, HttpStatus.BAD_REQUEST);
        RegistrationOtp registrationOtp = registrationOtpOptional.get();
        resendOtpValidator.validate(registrationOtp, userAuthOptional.get());
        registrationOtpRepository.delete(registrationOtp);
        sendRegistrationOtpProcessor.process(userAuthOptional.get(), registrationOtp.getTimesSent() + 1);
        log.info("Execution Time (Resend OTP) -> {}ms", System.currentTimeMillis() - startTime);
        return ModuleResponse
                .builder()
                .message("OTP Resend successfully to user email")
                .build();
    }

    @Override
    public AuthResponse logoutUser(HttpServletRequest request, HttpServletResponse response) {
        long startTime = System.currentTimeMillis();
        Cookie[] cookies = request.getCookies();
        if (cookies == null) throw new BadRequestException(ExceptionMessages.USER_ALREADY_LOGGED_OUT, HttpStatus.BAD_REQUEST);
        String cookieName = environment.getProperty(EnvironmentConstants.REFRESH_TOKEN_COOKIE_NAME);
        Optional<Cookie> refreshTokenCookie = CookieUtils.getCookie(request, cookieName);
        logoutUserRefreshTokenValidationProcessor.process(refreshTokenCookie, request);
        boolean isHttpOnly = true;
        if (refreshTokenCookie.isPresent()) {
            Cookie cookie = refreshTokenCookie.get();
            isHttpOnly = cookie.isHttpOnly();
        }
        assert cookieName != null;
        ResponseCookie cookie = ResponseCookie
                .from(cookieName, null)
                .httpOnly(isHttpOnly)
                .maxAge(1)
                .path("/")
                .sameSite("None")
                .secure(true)
                .build();

        log.info("Execution Time (Logout User) -> {}ms", System.currentTimeMillis() - startTime);
        return AuthResponse.builder().cookie(cookie).build();
    }

    @Override
    public AuthResponse refreshToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        long startTime = System.currentTimeMillis();
        String cookieName = environment.getProperty(EnvironmentConstants.REFRESH_TOKEN_COOKIE_NAME);
        Cookie[] cookies = httpServletRequest.getCookies();
        if (Objects.isNull(cookies)) throw new AuthenticationException(ExceptionMessages.LOGIN_AGAIN);
        Optional<Cookie> refreshTokenOptional = CookieUtils.getCookie(httpServletRequest, cookieName);
        if (refreshTokenOptional.isEmpty()) throw new AuthenticationException(ExceptionMessages.LOGIN_AGAIN);
        String refreshToken = refreshTokenOptional.get().getValue();
        UsernamePasswordAuthenticationToken authentication = getAuthenticationForRefreshToken(refreshToken, httpServletRequest);
        AuthResponse authResponse = getAuthResponse(httpServletRequest, authentication);
        log.info("Execution Time (Refresh Token) -> {}ms", System.currentTimeMillis() - startTime);
        return authResponse;
    }

    @Override
    public RemoteAddressResponse remoteAddress(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String v4 = IpUtils.getRemoteAddress(request);
        String localAddr = request.getLocalAddr();
        log.info("Execution Time (Remote Address) -> {}ms", System.currentTimeMillis() - startTime);
        return new RemoteAddressResponse(v4, localAddr);
    }

    private AuthResponse getAuthResponse(HttpServletRequest httpServletRequest, Authentication userAuthentication) {
        UserPrincipal principal = (UserPrincipal) userAuthentication.getPrincipal();
        String remoteAddress = IpUtils.getRemoteAddress(httpServletRequest);
        String cookieName = environment.getProperty(EnvironmentConstants.REFRESH_TOKEN_COOKIE_NAME);
        CompletableFuture<String> accessTokenFuture = CompletableFuture.supplyAsync(() -> jwtTokenGenerator.generateAccessToken(
                userAuthentication,
                getOrigin(httpServletRequest),
                false,
                remoteAddress
        ));
        CompletableFuture<String> refreshTokenFuture = CompletableFuture.supplyAsync(() -> jwtTokenGenerator.generateRefreshToken(
                userAuthentication,
                getOrigin(httpServletRequest),
                remoteAddress
        ));
        AsyncUtils.getAsyncResult(CompletableFuture.allOf(
                accessTokenFuture,
                refreshTokenFuture
        ));
        String accessToken = accessTokenFuture.join();
        String refreshToken = refreshTokenFuture.join();
        SecurityContextHolder.getContext().setAuthentication(userAuthentication);
        AuthResponse authResponse = AuthResponse.builder()
                .userId(principal.getUserId())
                .accessToken(accessToken)
                .remoteAddress(IpUtils.getRemoteAddress(httpServletRequest))
                .email(principal.getEmail())
                .username(principal.getUsername())
                .authorities(userAuthentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
        CompletableFuture.runAsync(() -> refreshTokenPersistence.persist(
                refreshToken,
                accessToken,
                principal,
                remoteAddress
        ));
        if (Objects.nonNull(refreshToken)) {
            String origin = httpServletRequest.getHeader(ORIGIN);
            boolean isHttpOnly = true;
            if (StringUtils.hasText(origin)) {
                isHttpOnly = !origin.contains("localhost:");
            }
            assert cookieName != null;
            ResponseCookie cookie = ResponseCookie
                    .from(cookieName, refreshToken)
                    .httpOnly(isHttpOnly)
                    .maxAge(86400)
                    .path("/")
                    .sameSite("None")
                    .secure(true)
                    .build();
            authResponse.setCookie(cookie);
        }
        return authResponse;
    }

    private String getOrigin(HttpServletRequest httpServletRequest) {
        String origin = httpServletRequest.getHeader(ORIGIN);
        if (ObjectUtils.isEmpty(httpServletRequest.getHeader(ORIGIN))) {
            origin = ServiceConstants.LOCAL_ORIGIN;
        }
        return origin;
    }

    private UsernamePasswordAuthenticationToken getAuthenticationForRefreshToken(String token, HttpServletRequest httpServletRequest) {
        nativeRefreshTokenValidator.validate(token);
        Object daoValidation = refreshTokenDaoValidation.validate(token, httpServletRequest);
        if (daoValidation instanceof RefreshTokenDaoValidationRecord record) {
                    AuthResponse
                            .builder()
                            .message(record.message())
                            .build();
        }
        String userId = jwtDecoderUtil.extractUserId(token);
        UserDetails userDetails = customUserDetailService.loadUserById(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    @SneakyThrows(value = {MessagingException.class})
    public CompletableFuture<ModuleResponse> forgetPasswordOtp(String email) {
        long startTime = System.currentTimeMillis();
        CompletableFuture<UserAuth> usetAuthEntityCompletableFuture = CompletableFuture.supplyAsync(() -> userAuthRepository.findByEmail(email)
                .orElseThrow(()-> new BadRequestException("User not found with email")));
        CompletableFuture<Optional<ForgetPassword>> optionalCompletableFuture = CompletableFuture.supplyAsync(() -> forgetPasswordRepository.findByEmail(email));
        AsyncUtils.getAsyncResult(CompletableFuture.allOf(usetAuthEntityCompletableFuture, optionalCompletableFuture));
        UserAuth userAuth = usetAuthEntityCompletableFuture.join();
        Optional<ForgetPassword> forgetPasswordDocument = optionalCompletableFuture.join();
        if (!userAuth.isEnabled()) throw new BadRequestException("User is not enabled");
        if (!userAuth.isAccountNonExpired()) throw new BadRequestException("User account is expired");
        if (!userAuth.isCredentialsNonExpired()) throw new BadRequestException("User account credentials are expired");
        if (!userAuth.isAccountNonLocked()) throw new BadRequestException("User account is locked");
        if (!userAuth.getProvider().equals(Provider.local)) {
            throw new BadRequestException("User account provider not supported forget password");
        }
        forgetPasswordDocument.ifPresent(document -> {
            if (document.getUserId().equals(userAuth.getUserId()))
                CompletableFuture.runAsync(() -> forgetPasswordRepository.delete(document));
            else throw new BadRequestException("Forget Password User Id not matched with User table", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        ForgetPassword forgetPassword = otpGenerator.forgetPasswordOtpSupplier(userAuth);
        ForgetPassword response = forgetPasswordRepository.save(forgetPassword);
        forgetPasswordMailSendingProcessor.sendMail(forgetPassword);
        log.info("Execution Time (Forget Password OTP Send) -> {}ms", System.currentTimeMillis() - startTime);
        return CompletableFuture.completedFuture(
                ModuleResponse
                        .builder()
                        .message("OTP sent")
                        .userId(response.getUserId())
                        .id(response.getOtpId())
                        .build()
        );
    }

    @Override
    public CompletableFuture<ModuleResponse> forgetPassword(ForgetPasswordRequest forgetPasswordRequest) {
        long startTime = System.currentTimeMillis();
        String userId = forgetPasswordRequest.getUserId();
        ValueCheckerUtil.isValidUUID(userId);
        CompletableFuture<ForgetPassword> forgetPasswordDocumentCompletableFuture = CompletableFuture.supplyAsync(() -> forgetPasswordRepository.findByUserId(userId)
                .orElseThrow(()-> new BadRequestException("User not found with id " + userId)));
        CompletableFuture<UserAuth> userAuthEntityCompletableFuture = CompletableFuture.supplyAsync(() -> userAuthRepository.findById(userId)
                .orElseThrow(()-> new BadRequestException("User not found with id " + userId)));
        AsyncUtils.getAsyncResult(CompletableFuture.allOf(forgetPasswordDocumentCompletableFuture, userAuthEntityCompletableFuture));
        ForgetPassword byUserId = forgetPasswordDocumentCompletableFuture.join();
        UserAuth userById = userAuthEntityCompletableFuture.join();
        if (!byUserId.getOtp().equals(forgetPasswordRequest.getOtp()))
            throw new AuthenticationException("OTP does not match");
        if (ObjectUtils.isEmpty(forgetPasswordRequest.getNewPassword())) {
            throw new AuthenticationException("Password is mandatory and could not be empty");
        }
        if (!Pattern.matches(ServiceConstants.PASSWORD_REGEX, forgetPasswordRequest.getNewPassword())) {
            throw new AuthenticationException("Password must contain 1 Uppercase, 1 Lowercase, 1 Number and 1 Special Case with more than or equal to 8 digits");
        }
        userById.setPassword(passwordEncoder.encode(forgetPasswordRequest.getNewPassword()));
        userById.setLastPasswordChanged(new Date());
        userAuthRepository.save(userById);
        CompletableFuture.runAsync(() -> forgetPasswordRepository.delete(byUserId));
        log.info("Execution Time (Forget Password) -> {}ms", System.currentTimeMillis() - startTime);
        return CompletableFuture.completedFuture(
                ModuleResponse
                        .builder()
                        .message("Password Updated")
                        .userId(userId)
                        .build()
        );
    }
}
