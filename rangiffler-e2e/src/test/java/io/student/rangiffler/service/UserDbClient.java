package io.student.rangiffler.service;

import io.student.rangiffler.config.Config;
import io.student.rangiffler.model.UserJson;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class UserDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final String SQL_CREATE_USER =
            """
                  INSERT INTO `rangiffler-auth`.`user`
                   (id, username,password,enabled,account_non_expired,account_non_locked,credentials_non_expired)  
                   VALUES (UUID_TO_BIN(?, true),?,?,?,?,?,?);
            """;

    private final String SQL_CREATE_AUTHORITY =
            """
                    INSERT INTO `rangiffler-auth`.authority 
                    (id, user_id, authority)
                    VALUES (UUID_TO_BIN(?, true),UUID_TO_BIN(?, true),?);
            """;

    private final String SQL_DELETE_USER_BY_USERNAME =
            """
                DELETE FROM `rangiffler-auth`.`user`
                WHERE username = ?;
            """;

    private final String SQL_DELETE_AUTHORITIES_BY_USERNAME =
            """
                DELETE a FROM `rangiffler-auth`.authority a
                JOIN `rangiffler-auth`.user u ON a.user_id = u.id
                WHERE u.username = ?;
            """;

    @Override
    public void deleteUserByUsername(String username) {

        try {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(
                    new SingleConnectionDataSource(
                            DriverManager.getConnection(
                                    CFG.authJdbcUrl(),
                                    CFG.dbUsername(),
                                    CFG.dbPassword()
                            ),
                            true
                    )
            );
            jdbcTemplate.update(SQL_DELETE_AUTHORITIES_BY_USERNAME, username);
            jdbcTemplate.update(SQL_DELETE_USER_BY_USERNAME, username);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserJson createUser(String userName, String password) {

        try {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(
                    DriverManager.getConnection(
                            CFG.authJdbcUrl(),
                            CFG.dbUsername(),
                            CFG.dbPassword()
                    ),
                    true)
            );

            final String userId = UUID.randomUUID().toString();
            insertUser(jdbcTemplate, userId, userName, password);
            insertAuthorities(jdbcTemplate, userId, Authority.read, Authority.write);

            return buildUserJson(userId, userName);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertUser(
            JdbcTemplate jdbcTemplate,
            String userId,
            String userName,
            String password
    ) {
        jdbcTemplate.update(
                SQL_CREATE_USER,
                userId,
                userName,
                passwordEncoder.encode(password),
                true,
                true,
                true,
                true
        );
    }

    private void insertAuthorities(
            JdbcTemplate jdbcTemplate,
            String userId,
            Authority... authorities
    ) {
        for (Authority authority : authorities) {
            jdbcTemplate.update(
                    SQL_CREATE_AUTHORITY,
                    UUID.randomUUID().toString(),
                    userId,
                    authority.toString()
            );
        }
    }

    private UserJson buildUserJson(String userId, String userName) {
        return new UserJson(
                new UserJson.Data(
                        new UserJson.User(
                                userId,
                                userName,
                                null,
                                null,
                                null,
                                null
                        )
                )
        );
    }
}