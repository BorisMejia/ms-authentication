package co.com.crediya.api;

import co.com.crediya.model.user.exception.DomainException;
import co.com.crediya.model.user.exception.NotFoundException;
import co.com.crediya.model.user.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ServerCodecConfigurer codecs;
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        var status = httpStatus(ex);
        String method = String.valueOf(exchange.getRequest().getMethod());

        Object body;
        if (ex instanceof ConstraintViolationException cve) {
            var errors = cve.getConstraintViolations().stream()
                    .map(v -> Map.of(
                            "field", v.getPropertyPath().toString(),
                            "message", v.getMessage(),
                            "rejectedValue", String.valueOf(v.getInvalidValue())
                    ))
                    .toList();
            body = Map.of(
                    "status", status.value(),
                    "error", status.getReasonPhrase(),
                    "message", "Validation failed",
                    "errors", errors,
                    "path", exchange.getRequest().getPath().value(),
                    "method", method
            );
        } else {
            body = Map.of(
                    "status", status.value(),
                    "error", status.getReasonPhrase(),
                    "message", safeMessage(ex),
                    "path", exchange.getRequest().getPath().value(),
                    "method", method
            );
        }

        var response = ServerResponse.status(status)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body));

        var request = ServerRequest.create(exchange, codecs.getReaders());
        return response.flatMap(res -> res.writeTo(exchange,
                new HandlerStrategiesResponseContext(HandlerStrategies.withDefaults())));
    }

    private HttpStatus httpStatus(Throwable ex) {
        if (ex instanceof ConstraintViolationException) return HttpStatus.BAD_REQUEST;
        if (ex instanceof ValidationException) return HttpStatus.BAD_REQUEST;
        if (ex instanceof NotFoundException) return HttpStatus.NOT_FOUND;
        if (ex instanceof DomainException || ex instanceof IllegalArgumentException) return HttpStatus.BAD_REQUEST;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String safeMessage(Throwable ex) {
        var msg = ex.getMessage();
        return (msg == null || msg.isBlank()) ? "Unexpected error" : msg;
    }

    private record HandlerStrategiesResponseContext(HandlerStrategies strategies)
            implements ServerResponse.Context {
        @Override public List<HttpMessageWriter<?>> messageWriters() { return strategies.messageWriters(); }
        @Override public List<ViewResolver> viewResolvers() { return strategies.viewResolvers(); }
    }
}
