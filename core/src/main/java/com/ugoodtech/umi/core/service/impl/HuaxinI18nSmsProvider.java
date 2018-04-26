package com.ugoodtech.umi.core.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.dto.SmsResponse;
import com.ugoodtech.umi.core.service.SmsProvider;
import com.ugoodtech.umi.core.utils.SmsEncoderUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;

@Component("i18nSmsProvider")
public class HuaxinI18nSmsProvider implements SmsProvider {
    @Value("${sms.huaxin.i18n.url}")
    private String sendSmsUrl;

    @Value("${sms.huaxin.userid}")
    private String userid;
    @Value("${sms.huaxin.i18n.account}")
    private String account;
    @Value("${sms.huaxin.i18n.password}")
    private String password;

    /**
     * https://dx.ipyy.net/I18Nsms.aspx?action=send&userid=&account=qq&password=123456&mobile=m1,m2&code=0
     * &content=00740065007300740020006D006500730073006100670065005B00480075006100580069006E005D&sendTime=&extno=
     * <p/>
     * <?xml version="1.0"  encoding="utf-8" ?>
     * <returnsms>
     * <returnstatus>status</returnstatus> ---------- 返回状态值：成功返回Success 失败返回：Faild
     * <message>message</message> ---------- 相关的错误描述
     * <balance>balance</balance> ---------- 返回余额
     * <taskID>taskID</taskID>  -----------  返回本次任务的序列ID
     * <BillingAmount> BillingAmount </ BillingAmount > ---------- 返回本次扣费金额
     * <successCounts>successCounts</successCounts> --成功短信数：当成功后返回提交成功短信数
     * </returnsms>
     *
     * @param mobilePhone mobile phone
     * @param message     send content
     */
    @Override
    public boolean sendSms(String mobilePhone, String message) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        String content = SmsEncoderUtil.encodeHexStr(8, message);

        String reuqestUrl = sendSmsUrl + "?action=send&userid=&account=" + account
                + "&password=" + password + "&mobile=" + mobilePhone + "&code=8"
                + "&content=" + content + "&sendTime=&extno=";
        logger.debug("send url is:" + reuqestUrl);
        HttpPost post = new HttpPost(reuqestUrl);
        try {
            HttpResponse response = httpClient.execute(post);
            String result = EntityUtils.toString(response.getEntity());
            logger.info("huaxin send to:" + mobilePhone + ",content:" + message + ",result:" + result);
            //
            JAXBContext jaxbContext = JAXBContext.newInstance(SmsResponse.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(result);
            SmsResponse smsResponse = (SmsResponse) jaxbUnmarshaller.unmarshal(reader);
            boolean success = smsResponse.getReturnStatus().equals(Success);
            if (!success) {
                logger.warn("send sms to phone " + mobilePhone + " faild!caused by:" + smsResponse.getMessage());
                return false;
            } else {
                logger.info("send sms to phone " + mobilePhone + " success!");
                return true;
            }
        } catch (IOException e) {
            logger.error("send message error", e);
        } catch (JAXBException e) {
            logger.error("parse xml error", e);
        }
        return false;

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


    private static final String Success = "Success";
    private static final String Faild = "Faild";
    private static final Logger logger = LoggerFactory.getLogger(HuaxinSmsProvider.class);
}
