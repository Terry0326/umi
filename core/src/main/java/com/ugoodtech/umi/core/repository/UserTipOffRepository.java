package com.ugoodtech.umi.core.repository;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.querydsl.core.types.Predicate;
import com.ugoodtech.umi.core.domain.UserTipOff;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;


public interface UserTipOffRepository extends PagingAndSortingRepository<UserTipOff, Long>,
        QueryDslPredicateExecutor<UserTipOff> {
    @Query(value="select count(*) from (select u.username userName,u.nickName,f.done,f.done_Str doneStr,f.update_time creationTime,COUNT(u.id) tipNum,f.tip_user_id from  `user_tip_off` f INNER JOIN users u on f.tip_user_id = u.id   WHERE f.deleted=FALSE     AND u.`nickname` LIKE %?1%   AND f.done=?2 AND f.creation_time>=?3 AND f.creation_time<=?4 group by f.tip_user_id)s",nativeQuery = true)
    long getTotal( String param,Boolean done,String startTime,String endTime);

    @Query(value="select u.username userName,u.nickName,f.done,f.done_Str doneStr,f.update_time creationTime,COUNT(u.id) tipNum,f.tip_user_id from  `user_tip_off` f INNER JOIN users u on f.tip_user_id = u.id   WHERE f.deleted=FALSE     AND u.`nickname` LIKE %?1%   AND f.done=?2 AND f.creation_time>=?3 AND f.creation_time<=?4 group by f.tip_user_id  ",nativeQuery = true)
    List getUserTipDto( String param,Boolean done,String startTime,String endTime);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update user_tip_off u set u.done=true ,u.done_str=:doneStr, update_time=CURTIME()  where u.tip_user_id=:id and u.done=false",nativeQuery = true)
    void disable(@Param("id")Long id,@Param("doneStr")String doneStr);
}