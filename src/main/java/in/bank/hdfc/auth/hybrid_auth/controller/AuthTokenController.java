package in.bank.hdfc.auth.hybrid_auth.controller;

import in.bank.hdfc.auth.hybrid_auth.dto.auth.AuthTokenRequest;
import in.bank.hdfc.auth.hybrid_auth.dto.auth.AuthTokenResponse;
import in.bank.hdfc.auth.hybrid_auth.dto.common.ApiResponse;
import in.bank.hdfc.auth.hybrid_auth.entities.AuthSession;
import in.bank.hdfc.auth.hybrid_auth.exception.AuthSessionException;
import in.bank.hdfc.auth.hybrid_auth.security.ClientType;
import in.bank.hdfc.auth.hybrid_auth.security.SecurityUtil;
import in.bank.hdfc.auth.hybrid_auth.service.auth.AuthTokenService;
import in.bank.hdfc.auth.hybrid_auth.util.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthTokenController {

    private final AuthTokenService tokenService;
    private final JwtUtil jwtUtil;

    public AuthTokenController(AuthTokenService tokenService, JwtUtil jwtUtil) {
        this.tokenService = tokenService;
        this.jwtUtil = jwtUtil;
    }@PostMapping("/token")
    public ApiResponse<AuthTokenResponse> token(
            @Valid @RequestBody AuthTokenRequest request,
            Authentication authentication
    ) {

        Claims claims = SecurityUtil.getClaims(authentication);

        String scope = claims.get("scope", String.class);
        String tokenType = claims.get("tokenType", String.class);

        if (!"PRE_AUTH".equals(scope) || !"PRE_AUTH".equals(tokenType)) {
            throw new AuthSessionException("INVALID_TOKEN_SCOPE");
        }

        String deviceId = SecurityUtil.getDeviceId(claims);
        ClientType clientType = SecurityUtil.getClientType(claims);

        AuthSession session =
                tokenService.validateAndConsume(request.getSessionId(), deviceId);

        if (session.getClientType() != clientType) {
            throw new AuthSessionException("CLIENT_TYPE_MISMATCH");
        }

        if (session.getUser() == null) {
            throw new AuthSessionException("SESSION_USER_NOT_BOUND");
        }

        String userToken = jwtUtil.generateUserToken(
                session.getUser().getUserId(),
                deviceId,
                clientType
        );

        return ApiResponse.success(
                new AuthTokenResponse(userToken, "Bearer", 900)
        );
    }}