package in.bank.hdfc.auth.hybrid_auth.util.jwt;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class RsaKeyLoader {

    public static PrivateKey loadPrivateKey(String path) throws Exception {
        String key = read(path)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    public static PublicKey loadPublicKey(String path) throws Exception {
        String key = read(path)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        return KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
    }

    private static String read(String path) throws Exception {
        try (InputStream is = RsaKeyLoader.class
                .getClassLoader()
                .getResourceAsStream(path)) {

            if (is == null) {
                throw new RuntimeException("Key not found: " + path);
            }

            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}