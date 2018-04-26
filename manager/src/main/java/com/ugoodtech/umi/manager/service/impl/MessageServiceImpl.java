package com.ugoodtech.umi.manager.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.core.domain.Message;
import com.ugoodtech.umi.core.domain.QMessage;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.repository.MessageRepository;
import com.ugoodtech.umi.manager.dto.MessageTask;
import com.ugoodtech.umi.manager.service.MessageService;
import com.ugoodtech.umi.manager.service.PushService;
import com.ugoodtech.umi.manager.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;


    @Autowired
    private PushService pushService;


    @Override
    public void createNotiMessage(String content,String countries,Date pushTime) {
        Message message = new Message();
        message.setType(Message.MessageType.NOTICE_MESSAGE);
        message.setFromUser(null);
        message.setToUser(null);
        message.setRead(false);
        message.setExpired(false);
        message.setDeleted(false);
        message.setNotifyTime(pushTime);
        String title = message.getType().getName() + "通知";
        message.setTitle(title);
        message.setContent(content);
        Message savedMessage=messageRepository.save(message);
        if(null!=pushTime&&pushTime.after(new Date())){
            Timer timer=new Timer("messageTimer");
            TimerTask task = new MessageTask(userService,countries,savedMessage,messageRepository,pushService,timer);
            timer.schedule(task,pushTime);
        }else{
            Page<User> clientUsers = userService.queryClientUser(null, null, countries, null, new PageRequest(0, 10000));
            Map<String, String> extra = new HashMap<>();
            extra.put("type", savedMessage.getType().getCode() + "");
            for(User user:clientUsers){
                    pushService.sendNotification(user, savedMessage.getTitle(), savedMessage.getContent(), extra);
            }
            message.setRead(true);
            messageRepository.save(message);

        }

    }


    @Override
    public Page<Message> queryMessages(String param,Integer pushStatus,Date stDate,Date edDate, Pageable pageable){
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.deleted.eq(false));
        builder.and(qMessage.type.eq(Message.MessageType.NOTICE_MESSAGE));
        if (!StringUtils.isEmpty(param)) {
            BooleanBuilder keyBuilder = new BooleanBuilder();
            keyBuilder.or(qMessage.title.like("%" + param + "%"));
            keyBuilder.or(qMessage.content.like("%" + param + "%"));
            try {
                keyBuilder.or(qMessage.id.eq(Long.parseLong(param)));                ;
            } catch (NumberFormatException e) {

            }
            builder.and(keyBuilder);
        }
        if(null!=pushStatus){
            if(0==pushStatus){
                builder.and(qMessage.expired.eq(false));
                builder.and(qMessage.read.eq(false));
            }else if(1==pushStatus){
                BooleanBuilder keyBuilder = new BooleanBuilder();
                keyBuilder.or(qMessage.read.eq(true));
                keyBuilder.or(qMessage.notifyTime.isNull());
                builder.and(keyBuilder);
            }else if(2==pushStatus){
                builder.and(qMessage.expired.eq(true));
            }
        }
        if(null!=stDate){
            builder.and(qMessage.creationTime.before(stDate).not());
        }
        if(null!=edDate){
            builder.and(qMessage.creationTime.after(edDate).not());
        }
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return messageRepository.findAll(builder, pageable);
    }

    @Override
    public void deleteCommentMessageByTopic(Long topicId) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.type.eq(Message.MessageType.INSTANT_COMMENT_MESSAGE));
        builder.and(qMessage.linkId.eq(topicId));
        builder.and(qMessage.deleted.isFalse());
        Iterable<Message> messages = messageRepository.findAll(builder);
        for (Message message : messages) {
            message.setDeleted(true);
            message.setUpdateTime(new Date());
            messageRepository.save(message);
        }
    }


}
