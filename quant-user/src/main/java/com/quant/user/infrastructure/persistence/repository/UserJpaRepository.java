package com.quant.user.infrastructure.persistence.repository;

import com.quant.database.repository.BaseRepository;
import com.quant.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User JPA Repository
 */
@Repository
public interface UserJpaRepository extends BaseRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByMobile(String mobile);
}
