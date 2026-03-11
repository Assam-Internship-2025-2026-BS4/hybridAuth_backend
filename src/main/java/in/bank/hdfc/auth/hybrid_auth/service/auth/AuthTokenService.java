package in.bank.hdfc.auth.hybrid_auth.service.auth;

import in.bank.hdfc.auth.hybrid_auth.entities.AuthSession;
import in.bank.hdfc.auth.hybrid_auth.exception.AuthSessionException;
import in.bank.hdfc.auth.hybrid_auth.repository.AuthSessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthTokenService {

    private final AuthSessionRepository repository;

    public AuthTokenService(AuthSessionRepository repository) {
        this.repository = repository;
    }
    @Transactional
    public AuthSession validateAndConsume(UUID sessionId, String deviceId) {

        AuthSession session = repository.findBySessionId(sessionId)
                .orElseThrow(() -> new AuthSessionException("SESSION_NOT_FOUND"));

        if (!session.getDeviceId().equals(deviceId)) {
            throw new AuthSessionException("SESSION_DEVICE_MISMATCH");
        }
        if (session.getUser() == null || session.getUser().getUserId() == null) {
            throw new AuthSessionException("SESSION_USER_NOT_BOUND");
        }
        if (session.isConsumed()) {
            throw new AuthSessionException("SESSION_ALREADY_USED");
        }

        if (session.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthSessionException("SESSION_EXPIRED");
        }

        if (!session.isApproved()) {
            throw new AuthSessionException("SESSION_NOT_APPROVED");
        }
        if (!session.isConsumable()) {
            throw new AuthSessionException("SESSION_NOT_CONSUMABLE");
        }

        session.markConsumed();
        repository.save(session);

        return session;
    }
}