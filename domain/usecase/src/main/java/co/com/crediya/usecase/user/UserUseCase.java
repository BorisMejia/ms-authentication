package co.com.crediya.usecase.user;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exception.EmailAlreadyExistsException;
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
                    if (exists)
                    {
                        return Mono.error(new EmailAlreadyExistsException(user.getEmail()));
                    }
                    if (user.getName() == null || user.getName().isBlank())
                        return Mono.error(new IllegalArgumentException("Name is required"));
                    if (user.getLastName() == null || user.getLastName().isBlank())
                        return Mono.error(new IllegalArgumentException("lastName is required"));
                    if (user.getEmail() == null || user.getEmail().isBlank())
                        return Mono.error(new IllegalArgumentException("email is required"));
                    if (user.getBaseSalary() == null ||
                            user.getBaseSalary() < 0 || user.getBaseSalary() > 15_000_000L)
                        return Mono.error(new IllegalArgumentException("baseSalary must be between 0 and 15000000"));
                    return userRepository.save(user);

                });
    }

    public Mono<Boolean>  existsByEmail(String email) {
        return userRepository.existsByEmail(email);

    }

}
