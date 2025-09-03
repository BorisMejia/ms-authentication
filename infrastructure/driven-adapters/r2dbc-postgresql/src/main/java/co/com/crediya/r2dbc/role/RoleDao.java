package co.com.crediya.r2dbc.role;

import co.com.crediya.r2dbc.entity.RoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RoleDao extends ReactiveCrudRepository<RoleEntity, Long> {
    Mono<RoleEntity>findByCode(String code);
}
