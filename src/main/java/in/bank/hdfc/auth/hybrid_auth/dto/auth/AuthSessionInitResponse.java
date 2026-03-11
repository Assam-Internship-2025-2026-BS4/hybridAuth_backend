package in.bank.hdfc.auth.hybrid_auth.dto.auth;

import java.util.UUID;

public record AuthSessionInitResponse(UUID sessionId, String authType, String status, long expiresIn) {

}