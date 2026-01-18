package io.student.rangiffler.data.dao;

import io.student.rangiffler.data.entity.Authority;

import java.util.UUID;

public interface AuthorityDao{
    void createAuthority(UUID userId, Authority... authority);

}
