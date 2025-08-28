package co.com.crediya.api.user.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import reactor.core.publisher.Mono;

import java.util.Set;

public class BadRequestException extends RuntimeException {
    private final String details;
    public BadRequestException(String message, String details){ super(message); this.details = details; }
    public String details(){ return details; }

    public static <T> Mono<T> validateOrFail(Validator v, T dto){
        Set<ConstraintViolation<T>> violations = v.validate(dto);
        if (violations.isEmpty()) return Mono.just(dto);
        String det = violations.stream()
                .map(e -> e.getPropertyPath()+": "+e.getMessage())
                .reduce((a,b)->a+", "+b).orElse("Datos inválidos");
        return Mono.error(new BadRequestException("Solicitud inválida", det));
    }
}
