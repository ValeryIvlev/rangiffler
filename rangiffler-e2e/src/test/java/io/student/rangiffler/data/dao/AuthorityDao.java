package io.student.rangiffler.data.dao;

import io.student.rangiffler.data.entity.Authority;
import io.student.rangiffler.data.entity.AuthorityEntity;


import java.util.UUID;

public interface AuthorityDao{
    void createAuthority(AuthorityEntity... authority);

}
