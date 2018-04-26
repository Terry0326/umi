package com.ugoodtech.umi.manager.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.manager.service.AliYunService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
@Api("AliYun 接口")
@RestController
public class AliYunController {
    @Autowired
    private AliYunService aliYunService;
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
    Logger logger = LoggerFactory.getLogger(AliYunController.class);
}