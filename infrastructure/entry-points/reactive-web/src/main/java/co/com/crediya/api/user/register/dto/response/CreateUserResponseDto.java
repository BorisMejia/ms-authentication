package co.com.crediya.api.user.dto.response;

public record CreateUserResponseDto(
        String name,
        String lastName,
        String email
) {
}
