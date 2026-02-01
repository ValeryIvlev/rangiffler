package io.student.rangiffler.data.repository;

import io.student.rangiffler.data.entity.AuthUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository {
    AuthUserEntity createUser(AuthUserEntity user);
    void deleteUserByUserName(String userName);
    List<AuthUserEntity> findAll();
    Optional<AuthUserEntity> findById(UUID id);
    Optional<AuthUserEntity> findByUsername(String username);
}
