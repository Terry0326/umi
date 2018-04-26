package com.ugoodtech.umi.manager.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.Topic;
import com.ugoodtech.umi.core.domain.TopicComment;
import com.ugoodtech.umi.core.domain.TopicTipOff;
import com.ugoodtech.umi.core.domain.converter.TopicTypeConverter;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.TopicRepository;
import com.ugoodtech.umi.core.repository.TopicTipOffRepository;
import com.ugoodtech.umi.manager.dto.CommentDto;
import com.ugoodtech.umi.manager.dto.RichTopic;
import com.ugoodtech.umi.manager.dto.RichTopicTipOff;
import com.ugoodtech.umi.manager.service.TopicService;
import com.ugoodtech.umi.manager.service.TopicTipOffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(description = "帖子举报管理api")
@RestController
@RequestMapping("/topicTipOff")
public class TopicTipOffController {

    @Autowired
    TopicTipOffService topicTipOffService;

    @Autowired
    TopicTipOffRepository topicTipOffRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    TopicService topicService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm"), true));
        binder.registerCustomEditor(Topic.TopicType.class, new TopicTypeConverter());
    }


    private PageImpl<RichTopicTipOff> getRichTopicTipOff(Pageable pageable, Page<TopicTipOff> topicPage) {
        List<RichTopicTipOff> richTopics = new ArrayList<>();
        for (TopicTipOff topicTipOff : topicPage) {
            RichTopicTipOff richTopicTipOff =new RichTopicTipOff();
            richTopicTipOff.setId(topicTipOff.getId());
            richTopicTipOff.setUser(topicTipOff.getUser());
            richTopicTipOff.setReason(topicTipOff.getReason());
            richTopicTipOff.setDone(topicTipOff.isDone());
            richTopicTipOff.setDoneStr(topicTipOff.getDoneStr());
            richTopicTipOff.setCreationTime(topicTipOff.getCreationTime());
            RichTopic richTopic=new RichTopic(topicTipOff.getTopic());
            richTopic.setLikeNum(topicService.getLikeNum(topicTipOff.getTopic().getId()));
            Pageable commentsPage = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "creationTime"));
            Page<TopicComment> comments = topicService.getTopicComments(topicTipOff.getTopic().getId(), commentsPage);
            richTopic.setCommentsNum(comments.getTotalElements());
            for (TopicComment comment : comments) {
                richTopic.getComments().add(new CommentDto(comment));
            }
            richTopicTipOff.setTopic(richTopic);
            richTopics.add(richTopicTipOff);
        }
        return new PageImpl<>(richTopics, pageable, topicPage.getTotalElements());
    }



    @ApiOperation("帖子举报列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResponse<List<RichTopicTipOff>> list(@ApiIgnore Authentication authentication,
                                                    String param,
                                                    Boolean done,
                                                    Date stDate,
                                                    Date edDate,
                                                    Pageable pageable
    ) throws UmiException {
        if (pageable == null) {
            pageable = new PageRequest(0, 10);
        }
        Page<TopicTipOff> topics = topicTipOffService.getTopicTipOff(param,done,stDate,edDate, pageable);
        Page<RichTopicTipOff> richTopics=getRichTopicTipOff(pageable,topics);
        return JsonResponse.successResponseWithPageData(richTopics);
    }



    @ApiOperation("改状态")
    @RequestMapping(value = "/changStatus", method = RequestMethod.POST)
    public JsonResponse<Topic> deleteTopic(@ApiIgnore Authentication authentication,
                                           @ApiParam("帖子举报id") @RequestParam Long topicTipOffId,
                                           @ApiParam("激活禁用") @RequestParam boolean enabled) throws UmiException {
        TopicTipOff topicTipOff=topicTipOffRepository.findOne(topicTipOffId);
        topicTipOff.setDone(true);
        if(enabled){
            topicTipOff.setDoneStr("不进行操作");
        }else{
            topicTipOff.setDoneStr("禁用帖子");
            Topic topic=topicTipOff.getTopic();
            topic.setEnabled(false);
            topicRepository.save(topic);
        }
        topicTipOffRepository.save(topicTipOff);

        return JsonResponse.successResponse();
    }


}
