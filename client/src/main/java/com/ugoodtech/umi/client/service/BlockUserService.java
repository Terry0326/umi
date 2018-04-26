package com.ugoodtech.umi.client.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface BlockUserService {
    void block(Long executorId, Long blockUserId);

    void unblock(Long executorId, Long blockUserId);

    void unSeeItsTopics(Long executorId, Long blockerId);

    void seeItsTopics(Long executorId, Long blockerId);

    Page<User> getBlockList(Long userId, Pageable pageable);

    Collection<Long> getBlockUserExecutorIds(Long userId);

    Collection<Long> getNotSeeItsTopicsUserIds(Long executorId);

    boolean isUserBlockedByOne(User user, Long executorId);

    boolean isSeeItsTopicByOne(User user, Long executorId);

    Collection<Long> getBlockUserIds(Long executorId);
}
