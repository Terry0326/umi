package com.ugoodtech.umi.client.util;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {
    public static String doPost(String url, Map<String, String> arguments) {
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost httppost = new HttpPost(url);
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        for (String key : arguments.keySet()) {
            nameValuePairList.add(new BasicNameValuePair(key, arguments.get(key)));
        }
        HttpResponse response;
        try {
            HttpEntity httpEntity = new UrlEncodedFormEntity(nameValuePairList, "UTF-8");
            httppost.setEntity(httpEntity);
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    public static String doGet(String url) {
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }



}
