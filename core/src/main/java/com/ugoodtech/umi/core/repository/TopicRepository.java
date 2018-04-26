package com.ugoodtech.umi.core.repository;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */
 
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ugoodtech.umi.core.domain.Topic;
import org.springframework.data.repository.query.Param;

public interface TopicRepository extends PagingAndSortingRepository<Topic, Long>,
        QueryDslPredicateExecutor<Topic> {
    @Query(value="SELECT * FROM topics  WHERE enabled =TRUE AND burn_topic=FALSE AND  lat >?1 AND lat < ?2 AND lng > ?3 AND lng <?4  ",nativeQuery = true)
    List<Topic> getNearTopics(Double lat1,Double lat2,Double lng1,Double lng2 );
    @Query(value="SELECT COUNT (*) FROM topics  WHERE enabled =TRUE AND burn_topic=FALSE AND  lat >?1 AND lat < ?2 AND lng > ?3 AND lng <?4 ",nativeQuery = true)
    Long getNearTopicCount(Double lat1,Double lat2,Double lng1,Double lng2);
    @Query(value = "SELECT * FROM topics  WHERE enabled =TRUE AND burn_topic=FALSE AND  lat >?1 AND lat < ?2 AND lng > ?3 AND lng <?4   ORDER BY ?#{#pageable}",countQuery = "SELECT COUNT (*) FROM topics  WHERE enabled =TRUE AND burn_topic=FALSE AND  lat >?1 AND lat < ?2 AND lng > ?3 AND lng <?4",nativeQuery = true)
    Page<Topic> getNearTopics2(Double lat1,Double lat2,Double lng1,Double lng2,Pageable pageable);
    @Query(value = "SELECT deleted FROM topics  WHERE  id=?1",nativeQuery = true)
    Boolean getDeteled(Long topicsId);


}