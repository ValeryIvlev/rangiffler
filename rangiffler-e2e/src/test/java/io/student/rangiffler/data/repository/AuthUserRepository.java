package io.student.rangiffler.data.repository;

import io.student.rangiffler.data.entity.AuthUserEntity;

import java.util.List;
import java.util.Optional;

public interface AuthUserRepository {
    AuthUserEntity createUser(AuthUserEntity user);
    void deleteUserByUserName(String userName);
    List<AuthUserEntity> findAll();
    Optional<AuthUserEntity> findByUsername(String username);
}
