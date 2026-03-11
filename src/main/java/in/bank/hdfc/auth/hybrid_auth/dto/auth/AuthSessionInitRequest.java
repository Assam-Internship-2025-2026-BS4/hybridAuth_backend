package in.bank.hdfc.auth.hybrid_auth.dto.auth;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class AuthSessionInitRequest {

    @NotBlank(message = "AUTH_TYPE_REQUIRED")
    private String authType;
}