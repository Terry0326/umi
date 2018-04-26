package com.ugoodtech.umi.client.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 16/2/3
 */

import com.ugoodtech.umi.client.service.ClientVersionService;
import com.ugoodtech.umi.core.dto.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Api(description = "客户端版本管理")
public class ClientVersionController {
    @Value("${apk.version.name}")
    private String versionName;
    @Value("${apk.version.code}")
    private Integer versionCode;
    @Value("${apk.download.url}")
    private String downloadUrl;
    @Autowired
    private ClientVersionService clientVersionService;

//    @RequestMapping(value = "/compareVersion", method = RequestMethod.GET)
//    public JsonResponse compareVersion(@RequestParam(value = "versionNum", required = false) Integer versionNum,
//                                       @RequestParam(value = "versionName", required = false) String versionName) {
//        ClientVersion clientVersion = null;
//        if (versionNum != null) {
//            clientVersion = clientVersionService.getUpgradeBasedOnVersionNum(versionNum,
//                    ClientVersion.Platform.Android);
//        } else if (versionName != null) {
//            clientVersion = clientVersionService.getUpgradeVersionBasedOnVersionName(versionName,
//                    ClientVersion.Platform.Android);
//        } else {
//            return JsonResponse.errorResponseWithError("参数错误", "必须提供versionNum或者versionName其中之一");
//        }
//        Map<String, Object> map = new HashMap<>();
//        if (clientVersion != null) {
//            map.put("upgradeVersion", clientVersion);
//            map.put("needUpgrade", true);
//        } else {
//            map.put("needUpgrade", false);
//        }
//        return JsonResponse.successResponseWithData(map);
//
//    }
    @ApiOperation("获取android最新版本信息,返回versionCode,versionName和downloadUrl")
    @RequestMapping(value = "/checkVersion", method = RequestMethod.GET)
    public JsonResponse<Map<String, Object>> checkVersion() {
        Map<String, Object> map = new HashMap<>();
        map.put("versionCode", versionCode);
        map.put("versionName", versionName);
        map.put("downloadUrl", downloadUrl);
        return JsonResponse.successResponseWithData(map);
    }
}