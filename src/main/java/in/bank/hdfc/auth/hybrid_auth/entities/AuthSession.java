package in.bank.hdfc.auth.hybrid_auth.entities;

import in.bank.hdfc.auth.hybrid_auth.security.ClientType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
@Entity
@Table(
        name = "auth_session",
        indexes = {
                @Index(name = "idx_auth_session_device", columnList = "device_id"),
                @Index(name = "idx_auth_session_status", columnList = "status")
        }
)
@Getter
@Setter
public class AuthSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "session_id")
    private UUID sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "device_id", nullable = false, length = 64)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", nullable = false, length = 20)
    private AuthType authType;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false, length = 20)
    private ClientType clientType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AuthStatus status;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "consumed", nullable = false)
    private boolean consumed = false;

    @Version
    private Long version;

    /* ---------- LIFECYCLE ---------- */

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();

        if (this.status == null) {
            this.status = AuthStatus.PENDING;
        }
        if (this.expiresAt == null) {
            this.expiresAt = this.createdAt.plusSeconds(120);
        }
    }

    /* ---------- HELPERS ---------- */

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public boolean isPending() {
        return this.status == AuthStatus.PENDING;
    }

    public boolean isApproved() {
        return this.status == AuthStatus.APPROVED;
    }

    public boolean isConsumable() {
        return isApproved() && !isExpired() && !consumed;
    }

    public void markConsumed() {
        this.consumed = true;
        this.completedAt = Instant.now();
    }
}