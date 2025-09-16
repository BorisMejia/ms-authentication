package co.com.crediya.api.user.login.support;

import co.com.crediya.api.jwt.service.JwtRsaService;
import co.com.crediya.api.user.login.dto.response.TokenResponse;
import co.com.crediya.model.user.exception.ValidationException;
import co.com.crediya.usecase.user.IUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class AuthenticateAndIssueToken {

    private final IUserUseCase userUseCase;
    private final PasswordEncoder passwordEncoder;
    private final JwtRsaService jwt;


    public Mono<TokenResponse> authenticateAndIssueToken(LoginAssembler.Credentials credential) {
        return userUseCase.getUserByEmail(credential.emailLower())
                .switchIfEmpty(Mono.error(new ValidationException("Credenciales inv?lidas")))
                .flatMap(user -> {
                    String encoded = user.getPassword();
                    if (encoded == null || encoded.isBlank())
                        return Mono.error(new ValidationException("Credenciales inv?lidas"));
                    try {
                        if (!passwordEncoder.matches(credential.rawPassword(), encoded))
                            return Mono.error(new ValidationException("Credenciales inv?lidas"));
                    } catch (IllegalArgumentException badFormat) {
                        return Mono.error(new ValidationException("Credenciales inv?lidas"));
                    }

                    if (user.getRole() == null)
                        return Mono.error(new ValidationException("Usuario sin rol asignado"));

                    String token = jwt.generateTimeToken(user.getEmail(), user.getRole().name(), user.getDocument());
                    return Mono.just(new TokenResponse(token, jwt.expiresAt(token)));
                });
    }
}
