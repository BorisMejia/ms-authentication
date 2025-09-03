package co.com.crediya.api.jwt.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final String token;
    private final String email;

    // Pre-auth (solo lleva el token crudo, aún no autenticado)
    public JwtAuthenticationToken(String token) {
        super(null);
        this.token = token;
        this.email = null;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(String token, String email, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.email = email;
        setAuthenticated(true);
    }

    @Override public Object getCredentials() { return token; }
    @Override public Object getPrincipal() { return email == null ? "" : email; }
}
