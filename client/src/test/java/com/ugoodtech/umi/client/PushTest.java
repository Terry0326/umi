package com.ugoodtech.umi.client;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PushTest {
    public static void main(String[] args) {
        String pass = RandomStringUtils.randomAlphabetic(10);
        System.out.println("pass = " + pass);
        //ne*hg101BSdl$IH
//        notification();
    }

    private static void notification() {
        String jpushMasterSecret = "8ee8619506e6f4c696e31590";
        String jpushAppKey = "0f4646f1fc7c9497863d48b4";
        ClientConfig instance = ClientConfig.getInstance();
        instance.setApnsProduction(false);

        JPushClient jPushClient = new JPushClient(jpushMasterSecret, jpushAppKey, null, instance);
        String registrationID = "141fe1da9e9e04ca182";
//        Message.Builder builder = Message.newBuilder()
//                .setTitle("推送测试")
//                .setMsgContent("推送测试内容");
//        Message message=Message.content("为什么没有发送???");
//        PushPayload payload = PushPayload.newBuilder()
//                .setPlatform(Platform.all())
//                .setAudience(Audience.registrationId(registrationID))
//                .setMessage(message)
//                .build();
        Map<String, String> extras = new HashMap<>();
        extras.put("a", "b");
        extras.put("sound", "b");
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.registrationId(registrationID))
//                .setMessage(builder.build())
                .setNotification(Notification.ios("alert222", extras))
                .build();
        try {
            PushResult result = jPushClient.sendPush(payload);
            if (!result.isResultOK()) {
                System.out.println("result = " + result.getResponseCode());
                System.out.println("result = " + result.getOriginalContent());
            }
        } catch (APIConnectionException | APIRequestException e) {
            e.printStackTrace();
        }
    }

    static Logger logger = LoggerFactory.getLogger(PushTest.class);
}
