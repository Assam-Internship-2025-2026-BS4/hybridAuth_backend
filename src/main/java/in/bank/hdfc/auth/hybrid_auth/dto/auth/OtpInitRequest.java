package in.bank.hdfc.auth.hybrid_auth.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OtpInitRequest {

    @NotNull(message = "SESSION_ID_REQUIRED")
    private UUID sessionId;

}