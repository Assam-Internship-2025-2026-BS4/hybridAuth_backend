package in.bank.hdfc.auth.hybrid_auth.service.qr;

import in.bank.hdfc.auth.hybrid_auth.entities.*;
import in.bank.hdfc.auth.hybrid_auth.exception.AuthSessionException;
import in.bank.hdfc.auth.hybrid_auth.repository.QrRepository;
import in.bank.hdfc.auth.hybrid_auth.security.ClientType;
import in.bank.hdfc.auth.hybrid_auth.service.auth.AuthSessionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class QrService {

    private final QrRepository repository;
    private final AuthSessionService sessionService;

    public QrService(
            QrRepository repository,
            AuthSessionService sessionService) {

        this.repository = repository;
        this.sessionService = sessionService;
    }

    public QrSession generate(UUID sessionId) {
        repository.findBySessionId(sessionId)
                .ifPresent(q -> {
                    if (q.getStatus() == QrStatus.ACTIVE || q.getStatus() == QrStatus.SCANNED) {
                        throw new AuthSessionException("QR_ALREADY_GENERATED");
                    }
                });

        AuthSession session = sessionService.getSession(sessionId);


        if (session.getAuthType() != AuthType.QR) {
            throw new AuthSessionException("INVALID_SESSION_FOR_QR");
        }

        if (session.getStatus() != AuthStatus.PENDING) {
            throw new AuthSessionException("SESSION_NOT_PENDING");
        }

        QrSession qr = new QrSession();
        qr.setQrId(UUID.randomUUID());
        qr.setSessionId(sessionId);
        qr.setCreatedAt(Instant.now());
        qr.setExpiresAt(Instant.now().plusSeconds(120));
        qr.setStatus(QrStatus.ACTIVE);

        return repository.save(qr);
    }

    public void validate(UUID qrId) {

        QrSession qr = repository.findById(qrId)
                .orElseThrow(() -> new AuthSessionException("QR_NOT_FOUND"));

        if (Instant.now().isAfter(qr.getExpiresAt())) {
            qr.setStatus(QrStatus.EXPIRED);
            repository.save(qr);
            throw new AuthSessionException("QR_EXPIRED");
        }

        if (qr.getStatus() == QrStatus.SCANNED) {
            throw new AuthSessionException("QR_ALREADY_SCANNED");
        }

        if (qr.getStatus() == QrStatus.USED) {
            throw new AuthSessionException("QR_ALREADY_USED");
        }

        if (qr.getStatus() != QrStatus.ACTIVE) {
            throw new AuthSessionException("QR_INVALID_STATUS");
        }

        AuthSession session = sessionService.getSession(qr.getSessionId());

        if (session.getStatus() != AuthStatus.PENDING) {
            throw new AuthSessionException("SESSION_NOT_PENDING");
        }

        qr.setStatus(QrStatus.SCANNED);
        repository.save(qr);
    }
    public void scan(UUID qrId, String deviceId, ClientType clientType, UUID userId) {

        QrSession qr = repository.findById(qrId)
                .orElseThrow(() -> new AuthSessionException("QR_NOT_FOUND"));

        if (Instant.now().isAfter(qr.getExpiresAt())) {
            qr.setStatus(QrStatus.EXPIRED);
            repository.save(qr);
            throw new AuthSessionException("QR_EXPIRED");
        }

        if (qr.getStatus() != QrStatus.ACTIVE) {
            throw new AuthSessionException("QR_ALREADY_USED");
        }

        AuthSession session = sessionService.getSession(qr.getSessionId());

        if (session.getStatus() != AuthStatus.PENDING) {
            throw new AuthSessionException("SESSION_NOT_PENDING");
        }

        /* bind mobile user to session */

        sessionService.bindUser(
                session.getSessionId(),
                userId,
                session.getDeviceId()
        );

        /* mark QR used */

        qr.setStatus(QrStatus.USED);
        repository.save(qr);

        /* approve session */

        sessionService.approveByDevice(
                session.getSessionId(),
                deviceId,
                clientType
        );
    }

}