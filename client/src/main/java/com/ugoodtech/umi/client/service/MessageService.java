package com.ugoodtech.umi.client.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.client.dto.MessageDto;
import com.ugoodtech.umi.core.domain.Message;
import com.ugoodtech.umi.core.domain.TopicComment;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.exception.UmiException;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface MessageService {
    Map<Integer, Map<Integer,List<MessageDto>>>getUserNotificationMessages(User user);

    Page<Message> getUserUnreadMessages(User user, Message.MessageType messageType, int page, Integer size);

    Message createMessage(Message.MessageType messageType, User follower, User targetUser);

    Message createMessage(Message.MessageType messageType, User fromUser, User targetUser,
                          Long linkId, Integer linkType, String content);

    Integer getUserUnreadMessagesNum(User user);

    Integer getUserUnreadMessagesNumByType(User user,Message.MessageType messageType);

    void createCommentMessage(TopicComment comment, Long userId);

    void markUserMessageRead(User user, Long messageId);

    void markUserMessagesRead(User user, Message.MessageType type);

    void markUserMessagesReadByLinkId(User user, Message.MessageType type, Long linkId);

    Map<Integer,List<MessageDto>>  convertToMessageDtoList(List<Message> messages, boolean queryFollowing);

    Page<MessageDto> getUserMessageDtos(User user, Message.MessageType type, Integer page, Integer size);

    Page<Message> getUserMessages(User user, Message.MessageType type, Integer page, Integer size);

    void deleteMessage(User user, Long messageId) throws UmiException;

    void deleteMessageForLink(User user, Long topicId, Message.MessageType messageType);

    void deleteMessageForType(User user, Message.MessageType messageType);

    void deleteCommentMessageByTopic(Long topicId);

    void deleteCommentMessage(Long commentId);

    void deleteFollowMessage(Long fromUserId, Long targetUserId);

    Message getMessageByUserAndLink(User user,Long topicIdAndType,Message.MessageType type);

     Map<Integer,List<MessageDto>> getInstantCommentMessages(final User user,Integer page,Integer size);
}
