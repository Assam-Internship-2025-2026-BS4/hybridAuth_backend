package in.bank.hdfc.auth.hybrid_auth.service.user;

import in.bank.hdfc.auth.hybrid_auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByMobile(String mobile);

    Optional<User> findByMobileAndPanHashAndDob(
            String mobile,
            String panHash,
            LocalDate dob
    );
    Optional<User> findByMobileAndDob(String mobile, LocalDate dob);

    Optional<User> findByPanAndDob(String pan, LocalDate dob);

    boolean existsByMobile(String mobile);
}