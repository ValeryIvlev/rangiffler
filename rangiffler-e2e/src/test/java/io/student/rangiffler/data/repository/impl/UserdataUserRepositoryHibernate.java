package io.student.rangiffler.data.repository.impl;

import io.student.rangiffler.config.Config;
import io.student.rangiffler.data.entity.*;
import io.student.rangiffler.data.repository.UserdataUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static io.student.rangiffler.data.jpa.EntityManagers.em;

public class UserdataUserRepositoryHibernate implements UserdataUserRepository {
    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.apiJdbcUrl());


    @Override
    public UserEntity create(UserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(UserEntity.class, id));
    }


    @Override
    public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        upsertFriendship(requester.getId(), addressee.getId(), FriendshipStatus.PENDING);

    }

    @Override
    public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        upsertFriendship(addressee.getId(), requester.getId(), FriendshipStatus.PENDING);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        upsertFriendship(requester.getId(), addressee.getId(), FriendshipStatus.ACCEPTED);
        upsertFriendship(addressee.getId(), requester.getId(), FriendshipStatus.ACCEPTED);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try {
            return Optional.of(entityManager.createQuery( "select U from UserEntity where u.username = :username", UserEntity.class)
                    .setParameter("username", username).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    private FriendshipId fid(UUID requesterId, UUID addresseeId) {
        return FriendshipId.builder()
                .requesterId(requesterId)
                .addresseeId(addresseeId)
                .build();
    }

    private void upsertFriendship(UUID requesterId, UUID addresseeId, FriendshipStatus status) {
        FriendshipId id = fid(requesterId, addresseeId);

        FriendshipEntity f = entityManager.find(FriendshipEntity.class, id);
        if (f == null) {
            f = FriendshipEntity.builder()
                    .id(id)
                    .createdDate(LocalDateTime.now())
                    .status(status)
                    .build();
            entityManager.persist(f);
        } else {
            f.setStatus(status);
        }
    }
}
