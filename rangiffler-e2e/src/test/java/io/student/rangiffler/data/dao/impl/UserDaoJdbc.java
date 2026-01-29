package io.student.rangiffler.data.dao.impl;

import io.student.rangiffler.config.Config;
import io.student.rangiffler.data.dao.UserDao;
import io.student.rangiffler.data.entity.UserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.student.rangiffler.data.tpl.Connections.holder;

public class UserDaoJdbc implements UserDao {

    private static final Config CFG = Config.getInstance();
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final String SQL_CREATE_USER =
            """
                  INSERT INTO `rangiffler-auth`.`user`
                   (id, username,password,enabled,account_non_expired,account_non_locked,credentials_non_expired)  
                   VALUES (UUID_TO_BIN(?, true),?,?,?,?,?,?);
            """;

    private final String SQL_DELETE_USER_BY_USERNAME =
            """
                DELETE FROM `rangiffler-auth`.`user`
                WHERE username = ?;
            """;

    private static final String FIND_ALL_SQL =
            """
                SELECT BIN_TO_UUID(id, true) AS id,
                username, password,
                enabled, account_non_expired, account_non_locked, credentials_non_expired
                FROM `rangiffler-auth`.`user`
            """;

    @Override
    public UserEntity createUser(UserEntity userEntity) {
        String encodedPassword = passwordEncoder.encode(userEntity.getPassword());

        if (userEntity.getId() == null) {
            userEntity.setId(UUID.randomUUID());
        }

        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(SQL_CREATE_USER)) {
            ps.setString(1, userEntity.getId().toString());
            ps.setString(2, userEntity.getUsername());
            ps.setString(3, encodedPassword);
            ps.setBoolean(4, userEntity.getEnabled());
            ps.setBoolean(5, userEntity.getAccountNonExpired());
            ps.setBoolean(6, userEntity.getAccountNonLocked());
            ps.setBoolean(7, userEntity.getCredentialsNonExpired());

            ps.executeUpdate();
            return userEntity;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    public void deleteUserByUserName(String userName) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(SQL_DELETE_USER_BY_USERNAME)) {
            ps.setString(1, userName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user by userName = " + userName, e);
        }
    }

    @Override
    public List<UserEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(FIND_ALL_SQL)) {
            List<UserEntity> users = new ArrayList<>();
            var rs = ps.executeQuery();

            while (rs.next()) {
                users.add(buildUser(rs));
            }

            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all users", e);
        }
    }

    private UserEntity buildUser(ResultSet rs) throws SQLException {
        UserEntity user = new UserEntity();
        user.setId(UUID.fromString(rs.getString("id")));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEnabled(rs.getBoolean("enabled"));
        user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
        user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
        user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
        return user;
    }
}
