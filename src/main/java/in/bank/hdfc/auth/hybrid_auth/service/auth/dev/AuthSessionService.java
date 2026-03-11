package in.bank.hdfc.auth.hybrid_auth.service.auth.dev;

import in.bank.hdfc.auth.hybrid_auth.entities.AuthSession;
import in.bank.hdfc.auth.hybrid_auth.entities.AuthType;
import in.bank.hdfc.auth.hybrid_auth.security.ClientType;

import java.util.UUID;

public interface AuthSessionService {

    AuthSession create(String deviceId, ClientType clientType, AuthType authType);

    AuthSession fetch(UUID sessionId, String deviceId);

    void approveByDevice(UUID sessionId, String deviceId, ClientType clientType);

    void rejectByDevice(UUID sessionId, String deviceId, ClientType clientType);

    AuthSession get(UUID sessionId);

}