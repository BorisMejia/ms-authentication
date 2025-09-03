package co.com.crediya.api.user.login;

import co.com.crediya.api.jwt.service.JwtRsaService;
import co.com.crediya.api.user.login.dto.request.LoginRequestDto;
import co.com.crediya.api.user.login.dto.response.TokenResponse;
import co.com.crediya.api.user.support.LoginAssembler;
import co.com.crediya.api.user.support.ResponseUtils;
import co.com.crediya.api.user.validations.LoginValidator;
import co.com.crediya.model.user.exception.ValidationException;
import co.com.crediya.usecase.user.IUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class LoginHandler {

    private final LoginValidator loginValidator;
    private final LoginAssembler loginAssembler;
    private final IUserUseCase userUseCase;
    private final PasswordEncoder passwordEncoder;
    private final JwtRsaService jwt;

    public Mono<ServerResponse> login(ServerRequest req) {
        return req.bodyToMono(LoginRequestDto.class)
                .flatMap(loginValidator::validate)
                .map(loginAssembler::normalize)
                .flatMap(this::authenticateAndIssueToken)
                .flatMap(ResponseUtils::okJson);
    }

    private Mono<TokenResponse> authenticateAndIssueToken(LoginAssembler.Credentials credential) {
        return userUseCase.getUserByEmail(credential.emailLower())
                .switchIfEmpty(Mono.error(new ValidationException("Credenciales inválidas")))
                .flatMap(u -> {
                    String encoded = u.getPassword();
                    if (encoded == null || encoded.isBlank()) return Mono.error(new ValidationException("Credenciales inválidas"));
                    try {
                        if (!passwordEncoder.matches(credential.rawPassword(), encoded))
                            return Mono.error(new ValidationException("Credenciales inválidas"));
                    } catch (IllegalArgumentException badFormat) {
                        return Mono.error(new ValidationException("Credenciales inválidas"));
                    }
                    var role = u.getRole() != null ? u.getRole().name() : "CLIENTE";

                    var token = jwt.generateTimeToken(u.getEmail(), role);
                    return Mono.just(new TokenResponse(token, jwt.expiresAt(token)));
                });
    }


}


