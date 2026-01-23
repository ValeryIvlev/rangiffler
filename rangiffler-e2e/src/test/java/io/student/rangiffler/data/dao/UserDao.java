package io.student.rangiffler.data.dao;

import io.student.rangiffler.data.entity.AuthorityEntity;
import io.student.rangiffler.data.entity.UserEntity;

import java.util.UUID;

public interface UserDao {
    UserEntity createUser(UserEntity user);
}
