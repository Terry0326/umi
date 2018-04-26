package com.ugoodtech.umi.client.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.exception.UmiException;

public interface ChatService {
    String getUserImToken(User user) throws UmiException;

    boolean createChatRoom(String roomId, String roomName);

    boolean joinChatRoom(String[] userIds, String roomId);

    String getPrivateMessageRoomId(Long userOneId, Long userTwoId) throws UmiException;

    void block(Long userId, Long blackUserId) throws UmiException;

    void unblock(Long executorId, Long blockerId);

    void seeItsTopics(Long executorId, Long blockerId);

    void unSeeItsTopics(Long executorId, Long blockerId);

    void removeNotSeeItsTopics(Long executorId, Long blockerId);

    void refreshUsers();

    boolean refreshUser(User user);
}
