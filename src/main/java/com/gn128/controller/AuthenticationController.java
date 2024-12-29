package com.gn128.controller;

import com.gn128.payloads.request.AuthenticationRequest;
import com.gn128.payloads.request.ForgetPasswordRequest;
import com.gn128.payloads.request.GoogleLoginRequest;
import com.gn128.payloads.request.RegisterRequest;
import com.gn128.payloads.response.AuthResponse;
import com.gn128.payloads.response.ExceptionResponse;
import com.gn128.payloads.response.ModuleResponse;
import com.gn128.service.AuthenticationService;
import com.gn128.utils.AsyncUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.controller
 * Created_on - November 10 - 2024
 * Created_at - 13:42
 */

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @Operation(
            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ModuleResponse.class)
                    )),
                    @ApiResponse(description = "No Content", responseCode = "401", content = {
                            @Content(schema = @Schema(implementation = Void.class))
                    }),
                    @ApiResponse(description = "FORBIDDEN", responseCode = "403", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    }),
                    @ApiResponse(description = "BAD REQUEST", responseCode = "400", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
                    })
            }
    )
    public ResponseEntity<ModuleResponse> register(@RequestBody @Valid RegisterRequest registerRequest, HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(authenticationService.register(registerRequest, httpServletRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(

            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)
                    )),
                    @ApiResponse(description = "No Content", responseCode = "401", content = {
                            @Content(schema = @Schema(implementation = Void.class))
                    }),
                    @ApiResponse(description = "FORBIDDEN", responseCode = "403", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    }),
                    @ApiResponse(description = "BAD REQUEST", responseCode = "400", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
                    })
            }
    )
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest, HttpServletRequest httpServletRequest) {
        AuthResponse response = authenticationService.authenticate(authenticationRequest, httpServletRequest);
        return ResponseEntity
                .accepted()
                .header(HttpHeaders.SET_COOKIE, response.getCookie().toString())
                .body(response);
    }

    @PostMapping("/google-sso")
    @Operation(
            description = "Google SSO",
            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)
                    )),
                    @ApiResponse(description = "No Content", responseCode = "401", content = {
                            @Content(schema = @Schema(implementation = Void.class))
                    }),
                    @ApiResponse(description = "FORBIDDEN", responseCode = "403", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    }),
                    @ApiResponse(description = "BAD REQUEST", responseCode = "400", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
                    })
            }
    )
    public ResponseEntity<AuthResponse> googleSSO(@RequestBody GoogleLoginRequest googleLoginRequest, HttpServletRequest httpServletRequest) {
        AuthResponse authenticate = authenticationService.googleSSO(googleLoginRequest, httpServletRequest);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, authenticate.getCookie().toString())
                .body(authenticate);
    }

    @GetMapping("/verify-otp")
    @Operation(

            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ModuleResponse.class)
                    )),
                    @ApiResponse(description = "No Content", responseCode = "401", content = {
                            @Content(schema = @Schema(implementation = Void.class))
                    }),
                    @ApiResponse(description = "FORBIDDEN", responseCode = "403", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    }),
                    @ApiResponse(description = "BAD REQUEST", responseCode = "400", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
                    })
            }
    )
    public ResponseEntity<ModuleResponse> verifyOtp(@RequestHeader("otp") String otp, @RequestParam("userId") String userId) {
        return ResponseEntity.ok(authenticationService.verifyOtp(otp, userId));
    }

    @GetMapping("/resend-otp")
    @Operation(

            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ModuleResponse.class)
                    )),
                    @ApiResponse(description = "No Content", responseCode = "401", content = {
                            @Content(schema = @Schema(implementation = Void.class))
                    }),
                    @ApiResponse(description = "FORBIDDEN", responseCode = "403", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    }),
                    @ApiResponse(description = "BAD REQUEST", responseCode = "400", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
                    })
            }
    )
    public ResponseEntity<ModuleResponse> resendOtp(@RequestParam(value = "userId") String userId) {
        return ResponseEntity.ok(authenticationService.resendOtp(userId));
    }

    @DeleteMapping("/logout")
    @Operation(

            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)
                    )),
                    @ApiResponse(description = "No Content", responseCode = "401", content = {
                            @Content(schema = @Schema(implementation = Void.class))
                    }),
                    @ApiResponse(description = "FORBIDDEN", responseCode = "403", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    }),
                    @ApiResponse(description = "BAD REQUEST", responseCode = "400", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
                    })
            }
    )
    public ResponseEntity<AuthResponse> logoutUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        AuthResponse authResponse = authenticationService.logoutUser(httpServletRequest, httpServletResponse);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, authResponse.getCookie().toString())
                .body(authResponse);
    }

    @GetMapping("/refresh-token")
    @Operation(

            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)
                    )),
                    @ApiResponse(description = "No Content", responseCode = "401", content = {
                            @Content(schema = @Schema(implementation = Void.class))
                    }),
                    @ApiResponse(description = "FORBIDDEN", responseCode = "403", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    }),
                    @ApiResponse(description = "BAD REQUEST", responseCode = "400", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
                    })
            }
    )
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        AuthResponse response = authenticationService.refreshToken(httpServletRequest, httpServletResponse);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, response.getCookie().toString())
                .body(response);
    }

    @GetMapping("/remote-address")
    @Operation(

            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = String.class)
                    )),
                    @ApiResponse(description = "No Content", responseCode = "401", content = {
                            @Content(schema = @Schema(implementation = Void.class))
                    }),
                    @ApiResponse(description = "FORBIDDEN", responseCode = "403", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    }),
                    @ApiResponse(description = "BAD REQUEST", responseCode = "400", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
                    })
            }
    )
    public ResponseEntity<String> userIp(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String clientIp = httpServletRequest.getHeader("X-Forwarded-For");
        if (clientIp != null && clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0].trim();
        }
        return ResponseEntity.ok(clientIp);
    }

    @GetMapping("/forget-password")
    @Operation(
            summary = "Forget Password OTP",
            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ModuleResponse.class)
                    )),
                    @ApiResponse(description = "No Content", responseCode = "401", content = {
                            @Content(schema = @Schema(implementation = Void.class))
                    }),
                    @ApiResponse(description = "FORBIDDEN", responseCode = "403", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    }),
                    @ApiResponse(description = "BAD REQUEST", responseCode = "400", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
                    })
            }
    )
    public ResponseEntity<ModuleResponse> forgetPasswordOtp(@RequestParam(name = "email") String email) {
        return ResponseEntity.ok(AsyncUtils.getAsyncResult(authenticationService.forgetPasswordOtp(email)));
    }

    @PostMapping("/forget-password")
    @Operation(
            summary = "Forget Password",
            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ModuleResponse.class)
                    )),
                    @ApiResponse(description = "No Content", responseCode = "401", content = {
                            @Content(schema = @Schema(implementation = Void.class))
                    }),
                    @ApiResponse(description = "FORBIDDEN", responseCode = "403", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    }),
                    @ApiResponse(description = "BAD REQUEST", responseCode = "400", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
                    })
            }
    )
    public ResponseEntity<ModuleResponse> forgetPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest) {
        return ResponseEntity.ok(AsyncUtils.getAsyncResult(authenticationService.forgetPassword(forgetPasswordRequest)));
    }
}
