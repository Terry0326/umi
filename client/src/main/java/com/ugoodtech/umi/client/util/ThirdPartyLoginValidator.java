package com.ugoodtech.umi.client.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Component
public class ThirdPartyLoginValidator {
    @Value("${qq.app.id}")
    private String qqAppId;
    @Value("${qq.app.key}")
    private String qqAppKey;

    public boolean checkWeiboToken(String accessToken) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", accessToken);
        String returnValue = HttpClientUtils.doPost(
                "https://api.weibo.com/oauth2/get_token_info", params);
        System.out.println(returnValue);
        return returnValue != null;
    }

    public boolean checkWechatToken(String wxId, String accessToken) {
        String returnValue = HttpClientUtils.doGet(
                String.format(
                        "https://api.weixin.qq.com/sns/auth?access_token=%s&openid=%s",
                        accessToken,
                        wxId)
        );
        Map<Object, Object> result = null;
        try {
            result = new ObjectMapper().readValue(returnValue, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int errCode = Integer.parseInt(result.get("errcode").toString());
        return errCode == 0;
    }

    public boolean checkQQToken(String openId, String accessToken, String pf, String useIp) {
        String combineParam =
                "appid=" + qqAppId + "&"
                        + "format=" + "json" + "&"
                        + "openid=" + openId + "&"
                        + "openkey=" + accessToken + "&"
                        + "pf=" + pf + "&"
                        + "userip=" + useIp;
        System.out.println(combineParam);
        String encodeParam = "";
        try {
            encodeParam = URLEncoder.encode(combineParam, "utf-8");
            encodeParam = "GET&%2Fv3%2Fuser%2Fget_info&" + encodeParam;
            String key = qqAppKey + "&";
            byte[] macCode = CryptalUtil.hamcsha1(encodeParam.getBytes(), key.getBytes());
            String requestUrl =
                    "http://openapi.tencentyun.com/v3/user/get_info?"
                            + combineParam
                            + "&sig="
                            + URLEncoder.encode(CryptalUtil.encodeBase64(macCode), "utf-8");
            String resultValue = HttpClientUtils.doGet(requestUrl);
            Map<Object, Object> ret = new ObjectMapper().readValue(resultValue, Map.class);
            if (ret != null && ret.get("ret") != null && Integer.parseInt(ret.get("ret").toString()) == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}