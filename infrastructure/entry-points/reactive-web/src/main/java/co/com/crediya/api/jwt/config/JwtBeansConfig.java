package co.com.crediya.api.jwt.config;

import co.com.crediya.api.jwt.components.KeyLoader;
import co.com.crediya.api.jwt.service.JwtRsaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtBeansConfig {

    @Bean
    public JwtRsaService jwtRsaService(
            @Value("${security.jwt.issuer:crediya-auth}") String issuer,
            @Value("${security.jwt.expiration-minutes:120}") long expMin,
            @Value("${security.jwt.kid:rsa-1}") String kid
    )throws Exception{
        var publicKey  = new ClassPathResource("jwtKeys/public_key.pem");
        var privateKey = new ClassPathResource("jwtKeys/private_key.pem");
        var pub  = KeyLoader.loadPublicKey(publicKey.getInputStream());
        var priv = KeyLoader.loadPrivateKey(privateKey.getInputStream());
        return new JwtRsaService(pub, priv, issuer, expMin, kid);

    }
    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        var pubRes = new ClassPathResource("jwtKeys/public_key.pem");
        return KeyLoader.loadPublicKey(pubRes.getInputStream());
    }

    @Bean
    public PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();
    }


}
