//package in.bank.hdfc.auth.hybrid_auth.controller;
//
//import in.bank.hdfc.auth.hybrid_auth.dto.auth.AuthTokenResponse;
//import in.bank.hdfc.auth.hybrid_auth.dto.common.ApiResponse;
//import in.bank.hdfc.auth.hybrid_auth.security.SecurityUtil;
//import in.bank.hdfc.auth.hybrid_auth.service.token.TokenService;
//import io.jsonwebtoken.Claims;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/v1/auth")
//public class TokenController {
//
//    private final TokenService service;
//
//    public TokenController(TokenService service) {
//        this.service = service;
//    }
//
//    @PostMapping("/token")
//    public ApiResponse<AuthTokenResponse> generate(
//            @RequestParam UUID sessionId,
//            Authentication authentication) {
//
//        Claims claims = SecurityUtil.getClaims(authentication);
//        String deviceId = claims.get("deviceId", String.class);
//
//        String token = service.generateUserToken(sessionId, deviceId);
//
//        return ApiResponse.success(
//                new AuthTokenResponse(
//                        token,
//                        "Bearer",
//                        900
//                )
//        );
//    }
//}