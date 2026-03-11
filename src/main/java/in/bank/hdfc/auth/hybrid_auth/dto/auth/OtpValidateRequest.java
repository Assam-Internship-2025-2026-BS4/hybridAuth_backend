package in.bank.hdfc.auth.hybrid_auth.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.util.UUID;

@Getter
public class OtpValidateRequest {

    @NotNull
    private UUID otpSessionId;

    @NotBlank
    @Pattern(regexp = "\\d{6}", message = "INVALID_OTP")
    private String otp;
}