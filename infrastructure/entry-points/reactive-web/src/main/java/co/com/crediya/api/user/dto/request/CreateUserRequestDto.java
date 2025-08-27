package co.com.crediya.api.user.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateUserRequestDto(
        @NotBlank String name,
        @NotBlank String lastName,
        LocalDate birthday,
        String     address,
        String     phone,
        @NotBlank @Email String email,
        @NotNull @DecimalMin("0") @DecimalMax("15000000") BigDecimal baseSalary,
        String roleId
) {
}
