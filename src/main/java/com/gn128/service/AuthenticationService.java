package com.gn128.service;

import com.gn128.payloads.record.RemoteAddressResponse;
import com.gn128.payloads.request.AuthenticationRequest;
import com.gn128.payloads.request.ForgetPasswordRequest;
import com.gn128.payloads.request.GoogleLoginRequest;
import com.gn128.payloads.request.RegisterRequest;
import com.gn128.payloads.response.AuthResponse;
import com.gn128.payloads.response.ModuleResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.CompletableFuture;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.service
 * Created_on - November 10 - 2024
 * Created_at - 13:43
 */

public interface AuthenticationService {

    ModuleResponse register(RegisterRequest registerRequest, HttpServletRequest httpServletRequest);
    AuthResponse authenticate(AuthenticationRequest authenticationRequest, HttpServletRequest httpServletRequest);
    AuthResponse googleSSO(GoogleLoginRequest googleLoginRequest, HttpServletRequest httpServletRequest);
    ModuleResponse verifyOtp(String otp, String userId);
    ModuleResponse resendOtp(String userId);
    AuthResponse logoutUser(HttpServletRequest request, HttpServletResponse response);
    AuthResponse refreshToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
    RemoteAddressResponse remoteAddress(HttpServletRequest request);
    CompletableFuture<ModuleResponse> forgetPasswordOtp(String email);
    CompletableFuture<ModuleResponse> forgetPassword(ForgetPasswordRequest forgetPasswordRequest);
}
