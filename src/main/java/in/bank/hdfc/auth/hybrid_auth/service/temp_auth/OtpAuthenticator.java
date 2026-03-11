package in.bank.hdfc.auth.hybrid_auth.service.Authenticator;

import in.bank.hdfc.auth.hybrid_auth.dto.auth.OtpValidateRequest;
import in.bank.hdfc.auth.hybrid_auth.entities.AuthType;
import in.bank.hdfc.auth.hybrid_auth.service.otp.OtpService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OtpAuthenticator implements Authenticator {

    private final OtpService otpService;

    public OtpAuthenticator(OtpService otpService) {
        this.otpService = otpService;
    }

    @Override
    public AuthType getType() {
        return AuthType.OTP;
    }

    @Override
    public void init(UUID sessionId) {
        otpService.init(sessionId);
    }

    @Override
    public void validate(UUID sessionId, Object payload) {
        OtpValidateRequest req = (OtpValidateRequest) payload;
        otpService.validate(req.getOtpSessionId(), req.getOtp());
    }
}