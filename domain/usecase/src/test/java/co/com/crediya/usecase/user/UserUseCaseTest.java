package co.com.crediya.usecase.user;

import co.com.crediya.model.role.Role;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exception.EmailAlreadyExistsException;
import co.com.crediya.model.user.exception.ValidationException;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private UserUseCase useCase;

    @BeforeEach
    void setUp(){
        useCase = new UserUseCase(userRepository);
    }

    private User validUser(){
        return User.builder()
                .id(null)
                .name("Pepe")
                .lastName("Perez")
                .email("Pepe.Perez@Example.COM")
                .password("hash-ya-generado")
                .baseSalary(1_000_000L)
                .role(null)
                .build();
    }

    @Test
    void createUser_shouldError_whenEmailExists() {
        User u = validUser();
        String emailLower = u.getEmail().toLowerCase();

        when(userRepository.existsByEmail(emailLower)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.createUser(u))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(EmailAlreadyExistsException.class);
                    assertThat(ex.getMessage()).isEqualTo("Correo electronico no valido");
                })
                .verify();

        verify(userRepository, times(1)).existsByEmail(emailLower);
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldError_whenNameBlank() {
        User u = validUser().toBuilder().name(" ").build();

        StepVerifier.create(useCase.createUser(u))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("name is required");
                })
                .verify();

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldError_whenLastNameBlank() {
        User u = validUser().toBuilder().lastName("").build();

        StepVerifier.create(useCase.createUser(u))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("lastName is required");
                })
                .verify();

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldError_whenEmailBlank() {
        User u = validUser().toBuilder().email(" ").build();

        StepVerifier.create(useCase.createUser(u))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("email is required");
                })
                .verify();

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldError_whenPasswordBlank() {
        User u = validUser().toBuilder().password(" ").build();

        StepVerifier.create(useCase.createUser(u))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("passwordHash is required");
                })
                .verify();

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldError_whenBaseSalaryNull() {
        User u = validUser().toBuilder().baseSalary(null).build();

        StepVerifier.create(useCase.createUser(u))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("baseSalary must be between 0 and 15000000");
                })
                .verify();

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldError_whenBaseSalaryNegative() {
        User u = validUser().toBuilder().baseSalary(-1L).build();

        StepVerifier.create(useCase.createUser(u))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("baseSalary must be between 0 and 15000000");
                })
                .verify();

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldError_whenBaseSalaryTooHigh() {
        User u = validUser().toBuilder().baseSalary(15_000_001L).build();

        StepVerifier.create(useCase.createUser(u))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(ValidationException.class);
                    assertThat(ex.getMessage()).isEqualTo("baseSalary must be between 0 and 15000000");
                })
                .verify();

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldSave_whenAllValid() {
        User input = validUser();
        String emailLower = input.getEmail().toLowerCase();

        // mock: no existe el email
        when(userRepository.existsByEmail(emailLower)).thenReturn(Mono.just(false));

        // mock: guarda cualquier User; devuelve el mismo con id seteado
        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> {
                    User toSave = inv.getArgument(0);
                    return Mono.just(toSave.toBuilder().id("generated-id-123").build());
                });

        StepVerifier.create(useCase.createUser(input))
                .assertNext(u -> {
                    assertThat(u.getId()).isEqualTo("generated-id-123");
                    assertThat(u.getEmail()).isEqualTo(emailLower);
                    assertThat(u.getRole()).isEqualTo(Role.CLIENT);   // <-- default esperado
                    assertThat(u.getName()).isEqualTo(input.getName());
                    assertThat(u.getLastName()).isEqualTo(input.getLastName());
                    assertThat(u.getBaseSalary()).isEqualTo(input.getBaseSalary());
                    assertThat(u.getPassword()).isEqualTo(input.getPassword());
                })
                .verifyComplete();

        // Captura lo que realmente se intentó guardar y valida normalización
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).existsByEmail(emailLower);
        verify(userRepository, times(1)).save(captor.capture());
        User toSave = captor.getValue();

        assertThat(toSave.getEmail()).isEqualTo(emailLower);
        assertThat(toSave.getRole()).isEqualTo(Role.CLIENT);
    }


    @Test
    void getUserByEmail_shouldLowercaseBeforeQuery() {
        String mixed = "Mi.Email@Example.com";
        String lower = mixed.toLowerCase();
        User mocked = validUser().toBuilder().email(lower).build();

        when(userRepository.findByEmail(lower)).thenReturn(Mono.just(mocked));

        StepVerifier.create(useCase.getUserByEmail(mixed))
                .expectNext(mocked)
                .verifyComplete();

        verify(userRepository).findByEmail(lower);
    }

    @Test
    void existsByEmail_passThrough() {
        when(userRepository.existsByEmail("x@y.com")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.existsByEmail("x@y.com"))
                .expectNext(true)
                .verifyComplete();

        verify(userRepository, times(1)).existsByEmail("x@y.com");
    }

}
