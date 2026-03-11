package in.bank.hdfc.auth.hybrid_auth.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class QrSession {

    @Id
    private UUID qrId;
    @Column(name = "created_at")
    private Instant createdAt;
    private UUID sessionId;
    private Instant expiresAt;
    @Enumerated(EnumType.STRING)
    private QrStatus status;
}