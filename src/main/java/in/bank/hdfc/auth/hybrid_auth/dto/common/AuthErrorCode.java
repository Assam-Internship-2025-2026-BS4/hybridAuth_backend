package in.bank.hdfc.auth.hybrid_auth.dto.common;

public enum AuthErrorCode {
    SESSION_NOT_FOUND,
    SESSION_EXPIRED,
    SESSION_OWNERSHIP_VIOLATION,
    INVALID_SESSION_STATE
}