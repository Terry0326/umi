package com.ugoodtech.umi.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ugoodtech.umi.core.domain.ThirdPartyAccount;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.domain.UserRemark;

public interface UserRemarkRepository extends PagingAndSortingRepository<UserRemark, Long>, QueryDslPredicateExecutor<UserRemark> {
	  @Query("select r from UserRemark r where r.remarker=:remarkerId and r.targetUserId=:targetId")
	  UserRemark findRemark(@Param("remarkerId") Long remarkerId,@Param("targetId")Long targetId); 
}
