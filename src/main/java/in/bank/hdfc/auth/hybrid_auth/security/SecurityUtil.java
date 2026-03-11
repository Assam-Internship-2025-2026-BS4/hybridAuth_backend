package in.bank.hdfc.auth.hybrid_auth.security;

import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import in.bank.hdfc.auth.hybrid_auth.exception.AuthSessionException;
import in.bank.hdfc.auth.hybrid_auth.util.jwt.JwtScopes;
import io.jsonwebtoken.Claims;
public final class SecurityUtil {

    private SecurityUtil() {}

    public static Claims getClaims(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthSessionException("UNAUTHENTICATED");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Claims claims)) {
            throw new AuthSessionException("INVALID_AUTH_CONTEXT");
        }

        return claims;
    }

    public static String getDeviceId(Claims claims) {

        String deviceId = claims.get("deviceId", String.class);

        if (deviceId == null
                || deviceId.isBlank()
                || deviceId.length() > 64
                || !deviceId.matches("^[a-zA-Z0-9\\-]{8,64}$")) {

            throw new AccessDeniedException("INVALID_DEVICE_ID");
        }

        return deviceId;
    }

    public static ClientType getClientType(Claims claims) {

        String value = claims.get("clientType", String.class);

        if (value == null) {
            throw new AccessDeniedException("MISSING_CLIENT_TYPE");
        }

        try {
            return ClientType.valueOf(value.toUpperCase());
        } catch (Exception ex) {
            throw new AccessDeniedException("INVALID_CLIENT_TYPE");
        }
    }

    public static UUID getUserId(Claims claims) {

        String subject = claims.getSubject();

        if (subject == null || !subject.startsWith("USER:")) {
            throw new AccessDeniedException("INVALID_SUBJECT");
        }

        try {
            return UUID.fromString(subject.substring(5));
        } catch (Exception ex) {
            throw new AccessDeniedException("INVALID_USER_ID");
        }
    }

    public static void requireScope(Claims claims, JwtScopes requiredScope) {

        String tokenScopeStr = claims.get("scope", String.class);

        if (tokenScopeStr == null) {
            throw new AccessDeniedException("MISSING_TOKEN_SCOPE");
        }

        JwtScopes tokenScope;

        try {
            tokenScope = JwtScopes.valueOf(tokenScopeStr);
        } catch (Exception ex) {
            throw new AccessDeniedException("INVALID_TOKEN_SCOPE");
        }

        if (!tokenScope.equals(requiredScope)) {
            throw new AccessDeniedException("INVALID_TOKEN_SCOPE");
        }
    }
}