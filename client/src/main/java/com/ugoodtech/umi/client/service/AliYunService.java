package com.ugoodtech.umi.client.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.aliyuncs.vod.model.v20170321.*;
import com.ugoodtech.umi.client.dto.VodCreateUploadVideoResponse;
import com.ugoodtech.umi.core.exception.UmiException;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AliYunService {
    // 目前只有"cn-hangzhou"这个region可用, 不要使用填写其他region的值
    public static final String REGION_CN_HANGZHOU = "cn-hangzhou";
    public static final String REGION_CN_SHANGHAI = "cn-shanghai";
    public static final String STS_API_VERSION = "2015-04-01";
    //
    private String accessKeyId;
    private String accessKeySecret;

    // RoleArn 需要在 RAM 控制台上获取
    private String roleArn;
    private long durationSeconds;
    private String policy;

    @PostConstruct
    public void init() {
        String data = ReadJson("/config.json");
        if (data == null ||"".equals(data)) {
            logger.error("/config.json is empty or not found");
            return;
        }
        logger.debug(data);
        JSONObject jsonObj = JSONObject.fromObject(data);


        // 只有 RAM用户（子账号）才能调用 AssumeRole 接口
        // 阿里云主账号的AccessKeys不能用于发起AssumeRole请求
        // 请首先在RAM控制台创建一个RAM用户，并为这个用户创建AccessKeys
        accessKeyId = jsonObj.getString("AccessKeyID");
        accessKeySecret = jsonObj.getString("AccessKeySecret");

        // RoleArn 需要在 RAM 控制台上获取
        roleArn = jsonObj.getString("RoleArn");
        durationSeconds = jsonObj.getLong("TokenExpireTime");
        policy = ReadJson(jsonObj.getString("PolicyFile"));
        //


    }

    private AssumeRoleResponse assumeRole(String roleArn,
                                          String roleSessionName, String policy, ProtocolType protocolType, long durationSeconds) throws ClientException {
        try {
            // 创建一个 Aliyun Acs Client, 用于发起 OpenAPI 请求
            IClientProfile profile = DefaultProfile.getProfile(REGION_CN_HANGZHOU, accessKeyId, accessKeySecret);
            DefaultAcsClient client = new DefaultAcsClient(profile);

            // 创建一个 AssumeRoleRequest 并设置请求参数
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setVersion(STS_API_VERSION);
            request.setMethod(MethodType.POST);
            request.setProtocol(protocolType);

            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy);
            request.setDurationSeconds(durationSeconds);

            // 发起请求，并得到response
            final AssumeRoleResponse response = client.getAcsResponse(request);

            return response;
        } catch (ClientException e) {
            logger.error("assumeRole error:", e);
            throw e;
        }
    }

    private String ReadJson(String path) {
        //从给定位置获取文件
        InputStream stream = null;
        String data = null;
        try {
            stream = this.getClass().getResourceAsStream(path);
            data = IOUtils.toString(stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return data;
    }

    public Map<String, String> getAppToken(String username)
            throws IOException {
        // RoleSessionName 是临时Token的会话名称，自己指定用于标识你的用户，主要用于审计，或者用于区分Token颁发给谁
        // 但是注意RoleSessionName的长度和规则，不要有空格，只能有'-' '_' 字母和数字等字符
        // 具体规则请参考API文档中的格式要求
        String roleSessionName = username;

        // 此处必须为 HTTPS
        ProtocolType protocolType = ProtocolType.HTTPS;

        try {
            final AssumeRoleResponse stsResponse = assumeRole(roleArn, roleSessionName,
                    policy, protocolType, durationSeconds);

            Map<String, String> respMap = new LinkedHashMap<>();
            respMap.put("status", "200");
            respMap.put("AccessKeyId", stsResponse.getCredentials().getAccessKeyId());
            respMap.put("AccessKeySecret", stsResponse.getCredentials().getAccessKeySecret());
            respMap.put("SecurityToken", stsResponse.getCredentials().getSecurityToken());
            respMap.put("Expiration", stsResponse.getCredentials().getExpiration());
            return respMap;

        } catch (ClientException e) {

            Map<String, String> respMap = new LinkedHashMap<>();
            respMap.put("status", e.getErrCode());
            respMap.put("AccessKeyId", "");
            respMap.put("AccessKeySecret", "");
            respMap.put("SecurityToken", "");
            respMap.put("Expiration", "");
            return respMap;
        }

    }

    //    public static void main(String[] args) {
//        User user = new User("test-umi-sst", "111111");
//        try {
//            Map<String, String> map = new OssService().getAppToken(user);
//            for (String key : map.keySet()) {
//                System.out.println(key + " = " + map.get(key));
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 获取视频上传凭证和地址
     *
     * @param filename    必选，视频源文件名称（必须带后缀, 支持 ".3gp", ".asf", ".avi", ".dat", ".dv", ".flv", ".f4v", ".gif", ".m2t", ".m3u8", ".m4v", ".mj2", ".mjpeg", ".mkv", ".mov", ".mp4", ".mpe", ".mpg", ".mpeg", ".mts", ".ogg", ".qt", ".rm", ".rmvb", ".swf", ".ts", ".vob", ".wmv", ".webm"".aac", ".ac3", ".acm", ".amr", ".ape", ".caf", ".flac", ".m4a", ".mp3", ".ra", ".wav", ".wma"）
     * @param title       必选，视频标题
     * @param categoryId  可选，分类ID
     * @param tags        可选，视频标签，多个用逗号分隔
     * @param description 可选，视频描述
     * @param fileSize    可选，视频源文件字节数
     * @return VodCreateUploadVideoResponse
     */

    public VodCreateUploadVideoResponse createUploadVideo(String filename, String title, Integer categoryId, String tags, String description, Long fileSize) throws UmiException {
        IClientProfile profile = DefaultProfile.getProfile(REGION_CN_SHANGHAI, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);

        CreateUploadVideoRequest request = new CreateUploadVideoRequest();
        CreateUploadVideoResponse response = null;
        try {
            request.setFileName(filename);
            request.setTitle(title);
            request.setCateId(categoryId);
            request.setTags(tags);
            request.setDescription(description);
            request.setFileSize(fileSize);
            response = client.getAcsResponse(request);
        } catch (ServerException e) {
            logger.error("CreateUploadVideoRequest Server Exception:", e);
        } catch (ClientException e) {
            logger.error("CreateUploadVideoRequest Client Exception:", e);
        }
        if (response != null) {
            logger.debug("RequestId:" + response.getRequestId());
            logger.debug("UploadAuth:" + response.getUploadAuth());
            logger.debug("UploadAddress:" + response.getUploadAddress());
            VodCreateUploadVideoResponse vodResponse = new VodCreateUploadVideoResponse();
            vodResponse.setRequestId(response.getRequestId());
            vodResponse.setUploadAddress(response.getUploadAddress());
            vodResponse.setUploadAuth(response.getUploadAuth());
            vodResponse.setVideoId(response.getVideoId());
            return vodResponse;
        }
        //
        throw new UmiException(1000, "无法获取上传地址和凭证");
    }

    /**
     * 2. 刷新视频上传凭证
     *
     * @param videoId
     */
    public void refreshUploadVideo(String videoId) {
        IClientProfile profile = DefaultProfile.getProfile(REGION_CN_SHANGHAI, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);

        RefreshUploadVideoRequest request = new RefreshUploadVideoRequest();
        RefreshUploadVideoResponse response = null;
        try {
            request.setVideoId(videoId);
            response = client.getAcsResponse(request);
        } catch (ServerException e) {
            logger.error("RefreshUploadVideoRequest Server Exception:", e);
        } catch (ClientException e) {
            logger.error("RefreshUploadVideoRequest Client Exception:", e);
        }
        logger.debug("RequestId:" + response.getRequestId());
        logger.debug("UploadAuth:" + response.getUploadAuth());
    }

    public GetPlayInfoResponse getPlayInfo(String videoId, String formats) throws UmiException {
        IClientProfile profile = DefaultProfile.getProfile(REGION_CN_SHANGHAI, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);

        GetPlayInfoRequest request = new GetPlayInfoRequest();
        GetPlayInfoResponse response = null;
        try {
            request.setVideoId(videoId);
            request.setFormats(formats == null ? "mp4,m3u8" : formats);
            response = client.getAcsResponse(request);
        } catch (ServerException e) {
            logger.error("GetPlayInfoRequest Server Exception:", e);
            throw new UmiException(1000, "服务器端错误");
        } catch (ClientException e) {
            logger.error("GetPlayInfoRequest Client Exception:", e);
            throw new UmiException(1000, "客户端错误");
        }

        logger.debug("RequestId:" + response.getRequestId());
        return response;
    }

    public GetVideoInfoResponse getVideoInfo(String videoId) throws UmiException {
        IClientProfile profile = DefaultProfile.getProfile(REGION_CN_SHANGHAI, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        GetVideoInfoRequest request = new GetVideoInfoRequest();
        request.setVideoId(videoId);
        GetVideoInfoResponse response = null;
        try {
            response = client.getAcsResponse(request);
        } catch (ServerException e) {
            logger.error("GetVideoPlayAuthRequest Server Exception:", e);
            throw new UmiException(1000, "服务器端错误");
        } catch (ClientException e) {
            logger.error("GetVideoPlayAuthRequest Client Exception:", e);
            throw new UmiException(1000, "客户端错误");
        }
        return response;
    }
    public GetVideoPlayAuthResponse getVideoPlayAuth(String videoId) throws UmiException {
        IClientProfile profile = DefaultProfile.getProfile(REGION_CN_SHANGHAI, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        request.setVideoId(videoId);
        GetVideoPlayAuthResponse response = null;
        try {
            response = client.getAcsResponse(request);
        } catch (ServerException e) {
            logger.error("GetVideoPlayAuthRequest Server Exception:", e);
            throw new UmiException(1000, "服务器端错误");
        } catch (ClientException e) {
            logger.error("GetVideoPlayAuthRequest Client Exception:", e);
            throw new UmiException(1000, "客户端错误");
        }
        response.getPlayAuth();              //播放凭证
        response.getVideoMeta();             //视频Meta信息
        return response;
    }

    Logger logger = LoggerFactory.getLogger(AliYunService.class);


}
