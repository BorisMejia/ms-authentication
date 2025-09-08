package co.com.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {

    @Id
    private String id;
    private String name;
    private String lastName;
    private LocalDate birthday;
    private String address;
    private String phone;
    private String email;
    private String password;
    private Long baseSalary;
    private Long roleId;
    private String document;
}
