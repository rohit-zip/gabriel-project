package com.gn128.controller;

import com.gn128.authentication.UserPrincipal;
import com.gn128.payloads.request.PaymentRequest;
import com.gn128.payloads.response.ExceptionResponse;
import com.gn128.payloads.response.ModuleResponse;
import com.gn128.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{transactionId}")
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
            },
            security = {
                    @SecurityRequirement(
                            name = "bearerAuth"
                    )
            }
    )
    public ResponseEntity<ModuleResponse> initiatePayment(
            @PathVariable String transactionId,
            @RequestBody PaymentRequest paymentRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            HttpServletRequest httpServletRequest
    ) {
        return ResponseEntity
                .accepted()
                .body(paymentService.initiatePayment(transactionId, paymentRequest, userPrincipal, httpServletRequest));
    }
}
