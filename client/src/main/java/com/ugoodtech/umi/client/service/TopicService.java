package com.ugoodtech.umi.client.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.client.dto.RichTopic;
import com.ugoodtech.umi.core.domain.Topic;
import com.ugoodtech.umi.core.domain.TopicComment;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.exception.UmiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface TopicService {
    Topic createTopic(User user, Topic topic, String markUserIds) throws UmiException;

    void deleteTopic(User user, Long topicId) throws UmiException;

    TopicComment addComment(Long commentUserId, Long topicId, String content, boolean burnable) throws UmiException;

    void like(User user, Long topicId) throws UmiException;

    Page<Topic> getHomeTopics(User user, Pageable pageable);

    long getTopicLikeNum(Long topicId);

    void unlike(User user, Long topicId) throws UmiException;

    Topic getTopic(Long topicId);

    Page<TopicComment> getTopicComments(User user, Long topicId, Pageable pageable);

    void tipOff(User user, Long topicId, String reason);

    Page<Topic> getUserPopularTopics(Long userId, Pageable pageable,Boolean anonymous);

    Page<Topic> getUserTopics(Long userId, Pageable pageable,Boolean anonymous);

    Map<Long,List<Topic>> getNearTopics(User user, Double lat, Double lng, Integer distance, Pageable pageable) throws UmiException;

    Page<Topic> getNearTopics2(User user, Double lat, Double lng, Integer distance, Pageable pageable) throws UmiException;

    Page<Topic> getNearByTopics(User user, Double lat, Double lng, Integer distance, Pageable pageable) throws UmiException;

    Page<Topic> getChinaTopics(User user, Pageable pageable);

    Page<Topic> getOverseaTopics(User user, Pageable pageable);

    Page<Topic> getBurnTopics(User user, Pageable pageable);

    boolean isUserLiked(User user, Long topicId);

    Page<Topic> query(User user, String qKey, Pageable pageable);

    Long getBurnableCommentsNum(Long topicId);

    RichTopic getRichTopic(User user, Topic topic, int commentPageSize) throws ParseException;

    Page<Topic> getWorldTopics(User user, Pageable pageable);

    void setTopicCover(String videoId, String coverUrl);

    void addPlayUrl(String videoId, String fileUrl);

    void deleteComment(Long id, Long commentId) throws UmiException;
    /**
     * 点击率
     */
     void updateTopicClickNum(Long topicId) throws UmiException;

    Page<Topic> getRandTopics( User user);

    Page<Topic> getRandBurnTopics( User user,Pageable pageable);
    Boolean isDelete( Long topicId);
    /**
     * 热门贴
     */
    Page<Topic> getHotTopics(User user,Pageable pageable);

}
