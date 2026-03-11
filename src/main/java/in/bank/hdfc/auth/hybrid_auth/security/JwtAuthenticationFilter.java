package in.bank.hdfc.auth.hybrid_auth.security;

import in.bank.hdfc.auth.hybrid_auth.util.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI();

        return path.startsWith("/api/v1/auth/init")
                || path.startsWith("/api/v1/auth/app-login")
                || path.startsWith("/actuator")
                || path.equals("/health")
                || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {

            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            Claims claims = jwtUtil.validate(token);

            validateClaims(claims);

            String scope = claims.get("scope", String.class);

            List<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority(scope));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            claims,
                            null,
                            authorities
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
    private void validateTokenType(String tokenType, String scope) {

        switch (tokenType) {

            case "PRE_AUTH" -> {
                if (!"PRE_AUTH".equals(scope)) {
                    throw new IllegalArgumentException("Scope mismatch");
                }
            }

            case "APP_AUTH" -> {
                if (!"INTERNAL".equals(scope)) {
                    throw new IllegalArgumentException("Scope mismatch");
                }
            }

            case "USER_AUTH" -> {
                if (!"USER".equals(scope)) {
                    throw new IllegalArgumentException("Scope mismatch");
                }
            }

            default -> throw new IllegalArgumentException("Unsupported tokenType");
        }
    }
    private void validateClaims(Claims claims) {

        String tokenType = claims.get("tokenType", String.class);
        String scope = claims.get("scope", String.class);
        String deviceId = claims.get("deviceId", String.class);
        String clientType = claims.get("clientType", String.class);

        if (tokenType == null)
            throw new IllegalArgumentException("MISSING_TOKEN_TYPE");

        if (scope == null)
            throw new IllegalArgumentException("MISSING_SCOPE");

        if (deviceId == null || deviceId.isBlank())
            throw new IllegalArgumentException("MISSING_DEVICE_ID");

        validateTokenType(tokenType, scope);

        // clientType required only for non PRE_AUTH tokens
        if (!"PRE_AUTH".equals(tokenType)) {

            if (clientType == null)
                throw new IllegalArgumentException("MISSING_CLIENT_TYPE");

            try {
                ClientType.valueOf(clientType);
            } catch (Exception ex) {
                throw new IllegalArgumentException("INVALID_CLIENT_TYPE");
            }
        }
    }
}