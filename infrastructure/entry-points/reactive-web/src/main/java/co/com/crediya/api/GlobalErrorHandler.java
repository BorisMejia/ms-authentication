package co.com.crediya.api;

import co.com.crediya.model.user.exception.DomainException;
import co.com.crediya.model.user.exception.NotFoundException;
import co.com.crediya.model.user.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ServerCodecConfigurer codecs;

//    @Value(staticConstructor = "of")
//    public static class ApiError {
//        int status;
//        String message;
//    }
//
//    @Override
//    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
//        log.error("Unhandled error", ex);
//
//        HttpStatus status = HttpStatus.BAD_REQUEST;
//        String message = ex.getMessage() == null ? "Bad request" : ex.getMessage();
//
//        if (ex instanceof IllegalArgumentException) {
//            status = HttpStatus.BAD_REQUEST;
//        } else {
//            status = HttpStatus.INTERNAL_SERVER_ERROR;
//            message = "Internal error";
//        }
//
//        exchange.getResponse().setStatusCode(status);
//        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
//        var bytes = ("{\"status\":" + status.value() + ",\"message\":\"" +
//                message.replace("\"","'") + "\"}")
//                .getBytes(StandardCharsets.UTF_8);
//
//        var buffer = exchange.getResponse().bufferFactory().wrap(bytes);
//        return exchange.getResponse().writeWith(Mono.just(buffer));
//    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        var status = httpStatus(ex);
        var body = Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", ex.getMessage()
        );

        var response = ServerResponse.status(status).body(BodyInserters.fromValue(body));
        var request = ServerRequest.create(exchange, codecs.getReaders());

        return response.flatMap(res -> res.writeTo(exchange, new HandlerStrategiesResponseContext(
                HandlerStrategies.builder().codecs(c -> c.defaultCodecs()).build()
        )));
    }

    private HttpStatus httpStatus(Throwable ex) {
        if (ex instanceof ValidationException) return HttpStatus.BAD_REQUEST;
        if (ex instanceof NotFoundException) return HttpStatus.NOT_FOUND;
        if (ex instanceof DomainException || ex instanceof IllegalArgumentException) return HttpStatus.BAD_REQUEST;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private record HandlerStrategiesResponseContext(HandlerStrategies strategies) implements ServerResponse.Context {
        @Override public List<HttpMessageWriter<?>> messageWriters() { return strategies.messageWriters(); }
        @Override public List<ViewResolver> viewResolvers() { return strategies.viewResolvers(); }
    }

}
