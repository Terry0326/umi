package com.ugoodtech.umi.core.repository;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.ThirdPartyAccount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ThirdPartyAccountRepository extends CrudRepository<ThirdPartyAccount, Long>,
        QueryDslPredicateExecutor<ThirdPartyAccount> {
    @Query("select t from ThirdPartyAccount t where t.user.id=:userId and t.thirdParty=:thirdParty and t.deleted=false")
    List<ThirdPartyAccount> findThirdPartyAccount(@Param("userId") Long userId,
                                                  @Param("thirdParty") ThirdPartyAccount.ThirdParty thirdParty);
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update ThirdPartyAccount t set t.token=:token where t.user.id=:userId and t.thirdParty=:thirdParty")
    void updateThirdPartyToken(@Param("userId") Long userId,
                               @Param("thirdParty") ThirdPartyAccount.ThirdParty thirdParty,
                               @Param("token") String token);

}
