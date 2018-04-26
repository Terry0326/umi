package com.ugoodtech.umi.client.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.User;

import java.util.Map;

public interface PushService {

    void sendNotification(User participant, String title, String msgContent, Map<String, String> extra, Integer badge);

    void sendMessage(User user, String title, String msgContent, Map<String, String> extra);

}
