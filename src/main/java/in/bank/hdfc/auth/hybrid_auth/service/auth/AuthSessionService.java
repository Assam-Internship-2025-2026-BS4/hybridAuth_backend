package in.bank.hdfc.auth.hybrid_auth.service.auth;

import in.bank.hdfc.auth.hybrid_auth.entities.AuthSession;
import in.bank.hdfc.auth.hybrid_auth.entities.AuthStatus;
import in.bank.hdfc.auth.hybrid_auth.entities.AuthType;
import in.bank.hdfc.auth.hybrid_auth.entities.User;
import in.bank.hdfc.auth.hybrid_auth.exception.AuthSessionException;
import in.bank.hdfc.auth.hybrid_auth.repository.AuthSessionRepository;
import in.bank.hdfc.auth.hybrid_auth.security.ClientType;
import in.bank.hdfc.auth.hybrid_auth.service.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
@Service
public class AuthSessionService {

    private final AuthSessionRepository repository;
    private final UserRepository userRepository;

    public AuthSessionService(AuthSessionRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    /* ---------- CREATE ---------- */
    public AuthSession create(
            String deviceId,
            ClientType clientType,
            AuthType authType
    ) {

        long activeCount =
                repository.countByDeviceIdAndStatus(deviceId, AuthStatus.PENDING);

        if (activeCount >= 3) {
            throw new AuthSessionException("TOO_MANY_ACTIVE_SESSIONS");
        }

        AuthSession session = new AuthSession();
        session.setDeviceId(deviceId);
        session.setClientType(clientType);
        session.setAuthType(authType);
        session.setStatus(AuthStatus.PENDING);
        session.setExpiresAt(Instant.now().plusSeconds(120));

        return repository.save(session);
    }

    /* ---------- FETCH ---------- */

    @Transactional
    public AuthSession fetch(UUID sessionId, String deviceId) {

        AuthSession session = getSession(sessionId);

        /* 1️⃣ Ownership check */
        if (!deviceId.equals(session.getDeviceId())) {
            throw new AuthSessionException("SESSION_OWNERSHIP_VIOLATION");
        }

        /* 2️⃣ If already terminal → just return */
        if (session.getStatus() != AuthStatus.PENDING) {
            return session;
        }

        /* 3️⃣ If pending but expired → expire once */
        if (Instant.now().isAfter(session.getExpiresAt())) {
            expire(session);
            throw new AuthSessionException("SESSION_EXPIRED");
        }

        /* 4️⃣ Still pending & valid */
        return session;
    }

    /* ---------- APPROVE ---------- */

    @Transactional
    public void approveByDevice(UUID sessionId, String deviceId, ClientType clientType) {
        AuthSession session = getSession(sessionId);
        transition(session, deviceId, clientType, AuthStatus.APPROVED, false);
    }

    @Transactional
    public void rejectByDevice(UUID sessionId, String deviceId, ClientType clientType) {
        AuthSession session = getSession(sessionId);
        transition(session, deviceId, clientType, AuthStatus.REJECTED, false);
    }

    @Transactional
    public void approveInternal(UUID sessionId) {

        AuthSession session = getSession(sessionId);

        if (session.getAuthType() != AuthType.WHATSAPP) {
            throw new AuthSessionException("INVALID_INTERNAL_APPROVAL");
        }

        transition(session, null, ClientType.INTERNAL, AuthStatus.APPROVED, true);
    }
    private void transition(
            AuthSession session,
            String deviceId,
            ClientType clientType,
            AuthStatus newStatus,
            boolean internalCall) {

        if (session.isConsumed()) {
            throw new AuthSessionException("SESSION_ALREADY_CONSUMED");
        }

        if (session.getStatus() != AuthStatus.PENDING) {
            throw new AuthSessionException(
                    "INVALID_SESSION_STATE_" + session.getStatus());
        }

        if (Instant.now().isAfter(session.getExpiresAt())) {
            expire(session);
            throw new AuthSessionException("SESSION_EXPIRED");
        }

        if (!internalCall) {

            validateClient(session, clientType);

            /* Ownership check only for non-QR auth */
            if (session.getAuthType() != AuthType.QR) {

                if (!session.getDeviceId().equals(deviceId)) {
                    throw new AuthSessionException("SESSION_OWNERSHIP_VIOLATION");
                }

            }
        }

        session.setStatus(newStatus);
        session.setCompletedAt(Instant.now());

        repository.save(session);
    }
    private void validateClient(AuthSession session, ClientType clientType) {

        switch (session.getAuthType()) {
            case QR -> {
                if (clientType != ClientType.MOBILE_APP) {
                    throw new AuthSessionException("INVALID_CLIENT_FOR_QR");
                }
            }
            case WHATSAPP -> {
                if (clientType != ClientType.WHATSAPP_APP) {
                    throw new AuthSessionException("INVALID_CLIENT_FOR_WHATSAPP");
                }
            }
            case OTP -> throw new AuthSessionException("OTP_CANNOT_BE_DEVICE_APPROVED");
        }
    }
    public AuthSession getSession(UUID sessionId) {
        return repository.findBySessionId(sessionId)
                .orElseThrow(() -> new AuthSessionException("SESSION_NOT_FOUND"));
    }
    private void expire(AuthSession session) {
        session.setStatus(AuthStatus.EXPIRED);
        session.setCompletedAt(Instant.now());
        repository.save(session);
    }
    @Transactional
    public void bindUser(UUID sessionId, UUID userId, String deviceId) {

        AuthSession session = getSession(sessionId);

        if (!session.getDeviceId().equals(deviceId)) {
            throw new AuthSessionException("SESSION_OWNERSHIP_VIOLATION");
        }

        if (session.getStatus() != AuthStatus.PENDING) {
            throw new AuthSessionException("SESSION_ALREADY_COMPLETED");
        }

        if (session.getUser() != null &&
                session.getUser().getUserId() != null &&
                !session.getUser().getUserId().equals(userId)) {

            throw new AuthSessionException("SESSION_ALREADY_BOUND");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthSessionException("USER_NOT_FOUND"));

        session.setUser(user);

        repository.save(session);
    }
    @Transactional
    public void approveOtp(UUID sessionId) {

        AuthSession session = getSession(sessionId);

        if (session.getAuthType() != AuthType.OTP) {
            throw new AuthSessionException("INVALID_OTP_APPROVAL");
        }

        transition(session, null, ClientType.INTERNAL, AuthStatus.APPROVED, true);
    }


}