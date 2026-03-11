package in.bank.hdfc.auth.hybrid_auth.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserIdentifyResponse {

    private UUID userId;

    private boolean whatsappRegistered;
}