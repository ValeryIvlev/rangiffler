package io.student.rangiffler.data.dao;

import io.student.rangiffler.data.entity.UserEntity;

import java.util.List;

public interface UserDao {
    UserEntity createUser(UserEntity user);
    void deleteUserByUserName(String userName);
    List<UserEntity> findAll();
}
