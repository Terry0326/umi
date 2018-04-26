package com.ugoodtech.umi.client.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.ugoodtech.umi.client.dto.CommentDto;
import com.ugoodtech.umi.client.dto.RichTopic;
import com.ugoodtech.umi.client.service.AliYunService;
import com.ugoodtech.umi.client.service.TopicService;
import com.ugoodtech.umi.core.domain.Topic;
import com.ugoodtech.umi.core.domain.TopicComment;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.exception.UmiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api("分享数据接口")
@RestController
@RequestMapping("/share")
public class ShareController {
    @Autowired
    private TopicService topicService;
    @Value("${oss.image.urlPrefix}")
    private String imageUrlPrefix;
    @Value("${oss.urlPrefix}")
    private String videoUrlPrefix;
    @Autowired
    AliYunService aliYunService;

    @ApiOperation("分享帖子详情")
    @ApiResponse(code = 200, message = "帖子详情", response = RichTopic.class)
    @RequestMapping(value = "/topic", method = RequestMethod.GET)
    public JsonResponse<RichTopic> getTopicDetail(@ApiParam("帖子id") @RequestParam Long id) throws UmiException {
        Topic topic = topicService.getTopic(id);
        RichTopic richTopic = getRichTopic(topic, 10);
        richTopic.setImageUrlPrefix(imageUrlPrefix);
        if (richTopic.getTopicType().equals(Topic.TopicType.Video)) {//视频
            GetPlayInfoResponse response = aliYunService.getPlayInfo(richTopic.getContent(), "mp4");
            richTopic.setContent(response.getPlayInfoList().get(0).getPlayURL());
        } else {//图片
            String content = topic.getContent();
            String[] imageKeys = content.split(",");
            StringBuilder builder = new StringBuilder();
            for (String imageKey : imageKeys) {
                builder.append(topic.getId()).append("-").append(imageKey).append(",");
            }
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
            richTopic.setContent(builder.toString());
        }
        return JsonResponse.successResponseWithData(richTopic);
    }

    private RichTopic getRichTopic(Topic topic, int commentPageSize) {
        RichTopic richTopic = new RichTopic(topic);
        richTopic.setLikeNum(topicService.getTopicLikeNum(topic.getId()));
        richTopic.setLiked(false);
        richTopic.setCommentsNum(topic.getCommentsNum() == null ? 0 : topic.getCommentsNum());
        richTopic.setBurnableCommentsNum(topicService.getBurnableCommentsNum(topic.getId()));
        //
        Pageable commentsPage = new PageRequest(0, commentPageSize, new Sort(Sort.Direction.DESC, "creationTime"));
        Page<TopicComment> comments = topicService.getTopicComments(null, topic.getId(), commentsPage);
        richTopic.setCommentsNum(comments.getTotalElements());
        for (TopicComment comment : comments) {
            richTopic.getComments().add(new CommentDto(comment));
        }

        return richTopic;
    }
}
