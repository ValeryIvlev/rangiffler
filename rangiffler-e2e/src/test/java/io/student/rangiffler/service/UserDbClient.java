package io.student.rangiffler.service;

import io.student.rangiffler.config.Config;
import io.student.rangiffler.model.UserJson;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class UserDbClient implements UsersClient{

    private static final Config CFG = Config.getInstance();
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final String SQL_CREATE =
            """
                  INSERT INTO `rangiffler-auth`.`user`
                   (id, username,password,enabled,account_non_expired,account_non_locked,credentials_non_expired)  
                   VALUES (UUID_TO_BIN(?, true),?,?,?,?,?,?);
            """;


    @Override
    public UserJson createUser(String userName, String password) {

        try {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(
                    DriverManager.getConnection(
                            CFG.spendJdbcUrl(),
                            CFG.dbUsername(),
                            CFG.dbPassword()
                    ),
                    true)
            );

            final String userId = UUID.randomUUID().toString();
            jdbcTemplate.update(
                    (conn) -> {
                        PreparedStatement ps = conn.prepareStatement(
                                SQL_CREATE,
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setString(1, userId);
                        ps.setString(2, userName);
                        ps.setString(3, passwordEncoder.encode(password));
                        ps.setBoolean(4, true);
                        ps.setBoolean(5, true);
                        ps.setBoolean(6, true);
                        ps.setBoolean(7, true);

                        return ps;
                    }
            );

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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
