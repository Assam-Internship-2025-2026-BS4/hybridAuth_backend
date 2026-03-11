package in.bank.hdfc.auth.hybrid_auth.service.Authenticator;

import in.bank.hdfc.auth.hybrid_auth.entities.AuthType;
import in.bank.hdfc.auth.hybrid_auth.service.qr.QrService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class QrAuthenticator implements Authenticator {

    private final QrService qrService;

    public QrAuthenticator(QrService qrService) {
        this.qrService = qrService;
    }

    @Override
    public AuthType getType() {
        return AuthType.QR;
    }

    @Override
    public void init(UUID sessionId) {
        qrService.generate(sessionId);
    }

    @Override
    public void validate(UUID sessionId, Object payload) {
        UUID qrId = (UUID) payload;
        qrService.validate(qrId);
    }
}