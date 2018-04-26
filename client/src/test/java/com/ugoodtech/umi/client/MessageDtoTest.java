package com.ugoodtech.umi.client;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageDtoTest {
    public static void main(String[] args) throws ParseException {
        String date = "2017-10-17 19:07:14";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date creationTime = sdf.parse(date);
        System.out.println("sdf = " + creationTime);
        //
        String interval;
        long howLong = (new Date().getTime() - creationTime.getTime()) / 1000;
        System.out.println("howLong = " + howLong);
        if (howLong > 365 * 24 * 60 * 60L) {
            interval = howLong / (365 * 24 * 60 * 60L) + "年";
        } else if (howLong > 24 * 60 * 60L) {
            interval = howLong / (24 * 60 * 60L) + "天";
        } else if (howLong > 60 * 60L) {
            interval = howLong / (60 * 60L) + "小时";
        } else if (howLong > 60L) {
            interval = howLong / 60L + "分钟";
        } else {
            interval = howLong + "秒";
        }
        System.out.println("interval = " + interval);
    }
}
