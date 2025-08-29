package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    User/* change for domain model */,
    UserEntity/* change for adapter model */,
    String,
        UserReactiveRepository
> implements UserRepository {
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.mapBuilder(d,User.UserBuilder.class).build());
    }

    @Override
    @Transactional
    public Mono<User> save(User user) {
        return saveData(toData(user))
                .doOnSubscribe(s -> log.debug("DB saving email={}", user.getEmail()))
                .doOnSuccess(d -> log.debug("DB saved id={} email={}", d.getId(), d.getEmail()))
                .map(this::toEntity)
                .onErrorMap(DuplicateKeyException.class, ex -> {
                    log.warn("DB duplicate email={}", user.getEmail());
                    return new IllegalArgumentException("Email already registered");
                });
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return null;
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email)
                .doOnNext(exists -> log.debug("existsByEmail({}) -> {}", email, exists))
                .switchIfEmpty(Mono.fromCallable(() ->{
                    log.debug("existsByEmail({}) -> empty (treat false)",email);
                    return false;
                }));
    }
}
