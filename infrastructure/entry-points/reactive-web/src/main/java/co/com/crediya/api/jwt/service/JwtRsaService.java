package co.com.crediya.api.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


public class JwtRsaService {

    private final Algorithm algorithm;
    private final String issuer;
    private final long expMinutes;

    public JwtRsaService(RSAPublicKey publicKey, RSAPrivateKey privateKey, String issuer, long expMinutes) {
        this.algorithm = Algorithm.RSA256(publicKey, privateKey);
        this.issuer = issuer;
        this.expMinutes = expMinutes;
    }

    public String generateTimeToken(String email, String role){
        var now = Instant.now();
        var exp = now.plus(expMinutes, ChronoUnit.MINUTES);
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(email)
                .withClaim("role", role)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(algorithm);
    }

    public boolean validateToken(String token){
        try {
            JWT.require(algorithm).withIssuer(issuer).build().verify(token); return true;
        }catch (Exception exception){return false;}
    }

    public String subject(String token){return JWT.decode(token).getSubject(); }
    public String role(String token) {return JWT.decode(token).getClaim("role").asString();}
    public long expiresAt(String token){
        var expireToken = JWT.decode(token).getExpiresAtAsInstant();
        return expireToken == null ? 0L : expireToken.getEpochSecond();
    }


}
