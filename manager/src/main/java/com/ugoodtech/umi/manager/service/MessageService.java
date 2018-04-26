package com.ugoodtech.umi.manager.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 16/3/17
 */

import com.ugoodtech.umi.core.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

public interface MessageService {

    void createNotiMessage(String content,String countries,Date pushTime);

    Page<Message> queryMessages(String param,Integer pushStatus,Date stDate,Date edDate, Pageable pageable);

    void deleteCommentMessageByTopic(Long topicId);
}
