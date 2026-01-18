package io.student.rangiffler.service;

import io.student.rangiffler.config.Config;
import io.student.rangiffler.data.DataBases;
import io.student.rangiffler.data.dao.impl.AuthorityDaoJdbc;
import io.student.rangiffler.data.dao.impl.UserDaoJdbc;
import io.student.rangiffler.data.entity.Authority;
import io.student.rangiffler.data.entity.UserEntity;

import java.util.UUID;

import static io.student.rangiffler.data.DataBases.transaction;
import static io.student.rangiffler.data.DataBases.xaTransaction;

public class AuthorityDbClient {
    private static final Config CFG = Config.getInstance();

    public UserEntity createAuthorityUser(UUID userId, String userName, String password) {
        return xaTransaction(
                new DataBases.XaFunction<>(
                        connection -> {
                            UserEntity user = new UserDaoJdbc(connection)
                                    .createUser(userId, userName, password);

                            new AuthorityDaoJdbc(connection).createAuthority(
                                    userId,
                                    Authority.read,
                                    Authority.write
                            );

                            return user;
                        },
                        CFG.authJdbcUrl()
                )
        );
    }
}
