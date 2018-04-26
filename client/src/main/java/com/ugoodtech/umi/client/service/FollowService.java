package com.ugoodtech.umi.client.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.exception.UmiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface FollowService {
    void addFollow(Long id, Long targetUserId) throws UmiException;

    boolean isFollowing(Long followerId, Long targetUserId);

    Long getFollowersNum(Long userId);

    Long getFollowingUsersNum(Long userId);

    Page<User> getFollowers(Long userId, Pageable pageable);

    void unFollow(Long fromUserId, Long targetUserId) throws UmiException;

    Page<User> getFollowingUsers(Long userId, Pageable pageable);

    Collection<Long> getFollowingUserIds(Long userId);

    Page<User> queryFollowers(User user, String qKey, Pageable pageable);

    Page<User> queryFollowingUsers(User user, String qKey, Pageable pageable);

    List<User> getFollowers(Long userId);

    List<User> getFollowingUsers(Long userId);
}
