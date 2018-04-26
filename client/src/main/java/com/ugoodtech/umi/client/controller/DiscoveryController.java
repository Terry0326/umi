package com.ugoodtech.umi.client.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */


import com.ugoodtech.umi.client.dto.RichTopic;
import com.ugoodtech.umi.client.service.TopicService;
import com.ugoodtech.umi.client.service.impl.UserService;
import com.ugoodtech.umi.client.util.LatitudeLontitudeUtil;
import com.ugoodtech.umi.core.domain.Topic;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.exception.UmiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Api("发现")
@RestController
@RequestMapping("/discovery")
public class DiscoveryController {
    @Autowired
    UserService userService;
    @Autowired
    TopicService topicService;

//    @ApiOperation("附近帖子")
//    @RequestMapping(value = "/nearby", method = RequestMethod.GET)
//    public JsonResponse<List<RichTopic>> nearByTopics(@ApiIgnore @AuthenticationPrincipal User user,
//                                                      @ApiParam("当前用户所在纬度") @RequestParam(required = false) Double lat,
//                                                      @ApiParam("当前用户所在经度") @RequestParam(required = false) Double lng,
//                                                      @ApiParam("距离,单位米") @RequestParam(defaultValue = "3000", required = false) Integer distance,
//                                                      @ApiParam("分页参数页码") @RequestParam Integer page,
//                                                      @ApiParam("分页参数每页条数") @RequestParam Integer size) throws UmiException {
//        Pageable pageable = new PageRequest(0, 10);
//        Page<Topic> topicPage = topicService.getNearByTopics(user, lat, lng, distance, pageable);
//        return JsonResponse.successResponseWithPageData(getRichTopics(user, pageable, topicPage));
//    }
    @ApiOperation("附近帖子")
    @RequestMapping(value = "/nearby", method = RequestMethod.GET)
    public JsonResponse<List<RichTopic>> nearByTopics(@ApiIgnore @AuthenticationPrincipal User user,
                                                      @ApiParam("当前用户所在纬度") @RequestParam(required = false) Double lat,
                                                      @ApiParam("当前用户所在经度") @RequestParam(required = false) Double lng,
                                                      @ApiParam("距离,单位米") @RequestParam(defaultValue = "5000", required = false) Integer distance,
                                                      @ApiParam("页码") @RequestParam(required = false, defaultValue = "0")
                                                                  Integer page,
                                                      @ApiParam("一页数据条数") @RequestParam(required = false, defaultValue = "15")
                                                                  Integer size) throws UmiException ,  ParseException{
//        Map<Long,List<Topic>> map = topicService.getNearTopics(user, lat, lng, distance, pageable);
//        if(null!=map){
//            System.out.println("=====================================================");
//            return JsonResponse.successResponseWithPageData(getRichTopics2(user, pageable, map.get(map.keySet().iterator().next()),lat,lng,map.keySet().iterator().next()));
//        }
//        return JsonResponse.successResponse();

        System.out.println("================================lat"+lat+"==lng"+lng+"===distance"+distance+"page"+page+"size"+size);
        Pageable pageable = new PageRequest(page, size);
        Page<Topic> topicPage = topicService.getNearTopics2(user, lat, lng, distance, pageable);
        return  JsonResponse.successResponseWithPageData(getRichTopics2(user, pageable, topicPage,lat,lng));
    }
    @ApiOperation("中国帖子")
    @RequestMapping(value = "/china", method = RequestMethod.GET)
    public JsonResponse<List<RichTopic>> chinaTopics(@ApiIgnore @AuthenticationPrincipal User user,
                                                     @ApiParam("分页参数,传page和size") Pageable pageable)throws ParseException {
        if (pageable == null) {
            pageable = new PageRequest(0, 10);
        }
        Page<Topic> topicPage = topicService.getChinaTopics(user, pageable);
        return JsonResponse.successResponseWithPageData(getRichTopics(user, pageable, topicPage));
    }

    @ApiOperation("中国帖子")
    @RequestMapping(value = "/world", method = RequestMethod.GET)
    public JsonResponse<List<RichTopic>> worldTopics(@ApiIgnore @AuthenticationPrincipal User user,
                                                     @ApiParam("分页参数,传page和size") Pageable pageable) throws ParseException{
        if (pageable == null) {
            pageable = new PageRequest(0, 10);
        }
        Page<Topic> topicPage = topicService.getWorldTopics(user, pageable);
        return JsonResponse.successResponseWithPageData(getRichTopics(user, pageable, topicPage));
    }
    @ApiOperation("随机出现帖子列表")
    @RequestMapping(value = "/rand", method = RequestMethod.GET)
    public JsonResponse<List<RichTopic>> getRandTopics(@ApiIgnore Authentication authentication,
                                                       @ApiParam("页码") @RequestParam(required = false, defaultValue = "0")
                                                               Integer page,
                                                       @ApiParam("一页数据条数") @RequestParam(required = false, defaultValue = "15")
                                                               Integer size)
            throws UmiException ,  ParseException{
        User user = (User) authentication.getPrincipal();
        Pageable pageable = new PageRequest(page, size);
        Page<Topic> topicPage = topicService.getRandBurnTopics(user, pageable);
        PageImpl<RichTopic> richTopicPage = getRichTopics(user, pageable, topicPage);
        return JsonResponse.successResponseWithPageData(richTopicPage);
    }
    @ApiOperation("海外帖子")
    @RequestMapping(value = "/oversea", method = RequestMethod.GET)
    public JsonResponse<List<RichTopic>> overseaTopics(@ApiIgnore @AuthenticationPrincipal User user,
                                                       @ApiParam("分页参数,传page和size") Pageable pageable) throws ParseException{
        if (pageable == null) {
            pageable = new PageRequest(0, 10);
        }
        Page<Topic> topicPage = topicService.getOverseaTopics(user, pageable);
        return JsonResponse.successResponseWithPageData(getRichTopics(user, pageable, topicPage));
    }

    private PageImpl<RichTopic> getRichTopics(User user, Pageable pageable, Page<Topic> topicPage) throws ParseException{
        List<RichTopic> richTopics = new ArrayList<>();
        for (Topic topic : topicPage) {
            richTopics.add(topicService.getRichTopic(user, topic, 10));
        }
        return new PageImpl<>(richTopics, pageable, topicPage.getTotalElements());
    }
    private PageImpl<RichTopic> getRichTopics2(User user, Pageable pageable, List<Topic> topicPage)throws ParseException {
        List<RichTopic> richTopics = new ArrayList<>();
        for (Topic topic : topicPage) {
            RichTopic richTopic = topicService.getRichTopic(user, topic, 4);
            richTopics.add(richTopic);
        }
        return new PageImpl<>(richTopics, pageable,topicPage.size());
    }
    private PageImpl<RichTopic> getRichTopics2(User user, Pageable pageable, Page<Topic> topicPage,Double lat,Double lng) throws ParseException {
        List<RichTopic> richTopics = new ArrayList<>();
        for (Topic topic : topicPage) {
            RichTopic richTopic = topicService.getRichTopic(user, topic, 4);
            richTopic.setDistance(LatitudeLontitudeUtil.getDistance(lat, lng, topic.getAddress().getLat(),topic.getAddress().getLng()));
            richTopics.add(richTopic);
        }
        return new PageImpl<>(richTopics, pageable,topicPage.getTotalElements());
    }
}
