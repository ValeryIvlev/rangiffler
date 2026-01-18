package io.student.rangiffler.data.dao.impl;

import io.student.rangiffler.data.dao.UserDao;
import io.student.rangiffler.data.entity.UserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class UserDaoJdbc implements UserDao {

    private final Connection connection;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    private final String SQL_CREATE_USER =
            """
                  INSERT INTO `rangiffler-auth`.`user`
                   (id, username,password,enabled,account_non_expired,account_non_locked,credentials_non_expired)  
                   VALUES (UUID_TO_BIN(?, true),?,?,?,?,?,?);
            """;

    @Override
    public UserEntity createUser(UUID userId, String userName, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        try (PreparedStatement ps = connection.prepareStatement(SQL_CREATE_USER)) {
            ps.setString(1, userId.toString());
            ps.setString(2, userName);
            ps.setString(3, encodedPassword);
            ps.setBoolean(4, true);
            ps.setBoolean(5, true);
            ps.setBoolean(6, true);
            ps.setBoolean(7, true);

            ps.executeUpdate();

            UserEntity user = new UserEntity();
            user.setId(userId);
            user.setUsername(userName);
            user.setPassword(encodedPassword);
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);

            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }
}
