package co.com.crediya.api.user.support;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public final class ResponseUtils {
    private ResponseUtils() {}
    public static <T> Mono<ServerResponse> okJson(T body) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(body);
    }
}