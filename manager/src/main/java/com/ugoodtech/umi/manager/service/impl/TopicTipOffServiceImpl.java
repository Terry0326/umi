package com.ugoodtech.umi.manager.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.core.domain.QTopicTipOff;
import com.ugoodtech.umi.core.domain.TopicTipOff;
import com.ugoodtech.umi.core.repository.TopicTipOffRepository;
import com.ugoodtech.umi.manager.service.TopicTipOffService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TopicTipOffServiceImpl implements TopicTipOffService{

    @Autowired
    private TopicTipOffRepository topicTipOffRepository;


    @Override
    public Page<TopicTipOff> getTopicTipOff(String param, Boolean done, Date stDate, Date edDate, Pageable pageable) {
        QTopicTipOff qTopicTipOff = QTopicTipOff.topicTipOff;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopicTipOff.deleted.isFalse());
        if (!StringUtils.isEmpty(param)) {
            BooleanBuilder keyBuilder = new BooleanBuilder();
            keyBuilder.or(qTopicTipOff.topic.user.username.like("%" + param + "%"));
            keyBuilder.or(qTopicTipOff.topic.user.nickname.like("%" + param + "%"));
            keyBuilder.or(qTopicTipOff.topic.description.like("%" + param + "%"));
            keyBuilder.or(qTopicTipOff.user.username.like("%" + param + "%"));
            keyBuilder.or(qTopicTipOff.user.nickname.like("%" + param + "%"));
            keyBuilder.or(qTopicTipOff.reason.like("%" + param + "%"));
            builder.and(keyBuilder);
        }
        if(null!=done){
            builder.and(qTopicTipOff.done.eq(done));
        }
        if(null!=stDate){
            builder.and(qTopicTipOff.creationTime.before(stDate).not());
        }
        if(null!=edDate){
            builder.and(qTopicTipOff.creationTime.after(edDate).not());
        }
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return topicTipOffRepository.findAll(builder, pageable);
    }

    Logger logger = LoggerFactory.getLogger(TopicTipOffServiceImpl.class);
}
