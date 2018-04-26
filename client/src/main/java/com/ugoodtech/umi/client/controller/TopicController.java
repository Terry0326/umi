package com.ugoodtech.umi.client.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.client.dto.CommentDto;
import com.ugoodtech.umi.client.dto.RichTopic;
import com.ugoodtech.umi.client.service.TopicService;
import com.ugoodtech.umi.core.domain.*;
import com.ugoodtech.umi.core.domain.converter.TopicTypeConverter;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.TopicRepository;
import com.ugoodtech.umi.core.utils.DateUtil;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Api(description = "帖子api")
@RestController
@RequestMapping("/topics")
public class TopicController {
    @Autowired
    TopicService topicService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
        binder.registerCustomEditor(Topic.TopicType.class, new TopicTypeConverter());
    }

    @ApiOperation("主页帖子列表")
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public JsonResponse<List<RichTopic>> getUserHomeTopics(@ApiIgnore Authentication authentication,
                                                           @ApiParam("页码") @RequestParam(required = false, defaultValue = "0")
                                                           Integer page,
                                                           @ApiParam("一页数据条数") @RequestParam(required = false, defaultValue = "10")
                                                           Integer size)
            throws UmiException, ParseException {

        User user = (User) authentication.getPrincipal();
        Pageable pageable = new PageRequest(page, size);
        Page<Topic> topicPage = topicService.getHomeTopics(user, pageable);
        PageImpl<RichTopic> richTopicPage = getRichTopics(user, pageable, topicPage);

        return JsonResponse.successResponseWithPageData(richTopicPage);
    }


    @ApiOperation("焚烧帖子列表")
    @RequestMapping(value = "/burnTopics", method = RequestMethod.GET)
    public JsonResponse<List<RichTopic>> getBurnTopics(@ApiIgnore Authentication authentication,
                                                       @ApiParam("页码") @RequestParam(defaultValue = "0") Integer page,
                                                       @ApiParam("一页数据条数") @RequestParam(defaultValue = "10") Integer size)
            throws UmiException, ParseException {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = new PageRequest(page, size);
        Page<Topic> topicPage = topicService.getBurnTopics(user, pageable);
        PageImpl<RichTopic> richTopicPage = getRichTopics(user, pageable, topicPage);
        return JsonResponse.successResponseWithPageData(richTopicPage);
    }
    @ApiOperation("随机焚烧帖子列表")
    @RequestMapping(value = "/randBurnTopics", method = RequestMethod.GET)
    public JsonResponse<List<RichTopic>> getRandBurnTopics(@ApiIgnore Authentication authentication,
                                                       @ApiParam("页码") @RequestParam(defaultValue = "0") Integer page,
                                                       @ApiParam("一页数据条数") @RequestParam(defaultValue = "10") Integer size)
            throws UmiException  , ParseException {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = new PageRequest(page, size);
        Page<Topic> topicPage = topicService.getRandBurnTopics(user, pageable);
        PageImpl<RichTopic> richTopicPage = getRichTopics(user, pageable, topicPage);
        return JsonResponse.successResponseWithPageData(richTopicPage);
    }
    @ApiOperation("判断帖子是否删除")
    @RequestMapping(value = "/isDelete", method = RequestMethod.GET)
    public JsonResponse<Boolean> getRandBurnTopics(@ApiIgnore Authentication authentication,
                                                           @ApiParam("帖子ID") Long topicId )
            throws UmiException  , ParseException {
        Boolean flag=topicService.isDelete(topicId);
        return JsonResponse.successResponseWithData(flag);
    }
    private PageImpl<RichTopic> getRichTopics(User user, Pageable pageable, Page<Topic> topicPage) throws ParseException {
        List<RichTopic> richTopics = new ArrayList<>();
        for (Topic topic : topicPage) {
            RichTopic richTopic = topicService.getRichTopic(user, topic, 4);
            richTopics.add(richTopic);
        }
        return new PageImpl<>(richTopics, pageable, topicPage.getTotalElements());
    }
    private PageImpl<RichTopic> getRichTopics2(User user, Pageable pageable, List<Topic> topicPage) throws ParseException  {
        List<RichTopic> richTopics = new ArrayList<>();
        for (Topic topic : topicPage) {
            RichTopic richTopic = topicService.getRichTopic(user, topic, 4);
            richTopics.add(richTopic);
        }
        return new PageImpl<>(richTopics, pageable,topicPage.size());
    }

    @ApiOperation("帖子详情")
    @ApiResponse(code = 200, message = "帖子详情", response = RichTopic.class)
    @RequestMapping(method = RequestMethod.GET)
    public JsonResponse<RichTopic> getTopicDetail(@ApiIgnore Authentication authentication,
                                                  @ApiParam("帖子id") @RequestParam Long topicId) throws ParseException{
        User user = (User) authentication.getPrincipal();
        Topic topic = topicService.getTopic(topicId);
        RichTopic richTopic = topicService.getRichTopic(user, topic, 10);
        return JsonResponse.successResponseWithData(richTopic);
    }

    @ApiOperation("发帖")
    @RequestMapping(method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address", value = "具体地址", paramType = "query"),
            @ApiImplicitParam(name = "buildingName", value = "建筑物名字", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "城市", paramType = "query"),
            @ApiImplicitParam(name = "lat", value = "纬度", paramType = "query"),
            @ApiImplicitParam(name = "lng", value = "经度", paramType = "query")})
    public JsonResponse<Topic> createTopic(@ApiIgnore Authentication authentication,
                                           @ApiParam("封面图片,可不传") @RequestParam(required = false) String cover,
                                           @ApiParam("图片key或视频key,多个key以逗号分隔") @RequestParam String content,
                                           @ApiParam(value = "说明文字") @RequestParam(required = false) String description,
                                           @ApiParam("帖子类型:{1:图片,2:视频}") @RequestParam Topic.TopicType type,
                                           @ApiParam("是否焚烧帖:true是,false不是") @RequestParam(defaultValue = "false") boolean isBurn,
                                           @ApiParam("是否匿名:true是,false不是") @RequestParam(defaultValue = "false") boolean isAnonymous,
                                           @ApiParam("标记用户id列表,多个逗号分隔") @RequestParam(required = false) String markUserIds,
                                           @ApiIgnore TopicAddress topicAddress) throws UmiException {
        User user = (User) authentication.getPrincipal();
        logger.debug("createTopic by user:" + user.getId());
        Topic topic = new Topic();
        topic.setUser(user);
        topic.setCover(cover);
        topic.setTimed(true);
        topic.setContent(content);
        topic.setBurned(false);
        topic.setBurnTopic(isBurn);
        topic.setAliveTime(DateUtil.getNextDayCalender());
        topic.setDeleted(false);
        topic.setTopicType(type);
        topic.setAnonymous(isAnonymous);
        topic.setDescription(description);
        topic.setAddress(topicAddress);
        topic = topicService.createTopic(user, topic, markUserIds);
        return JsonResponse.successResponseWithData(topic);
    }


    @ApiOperation("删帖")
    @RequestMapping(method = RequestMethod.DELETE)
    public JsonResponse<Topic> deleteTopic(@ApiIgnore Authentication authentication,
                                           @ApiParam("帖子id") @RequestParam Long topicId) throws UmiException {
        User user = (User) authentication.getPrincipal();
        topicService.deleteTopic(user, topicId);
        return JsonResponse.successResponse();
    }

    @ApiOperation("通过Post删帖")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public JsonResponse<Topic> deleteTopicByPost(@ApiIgnore Authentication authentication,
                                                 @ApiParam("帖子id") @RequestParam Long topicId) throws UmiException {
        User user = (User) authentication.getPrincipal();
        topicService.deleteTopic(user, topicId);
        return JsonResponse.successResponse();
    }

    @ApiOperation("添加评论")
    @RequestMapping(value = "/comments", method = RequestMethod.POST)
    public JsonResponse<CommentDto> addComment(@ApiIgnore Authentication authentication,
                                               @ApiParam("帖子id") @RequestParam Long topicId,
                                               @ApiParam("评论") @RequestParam String comment,
                                               @ApiParam("是否焚烧评论")
                                               @RequestParam(required = false, defaultValue = "false")
                                               boolean burnable
    ) throws UmiException {
        User user = (User) authentication.getPrincipal();
        TopicComment topicComment = topicService.addComment(user.getId(), topicId, comment, burnable);
        return JsonResponse.successResponseWithData(new CommentDto(topicComment));
    }

    @ApiOperation("删评论")
    @RequestMapping(value = "/comments", method = RequestMethod.DELETE)
    public JsonResponse deleteComment(@ApiIgnore Authentication authentication,
                                      @ApiParam("评论id") @RequestParam Long commentId
    ) throws UmiException {
        User user = (User) authentication.getPrincipal();
        topicService.deleteComment(user.getId(), commentId);
        return JsonResponse.successResponse();
    }

    @ApiOperation("删评论")
    @RequestMapping(value = "/comments/delete", method = RequestMethod.POST)
    public JsonResponse deleteCommentByPost(@ApiIgnore Authentication authentication,
                                            @ApiParam("评论id") @RequestParam Long commentId
    ) throws UmiException {
        User user = (User) authentication.getPrincipal();
        topicService.deleteComment(user.getId(), commentId);
        return JsonResponse.successResponse();
    }

    @ApiOperation("评论列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码从0开始", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "每页数据条数", paramType = "query")
    })
    @RequestMapping(value = "/comments", method = RequestMethod.GET)
    public JsonResponse<List<CommentDto>> getComments(@ApiIgnore @AuthenticationPrincipal User user,
                                                      @ApiParam("帖子id") @RequestParam Long topicId,
                                                      @ApiIgnore @PageableDefault Pageable pageable) throws UmiException {
        Page<TopicComment> comments = topicService.getTopicComments(user, topicId, pageable);
        List<CommentDto> commentDtos = new ArrayList<>();
        for (TopicComment comment : comments) {
            commentDtos.add(new CommentDto(comment));
        }
        return JsonResponse.successResponseWithPageData(commentDtos, comments.getTotalElements());
    }

    @ApiOperation("点赞")
    @RequestMapping(value = "/like", method = RequestMethod.POST)
    public JsonResponse like(@ApiIgnore Authentication authentication,
                             @ApiParam("帖子id") @RequestParam Long topicId) throws UmiException {
        User user = (User) authentication.getPrincipal();
            topicService.like(user, topicId);
            return JsonResponse.successResponse();
    }

    @ApiOperation("点击率")
    @RequestMapping(value = "/clickNum", method = RequestMethod.POST)
    public JsonResponse updateTopicClickNum(@ApiIgnore Authentication authentication,
                             @ApiParam("帖子id") @RequestParam Long topicId) throws UmiException {
        topicService.updateTopicClickNum(topicId);
        return JsonResponse.successResponse();
    }
    @ApiOperation("取消点赞")
    @RequestMapping(value = "/unlike", method = RequestMethod.POST)
    public JsonResponse<Topic> unlike(@ApiIgnore Authentication authentication,
                                      @ApiParam("帖子id") @RequestParam Long topicId) throws UmiException {
        System.out.println("unlike==========");
        User user = (User) authentication.getPrincipal();
        topicService.unlike(user, topicId);
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
    @ApiOperation("热门帖子")
    @RequestMapping(value = "/hotTopics")
    public JsonResponse<List<RichTopic>> getHotTopics(@ApiIgnore Authentication authentication,
                                                              @ApiParam("页码") @RequestParam(defaultValue = "0") Integer page,
                                                              @ApiParam("页码,可以不传搜索10条") @RequestParam(required = false) String size
    ) throws UmiException , ParseException{
        Integer sizes = null;
        try {
            sizes =  Integer.valueOf(size);
        }catch (Exception e){
            sizes = 10;
        }
        if(sizes==null||sizes<=0){
            sizes=10;
        }
        User user = (User) authentication.getPrincipal();
        Pageable pageable = new PageRequest(page, sizes);
        Page<Topic> topics = topicService.getHotTopics(user,pageable);
        Page<RichTopic> richTopicPage = getRichTopics(user, pageable, topics);
        return JsonResponse.successResponseWithPageData(richTopicPage);
    }

    @ApiOperation("主页用户热门帖子")
    @RequestMapping(value = "/userPopular")
    public JsonResponse<List<RichTopic>> getUserPopularTopics(@ApiIgnore Authentication authentication,
                                                              @ApiParam("用户id") @RequestParam Long userId,
                                                              @ApiParam("页码") @RequestParam(defaultValue = "0") Integer page,
                                                              @ApiParam("页码") @RequestParam(defaultValue = "10") Integer size
    ) throws UmiException , ParseException{
        System.out.println("user================="+userId);
        Boolean anonymous=false;
        User user = (User) authentication.getPrincipal();
        if (userId == null) {
            userId = user.getId();
            anonymous=true;
        }
        Pageable pageable = new PageRequest(page, size);
        Page<Topic> topics = topicService.getUserPopularTopics(
                +userId, pageable,anonymous);
        Page<RichTopic> richTopicPage = getRichTopics(user, pageable, topics);
        return JsonResponse.successResponseWithPageData(richTopicPage);
    }

    @ApiOperation("主页用户自己帖子")
    @RequestMapping(value = "/userTopics")
    public JsonResponse<List<RichTopic>> getUserTopics(@ApiIgnore Authentication authentication,
                                                       @ApiParam("用户id,不传取当前登录用户id") @RequestParam(required = false) Long userId,
                                                       @ApiParam("页码") @RequestParam(defaultValue = "0") Integer page,
                                                       @ApiParam("页码") @RequestParam(defaultValue = "10") Integer size
    ) throws UmiException , ParseException{
        System.out.println("user================="+userId);
        User user = (User) authentication.getPrincipal();
        Boolean anonymous=false;
        if (userId == null) {
            userId = user.getId();
            anonymous=true;
        }
        Pageable pageable = new PageRequest(page, size);
        Page<Topic> topics = topicService.getUserTopics(userId, pageable,anonymous);
        Page<RichTopic> richTopicPage = getRichTopics(user, pageable, topics);
        return JsonResponse.successResponseWithPageData(richTopicPage);
    }

    @ApiOperation("搜索帖子")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public JsonResponse<List<RichTopic>> unfollow(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("搜索关键字") @RequestParam String qKey,
            @ApiParam("页码") @RequestParam(defaultValue = "0") Integer page,
            @ApiParam("页码") @RequestParam(defaultValue = "10") Integer size) throws UmiException , ParseException{
        Pageable pageable = new PageRequest(page, size);
        Page<Topic> topicPage = topicService.query(user, qKey, pageable);
        Page<RichTopic> richTopicPage = getRichTopics(user, pageable, topicPage);
        return JsonResponse.successResponseWithPageData(richTopicPage);
    }
     Logger logger = LoggerFactory.getLogger(TopicController.class);
}
