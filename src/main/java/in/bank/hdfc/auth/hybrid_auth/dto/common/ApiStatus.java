package in.bank.hdfc.auth.hybrid_auth.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiStatus {
    private String code;
    private String message;
}