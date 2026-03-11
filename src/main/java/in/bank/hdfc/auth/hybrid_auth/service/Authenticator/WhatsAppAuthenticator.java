package in.bank.hdfc.auth.hybrid_auth.service.Authenticator;

import in.bank.hdfc.auth.hybrid_auth.entities.AuthType;
import in.bank.hdfc.auth.hybrid_auth.service.auth.AuthSessionService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WhatsAppAuthenticator implements Authenticator {

    private final AuthSessionService sessionService;

    public WhatsAppAuthenticator(AuthSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public AuthType getType() {
        return AuthType.WHATSAPP;
    }

    @Override
    public void init(UUID sessionId) {
        // send push notification to WhatsApp device
    }

    @Override
    public void validate(UUID sessionId, Object payload) {
        sessionService.approveInternal(sessionId);
    }
}