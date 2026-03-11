package in.bank.hdfc.auth.hybrid_auth.controller;

import in.bank.hdfc.auth.hybrid_auth.dto.auth.AuthInitResponse;
import in.bank.hdfc.auth.hybrid_auth.dto.common.ApiResponse;
import in.bank.hdfc.auth.hybrid_auth.exception.AuthSessionException;
import in.bank.hdfc.auth.hybrid_auth.security.ClientType;
import in.bank.hdfc.auth.hybrid_auth.util.jwt.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthInitController {

    private final JwtUtil jwtUtil;

    public AuthInitController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/init")
    public ResponseEntity<ApiResponse<AuthInitResponse>> init(
            @RequestHeader(value = "X-Device-Id", required = false) String deviceId,
            @RequestHeader(value = "X-Client-Type", defaultValue = "WEB") String clientTypeHeader
    ) {

        if (deviceId == null || deviceId.isBlank()) {
            deviceId = UUID.randomUUID().toString();
        }

        if (deviceId.length() > 64) {
            throw new AuthSessionException("INVALID_DEVICE_ID");
        }

        ClientType clientType;
        try {
            clientType = ClientType.valueOf(clientTypeHeader.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new AuthSessionException("INVALID_CLIENT_TYPE");
        }

        if (clientType == ClientType.INTERNAL) {
            throw new AuthSessionException("CLIENT_TYPE_NOT_ALLOWED");
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        new AuthInitResponse(
                                jwtUtil.generatePreAuthToken(deviceId,clientType),
                                "Bearer",
                                jwtUtil.getPreAuthTtl()
                        )
                )
        );
    }
}