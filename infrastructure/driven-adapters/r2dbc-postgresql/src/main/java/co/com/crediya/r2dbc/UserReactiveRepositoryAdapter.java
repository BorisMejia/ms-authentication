package co.com.crediya.r2dbc;

import co.com.crediya.model.role.Role;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbc.role.RoleDao;
import co.com.crediya.r2dbc.role.mapper.RoleCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
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
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper, RoleDao roleDao) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.mapBuilder(d,User.UserBuilder.class).build());
        this.roleDao = roleDao;
    }
    private final RoleDao roleDao;

    @Override
    @Transactional
    public Mono<User> save(User user) {
        final String dbCode = RoleCodeMapper.toDbCode(user.getRole());

        return roleDao.findByCode(dbCode)
                .switchIfEmpty(Mono.error(
                        new IllegalStateException("Rol no encontrado: " + dbCode)))
                .flatMap(roleEntity -> {
                    var data = toData(user);
                    data.setRoleId(roleEntity.getId());

                    return saveData(data)
                            .doOnSubscribe(s -> log.debug("DB saving email={}", user.getEmail()))
                            .doOnSuccess(d -> log.debug("DB saved id={} email={}", d.getId(), d.getEmail()));
                })
                .map(this::toEntity)
                .onErrorMap(org.springframework.dao.DuplicateKeyException.class, ex -> {
                    log.warn("DB duplicate email={}", user.getEmail());
                    return new IllegalArgumentException("Email already registered");
                });
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
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

    @Override
    public Mono<User> updateRole(Long id, Role newRole) {
        return null;
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(this::toEntity);
    }

}
