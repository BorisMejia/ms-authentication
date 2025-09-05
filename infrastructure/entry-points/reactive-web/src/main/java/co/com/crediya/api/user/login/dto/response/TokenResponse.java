package co.com.crediya.api.user.login.dto.response;

public record TokenResponse(
        String token, long expiresAtEpochSeconds
) {
}
