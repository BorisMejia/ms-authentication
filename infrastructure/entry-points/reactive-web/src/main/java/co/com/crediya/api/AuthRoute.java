package co.com.crediya.api;

import co.com.crediya.api.user.login.LoginHandler;
import co.com.crediya.api.user.login.dto.request.LoginRequestDto;
import co.com.crediya.api.user.login.dto.response.TokenResponse;
import co.com.crediya.api.user.register.RegisterHandler;
import co.com.crediya.api.user.register.dto.request.CreateUserRequestDto;
import co.com.crediya.api.user.register.dto.response.CreateUserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class AuthRoute {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/users",
                    method = RequestMethod.POST,
                    beanClass = RegisterHandler.class,
                    beanMethod = "registerUser",
                    operation = @Operation(
                            operationId = "registerUser",
                            summary = "register user",
                            description = "registro de clientes",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = CreateUserRequestDto.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "ok",
                                            content = @Content(schema = @Schema(implementation = CreateUserResponseDto.class)))
                            }


                    )
            ),
            @RouterOperation(
                    path = "/api/v1/health",
                    method = RequestMethod.GET,
                    beanClass = RegisterHandler.class,
                    beanMethod = "health",
                    operation = @Operation(
                            operationId = "health",
                            summary = "Healthcheck",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "OK")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/login",
                    method = RequestMethod.POST,
                    beanClass = LoginHandler.class,
                    beanMethod = "login",
                    operation = @Operation(
                            operationId = "loginUser",
                            summary = "login user",
                            description = "inicio de sesion para administradores, asesores y clientes",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = LoginRequestDto.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "ok",
                                            content = @Content(schema = @Schema(implementation = TokenResponse.class)))
                            }


                    )
            )
    })
    public RouterFunction<ServerResponse> routes(RegisterHandler registerHandler, LoginHandler loginHandler) {
        return RouterFunctions
                .route(GET("/api/v1/health"), registerHandler::health)
                .andRoute(POST("/api/v1/users"), registerHandler::registerUser)
                .andRoute(POST("/api/v1/login"), loginHandler::login);

    }
    record ErrorMessage(String message) {}

}
