package co.com.crediya.r2dbc.role.mapper;

import co.com.crediya.model.role.Role;

public final class RoleCodeMapper {
    private RoleCodeMapper() {}

    public static String toDbCode(Role role) {
        if (role == null) return "CLIENT";
        return switch (role) {
            case ADMIN   -> "ADMIN";
            case ASESOR  -> "ADVISOR";
            case CLIENTE -> "CLIENT";
        };
    }

    public static Role fromDbCode(String code) {
        return switch (code) {
            case "ADMIN"   -> Role.ADMIN;
            case "ADVISOR" -> Role.ASESOR;
            case "CLIENT"  -> Role.CLIENTE;
            default -> throw new IllegalArgumentException("Rol desconocido: " + code);
        };
    }
}
