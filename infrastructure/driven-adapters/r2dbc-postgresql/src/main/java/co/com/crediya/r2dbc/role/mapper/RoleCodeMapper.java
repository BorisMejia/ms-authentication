package co.com.crediya.r2dbc.role.mapper;

import co.com.crediya.model.role.Role;
import co.com.crediya.model.user.exception.ValidationException;

public final class RoleCodeMapper {
    public static Role fromDbCode(String code) {
        if (code == null || code.isBlank()) throw new ValidationException("Usuario sin rol asignado");
        return switch (code) {
            case "ADMIN"   -> Role.ADMIN;
            case "ADVISOR" -> Role.ADVISOR;
            case "CLIENT"  -> Role.CLIENT;
            default -> throw new ValidationException("Rol inválido: " + code);
        };
    }
    public static String toDbCode(Role role) {
        return switch (role) {
            case ADMIN   -> "ADMIN";
            case ADVISOR -> "ADVISOR";
            case CLIENT  -> "CLIENT";
        };
    }
}
