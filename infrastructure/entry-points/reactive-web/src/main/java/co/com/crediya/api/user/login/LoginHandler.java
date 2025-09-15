package co.com.crediya.api.user.login;

import co.com.crediya.api.user.login.dto.request.LoginRequestDto;
import co.com.crediya.api.user.login.support.AuthenticateAndIssueToken;
import co.com.crediya.api.user.login.support.LoginAssembler;
import co.com.crediya.api.user.support.ResponseUtils;
import co.com.crediya.api.user.validations.LoginValidator;
import co.com.crediya.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
    private final UserUseCase userUseCase;


    public Mono<ServerResponse> login(ServerRequest req) {
        return req.bodyToMono(LoginRequestDto.class)
                .flatMap(loginValidator::validate)
                .map(loginAssembler::normalize)
                .flatMap(authenticationToken::authenticateAndIssueToken)
                .flatMap(ResponseUtils::okJson);
    }

    public Mono<ServerResponse> getUserInfoByDocument(ServerRequest request) {
        String document = request.pathVariable("document");
        return userUseCase.getUserInfoByDocument(document)
                .flatMap(userInfo -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userInfo))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}


