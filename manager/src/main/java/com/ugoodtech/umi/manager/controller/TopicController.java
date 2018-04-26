package com.ugoodtech.umi.manager.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.aliyun.oss.OSSClient;
import com.ugoodtech.umi.core.domain.*;
import com.ugoodtech.umi.core.domain.converter.TopicTypeConverter;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.CommentRepository;
import com.ugoodtech.umi.core.utils.Base64ConvertImageUtil;
import com.ugoodtech.umi.manager.dto.CommentDto;
import com.ugoodtech.umi.manager.dto.RichTopic;
import com.ugoodtech.umi.manager.service.TopicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(description = "帖子管理api")
@RestController
@RequestMapping("/topics")
public class TopicController {
    @Autowired
    TopicService topicService;

    @Autowired
    CommentRepository commentRepository;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm"), true));
        binder.registerCustomEditor(Topic.TopicType.class, new TopicTypeConverter());
    }

    private RichTopic getRichTopics(Topic topic) {
        RichTopic richTopic = new RichTopic(topic);
        richTopic.setLikeNum(topicService.getLikeNum(topic.getId()));
        Pageable commentsPage = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "creationTime"));
        Page<TopicComment> comments = topicService.getTopicComments(topic.getId(), commentsPage);
        richTopic.setCommentsNum(comments.getTotalElements());
        for (TopicComment comment : comments) {
            richTopic.getComments().add(new CommentDto(comment));
        }
        return richTopic;
    }

    private PageImpl<RichTopic> getRichTopics(Pageable pageable, Page<Topic> topicPage) {
        List<RichTopic> richTopics = new ArrayList<>();
        for (Topic topic : topicPage) {
            RichTopic richTopic = getRichTopics(topic);
            richTopics.add(richTopic);
        }
        return new PageImpl<>(richTopics, pageable, topicPage.getTotalElements());
    }

    @ApiOperation("帖子列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResponse<List<RichTopic>> list(@ApiIgnore Authentication authentication,
                                              String param,
                                              Boolean enabled,
                                              Date stDate,
                                              Date edDate,
                                              Pageable pageable
    ) throws UmiException {
        if (pageable == null) {
            pageable = new PageRequest(0, 10);
        }
        Page<Topic> topics = topicService.getUserTopics(null, false, param, enabled, stDate, edDate, pageable);
        Page<RichTopic> richTopics = getRichTopics(pageable, topics);
        return JsonResponse.successResponseWithPageData(richTopics);
    }


    @ApiOperation("焚烧帖子列表")
    @RequestMapping(value = "/burnList", method = RequestMethod.GET)
    public JsonResponse<List<RichTopic>> burnList(@ApiIgnore Authentication authentication,
                                                  String param,
                                                  Boolean enabled,
                                                  Date stDate,
                                                  Date edDate,
                                                  @ApiParam("分页参数,传page和size即可") Pageable pageable
    ) throws UmiException {
        if (pageable == null) {
            pageable = new PageRequest(0, 10);
        }
        Page<Topic> topics = topicService.getUserTopics(null, true, param, enabled, stDate, edDate, pageable);
        Page<RichTopic> richTopics = getRichTopics(pageable, topics);
        return JsonResponse.successResponseWithPageData(richTopics);
    }
    @ApiOperation("定时帖子列表")
    @RequestMapping(value = "/timedList", method = RequestMethod.GET)
    public JsonResponse<List<RichTopic>> timedList(@ApiIgnore Authentication authentication,

                                                  @ApiParam("分页参数,传page和size即可") Pageable pageable
    ) throws UmiException {
        if (pageable == null) {
            pageable = new PageRequest(0, 10);
        }
        Page<Topic> topics = topicService.getTimedTopics(pageable);
        Page<RichTopic> richTopics = getRichTopics(pageable, topics);
        return JsonResponse.successResponseWithPageData(richTopics);
    }
    @ApiOperation("发帖")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JsonResponse<Topic> createTopic(@ApiIgnore Authentication authentication,
                                           @ApiParam("封面图片,可不传") @RequestParam(required = false) String cover,
                                           //   @ApiParam("图片key或视频key,多个key以逗号分隔") @RequestParam String content,
                                           @ApiParam(value = "说明文字") @RequestParam(required = false) String description,
                                           @ApiParam(value = "图片内容") @RequestParam(required = false) String content,
                                           @ApiParam("焚烧的结束时间") @RequestParam String aliveTime,
                                           @ApiParam("定时发送的时间") @RequestParam Date timedTime,
                                           @ApiParam("是否定时帖:true是,false不是") @RequestParam(defaultValue = "false") boolean isTime,
                                           @ApiParam("帖子类型:{1:图片,2:视频}") @RequestParam Integer typeCode,
                                           @ApiParam("是否焚烧帖:true是,false不是") @RequestParam(defaultValue = "false") boolean isBurn,
                                           @ApiParam("是否匿名:true是,false不是") @RequestParam(defaultValue = "false") boolean isAnonymous,
                                           @ApiParam("fileList(图片list)") @RequestParam(defaultValue = "false") String uploadList,HttpSession session
    ) throws UmiException {
        SystemUser currentUser = (SystemUser) authentication.getPrincipal();
        Topic topic = new Topic();
        if (null == currentUser.getAppUid()) {
            throw new UmiException(1000, "你没有发帖的权限！" + currentUser.getId() + "==" + currentUser.getAppUid());
        }
        topic.setCover(cover);
        topic.setContent(content);
        topic.setTimeTopic(isTime);
        if(isTime){
            topic.setTimed(false);
        }else{
            topic.setTimed(true);
        }

        TopicAddress address = new TopicAddress();
        address.setLat(121.469616);
        address.setLng(31.207518);
        address.setAddress("上海市");
        address.setBuildingName("上海市");
        topic.setBurned(false);
        topic.setBurnTopic(isBurn);
        topic.setEnabled(true);
        System.out.println("aaaaaaaaaaaaaaaaa===timedTime==="+timedTime);
        topic.setTimedTime(timedTime);
        if (isBurn){
            topic.setAliveTime(aliveTime);
        }
        topic.setDeleted(false);
        if (typeCode==1){
            topic.setTopicType(Topic.TopicType.Image);
        }else {
            topic.setTopicType(Topic.TopicType.Video);
        }
        topic.setAnonymous(isAnonymous);
        topic.setDescription(description);
        topic.setAddress(address);
        topic = topicService.createTopic(currentUser, topic,uploadList);

        return JsonResponse.successResponseWithData(topic);
    }
    @ApiOperation("帖子详情")
    @ApiResponse(code = 200, message = "帖子详情", response = RichTopic.class)
    @RequestMapping(method = RequestMethod.GET)
    public JsonResponse<RichTopic> getTopicDetail(@ApiIgnore Authentication authentication,
                                                  @ApiParam("帖子id") @RequestParam Long topicId) {
        User user = (User) authentication.getPrincipal();
        Topic topic = topicService.getTopic(topicId);
        RichTopic richTopic = new RichTopic(topic);
        richTopic.setLikeNum(topicService.getLikeNum(topic.getId()));
        List<TopicComment> comments = (List<TopicComment>) topicService.getTopicComments(topic.getId());
        richTopic.setCommentsNum(Long.parseLong(comments.size() + ""));
        for (TopicComment comment : comments) {
            richTopic.getComments().add(new CommentDto(comment));
        }
        return JsonResponse.successResponseWithData(richTopic);
    }

    @ApiOperation("删帖")
    @RequestMapping(method = RequestMethod.DELETE)
    public JsonResponse<Topic> deleteTopic(@ApiIgnore Authentication authentication,
                                           @ApiParam("帖子id") @RequestParam Long topicId) throws UmiException {
        User user = (User) authentication.getPrincipal();
        topicService.deleteTopic(user, topicId);
        return JsonResponse.successResponse();
    }

    @ApiOperation("改状态")
    @RequestMapping(value = "/changStatus", method = RequestMethod.POST)
    public JsonResponse<Topic> deleteTopic(@ApiIgnore Authentication authentication,
                                           @ApiParam("帖子id") @RequestParam Long topicId,
                                           @ApiParam("激活禁用") @RequestParam boolean enabled) throws UmiException {
        topicService.changeTopicEnable(topicId,enabled);
        return JsonResponse.successResponse();
    }

    @ApiOperation("举报帖子")
    @RequestMapping(value = "/tipOff", method = RequestMethod.POST)
    public JsonResponse<Topic> tipOff(@ApiIgnore Authentication authentication,
                                      @ApiParam("帖子id") @RequestParam Long topicId,
                                      @ApiParam("举报原因") @RequestParam String reason) throws UmiException {

        User user = (User) authentication.getPrincipal();
        topicService.tipOff(user, topicId, reason);
        return JsonResponse.successResponse();
    }

    @ApiOperation("帖子评论列表")
    @RequestMapping(value = "/{topicId}/comments", method = RequestMethod.GET)
    public JsonResponse<List<TopicComment>> getUserTopics(@ApiIgnore Authentication authentication,
                                                          @PathVariable("topicId") Long topicId,
                                                          @ApiParam("分页参数,传page和size即可")Pageable pageable
    ) throws UmiException {
        if (pageable == null) {
            pageable = new PageRequest(0, 10);
        }
        Page<TopicComment> comments = topicService.getTopicComments(topicId, pageable);
        List<TopicComment> topicComments=new ArrayList<>();
        for(TopicComment topicComment:comments){
            topicComment.setShowDeleted(topicComment.isDeleted());
            topicComments.add(topicComment);
        }
        Page<TopicComment> retComments=new PageImpl<TopicComment>(topicComments,pageable,comments.getTotalElements());
        return JsonResponse.successResponseWithPageData(retComments);
    }

    @ApiOperation("帖子评论列表")
    @RequestMapping(value = "/{commentId}/del", method = RequestMethod.POST)
    public JsonResponse delComment(@ApiIgnore Authentication authentication,
                                                          @PathVariable("commentId") Long commentId) throws UmiException {
        TopicComment comments = commentRepository.findOne(commentId);
        comments.setDeleted(true);
        commentRepository.save(comments);
        return JsonResponse.successResponse();
    }
}