package com.ugoodtech.umi.core.repository;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.Blacklist;
import com.ugoodtech.umi.core.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface BlackListRepository extends CrudRepository<Blacklist, Long>, QueryDslPredicateExecutor<Blacklist> {
    @Query(value = "select b from Blacklist b where b.user.id=?1")
    Blacklist findBlacklistByUser(Long uid);
}
