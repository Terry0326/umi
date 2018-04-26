package com.ugoodtech.umi.client;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import io.rong.RongCloud;

public class RongyunTest {
    private static String appKey = "qd46yzrfqesuf";
    private static String appSecret = "hJclgavBsROe";

    public static void main(String[] args) {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);

    }
}
