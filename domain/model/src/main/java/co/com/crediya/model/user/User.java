package co.com.crediya.model.user;
import co.com.crediya.model.role.Role;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private String id;
    private String name;
    private String lastName;
    private LocalDate birthday;
    private String address;
    private String phone;
    private String email;
    private String password;
    private Long baseSalary;
    private Role role;
}
