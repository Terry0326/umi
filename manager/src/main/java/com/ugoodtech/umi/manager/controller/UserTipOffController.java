package com.ugoodtech.umi.manager.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.Topic;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.domain.UserTipDto;
import com.ugoodtech.umi.core.domain.UserTipOff;
import com.ugoodtech.umi.core.domain.converter.TopicTypeConverter;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.TopicRepository;
import com.ugoodtech.umi.core.repository.UserRepository;
import com.ugoodtech.umi.core.repository.UserTipOffRepository;
import com.ugoodtech.umi.manager.service.BlacklistService;
import com.ugoodtech.umi.manager.service.UserTipOffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(description = "用户举报管理api")
@RestController
@RequestMapping("/userTipOff")
public class UserTipOffController {

    @Autowired
    UserTipOffService userTipOffService;

    @Autowired
    UserTipOffRepository userTipOffRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BlacklistService blacklistService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm"), true));
        binder.registerCustomEditor(Topic.TopicType.class, new TopicTypeConverter());
    }
    @ApiOperation("用户举报列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResponse<List<UserTipOff>> list(@ApiIgnore Authentication authentication,
                                                                  Long tipUserId,
                                                                  String param,
                                                                  Boolean done,
                                                                  Date stDate,
                                                                  Date edDate,
                                                                  Pageable pageable
    ) throws UmiException {
        if (pageable == null) {
            pageable = new PageRequest(0, 10);
        }
        Page<UserTipOff> topics = userTipOffService.getUserTipOff(tipUserId,param,done,stDate,edDate, pageable);
         return JsonResponse.successResponseWithPageData(topics);
    }

    @ApiOperation("用户举报列表")
    @RequestMapping(value = "/userTipList", method = RequestMethod.GET)
    public  JsonResponse<List<UserTipDto>> userTipList(@ApiIgnore Authentication authentication,
                                         Long tipUserId,
                                         String param,
                                         Boolean done,
                                         Date stDate,
                                         Date edDate,
                                         Pageable pageable
    ) throws UmiException {
        return JsonResponse.successResponseWithPageData(userTipOffService.getUserTipDto(tipUserId,param,done,stDate,edDate, pageable), userTipOffService.gerTotal(tipUserId,param,done,stDate,edDate));
     }

    @ApiOperation("改状态")
    @RequestMapping(value = "/changStatus", method = RequestMethod.POST)
    public JsonResponse<Topic> deleteTopic(@ApiIgnore Authentication authentication,
                                           @ApiParam("被举报用户id") @RequestParam Long userTipOffId,
                                           @ApiParam("是否禁用,禁用传入禁用天数") @RequestParam String enabled) throws UmiException {
        System.out.println("userTipOffId================="+userTipOffId);
       // UserTipOff userTipOff=userTipOffRepository.findOne(userTipOffId);
        String doneStr="";
        if("true".equals(enabled)){
            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaa不进行操作aa================="+enabled);
            doneStr="不进行操作";
        }else{
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            Date today = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(today);
            c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(enabled));//
            Date time = c.getTime();
            doneStr="禁用用户"+enabled+"天";
            User user=userRepository.findOne(userTipOffId);
            userRepository.disable(Arrays.asList(user.getId()));
            blacklistService.addToBlackList(user.getId(),"用户举报", f.format(time));
        }
        userTipOffRepository.disable(userTipOffId,doneStr);
        return JsonResponse.successResponse();
    }
}
