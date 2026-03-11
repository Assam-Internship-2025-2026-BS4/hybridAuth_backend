package in.bank.hdfc.auth.hybrid_auth.controller.dev;


import in.bank.hdfc.auth.hybrid_auth.dto.auth.dev.AppLoginRequest;
import in.bank.hdfc.auth.hybrid_auth.dto.auth.dev.AppLoginResponse;
import in.bank.hdfc.auth.hybrid_auth.dto.common.ApiResponse;
import in.bank.hdfc.auth.hybrid_auth.service.auth.dev.AppAuthService;
import in.bank.hdfc.auth.hybrid_auth.util.jwt.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/v1/auth")
public class AppAuthController {

    private final AppAuthService service;
    private final JwtUtil jwtUtil;

    public AppAuthController(AppAuthService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/app-login")
    public ResponseEntity<ApiResponse<AppLoginResponse>> login(

            @RequestHeader(value = "X-Device-Id", required = false) String deviceId,
            @RequestBody AppLoginRequest request
    ) {

        if (deviceId == null || deviceId.isBlank()) {
            deviceId = UUID.randomUUID().toString();
        }

        String token = service.login(deviceId, request.getMobile());

        return ResponseEntity.ok(
                ApiResponse.success(
                        new AppLoginResponse(
                                token,
                                "Bearer",
                                600
                        )
                )
        );
    }
}