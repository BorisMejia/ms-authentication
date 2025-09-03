package co.com.crediya.api.user.register.dto.response;

public record CreateUserResponseDto(
        String name,
        String lastName,
        String email
) {
}
