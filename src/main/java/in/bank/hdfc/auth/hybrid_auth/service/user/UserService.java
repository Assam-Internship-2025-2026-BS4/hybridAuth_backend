package in.bank.hdfc.auth.hybrid_auth.service.user;

import in.bank.hdfc.auth.hybrid_auth.entities.User;
import in.bank.hdfc.auth.hybrid_auth.exception.AuthSessionException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User identify(String mobile, String pan, LocalDate dob) {

        mobile = mobile == null ? null : mobile.trim();
        pan = pan == null ? null : pan.trim().toUpperCase();

        if (mobile != null && !mobile.isBlank()) {

            return repository
                    .findByMobileAndDob(mobile, dob)
                    .orElseThrow(() -> new AuthSessionException("USER_NOT_FOUND"));

        }

        if (pan != null && !pan.isBlank()) {

            return repository
                    .findByPanAndDob(pan, dob)
                    .orElseThrow(() -> new AuthSessionException("USER_NOT_FOUND"));

        }

        throw new AuthSessionException("INVALID_IDENTIFY_INPUT");
    }

    public User getUser(UUID userId) {

        return repository.findById(userId)
                .orElseThrow(() -> new AuthSessionException("USER_NOT_FOUND"));
    }
}