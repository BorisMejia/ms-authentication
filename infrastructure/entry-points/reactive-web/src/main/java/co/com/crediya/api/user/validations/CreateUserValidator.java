package co.com.crediya.api.user.validations;

import co.com.crediya.api.user.register.dto.request.CreateUserRequestDto;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;



@Component
@RequiredArgsConstructor
public class CreateUserValidator {

    private final Validator validator;
    public Mono<CreateUserRequestDto> validate(CreateUserRequestDto dto) {
        var violations = validator.validate(dto);
        if (violations.isEmpty()) return Mono.just(dto);
        throw new ConstraintViolationException("Validation failed", violations);
    }
}
