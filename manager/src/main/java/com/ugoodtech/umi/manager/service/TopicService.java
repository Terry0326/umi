package com.ugoodtech.umi.manager.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.SystemUser;
import com.ugoodtech.umi.core.domain.Topic;
import com.ugoodtech.umi.core.domain.TopicComment;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.exception.UmiException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TopicService {
	 Topic createTopic(SystemUser user, Topic topic,String imgBase) throws UmiException;
	 
    void deleteTopic(User user, Long topicId) throws UmiException;

    void changeTopicEnable( Long topicId,boolean enabled);

    Iterable<TopicComment> getTopicComments(Long topicId);

    long getLikeNum(Long topicId);

    Topic getTopic(Long topicId);


    Page<TopicComment> getTopicComments(Long id, Pageable pageable);

    Page<TopicComment> getUserTopicComments(Long userId,String param,Date stDate,Date edDate, Pageable pageable);

    void tipOff(User user, Long topicId, String reason);

    Page<Topic> getUserTopics(Long userId,Boolean burnTopic,String param,Boolean enabled, Date stDate,Date edDate,Pageable pageable);
    Page<Topic> getTimedTopics(Pageable pageable);
    long countUserTopics(Long userId,Boolean burnTopic);


}
