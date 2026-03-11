package in.bank.hdfc.auth.hybrid_auth.controller;

import in.bank.hdfc.auth.hybrid_auth.dto.auth.QrGenerateResponse;
import in.bank.hdfc.auth.hybrid_auth.dto.common.ApiResponse;
import in.bank.hdfc.auth.hybrid_auth.entities.QrSession;
import in.bank.hdfc.auth.hybrid_auth.entities.QrStatus;
import in.bank.hdfc.auth.hybrid_auth.security.ClientType;
import in.bank.hdfc.auth.hybrid_auth.security.SecurityUtil;
import in.bank.hdfc.auth.hybrid_auth.service.qr.QrService;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/qr")
public class QrController {

    private final QrService service;

    public QrController(QrService service) {
        this.service = service;
    }

    /* ---------- GENERATE QR ---------- */

    @PostMapping("/generate")
    public ApiResponse<QrGenerateResponse> generate(
            @RequestParam UUID sessionId) {

        QrSession qr = service.generate(sessionId);


        long expiresIn = 120;

        String deeplink = "hdfc://auth/qr?qrId=" + qr.getQrId();

        return ApiResponse.success(
                new QrGenerateResponse(
                        qr.getQrId(),
                        qr.getCreatedAt().toString(),
                        expiresIn,
                        deeplink
                )
        );
    }

    /* ---------- VALIDATE QR ---------- */

    @PostMapping("/validate")
    public ApiResponse<Void> validate(
            @RequestParam UUID qrId) {

        service.validate(qrId);

        return ApiResponse.success(null);
    }
    @PostMapping("/scan")
    public ApiResponse<Void> scan(
            @RequestParam UUID qrId,
            Authentication authentication) {

        Claims claims = SecurityUtil.getClaims(authentication);

        String deviceId = SecurityUtil.getDeviceId(claims);
        ClientType clientType = SecurityUtil.getClientType(claims);

        UUID userId = UUID.fromString(
                claims.get("userId", String.class)
        );

        service.scan(qrId, deviceId, clientType, userId);

        return ApiResponse.success(null);
    }
}