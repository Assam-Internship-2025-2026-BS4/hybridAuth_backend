package in.bank.hdfc.auth.hybrid_auth.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class OtpInitResponse {

    private UUID otpSessionId;

    private long expiresIn;
}