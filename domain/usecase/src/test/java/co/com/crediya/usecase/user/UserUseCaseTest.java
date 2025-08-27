package co.com.crediya.usecase.user;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exception.EmailAlreadyExistsException;
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
                .email("perez@example.com")
                .baseSalary(1_000_000L)
                .build();
    }

    @Test
    void createUser_shouldError_whenEmailExists() {
        User u = validUser();
        when(userRepository.existsByEmail(u.getEmail())).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.createUser(u))
                .expectErrorSatisfies(ex -> assertThat(ex).isInstanceOf(EmailAlreadyExistsException.class))
                .verify();

        verify(userRepository, times(1)).existsByEmail(u.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldError_whenNameBlank() {
        User u = validUser().toBuilder().name("").build();
        when(userRepository.existsByEmail(u.getEmail())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.createUser(u))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(IllegalArgumentException.class);
                    assertThat(ex.getMessage()).isEqualTo("Name is required");
                })
                .verify();

        verify(userRepository).existsByEmail(u.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldError_whenLastNameBlank() {
        User u = validUser().toBuilder().lastName(" ").build();
        when(userRepository.existsByEmail(u.getEmail())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.createUser(u))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(IllegalArgumentException.class);
                    assertThat(ex.getMessage()).isEqualTo("lastName is required");
                })
                .verify();

        verify(userRepository).existsByEmail(u.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldError_whenEmailBlank() {
        User u = validUser().toBuilder().email(" ").build();
        when(userRepository.existsByEmail(u.getEmail())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.createUser(u))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(IllegalArgumentException.class);
                    assertThat(ex.getMessage()).isEqualTo("email is required");
                })
                .verify();

        verify(userRepository).existsByEmail(u.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldError_whenBaseSalaryNull() {
        User u = validUser().toBuilder().baseSalary(null).build();
        when(userRepository.existsByEmail(u.getEmail())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.createUser(u))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(IllegalArgumentException.class);
                    assertThat(ex.getMessage()).isEqualTo("baseSalary must be between 0 and 15000000");
                })
                .verify();

        verify(userRepository).existsByEmail(u.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldError_whenBaseSalaryNegative() {
        User u = validUser().toBuilder().baseSalary(-1L).build();
        when(userRepository.existsByEmail(u.getEmail())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.createUser(u))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(IllegalArgumentException.class);
                    assertThat(ex.getMessage()).isEqualTo("baseSalary must be between 0 and 15000000");
                })
                .verify();

        verify(userRepository).existsByEmail(u.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldError_whenBaseSalaryTooHigh() {
        User u = validUser().toBuilder().baseSalary(15_000_001L).build();
        when(userRepository.existsByEmail(u.getEmail())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.createUser(u))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(IllegalArgumentException.class);
                    assertThat(ex.getMessage()).isEqualTo("baseSalary must be between 0 and 15000000");
                })
                .verify();

        verify(userRepository).existsByEmail(u.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldSave_whenAllValid() {
        User input = validUser();
        User saved = input.toBuilder().id("generated-id-123").build();

        when(userRepository.existsByEmail(input.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.save(input)).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.createUser(input))
                .expectNext(saved)
                .verifyComplete();

        verify(userRepository, times(1)).existsByEmail(input.getEmail());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(input);
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
