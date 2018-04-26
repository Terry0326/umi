package com.ugoodtech.umi.manager.controller;


import com.ugoodtech.umi.core.domain.Topic;
import com.ugoodtech.umi.core.domain.TopicComment;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.domain.converter.TopicTypeConverter;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.UserRepository;
import com.ugoodtech.umi.manager.dto.CommentDto;
import com.ugoodtech.umi.manager.dto.RichTopic;
import com.ugoodtech.umi.manager.dto.RichTopicComment;
import com.ugoodtech.umi.manager.service.BlacklistService;
import com.ugoodtech.umi.manager.service.TopicService;
import com.ugoodtech.umi.manager.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Copyright © 2013 All Rights Reserved, Ugood Technology, Inc.
 */
@RestController
@RequestMapping("/users")
@Api("用户接口")
public class UserController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    TopicService topicService;

    @Autowired
    BlacklistService blacklistService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm"), true));
        binder.registerCustomEditor(Topic.TopicType.class, new TopicTypeConverter());
    }

    @ApiOperation("获取用户列表")
    @RequestMapping(method = RequestMethod.GET)

    public JsonResponse<List<Map<String ,Object>>> getUsers(String param,@PageableDefault Pageable pageable) throws Exception {
        Page<User> userPage = userService.getUsers(param,pageable);
        List<Map<String ,Object>> dataArray=new ArrayList<>();
        for(User user:userPage){
            Map<String,Object> data=new HashMap<>();
            data.put("user",user);
            data.put("topicNum",topicService.countUserTopics(user.getId(),null));
            data.put("blackTime",null!=blacklistService.getAddBlack(user.getId())?
                    blacklistService.getAddBlack(user.getId()).getCreationTime():null);
            data.put("blackReason",null!=blacklistService.getAddBlack(user.getId())?
                    blacklistService.getAddBlack(user.getId()).getReason():null);

            dataArray.add(data);
        }
        Page<Map<String, Object>> userDataPage = new PageImpl<Map<String, Object>>(dataArray, pageable, userPage.getTotalElements());
        return JsonResponse.successResponseWithPageData(userDataPage);

    }

    @ApiOperation("获取黑名单用户列表")
    @RequestMapping(value = "/disableList", method = RequestMethod.GET)
    public JsonResponse<List<Map<String ,Object>>> disableList(String param,@PageableDefault Pageable pageable) throws Exception {
        Page<User> userPage = userService.getDisableUsers(param,pageable);
        List<Map<String ,Object>> dataArray=new ArrayList<>();
        for(User user:userPage){
            Map<String,Object> data=new HashMap<>();
            data.put("user",user);
            data.put("topicNum",topicService.countUserTopics(user.getId(),null));
            data.put("blackTime",null!=blacklistService.getAddBlack(user.getId())?
                            blacklistService.getAddBlack(user.getId()).getCreationTime():null);
            data.put("endTime",null!=blacklistService.getAddBlack(user.getId())?
                    blacklistService.getAddBlack(user.getId()).getEndTime():null);
            data.put("blackReason",null!=blacklistService.getAddBlack(user.getId())?
                    blacklistService.getAddBlack(user.getId()).getReason():null);

            dataArray.add(data);
        }
        Page<Map<String, Object>> userDataPage = new PageImpl<Map<String, Object>>(dataArray, pageable, userPage.getTotalElements());
        return JsonResponse.successResponseWithPageData(userDataPage);
    }

    @RequestMapping(value = "/editClientUser", method = RequestMethod.POST)
    public JsonResponse editClientUser(User user,
                                       @RequestParam(value = "positionId", required = false) Long positionId) throws Exception {
        try {
            userService.modifyClientUser(user);
        } catch (Exception e) {
            debugException("editClientUser error:", e, logger);
        }

        return JsonResponse.successResponse();
    }

    @RequestMapping(value = "/enableClientUser", method = RequestMethod.POST)
    public JsonResponse enableClientUser(@RequestParam(value = "ids") Long[] ids) throws Exception {

        if (null != ids && ids.length > 0) {
            userRepository.enable(Arrays.asList(ids));
            return JsonResponse.successResponse();
        } else {
            return JsonResponse.errorResponseWithError("fail", "error clientUser id");
        }

    }

    @RequestMapping(value = "/disableClientUser", method = RequestMethod.POST)
    public JsonResponse disableClientUser(@RequestParam(value = "ids") Long[] ids,String reason,Integer endTime) throws Exception {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);

        if (null != ids && ids.length > 0) {
            c.add(Calendar.DAY_OF_MONTH, endTime);//
            Date time = c.getTime();
            userRepository.disable(Arrays.asList(ids));
            blacklistService.addToBlackList(ids[0],reason,f.format(time));
            return JsonResponse.successResponse();
        } else {
            return JsonResponse.errorResponseWithError("fail", "error clientUser id");
        }

    }

    @RequestMapping(value = "/deleteClientUser", method = RequestMethod.POST)
    public JsonResponse deleteClientUser(@RequestParam(value = "ids") Long[] ids) throws Exception {

        if (null != ids && ids.length > 0) {
            userService.deleteClientUser(Arrays.asList(ids));
            return JsonResponse.successResponse();
        } else {
            return JsonResponse.errorResponseWithError("fail", "error clientUser id");
        }

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
            RichTopic richTopic = new RichTopic(topic);
            richTopic.setLikeNum(topicService.getLikeNum(topic.getId()));
            Pageable commentsPage = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "creationTime"));
            Page<TopicComment> comments = topicService.getTopicComments(topic.getId(), commentsPage);
            richTopic.setCommentsNum(comments.getTotalElements());
            for (TopicComment comment : comments) {
                richTopic.getComments().add(new CommentDto(comment));
            }
            richTopics.add(richTopic);
        }
        return new PageImpl<>(richTopics, pageable, topicPage.getTotalElements());
    }

    @ApiOperation("我的帖子列表")
    @RequestMapping(value = "/getMyCommunity", method = RequestMethod.GET)
    public JsonResponse<List<RichTopic>> list(@ApiIgnore Authentication authentication,
                                              Long userId,
                                              String param,
                                              Boolean enabled,
                                              Boolean burnTopic,
                                              Date stDate,
                                              Date edDate,
                                              @ApiParam("分页参数,传page和size即可")Pageable pageable
    ) throws UmiException {
        Page<Topic> topics = topicService.getUserTopics(userId, burnTopic, param, enabled, stDate, edDate, pageable);
        Page<RichTopic> richTopics = getRichTopics(pageable, topics);
        return JsonResponse.successResponseWithPageData(richTopics);
    }

    @ApiOperation("获取我的评论")
    @RequestMapping(value = "/{userId}/getMyComments", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse getMyReply(@PathVariable("userId") Long userId, String param, Date stDate, Date edDate, Pageable pageable) {
        Page<TopicComment> comments = topicService.getUserTopicComments(userId, param, stDate, edDate, pageable);
        List<RichTopicComment> richTopicComments = new ArrayList<>();
        for (TopicComment topicComment : comments) {
            RichTopicComment richTopicComment = new RichTopicComment();
            richTopicComment.setTopic(getRichTopics(topicComment.getTopic()));
            richTopicComment.setContent(topicComment.getContent());
            richTopicComment.setPublisher(topicComment.getPublisher());
            richTopicComment.setShowDeleted(topicComment.isDeleted());
            richTopicComment.setCreationTime(topicComment.getCreationTime());
            richTopicComment.setBurnable(topicComment.isBurnable());
            richTopicComments.add(richTopicComment);
        }
        Page<RichTopicComment> retComments = new PageImpl<RichTopicComment>(richTopicComments);
        return JsonResponse.successResponseWithPageData(retComments);
    }


}
