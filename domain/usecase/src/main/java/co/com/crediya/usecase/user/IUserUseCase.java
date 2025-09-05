package co.com.crediya.usecase.user;

import co.com.crediya.model.user.User;
import reactor.core.publisher.Mono;

public interface IUserUseCase {

    Mono<User> createUser(User user);
    Mono<Boolean> existsByEmail(String email);

    Mono<User> getUserByEmail(String email);
}
