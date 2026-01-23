package io.student.rangiffler.service;

import io.student.rangiffler.config.Config;
import io.student.rangiffler.data.DataBases;
import io.student.rangiffler.data.dao.impl.AuthorityDaoJdbc;
import io.student.rangiffler.data.dao.impl.UserDaoJdbc;
import io.student.rangiffler.data.entity.Authority;
import io.student.rangiffler.data.entity.AuthorityEntity;
import io.student.rangiffler.data.entity.UserEntity;
import io.student.rangiffler.model.UserJson;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

import static io.student.rangiffler.data.DataBases.xaTransaction;

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
        UserEntity newUser = xaTransaction(
                new DataBases.XaFunction<>(
                        connection -> {
                            UserEntity userEntity = new UserEntity();
                            userEntity.setId(UUID.randomUUID());
                            userEntity.setUsername(userName);
                            userEntity.setPassword(password);
                            userEntity.setEnabled(true);
                            userEntity.setAccountNonExpired(true);
                            userEntity.setAccountNonLocked(true);
                            userEntity.setCredentialsNonExpired(true);
                            UserEntity user = new UserDaoJdbc(connection)
                                    .createUser(userEntity);

                            new AuthorityDaoJdbc(connection).createAuthority(
                                    Arrays.stream(Authority.values())
                                            .map(a -> {
                                                        AuthorityEntity ae = new AuthorityEntity();
                                                        ae.setUser(user);
                                                        ae.setAuthority(a);
                                                        return ae;
                                                    }
                                            )
                                            .toArray(AuthorityEntity[]::new));
                            return user;
                        },
                        CFG.authJdbcUrl()
                )
        );
        return buildUserJson(newUser.getId().toString(), newUser.getUsername());
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