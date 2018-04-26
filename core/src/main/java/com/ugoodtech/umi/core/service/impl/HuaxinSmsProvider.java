package com.ugoodtech.umi.core.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ugoodtech.umi.core.service.SmsProvider;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component("chinaSmsProvider")
public class HuaxinSmsProvider implements SmsProvider {
    @Value("${sms.huaxin.url}")
    private  String sendSmsUrl ;

    @Value("${sms.huaxin.userid}")
    private String userid;
    @Value("${sms.huaxin.account}")
    private String account;
    @Value("${sms.huaxin.password}")
    private String password;

    /**
     * send url: http://sh2.ipyy.com/sms.aspx?action=send&userid="+userid+"&account="+account+"&password="+password
     * +"&mobile="+mobile+"&content="+send_content+"&sendTime="+sendTime+""
     * send result:
     * <p/>
     * {"returnstatus":"Success","message":"操作成功","remainpoint":"99","taskID":"1606013552246778","successCounts":"1"}
     * {"returnstatus":"Faild","message":"敏感词(??)","remainpoint":"0","taskID":"","successCounts":"0"}
     *
     * @param mobilePhone mobile phone
     * @param message     send content
     */
    @Override
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
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(result, JsonNode.class);
            String nodeMsg = node.get("message").asText();
            if (nodeMsg.indexOf("(") > 0) {//filter some special characters
                nodeMsg = nodeMsg.substring(0, nodeMsg.indexOf("("));
                logger.info("send sms feedback :" + nodeMsg);
            }
            boolean returnstatus = node.get("returnstatus").asText().equals(Success);
            logger.info("huaxin send to:" + mobilePhone + ",content:" + message + ",result:" + result);
            return returnstatus;
        } catch (IOException e) {
            logger.error("send message error", e);
            e.printStackTrace();
        }
        return false;

    }

    private static final String Success = "Success";
    private static final String Faild = "Faild";
    private static final Logger logger = LoggerFactory.getLogger(HuaxinSmsProvider.class);
}
