package com.ugoodtech.umi.core.repository;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.SensitiveWord;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SensitiveRepository extends CrudRepository<SensitiveWord, Long>,
        QueryDslPredicateExecutor<SensitiveWord> {

    List<SensitiveWord> findByName(String name);


}
