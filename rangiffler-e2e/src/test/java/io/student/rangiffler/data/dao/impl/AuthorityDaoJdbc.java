package io.student.rangiffler.data.dao.impl;

import io.student.rangiffler.config.Config;
import io.student.rangiffler.data.dao.AuthorityDao;
import io.student.rangiffler.data.entity.Authority;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class AuthorityDaoJdbc implements AuthorityDao {

    private static final Config CFG = Config.getInstance();
    private final Connection connection;

    public AuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    private final String SQL_CREATE_AUTHORITY =
            """
                    INSERT INTO `rangiffler-auth`.authority 
                    (id, user_id, authority)
                    VALUES (UUID_TO_BIN(?, true),UUID_TO_BIN(?, true),?);
            """;

    @Override
    public void createAuthority(UUID userId, Authority... authorities) {
        try (PreparedStatement ps = connection.prepareStatement(SQL_CREATE_AUTHORITY)) {

            for (Authority authority : authorities) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, userId.toString());
                ps.setString(3, authority.name());
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create authority", e);
        }
    }
}
