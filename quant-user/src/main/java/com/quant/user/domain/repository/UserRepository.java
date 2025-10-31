package com.quant.user.domain.repository;

import com.quant.user.domain.model.User;

import java.util.Optional;

/**
 * User Repository Interface (DDD Repository)
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    void deleteById(Long id);
}
