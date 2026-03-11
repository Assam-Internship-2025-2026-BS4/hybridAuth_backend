package in.bank.hdfc.auth.hybrid_auth.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class QrGenerateResponse {

    private UUID qrId;
    private String createdAt;
    private long expiresIn;
    private String deeplink;
}