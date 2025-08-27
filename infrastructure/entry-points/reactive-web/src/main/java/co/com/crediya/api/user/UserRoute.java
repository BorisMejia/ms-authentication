package co.com.crediya.api.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.Instant;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRoute {

        @Bean
        RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
            return route(POST("/api/v1/users"), userHandler::createUser)
                    .andRoute(GET("/api/v1/health"), req ->
                            ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(
                                            Map.of(
                                                    "status", "UP",
                                                    "service", "ms-authentication",
                                                    "timestamp", Instant.now().toString()
                                            )
                                    ));
        }
}

