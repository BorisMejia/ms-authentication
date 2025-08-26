package co.com.crediya.usecase.user;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exception.EmailAlreadyExists;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase implements IUserUseCase{

    private final UserRepository userRepository;
    public Mono<User> createUser(User user) {
        return userRepository
                .existsByEmail(user.getEmail())
                .flatMap(exists ->
                {
                    if (exists){
                        return Mono.error(new EmailAlreadyExists(user.getEmail()));
                    }
                    return userRepository.save(user);

                });
    }

    public Mono<Boolean>  existsByEmail(String email) {
        return userRepository.existsByEmail(email);

    }
}
