package co.com.crediya.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateUserRequestDto(
        @NotBlank String name,
        @NotBlank String lastName,
        LocalDate birthday,
        String     address,
        String     phone,
        @Schema(example = "example@gmail.com") @NotBlank @Email String email,
        @NotBlank String password,
        @Schema(example = "3500000.00", minimum = "0", maximum = "15000000")
        @NotNull @DecimalMin("0") @DecimalMax("15000000") Long baseSalary
) {
}
