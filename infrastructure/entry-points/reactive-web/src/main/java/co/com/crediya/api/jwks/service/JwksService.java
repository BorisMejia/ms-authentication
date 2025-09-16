package co.com.crediya.api.jwks.service;

import org.springframework.beans.factory.annotation.Value;

import java.security.MessageDigest;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

public class JwksService {
    private final Map<String, Object> jwks;

    public JwksService(RSAPublicKey publicKey, @Value("${security.jwt.kid:}") String kid){
        String finalKid = (kid == null || kid.isBlank()) ? computeKid(publicKey) : kid;
        this.jwks = Map.of("keys", List.of(buildJwk(publicKey, finalKid)));
    }

    public Map<String, Object> current(){ return jwks; }

    private static Map<String, Object> buildJwk(RSAPublicKey pub, String kid) {
        String n = b64u(unsigned(pub.getModulus().toByteArray()));
        String e = b64u(unsigned(pub.getPublicExponent().toByteArray()));
        Map<String, Object> jwk = new LinkedHashMap<>();
        jwk.put("kty", "RSA");
        jwk.put("alg", "RS256");
        jwk.put("use", "sig");
        jwk.put("kid", kid);
        jwk.put("n", n);
        jwk.put("e", e);
        return jwk;
    }

    private static String computeKid(RSAPublicKey pub) {
        String n = b64u(unsigned(pub.getModulus().toByteArray()));
        String e = b64u(unsigned(pub.getPublicExponent().toByteArray()));
        String canonical = "{\"e\":\"" + e + "\",\"kty\":\"RSA\",\"n\":\"" + n + "\"}";
        try {
            byte[] sha = MessageDigest.getInstance("SHA-256")
                    .digest(canonical.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return b64u(sha);
        } catch (Exception ex) { throw new IllegalStateException(ex); }
    }

    private static String b64u(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    private static byte[] unsigned(byte[] b) {
        return (b.length > 1 && b[0] == 0) ? Arrays.copyOfRange(b, 1, b.length) : b;
    }

}
