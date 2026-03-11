package in.bank.hdfc.auth.hybrid_auth.controller;

import in.bank.hdfc.auth.hybrid_auth.dto.common.ApiResponse;
import in.bank.hdfc.auth.hybrid_auth.dto.user.UserIdentifyRequest;
import in.bank.hdfc.auth.hybrid_auth.dto.user.UserIdentifyResponse;
import in.bank.hdfc.auth.hybrid_auth.dto.user.UserDetailsResponse;
import in.bank.hdfc.auth.hybrid_auth.entities.User;
import in.bank.hdfc.auth.hybrid_auth.security.SecurityUtil;
import in.bank.hdfc.auth.hybrid_auth.service.auth.AuthSessionService;
import in.bank.hdfc.auth.hybrid_auth.service.user.UserService;
import in.bank.hdfc.auth.hybrid_auth.util.jwt.JwtScopes;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user/details")
public class UserController {

    private final UserService service;
    private final AuthSessionService authSessionService;

    public UserController(
            UserService service,
            AuthSessionService authSessionService) {

        this.service = service;
        this.authSessionService = authSessionService;
    }

    /* ---------- IDENTIFY USER ---------- */
    @PostMapping("/identify")
    public ApiResponse<UserIdentifyResponse> identify(
            @Valid @RequestBody UserIdentifyRequest request,
            Authentication authentication) {

        Claims claims = SecurityUtil.getClaims(authentication);

        SecurityUtil.requireScope(claims, JwtScopes.PRE_AUTH);

        String deviceId = SecurityUtil.getDeviceId(claims);

        if (request.getSessionId() == null) {
            throw new IllegalArgumentException("SESSION_ID_REQUIRED");
        }

        /* validate session ownership & expiry */
        authSessionService.fetch(request.getSessionId(), deviceId);

        User user = service.identify(
                request.getMobile(),
                request.getPan(),
                request.getDob()
        );

        authSessionService.bindUser(
                request.getSessionId(),
                user.getUserId(),
                deviceId
        );

        return ApiResponse.success(
                new UserIdentifyResponse(
                        user.getUserId(),
                        user.isWhatsappRegistered()
                )
        );
    }
    /* ---------- FETCH USER DETAILS ---------- */

    @PostMapping("/fetch")
    public ApiResponse<UserDetailsResponse> fetch(
            @RequestParam UUID userId) {

        User user = service.getUser(userId);

        return ApiResponse.success(
                new UserDetailsResponse(
                        user.getUserId(),
                        user.getName(),
                        user.getMobile()
                )
        );
    }
@GetMapping("/me")
public ApiResponse<UserDetailsResponse> me(Authentication authentication) {

    Claims claims = SecurityUtil.getClaims(authentication);

    SecurityUtil.requireScope(claims, JwtScopes.USER);

    UUID userId = SecurityUtil.getUserId(claims);

    User user = service.getUser(userId);

    return ApiResponse.success(
            new UserDetailsResponse(
                    user.getUserId(),
                    user.getName(),
                    user.getMobile()
            )
    );
}
}