package com.ugoodtech.umi.core.repository;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.GeneralCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GeneralCodeRepository extends PagingAndSortingRepository<GeneralCode, Long>,
        QueryDslPredicateExecutor<GeneralCode> {
    @Query("from GeneralCode c where c.category=:category and deleted=false order by orderNum")
    List<GeneralCode> findByCategory(@Param("category") Integer category);
}
