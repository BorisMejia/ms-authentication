package co.com.crediya.api.user;

import co.com.crediya.api.user.dto.request.CreateUserRequestDto;
import co.com.crediya.api.user.dto.response.CreateUserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.Instant;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRoute {

//        @Bean
//        RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
//            return route(POST("/api/v1/users"), userHandler::createUser)
//                    .andRoute(GET("/api/v1/health"), req ->
//                            ServerResponse.ok()
//                                    .contentType(MediaType.APPLICATION_JSON)
//                                    .bodyValue(
//                                            Map.of(
//                                                    "status", "UP",
//                                                    "service", "ms-authentication",
//                                                    "timestamp", Instant.now().toString()
//                                            )
//                                    ));
//        }
//
//    @Bean
//    @RouterOperation
//    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
//        return route(POST("/api/v1/users"), handler::createUser)
//                .andRoute(GET("/api/v1/health"), handler::health);
//    }
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/users",
                    produces = { MediaType.APPLICATION_JSON_VALUE },
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "createUser",
                    operation = @Operation(
                            operationId = "createUser",
                            summary = "Register new applicant",
                            description = "Crea un nuevo usuario solicitante",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = CreateUserRequestDto.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Creado",
                                            content = @Content(schema = @Schema(implementation = CreateUserResponseDto.class))),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                                            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
                                    @ApiResponse(responseCode = "409", description = "Duplicado",
                                            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
                                    @ApiResponse(responseCode = "500", description = "Error interno",
                                            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/health",
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "health",
                    operation = @Operation(
                            operationId = "health",
                            summary = "Healthcheck",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "OK")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routes(UserHandler handler) {
        return route(POST("/api/v1/users"), handler::createUser)
                .andRoute(GET("/api/v1/health"), handler::health);
    }

    record ErrorMessage(String message) {}
}

