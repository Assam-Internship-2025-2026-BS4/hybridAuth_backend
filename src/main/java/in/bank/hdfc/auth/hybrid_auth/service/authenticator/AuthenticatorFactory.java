package in.bank.hdfc.auth.hybrid_auth.service.authenticator;

import in.bank.hdfc.auth.hybrid_auth.entities.AuthType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AuthenticatorFactory {

    private final Map<AuthType, Authenticator> authenticators;

    public AuthenticatorFactory(List<Authenticator> list) {
        this.authenticators = list.stream()
                .collect(Collectors.toMap(
                        Authenticator::getType,
                        Function.identity()
                ));
    }

    public Authenticator get(AuthType type) {
        Authenticator authenticator = authenticators.get(type);

        if (authenticator == null) {
            throw new IllegalArgumentException("Unsupported auth type: " + type);
        }

        return authenticator;
    }
}
