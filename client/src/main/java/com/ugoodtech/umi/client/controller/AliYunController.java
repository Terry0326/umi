package com.ugoodtech.umi.client.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoInfoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.ugoodtech.umi.client.dto.VodCreateUploadVideoResponse;
import com.ugoodtech.umi.client.service.AliYunService;
import com.ugoodtech.umi.client.service.TopicService;
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
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.Map;

@Api("AliYun 接口")
@RestController
public class AliYunController {
    @Autowired
    private AliYunService aliYunService;
    @Autowired
    private TopicService topicService;

    @ApiOperation("获取oss 上传的STS token,需登录后可以访问")
    @RequestMapping(value = "/stsToken", method = RequestMethod.GET)
    public JsonResponse<Map<String, String>> getStsToken(@ApiIgnore @AuthenticationPrincipal User user) {
        try {
            Map<String, String> token = aliYunService.getAppToken(user.getUsername());
            String status = token.get("status");
            if ("200".equals(status)) {
                return JsonResponse.successResponseWithData(token);
            } else {
                return JsonResponse.errorResponse(Integer.parseInt(status), "获取token失败");
            }
        } catch (IOException e) {
            return JsonResponse.errorResponse(1000, "获取token失败");
        }
    }

    /**
     * * @param filename    必选，视频源文件名称（必须带后缀, 支持 ".3gp", ".asf", ".avi", ".dat", ".dv", ".flv", ".f4v", ".gif", ".m2t", ".m3u8", ".m4v", ".mj2", ".mjpeg", ".mkv", ".mov", ".mp4", ".mpe", ".mpg", ".mpeg", ".mts", ".ogg", ".qt", ".rm", ".rmvb", ".swf", ".ts", ".vob", ".wmv", ".webm"".aac", ".ac3", ".acm", ".amr", ".ape", ".caf", ".flac", ".m4a", ".mp3", ".ra", ".wav", ".wma"）
     *
     * @param title       必选，视频标题
     * @param categoryId  可选，分类ID
     * @param tags        可选，视频标签，多个用逗号分隔
     * @param description 可选，视频描述
     * @param fileSize    可选，视频源文件字节数
     * @param user
     * @return
     */
    @ApiOperation("获取视频上传地址和凭证")
    @RequestMapping(value = "/uploadVideoAddressAndAuth", method = RequestMethod.GET)
    public JsonResponse<VodCreateUploadVideoResponse> getUploadVideoAddressAndAuth(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("必选，视频源文件名称,必须带后缀:3gp,asf,avi,dat,dv,flv,mp4,mpeg等") @RequestParam String filename,
            @ApiParam("必选，视频标题") @RequestParam String title,
            @ApiParam("可选，分类ID") @RequestParam(required = false) Integer categoryId,
            @ApiParam("可选，视频标签，多个用逗号分隔") @RequestParam(required = false) String tags,
            @ApiParam("可选，视频描述") @RequestParam(required = false) String description,
            @ApiParam("可选，视频源文件字节数") @RequestParam(required = false) Long fileSize
    ) throws UmiException {
        VodCreateUploadVideoResponse response =
                aliYunService.createUploadVideo(filename, title, categoryId, tags, description, fileSize);
        return JsonResponse.successResponseWithData(response);
    }

    @ApiOperation("获取视频播放地址.（可使用阿里云播放器或自主播放器）\n" +
            "您可以获取到播放地址后，传递给播放器进行播放，方式比较灵活，但需要自己实现清晰度切换、异常处理等开发工作。" +
            "目前阿里云播放器支持直接使用播放地址进行播放，您也可以使用系统原生播放器、开源播放器或自研播放器等。")
    @RequestMapping(value = "/playInfo", method = RequestMethod.GET)
    public JsonResponse<GetPlayInfoResponse> getPlayInfo(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("必选，视频ID") @RequestParam String videoId,
            @ApiParam("可选，视频格式,不传返回m3u8和mp4格式") @RequestParam(required = false) String formats

    ) throws UmiException {
        GetPlayInfoResponse response = aliYunService.getPlayInfo(videoId, formats);
        return JsonResponse.successResponseWithData(response);
    }

    @ApiOperation("获取视频播放凭证.（只能使用阿里云播放器）\n" +
            "可以从服务端获取播放凭证，回传给客户端进行播放，好处在于安全性较高")
    @RequestMapping(value = "/videoPlayAuth", method = RequestMethod.GET)
    public JsonResponse<GetVideoPlayAuthResponse> getVideoPlayAuth(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("必选，视频ID") @RequestParam String videoId
    ) throws UmiException {
        GetVideoPlayAuthResponse authResponse = aliYunService.getVideoPlayAuth(videoId);
        return JsonResponse.successResponseWithData(authResponse);
    }
    @ApiOperation("通过视频ID获取视频的基本信息，包括：视频标题、描述、时长、封面URL、状态、创建时间、大小、截图、分类和标签等信息。")
    @RequestMapping(value = "/videoInfo", method = RequestMethod.GET)
    public JsonResponse<GetVideoInfoResponse> getVideoInfo(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("必选，视频ID") @RequestParam String videoId

    ) throws UmiException {
        GetVideoInfoResponse authResponse = aliYunService.getVideoInfo(videoId);
        return JsonResponse.successResponseWithData(authResponse);
    }

    @ApiOperation("当视频处理（如上传、转码）完成后，点播服务可以及时通知用户")
    @RequestMapping(value = "/vodCallback", method = RequestMethod.POST)
    public void vodCallback(@RequestBody Map data) throws UmiException {
        String eventType = (String) data.get("EventType");
        String status = (String) data.get("Status");
        String videoId = (String) data.get("VideoId");
        logger.debug("EventType:" + eventType);
        logger.debug("Status:" + status);
        logger.debug("videoId:" + videoId);
        System.out.println("==============vodCallback================================");
        if ( "SnapshotComplete".equals(eventType)
                && "success".equals(status)) {
            String coverUrl = (String) data.get("CoverUrl");
            System.out.println("cover===="+coverUrl);
            topicService.setTopicCover(videoId, coverUrl);
        } else if ("StreamTranscodeComplete".equals(eventType)) {
            String fileUrl = (String) data.get("FileUrl");
            logger.debug("StreamTranscodeComplete:" + fileUrl);
            topicService.addPlayUrl(videoId, fileUrl);
        }
    }
    Logger logger = LoggerFactory.getLogger(AliYunController.class);
}
