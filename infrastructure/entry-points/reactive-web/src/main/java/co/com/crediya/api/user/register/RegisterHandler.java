package co.com.crediya.api.user.register;

import co.com.crediya.api.user.register.dto.mapper.UserMapperDto;
import co.com.crediya.api.user.register.dto.request.CreateUserRequestDto;
import co.com.crediya.api.user.support.RegisterUserAssembler;
import co.com.crediya.api.user.support.ResponseUtils;
import co.com.crediya.api.user.validations.CreateUserValidator;
import co.com.crediya.usecase.user.IUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterHandler {

    private final CreateUserValidator validator;
    public final RegisterUserAssembler registerUserAssembler;
    private final IUserUseCase userUseCase;
    private final UserMapperDto mapperDto;

    public Mono<ServerResponse> registerUser(ServerRequest request){
        return request.bodyToMono(CreateUserRequestDto.class)
                .flatMap(validator::validate)
                .map(registerUserAssembler::toDomain)
                .flatMap(userUseCase::createUser)
                .map(mapperDto::toResponse)
                .flatMap(ResponseUtils::okJson);
    }

    public Mono<ServerResponse> health(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("status","UP","timestamp", Instant.now().toString()));
    }

}
