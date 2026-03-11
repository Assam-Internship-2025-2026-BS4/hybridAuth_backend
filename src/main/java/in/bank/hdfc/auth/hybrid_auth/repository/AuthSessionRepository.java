package in.bank.hdfc.auth.hybrid_auth.repository;


import in.bank.hdfc.auth.hybrid_auth.entities.AuthSession;
import in.bank.hdfc.auth.hybrid_auth.entities.AuthStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthSessionRepository
        extends JpaRepository<AuthSession, UUID> {

    Optional<AuthSession> findBySessionId(UUID sessionId);

    long countByDeviceIdAndStatus(String deviceId, AuthStatus authStatus);
}