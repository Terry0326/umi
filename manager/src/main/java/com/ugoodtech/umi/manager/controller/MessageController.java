package com.ugoodtech.umi.manager.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.Constants;
import com.ugoodtech.umi.core.domain.Message;
import com.ugoodtech.umi.core.domain.Topic;
import com.ugoodtech.umi.core.domain.converter.TopicTypeConverter;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.MessageRepository;
import com.ugoodtech.umi.manager.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Api(description = "推送api")
@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    MessageService messageService;

    @Autowired
    MessageRepository messageRepository;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm"), true));
        binder.registerCustomEditor(Topic.TopicType.class, new TopicTypeConverter());
    }



//    private PageImpl<RichTopic> getRichTopics(Pageable pageable, Page<Topic> topicPage) {
//        List<RichTopic> richTopics = new ArrayList<>();
//        for (Topic topic : topicPage) {
//            RichTopic richTopic = new RichTopic(topic);
//            richTopic.setLikeNum(topicService.getLikeNum(topic.getId()));
//            Pageable commentsPage = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "creationTime"));
//            Page<TopicComment> comments = topicService.getTopicComments(topic.getId(), commentsPage);
//            richTopic.setCommentsNum(comments.getTotalElements());
//            for (TopicComment comment : comments) {
//                richTopic.getComments().add(new CommentDto(comment));
//            }
//            richTopics.add(richTopic);
//        }
//        return new PageImpl<>(richTopics, pageable, topicPage.getTotalElements());
//    }
//
    @ApiOperation("添加推送")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JsonResponse add(@ApiIgnore Authentication authentication,
                            String content,
                            String pushTimeStr,
                            boolean pushAll,
                            String countries
                            ) throws UmiException, ParseException {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
        Date pushTime=null;
        if(!StringUtils.isEmpty(pushTimeStr)){
            pushTime=simpleDateFormat.parse(pushTimeStr);
        }
        if(pushAll){
            countries="";
        }
       messageService.createNotiMessage(content,countries,pushTime);
       return JsonResponse.successResponse();
    }
//
    @ApiOperation("推送列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResponse burnList(@ApiIgnore Authentication authentication,
                                                      String param,
                                                      Integer pushStatus,
                                                      Date  stDate,
                                                      Date  edDate,
                                                      Pageable pageable
    ) throws UmiException {
        if (pageable == null) {
            pageable = new PageRequest(0, 10);
        }
        Page<Message> messages = messageService.queryMessages(param,pushStatus,stDate,edDate,pageable);
        return JsonResponse.successResponseWithPageData(messages);
    }

    @ApiOperation("修改推送状态")
    @RequestMapping(value = "/changeStatus", method = RequestMethod.GET)
    public JsonResponse burnList(@ApiIgnore Authentication authentication,
                                 Long messageId,
                                 boolean expired
    ) throws UmiException {

        Message messages = messageRepository.findOne(messageId);
        if(expired){
            if(messages.isRead()){
                return JsonResponse.errorResponseWithError("","该内容已推送,无法取消");
            }
        }else{
            if(null!=messages.getNotifyTime()){
                if(System.currentTimeMillis()>messages.getNotifyTime().getTime()){
                    return JsonResponse.errorResponseWithError("","该内容已推送,无法取消");
                }
            }else{
                return JsonResponse.errorResponseWithError("","该内容为立即推送,无法取消");
            }
        }
        messages.setExpired(expired);
        messageRepository.save(messages);
        return JsonResponse.successResponse();
    }

}
