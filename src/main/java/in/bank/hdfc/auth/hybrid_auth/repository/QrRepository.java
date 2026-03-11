package in.bank.hdfc.auth.hybrid_auth.repository;

import in.bank.hdfc.auth.hybrid_auth.entities.QrSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface QrRepository extends JpaRepository<QrSession, UUID> {
    Optional<QrSession> findBySessionId(UUID sessionId);
}