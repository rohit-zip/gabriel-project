package com.gn128.controller;

import com.gn128.authentication.UserPrincipal;
import com.gn128.payloads.ListPayload;
import com.gn128.payloads.request.InitialProfileRequest;
import com.gn128.payloads.request.ProfileRequest;
import com.gn128.payloads.response.ExceptionResponse;
import com.gn128.payloads.response.ListResponse;
import com.gn128.payloads.response.ModuleResponse;
import com.gn128.payloads.response.ProfileResponse;
import com.gn128.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.controller
 * Created_on - December 03 - 2024
 * Created_at - 19:58
 */

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/initial-addition")
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
    public ModuleResponse addProfile(@RequestBody @Valid InitialProfileRequest initialProfileRequest, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return profileService.addProfile(initialProfileRequest, userPrincipal);
    }

    @PostMapping("/list")
    @Operation(
            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ListResponse.class)
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
    public ListResponse profileList(@RequestBody ListPayload listPayload, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return profileService.list(listPayload, userPrincipal);
    }

    @PostMapping("/add-image")
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
    public ResponseEntity<ModuleResponse> addUserImages(@RequestPart List<MultipartFile> images, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.accepted().body(profileService.addUserImages(images, userPrincipal));
    }

    @GetMapping
    @Operation(
            responses = {
                    @ApiResponse(description = "SUCCESS", responseCode = "200", content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ProfileResponse.class)
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
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(profileService.getProfile(userPrincipal));
    }

    @PostMapping("/profile-detailed")
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
    public ResponseEntity<ModuleResponse> addDetailedOrUpdateProfile(@RequestBody ProfileRequest profileRequest, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new ResponseEntity<>(profileService.addDetailedOrUpdateProfile(profileRequest, userPrincipal), HttpStatus.CREATED);
    }
}
