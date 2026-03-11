package in.bank.hdfc.auth.hybrid_auth.controller;

import in.bank.hdfc.auth.hybrid_auth.dto.auth.OtpInitRequest;
import in.bank.hdfc.auth.hybrid_auth.dto.auth.OtpInitResponse;
import in.bank.hdfc.auth.hybrid_auth.dto.auth.OtpValidateRequest;
import in.bank.hdfc.auth.hybrid_auth.dto.common.ApiResponse;
import in.bank.hdfc.auth.hybrid_auth.entities.OtpSession;
import in.bank.hdfc.auth.hybrid_auth.service.otp.OtpService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/auth/otp")
public class OtpController {

    private final OtpService service;

    public OtpController(OtpService service) {
        this.service = service;
    }

    /* ---------- INIT OTP ---------- */

    @PostMapping("/init")
    public ApiResponse<OtpInitResponse> init(
            @Valid @RequestBody OtpInitRequest request,
            Authentication authentication) {

        OtpSession otpSession = service.init(request.getSessionId());

        long expiresIn = Duration.between(
                Instant.now(),
                otpSession.getExpiresAt()
        ).getSeconds();

        return ApiResponse.success(
                new OtpInitResponse(
                        otpSession.getOtpSessionId(),
                        expiresIn
                )
        );
    }

    /* ---------- VALIDATE OTP ---------- */

    @PostMapping("/validate")
    public ApiResponse<Void> validate(
            @Valid @RequestBody OtpValidateRequest request) {

        service.validate(
                request.getOtpSessionId(),
                request.getOtp()
        );

        return ApiResponse.success(null);
    }
}