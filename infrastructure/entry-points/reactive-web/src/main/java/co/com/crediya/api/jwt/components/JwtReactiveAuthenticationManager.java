package co.com.crediya.api.jwt.components;

import co.com.crediya.api.jwt.config.JwtAuthenticationToken;
import co.com.crediya.api.jwt.service.JwtRsaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtRsaService jwt;

    public JwtReactiveAuthenticationManager(JwtRsaService jwt) {
        this.jwt = jwt;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken tokenAuth)) return Mono.empty();

        String token = (String) tokenAuth.getCredentials();
        if (!jwt.validateToken(token)) return Mono.empty();

        String email = jwt.subject(token);
        String role   = jwt.role(token);
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        return Mono.just(new JwtAuthenticationToken(token, email, authorities));
    }


}

