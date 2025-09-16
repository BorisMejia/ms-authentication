package co.com.crediya.api.jwks.config;

import co.com.crediya.api.jwks.service.JwksService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwksBeansConfig {

    @Bean
    public JwksService jwksService(RSAPublicKey publicKey, @Value("${security.jwt.kid:}") String kid){
        return new JwksService(publicKey, kid);
    }


}
