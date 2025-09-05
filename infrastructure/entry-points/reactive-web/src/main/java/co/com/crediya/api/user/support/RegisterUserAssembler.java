package co.com.crediya.api.user.support;

import co.com.crediya.api.user.register.dto.request.CreateUserRequestDto;
import co.com.crediya.model.role.Role;
import co.com.crediya.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterUserAssembler {
    private final PasswordEncoder passwordEncoder;

    public User toDomain(CreateUserRequestDto createUser){
        return User.builder()
                .name(createUser.name())
                .lastName(createUser.lastName())
                .birthday(createUser.birthday())
                .address(createUser.address())
                .phone(createUser.phone())
                .email(createUser.email())
                .password(passwordEncoder.encode(createUser.password()))
                .role(Role.CLIENTE)
                .baseSalary(createUser.baseSalary())
                .build();
    }
}
