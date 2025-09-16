package co.com.crediya.api.user.login;

import co.com.crediya.api.user.login.dto.request.LoginRequestDto;
import co.com.crediya.api.user.login.support.AuthenticateAndIssueToken;
import co.com.crediya.api.user.login.support.LoginAssembler;
import co.com.crediya.api.user.support.ResponseUtils;
import co.com.crediya.api.user.validations.LoginValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class LoginHandler {

    private final LoginValidator loginValidator;
    private final LoginAssembler loginAssembler;
    private final AuthenticateAndIssueToken authenticationToken;

    public Mono<ServerResponse> login(ServerRequest req) {
        return req.bodyToMono(LoginRequestDto.class)
                .flatMap(loginValidator::validate)
                .map(loginAssembler::normalize)
                .flatMap(authenticationToken::authenticateAndIssueToken)
                .flatMap(ResponseUtils::okJson);
    }

}


