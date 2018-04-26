package com.ugoodtech.umi.core.repository;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.Resource;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface ResourceRepository extends CrudRepository<Resource, Long>, QueryDslPredicateExecutor<Resource> {
    @Override
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Resource b set b.deleted=true where b.id=:id")
    void delete(@Param("id") Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Resource b set b.deleted=true where b.id in :ids")
    void delete(@Param("ids") Collection<Long> ids);

}
