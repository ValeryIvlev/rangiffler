package io.student.rangiffler.data.repository;

import io.student.rangiffler.data.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataUserRepository {

    UserEntity create(UserEntity user);
    Optional<UserEntity> findById(UUID id);
    void addIncomeInvitation(UserEntity requester, UserEntity addressee);
    void addOutcomeInvitation(UserEntity requester, UserEntity addressee);
    void addFriend(UserEntity requester, UserEntity addressee);
    Optional<UserEntity> findByUsername(String username);
}
