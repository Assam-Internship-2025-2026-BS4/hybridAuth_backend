package in.bank.hdfc.auth.hybrid_auth.service.token;

import in.bank.hdfc.auth.hybrid_auth.entities.AuthSession;
import in.bank.hdfc.auth.hybrid_auth.entities.AuthStatus;
import in.bank.hdfc.auth.hybrid_auth.exception.AuthSessionException;
import in.bank.hdfc.auth.hybrid_auth.repository.AuthSessionRepository;
import in.bank.hdfc.auth.hybrid_auth.util.jwt.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenService {

    private final AuthSessionRepository repository;
    private final JwtUtil jwtUtil;

    public TokenService(
            AuthSessionRepository repository,
            JwtUtil jwtUtil) {

        this.repository = repository;
        this.jwtUtil = jwtUtil;
    }

    public String generateUserToken(UUID sessionId, String deviceId) {

        AuthSession session = repository.findBySessionId(sessionId)
                .orElseThrow(() -> new AuthSessionException("SESSION_NOT_FOUND"));

        /* 1️⃣ Device ownership validation */
        if (!session.getDeviceId().equals(deviceId)) {
            throw new AuthSessionException("SESSION_OWNERSHIP_VIOLATION");
        }

        /* 2️⃣ Session must be approved */
        if (session.getStatus() != AuthStatus.APPROVED) {
            throw new AuthSessionException("SESSION_NOT_APPROVED");
        }

        /* 3️⃣ Generate USER JWT */
        return jwtUtil.generateUserToken(
                session.getUser().getUserId(),
                session.getDeviceId(),
                session.getClientType()
        );
    }
}