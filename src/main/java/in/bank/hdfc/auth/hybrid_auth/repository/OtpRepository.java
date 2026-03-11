package in.bank.hdfc.auth.hybrid_auth.repository;

import in.bank.hdfc.auth.hybrid_auth.entities.OtpSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<OtpSession, UUID> {
    Optional<OtpSession> findBySessionId(UUID sessionId);
}