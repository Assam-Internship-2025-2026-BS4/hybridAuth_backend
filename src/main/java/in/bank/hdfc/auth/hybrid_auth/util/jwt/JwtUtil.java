package in.bank.hdfc.auth.hybrid_auth.util.jwt;

import in.bank.hdfc.auth.hybrid_auth.security.ClientType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
@Component
public final class JwtUtil {

    private static final String ISSUER = "hybrid-auth";
    private static final String AUDIENCE = "HYBRID_AUTH_API";

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    @Getter
    private final long preAuthTtl;

    public JwtUtil(@Value("${jwt.pre-auth-ttl}") long preAuthTtl) {
        this.preAuthTtl = preAuthTtl;
        try {
            this.privateKey = RsaKeyLoader.loadPrivateKey("keys/private.pem");
            this.publicKey = RsaKeyLoader.loadPublicKey("keys/public.pem");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA keys", e);
        }
    }

    public String generatePreAuthToken(String deviceId, ClientType clientType) {

        Instant now = Instant.now();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(deviceId)
                .issuer(ISSUER)
                .audience().add(AUDIENCE).and()
                .claim("scope", JwtScopes.PRE_AUTH.name())
                .claim("deviceId", deviceId)
                .claim("clientType", clientType.name())
                .claim("tokenType", "PRE_AUTH")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(preAuthTtl)))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public String generateAppToken(
            UUID userId,
            String deviceId,
            ClientType clientType) {

        Instant now = Instant.now();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(deviceId)
                .issuer(ISSUER)
                .audience().add(AUDIENCE).and()
                .claim("scope", JwtScopes.INTERNAL.name())
                .claim("clientType", clientType.name())
                .claim("deviceId", deviceId)
                .claim("userId", userId.toString())   // ⭐ important
                .claim("tokenType", "APP_AUTH")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(600)))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public String generateUserToken(UUID userId, String deviceId, ClientType clientType) {

        Instant now = Instant.now();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .issuer(ISSUER)
                .audience().add(AUDIENCE).and()
                .claim("scope", JwtScopes.USER.name())
                .claim("deviceId", deviceId)
                .claim("clientType", clientType.name())
                .claim("tokenType", "USER_AUTH")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(900)))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public Claims validate(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(publicKey)
                .requireIssuer(ISSUER)
                .requireAudience(AUDIENCE)
                .clockSkewSeconds(30)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (claims.get("tokenType") == null)
            throw new IllegalArgumentException("Missing tokenType");

        if (claims.get("scope") == null)
            throw new IllegalArgumentException("Missing scope");

        return claims;
    }
}