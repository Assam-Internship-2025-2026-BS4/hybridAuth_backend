package in.bank.hdfc.auth.hybrid_auth.dto.auth.dev;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppLoginResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;

}