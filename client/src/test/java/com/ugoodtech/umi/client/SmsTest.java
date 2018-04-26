package com.ugoodtech.umi.client;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.dto.SmsResponse;
import com.ugoodtech.umi.core.utils.SmsEncoderUtil;
import org.apache.commons.codec.DecoderException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

public class SmsTest {
    public static void main(String[] args) throws JAXBException, UnsupportedEncodingException, DecoderException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        String msg = "【UING】您申请注册UING，验证码：6155（30分钟内有效）";
        String content = SmsEncoderUtil.encodeHexStr(8,msg);
        System.out.println("content = " + content);
        String reuqestUrl = "https://dx.ipyy.net/I18NSms.aspx?action=send&userid=&account=SHGJ023&password=SHGJ02366&mobile=17789262588&code=8&content=" + content + "&sendTime=&extno=";
        HttpPost post = new HttpPost(reuqestUrl);
        HttpResponse response = null;
        try {
            response = httpClient.execute(post);
            String result = EntityUtils.toString(response.getEntity());
            System.out.println("result = " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static void parseSmsResponse() throws JAXBException {
        String result =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<returnsms>\n" +
                        "    <returnstatus>Faild</returnstatus>\n" +
                        "    <message>content解析错误</message>\n" +
                        "    <balance>0</balance>\n" +
                        "    <taskID>0</taskID>\n" +
                        "    <BillingAmount>0</BillingAmount>\n" +
                        "    <successCounts>0</successCounts>\n" +
                        "</returnsms>";
        JAXBContext jaxbContext = JAXBContext.newInstance(SmsResponse.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        StringReader reader = new StringReader(result);
        SmsResponse smsResponse = (SmsResponse) jaxbUnmarshaller.unmarshal(reader);
    }
}
