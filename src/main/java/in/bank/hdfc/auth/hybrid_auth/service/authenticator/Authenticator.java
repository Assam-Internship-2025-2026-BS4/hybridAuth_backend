package in.bank.hdfc.auth.hybrid_auth.service.authenticator;

import in.bank.hdfc.auth.hybrid_auth.entities.AuthType;

import java.util.UUID;

public interface Authenticator {

    AuthType getType();

    void init(UUID sessionId);

    void validate(UUID sessionId, Object payload);

}
