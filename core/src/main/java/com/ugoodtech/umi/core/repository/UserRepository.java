package com.ugoodtech.umi.core.repository;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

import com.ugoodtech.umi.core.domain.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface UserRepository extends PagingAndSortingRepository<User, Long>
        , QueryDslPredicateExecutor<User> {
    User findByUsername(String username);

    @Override
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update User b set b.deleted=true where b.id=:id")
    void delete(@Param("id") Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update User b set b.deleted=true where b.id in :ids")
    void delete(@Param("ids") Collection<Long> ids);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update User b set b.enabled=false where b.id in :ids")
    void disable(@Param("ids") Collection<Long> ids);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update User b set b.enabled=true where b.id in :ids")
    void enable(@Param("ids") Collection<Long> ids);

}
