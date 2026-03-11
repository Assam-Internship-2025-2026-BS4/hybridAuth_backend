package in.bank.hdfc.auth.hybrid_auth.exception;

import lombok.Getter;

@Getter
public class AuthSessionException extends RuntimeException {

    private final String code;

    public AuthSessionException(String code) {
        super(code);
        this.code = code;
    }

}