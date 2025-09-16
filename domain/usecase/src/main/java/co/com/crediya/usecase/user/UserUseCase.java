package co.com.crediya.usecase.user;

import co.com.crediya.model.role.Role;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exception.EmailAlreadyExistsException;
import co.com.crediya.model.user.exception.ValidationException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Locale;

@RequiredArgsConstructor
public class UserUseCase implements IUserUseCase{

    private final UserRepository userRepository;
    @Override
    public Mono<User> createUser(User user) {
        final String email = normalizeEmail(user.getEmail());
        return Mono.fromCallable(() -> sanitizeAndValidate(user, email))
                .flatMap(createUser -> userRepository.existsByEmail(email)
                        .flatMap(exists -> exists
                                ? Mono.error(new EmailAlreadyExistsException("Correo electronico no valido"))
                                : userRepository.save(createUser)));
    }

    public Mono<Boolean>  existsByEmail(String email) {
        return userRepository.existsByEmail(email);

    }

    @Override
    public Mono<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }

    public Mono<UserInfoDto> getUserInfoByDocument(String document){
        return userRepository.findAll()
                .filter(user -> document.equals(user.getDocument()))
                .next()
                .map(user ->  new UserInfoDto(user.getName(), user.getBaseSalary() != null ? user.getBaseSalary().doubleValue() : null));
    }

    private User sanitizeAndValidate(User user, String emailLower) {
        if (isBlank(user.getName()))     throw new ValidationException("name is required");
        if (isBlank(user.getLastName())) throw new ValidationException("lastName is required");
        if (isBlank(emailLower))        throw new ValidationException("email is required");
        if (isBlank(user.getPassword()))
            throw new ValidationException("passwordHash is required");
        if (isBlank(user.getDocument())) throw new ValidationException("Document is required");

        Long validateSalary = user.getBaseSalary();
        if (validateSalary == null || validateSalary < 0 || validateSalary > 15_000_000L)
            throw new ValidationException("baseSalary must be between 0 and 15000000");

        Role finalRole = user.getRole() != null ? user.getRole() : Role.CLIENT;

        return user.toBuilder()
                .email(emailLower)
                .role(finalRole)
                .build();
    }

    private static boolean isBlank(String dataUser) {
        return dataUser == null || dataUser.trim().isEmpty();
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

}
