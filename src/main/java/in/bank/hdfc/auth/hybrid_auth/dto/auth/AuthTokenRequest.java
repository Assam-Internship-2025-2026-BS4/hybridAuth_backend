package in.bank.hdfc.auth.hybrid_auth.dto.auth;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AuthTokenRequest {
    private UUID sessionId;
}