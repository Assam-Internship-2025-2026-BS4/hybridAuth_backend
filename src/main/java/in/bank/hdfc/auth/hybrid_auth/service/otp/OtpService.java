package in.bank.hdfc.auth.hybrid_auth.service.otp;

import in.bank.hdfc.auth.hybrid_auth.entities.AuthSession;
import in.bank.hdfc.auth.hybrid_auth.entities.AuthType;
import in.bank.hdfc.auth.hybrid_auth.entities.OtpSession;
import in.bank.hdfc.auth.hybrid_auth.exception.AuthSessionException;
import in.bank.hdfc.auth.hybrid_auth.repository.OtpRepository;
import in.bank.hdfc.auth.hybrid_auth.service.auth.AuthSessionService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
@Service
public class OtpService {

    private final OtpRepository repository;
    private final AuthSessionService sessionService;

    public OtpService(
            OtpRepository repository,
            AuthSessionService sessionService) {

        this.repository = repository;
        this.sessionService = sessionService;
    }

    public OtpSession init(UUID sessionId) {

        AuthSession session = sessionService.getSession(sessionId);

        if (session.getAuthType() != AuthType.OTP)
            throw new AuthSessionException("INVALID_SESSION_FOR_OTP");

        String otp = String.valueOf(
                ThreadLocalRandom.current().nextInt(100000, 999999)
        );

        OtpSession otpSession = new OtpSession();
        otpSession.setOtpSessionId(UUID.randomUUID());
        otpSession.setSessionId(sessionId);
        otpSession.setOtpHash(hash(otp));
        otpSession.setAttempts(0);
        otpSession.setExpiresAt(Instant.now().plusSeconds(120));

        repository.save(otpSession);

        // TODO integrate SMS provider
        System.out.println("OTP = " + otp);

        return otpSession;
    }

    public void validate(UUID otpSessionId, String otp) {

        OtpSession otpSession = repository.findById(otpSessionId)
                .orElseThrow(() -> new AuthSessionException("OTP_SESSION_NOT_FOUND"));

        if (Instant.now().isAfter(otpSession.getExpiresAt()))
            throw new AuthSessionException("OTP_EXPIRED");

        if (otpSession.getAttempts() >= 3)
            throw new AuthSessionException("OTP_ATTEMPTS_EXCEEDED");

        otpSession.setAttempts(otpSession.getAttempts() + 1);
        repository.save(otpSession);

        if (!hash(otp).equals(otpSession.getOtpHash()))
            throw new AuthSessionException("INVALID_OTP");

        sessionService.approveOtp(otpSession.getSessionId());
    }


    private String hash(String value) {
        return DigestUtils.md5DigestAsHex(value.getBytes());
    }
}