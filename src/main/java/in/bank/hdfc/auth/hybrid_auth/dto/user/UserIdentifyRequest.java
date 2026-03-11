package in.bank.hdfc.auth.hybrid_auth.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UserIdentifyRequest {

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "INVALID_MOBILE")
    private String mobile;

    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "INVALID_PAN")
    private String pan;

    @NotNull
    @Past(message = "INVALID_DOB")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @JsonProperty("sessionId")
    @NotNull(message = "SESSION_ID_REQUIRED")
    private UUID sessionId;

    /* Ensure either mobile OR pan is present */
    @AssertTrue(message = "MOBILE_OR_PAN_REQUIRED")
    public boolean isValidIdentifier() {

        boolean mobileProvided = mobile != null && !mobile.isBlank();
        boolean panProvided = pan != null && !pan.isBlank();

        return mobileProvided ^ panProvided; // XOR
    }

    public String getPan() {
        return pan == null ? null : pan.toUpperCase();
    }
}