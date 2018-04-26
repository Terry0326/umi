package com.ugoodtech.umi.client;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 15/12/18
 */

import com.ugoodtech.umi.core.domain.ThirdPartyAccount;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OauthTest {
    public static final String clientID = "umi_client";
    public static final String clientSecret = "top_secret";


    public static final String urlPrefix = "http://127.0.0.1:9010/umiclient";
    //    public static final String accessToken = "05921b7c-2038-4bc7-b6ff-33840943a4a7";//15822059755,id:40
   public static final String accessToken = "9edf1a23-f831-4992-9a83-761e2577e5d8";//15522085206,id:62
   //    public static final String accessToken = "741d182c-547e-4b08-85b7-d8a2033ec9ab";//qq,id=80,李忠蔚_285671


    public static void main(String[] args) {//0beee760-715c-4f24-9628-692185993bc3
    testLogin("15221043257","wcs123456");
//        testLogin("15821390071", "123456q");
//        testLogin("15822059755", "111111q");
//        testLogin("13916059514", "123456q");
//        testLogin("13023126246", "123456q");
//        testLogin("15522085206", "111111");
//        testQQLogin();
//        getChatToken();
//        testCompleteDetail();
//        testDetail(null);
 //       testTopicDetail(111L);
//        for (int i = 0; i < 5; ++i)
//        testPostComment(380L);
//        testComments(1624L);
       //       getHomeData();
      // testNear();
     //   testSearchUser("你");
//        testTipOff(160L);
//        testOversea();
//        testCreateTopic();
//        testCreateVideoTopic();
//        testMessages();
//        testFollow(77L);
//        testUnfollow(62L);
//        testLike();
//        testSendSmsValidationCode();
//        testValidateCode();
//        testBurnTopics();
//        testGetFollowing();
//        testGetFollowers();
//        testgetUserPopular();
//        testDetail();
//        testBlock(62L);
//        testMessageMarkRead();
//        testDeleteCommentMsg();
//        testDeleteMsg();
//        testGetVideoPLayAuth();
//        testGetVideoPLayInfo();
    }

    private static void testGetVideoPLayInfo() {
        doHttpAction(urlPrefix + "/playInfo?videoId=04d2c1b59c094cc69595aadd0a48aaf5");
    }

    private static void testGetVideoPLayAuth() {
        doHttpAction(urlPrefix + "/videoPlayAuth?videoId=04d2c1b59c094cc69595aadd0a48aaf5");
    }

    private static void testQQLogin() {
        ThirdPartyAccount.ThirdParty qq = ThirdPartyAccount.ThirdParty.QQ;
        String openId = "84997222444046726692520E25697849";
        String username = qq.getName() + "_" + openId;
        String password = openId + "_" + qq.getName() + qq.getCode();
        testLogin(username, password);
    }

    private static void testUnfollow(long userId) {
        post("/users/unfollow?followUserId=" + userId, null);
    }

    private static void testMessageMarkRead() {
        post("/messages/markReadForLink?topicId=409&messageType=2", null);
    }

    private static void testDeleteCommentMsg() {
        post("/messages/deleteForLink?topicId=430&messageType=2", null);
    }

    private static void testDeleteMsg() {
        post("/messages/delete?messageId=654", null);
    }

    private static void testBlock(Long userId) {
        post("/users/blocks?followUserId=" + userId, null);
    }

    private static void testgetUserPopular() {
        doHttpAction(urlPrefix + "/topics/userPopular?userId=44");
    }

    private static void testDetail(Long userId) {
        String url = urlPrefix + "/users/detail" + (userId == null ? "" : "?userId=" + userId);
        doHttpAction(url);
    }

    private static void testGetFollowing() {
        doHttpAction(urlPrefix + "/users/following?page=0&size=10");
    }

    private static void testGetFollowers() {
        doHttpAction(urlPrefix + "/users/followers?page=0&size=10");
    }

    private static void testBurnTopics() {
        doHttpAction(urlPrefix + "/topics/burnTopics?page=0&size=10");
    }

    private static void testValidateCode() {
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpGet httppost = new HttpGet(urlPrefix + "/sms/validatePhone?dialingCode=90" +
                "&phoneNumber=15522085206&validationCode=7555");
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Set-Cookie", "JSESSIONID=085F3E06F7B6ACC7CF157394B2B39EB4; Path=/umiclient; HttpOnly");
        System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = null;
        try

        {
            response = httpclient.execute(httppost);
            System.out.println("response = " + response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println("result = " + result);
            //
            HeaderIterator iterator = response.headerIterator();
            while (iterator.hasNext()) {
                Header header = iterator.nextHeader();
                System.out.println(header.getName() + "=" + header.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testSendSmsValidationCode() {
        doHttpActionWihoutToken(urlPrefix + "/sms/sendSmsValidationCode?dialingCode=86&phoneNumber=15522085206");
    }

    public static void doHttpActionWihoutToken(String url) {
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpGet httppost = new HttpGet(url);
        httppost.setHeader("Accept", "application/json");
        System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = null;
        try

        {
            response = httpclient.execute(httppost);
            System.out.println("response = " + response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println("result = " + result);
            //
            HeaderIterator iterator = response.headerIterator();
            while (iterator.hasNext()) {
                Header header = iterator.nextHeader();
                System.out.println(header.getName() + "=" + header.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void testLike() {
        Map<String, String> arguments = new HashMap<>();
        arguments.put("topicId", "206");
        post("/topics/like", arguments);
    }

    private static void testFollow(Long followingId) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put("followUserId", followingId + "");
        post("/users/follow", arguments);
    }

    private static void testHome() {
        doHttpAction(urlPrefix + "/topics/home?page=0&size=10");
    }
    private static void testNear() {
        doHttpAction(urlPrefix + "/discovery/rand");
    }
    private static void testOversea() {
        doHttpAction(urlPrefix + "/discovery/oversea?page=0&size=10");
    }

    private static void testSearchUser(String key) {
        try {
            doHttpAction(urlPrefix + "/users/search?page=0&pagesize=10&qKey=" + URLEncoder.encode(key, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void testTopicDetail(Long topicId) {
        doHttpAction(urlPrefix + "/topics?topicId=" + topicId);
    }

    private static void testComments(Long topicId) {
        doHttpAction(urlPrefix + "/topics/comments?topicId="+topicId);
    }

    private static void testMessages() {
        doHttpAction(urlPrefix + "/messages");
    }

    public static void testTipOff(Long topicId) {
        post("/topics/tipOff?topicId=160&reason=举报原因aaa", null);
    }

    public static void testPostComment(Long topicId) {
        String urlSuffix = "/topics/comments";
        Map<String, String> arguments = new HashMap<>();
        arguments.put("topicId", topicId + "");
//        char[] tear = Character.toChars(0x1F602); // Face with Tears of Joy
        final String s = "{" +
                "  \"msgtype\": \"text\"," +
                "  \"text\" :    {" +
                "  \"content\" : \"test contet!\"" +
                "  }" +
                " }";
        arguments.put("comment", s);
        post(urlSuffix, arguments);
    }

    public static void testCreateTopic() {
        Map<String, String> arguments = new HashMap<>();
        arguments.put("content", "img0");
        arguments.put("description", "测试2");
        arguments.put("type", "1");
//        arguments.put("isBurn", "true");
//        arguments.put("isAnonymous", "true");
        post("/topics", arguments);
    }

    public static void testCreateVideoTopic() {
        Map<String, String> arguments = new HashMap<>();
        arguments.put("content", "c6b8434cb63c4b96bc4d35f7bfdec707");
        arguments.put("description", "测试视频");
        arguments.put("type", "2");
//        arguments.put("markUserIds", "62");
        arguments.put("isBurn", "true");
//        arguments.put("isAnonymous", "true");
        post("/topics", arguments);
    }

    public static void testCompleteDetail() {
        String urlSuffix = "/users/detail";
        Map<String, String> arguments = new HashMap<>();
        arguments.put("lat", "121.111111");
        arguments.put("lng", "63.111111");
        arguments.put("country", "中国");
        arguments.put("city", "上海");
        arguments.put("nickname", "admim");
        arguments.put("gender", "1");
        arguments.put("signature", "test");
        arguments.put("avatar", "4-touxiang");
        post(urlSuffix, arguments);
    }

    private static void testRefreshToken() {
        HttpClient httpclient = HttpClientBuilder.create().build();
        String encoding = oauthBasicAuthBase64();
        //old access token:d39c0241-2abf-495c-b80c-4c3d3c86f40d
        HttpPost httppost = new HttpPost(urlPrefix + "/oauth/token?" +
                "grant_type=refresh_token&" +
                "refresh_token=e41a0288-ce32-4371-b06d-70269c224c81");
        // "&client_secret=" + clientSecret + "&client_id=" + clientID);
        httppost.setHeader("Authorization", "Basic " + encoding);
        httppost.setHeader("Accept", "application/json");
        //httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        //
        System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println("result = " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String oauthBasicAuthBase64() {
        return Base64.encodeBase64String((clientID + ":" + clientSecret).getBytes());
    }

    private static void testLogin(String username, String password) {
        HttpClient httpclient = HttpClientBuilder.create().build();
        String encoding = oauthBasicAuthBase64();
        HttpPost httppost = new HttpPost(urlPrefix + "/oauth/token?username=" + username
                + "&password=" + password + "&grant_type=password");
        httppost.setHeader("Authorization", "Basic " + encoding);
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");

        System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
            System.out.println("response = " + response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println("result = " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testExpiredToken() {
        HttpClient httpclient = HttpClientBuilder.create().build();
        String accessToken = "e23e3d88-3da7-41c3-89cc-f8712094ec4b";
        HttpGet httppost = new HttpGet(urlPrefix);
        httppost.setHeader("Authorization", "Bearer " + accessToken);
        httppost.setHeader("Accept", "application/json");

        System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
            System.out.println("response = " + response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println("result = " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void post(String urlSuffix, Map<String, String> arguments) {
        HttpClient httpclient = HttpClientBuilder.create().build();
//        String accessToken = "b8de69d3-8b5f-4c98-8c99-c19678a2f84b";
        HttpPost httppost = new HttpPost(urlPrefix + urlSuffix);
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        if (arguments != null) {
            for (String key : arguments.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, arguments.get(key)));
            }
        }
        httppost.setHeader("Authorization", "Bearer " + accessToken);
        httppost.setHeader("Accept", "application/json");
        HttpResponse response = null;
        try {
            HttpEntity httpEntity = new UrlEncodedFormEntity(nameValuePairList, "UTF-8");
            httppost.setEntity(httpEntity);
            response = httpclient.execute(httppost);
            System.out.println("response = " + response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println("result = " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getHomeData() {
     doHttpAction(urlPrefix + "/topics/home?page=0&size=10");
        //    doHttpAction(urlPrefix + "/topics/rand");
    }

    private static void getChatToken() {
        doHttpAction(urlPrefix + "/chats/token");
    }

    private static void doHttpAction(String url) {
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpGet httppost = new HttpGet(url);
        httppost.setHeader("Authorization", "Bearer " + accessToken);
        httppost.setHeader("Accept", "application/json");

        System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
            System.out.println("response = " + response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println("result = " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

