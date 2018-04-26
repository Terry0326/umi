package com.ugoodtech.umi.client.service.impl;

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
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.ugoodtech.umi.client.service.PushService;
import com.ugoodtech.umi.core.domain.ThirdPartyAccount;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.repository.ThirdPartyAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
public class JPushServiceImpl implements PushService {
    @Value("${jpush.appKey}")
    private String jpushAppKey;
    @Value("${jpush.masterSecret}")
    private String jpushMasterSecret;
    @Value("${jpush.apnsProduction}")
    private boolean apnsProduction;
    private JPushClient jPushClient;


    @Autowired
    private ThirdPartyAccountRepository thirdPartyAccountRepository;

    @PostConstruct
    public void init() {
        ClientConfig instance = ClientConfig.getInstance();
        instance.setApnsProduction(apnsProduction);
        jPushClient = new JPushClient(jpushMasterSecret, jpushAppKey, null, instance);
    }

    @Override
    public void sendNotification(User user, String title, String msgContent, Map<String, String> extra, Integer badge) {
        logger.debug("sendNotification " + title + " to user " + user.getUsername());
        List<ThirdPartyAccount> thirdPartyAccounts = thirdPartyAccountRepository.findThirdPartyAccount(user.getId(),
                ThirdPartyAccount.ThirdParty.J_PUSH);
        if (!thirdPartyAccounts.isEmpty()) {
            System.out.println("=================================sssssssssss");
            ThirdPartyAccount thirdPartyAccount = thirdPartyAccounts.get(0);
            String registrationID = thirdPartyAccount.getAccId();
            logger.debug("get user token " + registrationID + ",source:" + thirdPartyAccount.getSource().name());
            Notification notification;
            Platform platform;
            ThirdPartyAccount.AccountSource source = thirdPartyAccount.getSource();
            logger.debug("myselflikeit"+user.getId()+"=====昵称"+user.getNickname());
            logger.debug("third======"+registrationID);
            //判断
//            if (!(user.getId() + "").equals(registrationID)) {
            if (source.equals(ThirdPartyAccount.AccountSource.Android)) {
                platform = Platform.android();
                notification = Notification.android(msgContent, title, extra);
            } else if (source.equals(ThirdPartyAccount.AccountSource.iOS)) {
                System.out.println("======ios=");
                platform = Platform.ios();
                notification = Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(msgContent)
                                .addExtras(extra)
                                .setSound("default")
                                .setBadge(badge == null ? 0 : badge)
                                .build())
                        .build();
                System.out.println("======ios22=");
            } else {
                platform = Platform.all();
                notification = Notification.alert(msgContent);
            }
            try {
                sendNotification(registrationID, notification, platform);
            } catch (APIConnectionException | APIRequestException e) {
                logger.error("error sendNotification " + msgContent + " to user:" + user.getId() + ",cause:" + e.getMessage());
//            }
            }
        } else {
            logger.warn(user.getUsername() + " has not registered the sendNotification ID!");
        }
    }

    private PushResult sendNotification(String registrationID, Notification notification, Platform platform) throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setAudience(Audience.registrationId(registrationID))
                .setPlatform(platform)
                .setNotification(notification)
                .build();
        PushResult result = jPushClient.sendPush(payload);
        logger.debug("notification result:" + result);
        return result;
    }

    @Override
    public void sendMessage(User user, String title, String msgContent, Map<String, String> extra) {
        logger.debug("send message " + title + " to user " + user.getUsername());
        List<ThirdPartyAccount> thirdPartyAccounts = thirdPartyAccountRepository.findThirdPartyAccount(user.getId(),
                ThirdPartyAccount.ThirdParty.J_PUSH);
        if (!thirdPartyAccounts.isEmpty()) {
            ThirdPartyAccount thirdPartyAccount = thirdPartyAccounts.get(0);
            String registrationID = thirdPartyAccount.getAccId();
            logger.debug("get user token " + registrationID);
            Message.Builder builder = Message.newBuilder()
                    .setTitle(title)
                    .setMsgContent(msgContent);
            if (extra != null) {
                for (String key : extra.keySet()) {
                    if (key == null || extra.get(key) == null) {
                        continue;
                    }
                    builder.addExtra(key, extra.get(key));
                }
            }
            PushPayload payload = PushPayload.newBuilder()
                    .setPlatform(Platform.all())
                    .setAudience(Audience.registrationId(registrationID))
                    .setMessage(builder.build())
                    .build();
            try {
                jPushClient.sendPush(payload);
            } catch (APIConnectionException | APIRequestException e) {
                logger.error("error sendNotification " + msgContent + " to user:" + user.getId() + ",cause:" + e.getMessage());
            }
        } else {
            logger.warn(user.getUsername() + " has not registered the sendNotification ID!");
        }
    }

    Logger logger = LoggerFactory.getLogger(JPushServiceImpl.class);

}
