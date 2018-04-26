package com.ugoodtech.umi.client.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.client.dto.MessageDto;
import com.ugoodtech.umi.client.service.MessageService;
import com.ugoodtech.umi.core.domain.Message;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.domain.converter.MessageTypeConverter;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.TopicRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Api("消息接口")
@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private TopicRepository topicRepository;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
        binder.registerCustomEditor(Message.MessageType.class, new MessageTypeConverter());
    }

    @ApiOperation("获取用户消息列表")
    @RequestMapping(method = RequestMethod.GET)
    public JsonResponse<Map<Integer, Map<Integer,List<MessageDto>>>> getUserMessages(@AuthenticationPrincipal User user) {
        Map<Integer, Map<Integer,List<MessageDto>>> map=messageService.getUserNotificationMessages(user);
      //  Map<Integer, List<MessageDto>> messageMap = messageService.getUserNotificationMessages(user);
        System.out.println("map==="+map);
         return  JsonResponse.successResponseWithData(map);
    }

    @ApiOperation("获取用户未读消息数量,不包括实时评论消息")
    @RequestMapping(value = "/unreadNum", method = RequestMethod.GET)
    public JsonResponse<Integer> getUserUnreadMessagesNum(@AuthenticationPrincipal User user) {
        Integer unreadNum = messageService.getUserUnreadMessagesNum(user);
        return JsonResponse.successResponseWithData(unreadNum);
    }
    @ApiOperation("获取用户评论列表")
    @RequestMapping(value = "/commList", method = RequestMethod.GET)
    public JsonResponse<List<MessageDto>>  getUserCommentList(@AuthenticationPrincipal User user,
                                                    @ApiParam("页码,从0开始,默认0")
                                                    @RequestParam(defaultValue = "0") Integer page,
                                                    @ApiParam("每页条数,默认10")
                                                    @RequestParam(defaultValue = "10") Integer size) {
        Map<Integer,List<MessageDto>> map= messageService.getInstantCommentMessages(user,page,size);
        System.out.println("===============commList====================="+map);
        List<MessageDto> list=new ArrayList<>();
        for(Integer key : map.keySet()){
            list=map.get(key);
             return JsonResponse.successResponseWithPageData(list,(long)list.size());
        }
        return JsonResponse.successResponseWithPageData(list,(long)list.size());
    }
    @ApiOperation("获取用户指定类型的消息列表")
    @RequestMapping(value = "/byType", method = RequestMethod.GET)
    public JsonResponse<List<MessageDto>> getUserMessagesByType(@AuthenticationPrincipal User user,
                                                                @ApiParam("消息类型:{0: '公告';1:'关注';2:实时评论;3:@了你;4:点赞;6:标记")
                                                                @RequestParam Message.MessageType type,
                                                                @ApiParam("页码,从0开始,默认0")
                                                                @RequestParam(defaultValue = "0") Integer page,
                                                                @ApiParam("每页条数,默认10")
                                                                @RequestParam(defaultValue = "10") Integer size) {
        Page<MessageDto> messagesByType = messageService.getUserMessageDtos(user, type, page, size);
        return JsonResponse.successResponseWithPageData(messagesByType);
    }

    @ApiOperation("标记消息已读")
    @RequestMapping(value = "/markRead", method = RequestMethod.POST)
    public JsonResponse markMessageRead(@ApiIgnore @AuthenticationPrincipal User user,
                                        @ApiParam("消息id") @RequestParam Long messageId) {

        messageService.markUserMessageRead(user, messageId);
        return JsonResponse.successResponse();
    }

    @ApiOperation("标记指定帖子下所有未读的指定类型的消息为已读")
    @RequestMapping(value = "/markReadForLink", method = RequestMethod.POST)
    public JsonResponse markReadForLink(@ApiIgnore @AuthenticationPrincipal User user,
                                        @ApiParam("帖子id") @RequestParam Long topicId,
                                        @ApiParam("消息类型{2:实时评论,4:点赞}")
                                        @RequestParam Message.MessageType messageType) {
        System.out.println("=========markRead==================");
        messageService.markUserMessagesReadByLinkId(user, messageType, topicId);
        return JsonResponse.successResponse();
    }
    @ApiOperation("根据消息类型标记消息已读")
    @RequestMapping(value = "/markReadByType", method = RequestMethod.POST)
    public JsonResponse markMessageReadByType(@ApiIgnore @AuthenticationPrincipal User user,
                                              @ApiParam("消息类型{1:关注,2:实时评论,3:@,4:点赞}")
                                              @RequestParam Message.MessageType messageType) {
        messageService.markUserMessagesRead(user, messageType);
        return JsonResponse.successResponse();
    }

    @ApiOperation("删除消息")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public JsonResponse delete(@ApiIgnore @AuthenticationPrincipal User user,
                               @ApiParam("消息ID") @RequestParam Long messageId) throws UmiException {
        messageService.deleteMessage(user, messageId);
        return JsonResponse.successResponse();
    }

    @ApiOperation("通过linkId删除消息")
    @RequestMapping(value = "/deleteForLink", method = RequestMethod.POST)
    public JsonResponse deleteForLink(@ApiIgnore @AuthenticationPrincipal User user,
                                      @ApiParam("帖子id") @RequestParam Long topicId,
                                      @ApiParam("消息类型{2:实时评论,4:点赞}")
                                      @RequestParam Message.MessageType messageType) throws UmiException {
        messageService.deleteMessageForLink(user, topicId, messageType);
        return JsonResponse.successResponse();
    }

    @ApiOperation("通过类型删除消息")
    @RequestMapping(value = "/deleteForType", method = RequestMethod.POST)
    public JsonResponse deleteForType(@ApiIgnore @AuthenticationPrincipal User user,
                                      @ApiParam("消息类型{2:实时评论,3:AT}")
                                      @RequestParam Message.MessageType messageType) throws UmiException {
        messageService.deleteMessageForType(user, messageType);
        return JsonResponse.successResponse();
    }

}
