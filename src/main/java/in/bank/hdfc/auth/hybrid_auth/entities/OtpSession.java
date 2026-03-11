package in.bank.hdfc.auth.hybrid_auth.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class OtpSession {

    @Id
    private UUID otpSessionId;

    private UUID sessionId;
    private String otpHash;
    private Instant expiresAt;
    private int attempts;


}