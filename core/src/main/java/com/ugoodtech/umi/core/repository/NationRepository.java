package com.ugoodtech.umi.core.repository;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.Nation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface NationRepository extends CrudRepository<Nation, Long>, QueryDslPredicateExecutor<Nation> {
    //    Nation findByCodeAndCity(String code,String city);

    @Query("select distinct c.province from Nation c where c.code=:code and (c.city='' or c.city is null)")
    String findProvinceByCode(@Param("code") String code);

    @Query("select distinct c.city from Nation c where c.code=:code and (c.province='' or c.province is null)")
    String findCityByCode(@Param("code") String code);

    @Query("select distinct c.district from Nation c where c.code=:code and (c.city='' or c.city is null)")
    String findDistrictByCode(@Param("code") String code);
}
