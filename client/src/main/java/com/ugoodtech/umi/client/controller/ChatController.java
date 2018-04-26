package com.ugoodtech.umi.client.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.client.dto.UserDetailDto;
import com.ugoodtech.umi.client.dto.UserDto;
import com.ugoodtech.umi.client.service.ChatService;
import com.ugoodtech.umi.client.service.impl.UserService;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.exception.UmiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Api("聊天接口")
@RestController
@RequestMapping("/chats")
public class ChatController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;

    @ApiOperation("获取登录用户聊天token")
    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public JsonResponse<String> getImToken(
            @ApiIgnore @AuthenticationPrincipal User user) throws UmiException {
        String token = chatService.getUserImToken(user);
        return JsonResponse.successResponseWithData(token);
    }

    @ApiOperation("根据聊天用户id获取聊天用户基本信息")
    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
    public JsonResponse<UserDto> getUserInfo(@ApiParam("聊天用户id") String userId) throws UmiException {
        User user = userService.getUserByUsername(userId);
        UserDto dto = new UserDto(user);
        return JsonResponse.successResponseWithData(dto);
    }

    @ApiOperation("获取私信聊天室,需要传递两个用户id,不分先后次序")
    @RequestMapping(value = "/privateMessageRoomId", method = RequestMethod.GET)
    public JsonResponse<String> getPrivateMessageRoomId(
            @ApiParam("第一个用户id") Long userOneId,
            @ApiParam("第二个用户id") Long userTwoId
    ) throws UmiException {
        String roomId = chatService.getPrivateMessageRoomId(userOneId, userTwoId);
        return JsonResponse.successResponseWithData(roomId);
    }

    @RequestMapping(value = "/refresh")
    public JsonResponse refreshUsers(
            @ApiIgnore @AuthenticationPrincipal User user
    ) throws UmiException {
        chatService.refreshUsers();
        return JsonResponse.successResponse();
    }
    Logger logger = LoggerFactory.getLogger(ChatController.class);

}
