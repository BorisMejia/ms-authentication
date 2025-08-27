package co.com.crediya.api.user;

import co.com.crediya.api.user.dto.mapper.UserMapperDto;
import co.com.crediya.api.user.dto.request.CreateUserRequestDto;
import co.com.crediya.usecase.user.IUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserHandler {

    private final IUserUseCase userUseCase;
    private final UserMapperDto userMapperDto;
    private final SpringValidatorAdapter validateAdapter;


    public Mono<ServerResponse> health(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("status","UP","timestamp", Instant.now().toString()));
    }

    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(CreateUserRequestDto.class)
                .flatMap(this::validate)
                .map(userMapperDto::toDomain)
                .doOnNext(userLog -> log.info("Creating user"))
                .flatMap(userUseCase::createUser)
                .map(userMapperDto::toResponse)
                .flatMap(resp -> ServerResponse.status(201).bodyValue(resp));
    }

    private Mono<CreateUserRequestDto> validate(CreateUserRequestDto createUserDto) {
        var errors = new BeanPropertyBindingResult(createUserDto, "createUserDto");
        validateAdapter.validate(createUserDto, errors);
        if (errors.hasErrors()) {
            log.warn("Validate fail");
            return Mono.error(new IllegalArgumentException(
                    errors.getAllErrors().get(0).getDefaultMessage()));
        }
        return Mono.just(createUserDto);
    }
}
