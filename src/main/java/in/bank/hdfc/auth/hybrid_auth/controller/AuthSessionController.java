package in.bank.hdfc.auth.hybrid_auth.controller;


import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.bank.hdfc.auth.hybrid_auth.dto.auth.AuthSessionFetchResponse;
import in.bank.hdfc.auth.hybrid_auth.dto.auth.AuthSessionInitRequest;
import in.bank.hdfc.auth.hybrid_auth.dto.auth.AuthSessionInitResponse;
import in.bank.hdfc.auth.hybrid_auth.dto.common.ApiResponse;
import in.bank.hdfc.auth.hybrid_auth.entities.AuthSession;
import in.bank.hdfc.auth.hybrid_auth.entities.AuthType;
import in.bank.hdfc.auth.hybrid_auth.exception.AuthSessionException;
import in.bank.hdfc.auth.hybrid_auth.security.ClientType;
import in.bank.hdfc.auth.hybrid_auth.security.SecurityUtil;
import in.bank.hdfc.auth.hybrid_auth.service.auth.AuthSessionService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/v1/auth/session")
public class AuthSessionController {

    private final AuthSessionService service;

    public AuthSessionController(AuthSessionService service) {
        this.service = service;
    }

    /* ---------- INIT ---------- */

    @PostMapping("/init")
    public ApiResponse<AuthSessionInitResponse> init(
            @Valid @RequestBody AuthSessionInitRequest request,
            Authentication authentication) {

        Claims claims = SecurityUtil.getClaims(authentication);
        String deviceId = SecurityUtil.getDeviceId(claims);
        ClientType clientType = SecurityUtil.getClientType(claims);

        AuthType authType;
        try {
            authType = AuthType.valueOf(request.getAuthType().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new AuthSessionException("INVALID_AUTH_TYPE");
        }

        AuthSession session = service.create(deviceId, clientType, authType);
        return ApiResponse.success(
                new AuthSessionInitResponse(
                        session.getSessionId(),
                        session.getAuthType().name(),
                        session.getStatus().name(),
                        Duration.between(Instant.now(), session.getExpiresAt()).getSeconds()
                )
        );
    }

    /* ---------- FETCH ---------- */


    @PostMapping("/fetch")
    public ApiResponse<AuthSessionFetchResponse> fetch(
            @RequestParam UUID sessionId,
            Authentication authentication) {

        Claims claims = SecurityUtil.getClaims(authentication);
        String deviceId = claims.get("deviceId", String.class);

        AuthSession session = service.fetch(sessionId, deviceId);
        return ApiResponse.success(
                new AuthSessionFetchResponse(session.getStatus().name())
        );
    }

    /* ---------- APPROVE ---------- */

    @PostMapping("/approve")
    public ApiResponse<Void> approve(
            @RequestParam UUID sessionId,
            Authentication authentication) {

        Claims claims = SecurityUtil.getClaims(authentication);

        String deviceId = SecurityUtil.getDeviceId(claims);
        ClientType clientType = SecurityUtil.getClientType(claims);

        service.approveByDevice(sessionId, deviceId, clientType);
        return ApiResponse.success(null);
    }

    /* ---------- REJECT ---------- */
    @PostMapping("/reject")
    public ApiResponse<Void> reject(
            @RequestParam UUID sessionId,
            Authentication authentication) {

        Claims claims = SecurityUtil.getClaims(authentication);

        String deviceId = SecurityUtil.getDeviceId(claims);
        ClientType clientType = SecurityUtil.getClientType(claims);

        service.rejectByDevice(sessionId, deviceId, clientType);
        return ApiResponse.success(null);
    }
}