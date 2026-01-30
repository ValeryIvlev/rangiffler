package io.student.rangiffler.data.repository;

import io.student.rangiffler.data.entity.AuthUserEntity;

import java.util.List;

public interface AuthUserRepository {
    AuthUserEntity createUser(AuthUserEntity user);
    void deleteUserByUserName(String userName);
    List<AuthUserEntity> findAll();
}
