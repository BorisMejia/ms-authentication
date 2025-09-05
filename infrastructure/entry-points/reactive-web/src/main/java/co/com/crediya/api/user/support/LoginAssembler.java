package co.com.crediya.api.user.support;

import co.com.crediya.api.user.login.dto.request.LoginRequestDto;
import org.springframework.stereotype.Component;

@Component
public class LoginAssembler {

    public Credentials normalize(LoginRequestDto dto) {
        return new Credentials(dto.email(), dto.password());
    }

    public record Credentials(String emailLower, String rawPassword) {}
}
