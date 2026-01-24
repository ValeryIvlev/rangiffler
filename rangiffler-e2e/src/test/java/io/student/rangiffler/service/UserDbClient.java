package io.student.rangiffler.service;

import io.student.rangiffler.config.Config;
import io.student.rangiffler.data.dao.AuthorityDao;
import io.student.rangiffler.data.dao.UserDao;
import io.student.rangiffler.data.dao.impl.AuthorityDaoJdbc;
import io.student.rangiffler.data.dao.impl.AuthorityDaoSpringJdbc;
import io.student.rangiffler.data.dao.impl.UserDaoJdbc;
import io.student.rangiffler.data.dao.impl.UserDaoSpringJdbc;
import io.student.rangiffler.data.entity.Authority;
import io.student.rangiffler.data.entity.AuthorityEntity;
import io.student.rangiffler.data.entity.UserEntity;
import io.student.rangiffler.data.tpl.JdbcTransactionTemplate;
import io.student.rangiffler.data.tpl.XaTransactionTemplate;
import io.student.rangiffler.model.UserJson;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class UserDbClient {

    private static final Config CFG = Config.getInstance();

    private final UserDao userDao = new UserDaoJdbc();
    private final AuthorityDao authorityDao = new AuthorityDaoJdbc();

    private final UserDao userDaoSpringJdbc = new UserDaoSpringJdbc();
    private final AuthorityDao authorityDaoSpringJdbc = new AuthorityDaoSpringJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(CFG.authJdbcUrl());
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.authJdbcUrl());


    public void deleteUserByUsername(String username) {
        xaTransactionTemplate.execute(() -> {
            authorityDao.deleteAuthorityByUserName(username);
            userDao.deleteUserByUserName(username);
            return null;
        });
    }

    public List<UserJson> findAllUsers() {
        return userDaoSpringJdbc.findAll().stream()
                .map(x -> buildUserJson(x.getId().toString(), x.getUsername()))
                .toList();
    }

    public List<UserJson> findAllUsersSpringJdbc() {
        return userDao.findAll().stream()
                .map(x -> buildUserJson(x.getId().toString(), x.getUsername()))
                .toList();
    }

    public List<AuthorityEntity> findAllAuthority() {
        return authorityDao.findAll();
    }

    public List<AuthorityEntity> findAllAuthoritySpringJdbc() {
        return authorityDaoSpringJdbc.findAll();
    }

    public UserJson createUser(String userName, String password) {
        UserEntity newUser = xaTransactionTemplate.execute(
                        ()-> {
                            UserEntity userEntity = new UserEntity();
                            userEntity.setId(UUID.randomUUID());
                            userEntity.setUsername(userName);
                            userEntity.setPassword(password);
                            userEntity.setEnabled(true);
                            userEntity.setAccountNonExpired(true);
                            userEntity.setAccountNonLocked(true);
                            userEntity.setCredentialsNonExpired(true);
                            UserEntity user = userDao.createUser(userEntity);

                            authorityDao.createAuthority(
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
                        }
                );
        return buildUserJson(newUser.getId().toString(), newUser.getUsername());
    }

    public void deleteUserByUsernameSpringJdbc(String username) {
        jdbcTxTemplate.execute(() -> {
            authorityDaoSpringJdbc.deleteAuthorityByUserName(username);
            userDaoSpringJdbc.deleteUserByUserName(username);
            return null;
        });
    }

    public UserJson createUserSpringJdbc(String userName, String password) {
        UserEntity newUser = jdbcTxTemplate.execute(
                ()-> {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(UUID.randomUUID());
                    userEntity.setUsername(userName);
                    userEntity.setPassword(password);
                    userEntity.setEnabled(true);
                    userEntity.setAccountNonExpired(true);
                    userEntity.setAccountNonLocked(true);
                    userEntity.setCredentialsNonExpired(true);
                    UserEntity user = userDaoSpringJdbc.createUser(userEntity);

                    authorityDaoSpringJdbc.createAuthority(
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
                }
        );
        return buildUserJson(newUser.getId().toString(), newUser.getUsername());
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