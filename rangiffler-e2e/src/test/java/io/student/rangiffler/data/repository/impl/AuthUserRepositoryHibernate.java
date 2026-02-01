package io.student.rangiffler.data.repository.impl;

import io.student.rangiffler.config.Config;
import io.student.rangiffler.data.entity.AuthUserEntity;
import io.student.rangiffler.data.jpa.EntityManagers;
import io.student.rangiffler.data.repository.AuthUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.student.rangiffler.data.jpa.EntityManagers.em;

public class AuthUserRepositoryHibernate implements AuthUserRepository {
    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.authJdbcUrl());

    @Override
    public AuthUserEntity createUser(AuthUserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(AuthUserEntity.class, id));
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try {
            return Optional.of(entityManager.createQuery( "select U from AuthUserEntity where u.username = :username", AuthUserEntity.class)
                    .setParameter("username", username).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }

    }

    @Override
    public void deleteUserByUserName(String userName) {

    }

    @Override
    public List<AuthUserEntity> findAll() {
        return List.of();
    }
}
