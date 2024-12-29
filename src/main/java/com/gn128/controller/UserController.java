package com.gn128.controller;

import com.gn128.authentication.UserPrincipal;
import com.gn128.payloads.request.ChangePasswordRequest;
import com.gn128.payloads.response.ExceptionResponse;
import com.gn128.payloads.response.ModuleResponse;
import com.gn128.service.UserService;
import com.gn128.utils.AsyncUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.controller
 * Created_on - November 13 - 2024
 * Created_at - 20:28
 */

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/update-password")
    @Operation(
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = false
            ),
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
            },
            security = {
                    @SecurityRequirement(
                            name = "bearerAuth"
                    )
            }
    )
    public ResponseEntity<ModuleResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(AsyncUtils.getAsyncResult(userService.changePassword(changePasswordRequest, userPrincipal)));
    }
}
