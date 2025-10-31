package com.quant.user.infrastructure.persistence.repository;

import com.quant.user.domain.model.User;
import com.quant.user.domain.repository.UserRepository;
import com.quant.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * User Repository Implementation
 */
@Component
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        BeanUtils.copyProperties(user, entity);
        if (user.getStatus() != null) {
            entity.setStatus(UserEntity.UserStatus.valueOf(user.getStatus().name()));
        }
        return entity;
    }

    private User toDomain(UserEntity entity) {
        User user = new User();
        BeanUtils.copyProperties(entity, user);
        if (entity.getStatus() != null) {
            user.setStatus(User.UserStatus.valueOf(entity.getStatus().name()));
        }
        return user;
    }
}
