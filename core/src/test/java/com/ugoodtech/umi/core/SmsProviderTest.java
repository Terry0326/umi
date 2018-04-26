package com.ugoodtech.umi.core;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SmsProviderTest {
    String sendSmsUrl = "http://sh2.cshxsp.com/smsJson.aspx";
    String account = "ugkmd";
    String password = "kmd@ug1605";

    public boolean sendSms(String mobilePhone, String message) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            message = URLEncoder.encode(message, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String reuqestUrl = sendSmsUrl + "?action=send&userid=&account=" + account
                + "&password=" + password + "&mobile=" + mobilePhone
                + "&content=" + message + "&sendTime=&extno=";
        logger.debug("send url is:" + reuqestUrl);
        HttpPost post = new HttpPost(reuqestUrl);
        try {
            HttpResponse response = httpClient.execute(post);
            String result = EntityUtils.toString(response.getEntity());
            System.out.println("result = " + result);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(result, JsonNode.class);
            String nodeMsg = node.get("message").asText();
            if (nodeMsg.indexOf("(") > 0) {//filter some special characters
                nodeMsg = nodeMsg.substring(0, nodeMsg.indexOf("("));
                logger.info("send sms feedback :" + nodeMsg);
            }

        } catch (IOException e) {
            logger.error("send message error", e);
            e.printStackTrace();
        }
        return false;

    }

    public static void main(String[] args) {
        SmsProviderTest smsProviderTest=new SmsProviderTest();
        smsProviderTest.sendSms("15822059755","尊敬的用户您好，您本次的验证码:1111，请不要告诉别人【KMD】");
    }

    Logger logger = LoggerFactory.getLogger(SmsProviderTest.class);
}
