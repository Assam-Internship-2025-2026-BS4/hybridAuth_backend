package in.bank.hdfc.auth.hybrid_auth.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserDetailsResponse {

    private UUID userId;

    private String name;

    private String mobile;
}