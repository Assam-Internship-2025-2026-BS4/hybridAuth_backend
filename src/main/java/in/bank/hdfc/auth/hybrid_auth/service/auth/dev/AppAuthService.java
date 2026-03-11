package in.bank.hdfc.auth.hybrid_auth.service.auth.dev;

import in.bank.hdfc.auth.hybrid_auth.entities.User;
import in.bank.hdfc.auth.hybrid_auth.security.ClientType;
import in.bank.hdfc.auth.hybrid_auth.service.user.UserRepository;
import in.bank.hdfc.auth.hybrid_auth.util.jwt.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class AppAuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AppAuthService(JwtUtil jwtUtil,
                          UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    public String login(String deviceId, String mobile) {

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        return jwtUtil.generateAppToken(
                user.getUserId(),
                deviceId,
                ClientType.MOBILE_APP
        );
    }
}