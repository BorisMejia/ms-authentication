package co.com.crediya.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;


@Slf4j
@Component
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Value(staticConstructor = "of")
    public static class ApiError {
        int status;
        String message;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Unhandled error", ex);

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = ex.getMessage() == null ? "Bad request" : ex.getMessage();

        if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Internal error";
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        var bytes = ("{\"status\":" + status.value() + ",\"message\":\"" +
                message.replace("\"","'") + "\"}")
                .getBytes(StandardCharsets.UTF_8);

        var buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }


}
