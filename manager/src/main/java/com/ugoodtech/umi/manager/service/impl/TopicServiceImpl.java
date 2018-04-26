package com.ugoodtech.umi.manager.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.aliyun.oss.OSSClient;
import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.core.domain.*;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.CommentRepository;
import com.ugoodtech.umi.core.repository.LikeRepository;
import com.ugoodtech.umi.core.repository.TopicRepository;
import com.ugoodtech.umi.core.repository.TopicTipOffRepository;
import com.ugoodtech.umi.core.repository.UserRepository;
import com.ugoodtech.umi.manager.service.AliYunService;
import com.ugoodtech.umi.manager.service.MessageService;
import com.ugoodtech.umi.manager.service.TopicService;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private TopicTipOffRepository topicTipOffRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageService messageService;
    @Autowired
    private AliYunService aliYunService;
    @Override
    public void deleteTopic(User user, Long topicId) throws UmiException {
        Topic topic = topicRepository.findOne(topicId);
        if (topic.getUser().getId().equals(user.getId())) {
            topic.setDeleted(true);
            topic.setUpdateTime(new Date());
            topicRepository.save(topic);
            messageService.deleteCommentMessageByTopic(topicId);
        } else {
            throw new UmiException(1000, "不能删除别人创建的帖子");
        }
    }

    @Override
    public void changeTopicEnable(Long topicId, boolean enabled) {
        Topic topic = topicRepository.findOne(topicId);
        topic.setEnabled(enabled);
        topic.setUpdateTime(new Date());
        topicRepository.save(topic);
        if (!enabled) {
            messageService.deleteCommentMessageByTopic(topicId);
        } else {
            //todo:should recovery the comment messages which have been deleted ?
        }
    }

    @Override
    public Iterable<TopicComment> getTopicComments(Long topicId) {
        QTopicComment qTopicComment = QTopicComment.topicComment;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopicComment.deleted.isFalse());
        builder.and(qTopicComment.topic.id.eq(topicId));
        return commentRepository.findAll(builder);
    }

    @Override
    public long getLikeNum(Long topicId) {
        QTopicLike qTopicLike = QTopicLike.topicLike;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopicLike.deleted.isFalse());
        builder.and(qTopicLike.topic.id.eq(topicId));
        return likeRepository.count(builder);
    }

    @Override
    public Topic getTopic(Long topicId) {
        return topicRepository.findOne(topicId);
    }

    @Override
    public Page<TopicComment> getTopicComments(Long topicId, Pageable pageable) {
        QTopicComment qTopicComment = QTopicComment.topicComment;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopicComment.topic.id.eq(topicId));
        return commentRepository.findAll(builder, pageable);
    }

    @Override
    public Page<TopicComment> getUserTopicComments(Long userId, String param, Date stDate, Date edDate, Pageable pageable) {
        QTopicComment qTopicComment = QTopicComment.topicComment;
        BooleanBuilder builder = new BooleanBuilder();
        if (!StringUtils.isEmpty(param)) {
            BooleanBuilder keyBuilder = new BooleanBuilder();
            builder.and(
                    keyBuilder.or(qTopicComment.content.like("%" + param + "%"))
                            .or(qTopicComment.publisher.username.like("%" + param + "%"))
                            .or(qTopicComment.publisher.nickname.like("%" + param + "%"))
            );
        }
        if (null != stDate) {
            builder.and(qTopicComment.creationTime.before(stDate).not());
        }
        if (null != edDate) {
            builder.and(qTopicComment.creationTime.after(edDate).not());
        }
        builder.and(qTopicComment.publisher.id.eq(userId));
        return commentRepository.findAll(builder, pageable);
    }

    @Override
    public void tipOff(User user, Long topicId, String reason) {
        Topic topic = topicRepository.findOne(topicId);
        TopicTipOff tipOff = new TopicTipOff();
        tipOff.setUser(user);
        tipOff.setTopic(topic);
        tipOff.setReason(reason);
        topicTipOffRepository.save(tipOff);
    }

    @Override
    public Page<Topic> getUserTopics(Long userId, Boolean burnTopic, String param, Boolean enabled, Date stDate, Date edDate, Pageable pageable ) {
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopic.deleted.isFalse());
        builder.and(qTopic.timed.isTrue());
        if (null != userId) {
            builder.and(qTopic.user.id.eq(userId));
        }
        if (null != burnTopic) {
            builder.and(qTopic.burnTopic.eq(burnTopic));
        }
        if (null != stDate) {
            builder.and(qTopic.creationTime.before(stDate).not());
        }
        if (null != edDate) {
            builder.and(qTopic.creationTime.after(edDate).not());
        }
        if (null != enabled) {
            builder.and(qTopic.enabled.eq(enabled));
        }
        if (!StringUtils.isEmpty(param)) {
            BooleanBuilder keyBuilder = new BooleanBuilder();
            builder.and(
                    keyBuilder.or(qTopic.description.like("%" + param + "%"))
                            .or(qTopic.user.username.like("%" + param + "%"))
                            .or(qTopic.user.nickname.like("%" + param + "%"))
            );
        }
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return topicRepository.findAll(builder, pageable);
    }

    @Override
    public Page<Topic> getTimedTopics(Pageable pageable) {
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopic.deleted.isFalse());
        builder.and(qTopic.enabled.isTrue());
        builder.and(qTopic.timeTopic.isTrue());
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return topicRepository.findAll(builder, pageable);
    }

    @Override
    public long countUserTopics(Long userId, Boolean burnTopic) {
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopic.deleted.isFalse());
        if (null != userId) {
            builder.and(qTopic.user.id.eq(userId));
        }
        if (null != burnTopic) {
            builder.and(qTopic.burnTopic.eq(burnTopic));
        }
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        return topicRepository.count(builder);
    }


    Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);
    private Long getUserTopicNum(User user) {
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopic.user.id.eq(user.getId()));
        builder.and(qTopic.deleted.isFalse());
        return topicRepository.count(builder);
    }
	@Override
    @Transactional(rollbackFor = Exception.class)
	public Topic createTopic(SystemUser user, Topic topic,String imgBase) throws UmiException {
 		// TODO Auto-generated method stub
		 topic.setEnabled(true);
		 User dbUser = userRepository.findOne(user.getAppUid());
		 topic.setUser(dbUser);
        topic=   topicRepository.save(topic);
	     dbUser.setPublishTopicsNum(getUserTopicNum(dbUser));
        if(topic.getTopicType().getCode()==1){
            userRepository.save(dbUser);
            String[] imgList = imgBase.split(";;;;;");
            System.out.println(imgList.length+"length");
            String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
            String accessKeyId = "LTAI3tgCwJVQFwBB";
            String accessKeySecret = "BrurxANVR1q3q0Qmi5P3WEA24pm9KL";
            OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            for (int i = 0; i < imgList.length; i++) {
                String[] baseStr = imgList[i].split("base64,");
                String sst=baseStr[1].replace(" ", "+");
                byte[] data= Base64.decodeBase64(sst);
                InputStream sbs=new ByteArrayInputStream(data);
                ossClient.putObject("umi-test1", topic.getId()+"-img"+i,sbs);
            }
        }else {
            String fileName=imgBase.substring(imgBase.lastIndexOf("/") + 1);
            String videoId=aliYunService.uploadVideo("Uing精彩瞬间","aa.mp4",imgBase);
            topic.setContent(videoId);
            userRepository.save(dbUser);
        }
		return topic;
	}
}
