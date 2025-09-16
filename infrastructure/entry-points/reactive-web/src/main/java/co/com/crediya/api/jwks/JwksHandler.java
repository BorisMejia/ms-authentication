package co.com.crediya.api.jwks;

import co.com.crediya.api.jwks.service.JwksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwksHandler {
    private final JwksService jwksService;

    public Mono<ServerResponse> jwks(ServerRequest request){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CACHE_CONTROL, "public,max-age=3600")
                .bodyValue(jwksService.current());
    }


}
