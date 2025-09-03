package co.com.crediya.api.user.validations;

import co.com.crediya.api.user.login.dto.request.LoginRequestDto;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoginValidator {
    private final Validator validator;

    public Mono<LoginRequestDto> validate(LoginRequestDto dto) {
        var violations = validator.validate(dto);
        if (violations.isEmpty()) return Mono.just(dto);
        throw new ConstraintViolationException("Validation failed", violations);
    }
}

