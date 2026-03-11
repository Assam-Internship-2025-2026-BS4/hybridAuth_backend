package in.bank.hdfc.auth.hybrid_auth.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class IdentifyRequest {

    private String mobile;

    private String pan;

    @NotBlank
    private String dob;

    private UUID sessionId;

    public boolean isValid() {

        boolean mobileFlow = mobile != null && !mobile.isBlank() && pan == null;
        boolean panFlow = pan != null && !pan.isBlank() && mobile == null;

        return mobileFlow || panFlow;
    }
}