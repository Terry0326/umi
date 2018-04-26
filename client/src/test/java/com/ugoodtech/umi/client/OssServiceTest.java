package com.ugoodtech.umi.client;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadVideoRequest;
import com.aliyun.vod.upload.resp.UploadVideoResponse;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoInfoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ugoodtech.umi.client.dto.VodCreateUploadVideoResponse;
import com.ugoodtech.umi.client.service.AliYunService;
import com.ugoodtech.umi.core.exception.UmiException;

public class OssServiceTest {
    public static void main(String[] args) throws UmiException, JsonProcessingException {
//        testUpload();
//        getVideoPlayAuth();
//        createUploadVideo();
//        testUpload();
//        getPlayInfo();
//        getVideoInfo("");
        String videoId = testUpload();
//        System.out.println("get video info = " );
//        getVideoInfo("12d01031e4444a2a9c1d18370ef02794");
    }

    private static void getVideoInfo(String videoId) throws UmiException, JsonProcessingException {
        AliYunService aliYunService = new AliYunService();
        aliYunService.init();
        GetVideoInfoResponse videoInfoResponse = aliYunService.getVideoInfo(videoId);
        ObjectMapper mapper = new ObjectMapper();
        String resp = mapper.writeValueAsString(videoInfoResponse);
        System.out.println("resp = " + resp);
    }

    private static String testUpload() {
        String accessKeyId = "LTAI9QHU4ycRxa53";         //帐号AK
        String accessKeySecret = "KuFWwRkQpNUGzbJStxOyQcpsGOV3Vz"; //帐号AK
        String fileName = "/Users/stone/Downloads/IMG_3590.MOV";      //指定上传文件绝对路径(文件名称必须包含扩展名)
        String title = "标题12345";                  //视频标题
         UploadVideoRequest request = new UploadVideoRequest(accessKeyId, accessKeySecret, title, fileName);
        request.setCateId(0);                                         //视频分类ID
        request.setTags("标签1,标签2");                               //视频标签,多个用逗号分隔
        request.setDescription("测试视频");                           //视频描述
//        request.setCoverURL("http://cover.sample.com/sample.jpg");    //视频自定义封面URL
//        request.setCallback("http://callback.sample.com");            //设置上传完成后的回调URL
//        request.setPartSize(10 * 1024 * 1024L);     //可指定分片上传时每个分片的大小，默认为10M字节
        request.setTaskNum(1);                      //可指定分片上传时的并发线程数，默认为1，(注：该配置会占用服务器CPU资源，需根据服务器情况指定）
//        request.setIsShowWaterMark(true);           //是否使用水印

        try {
            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadVideoResponse response = uploader.uploadVideo(request);

            System.out.print(response.getVideoId()); //上传成功后返回视频ID
            return response.getVideoId();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print(e.getCause());
            System.out.print(e.getMessage());
            return null;
        }
    }

    private static void getPlayInfo() throws UmiException, JsonProcessingException {
        AliYunService aliYunService = new AliYunService();
        aliYunService.init();
        GetPlayInfoResponse playInfoResponse = aliYunService.getPlayInfo("8b4679288c164a1788d976b778d41caa", null);
        ObjectMapper mapper = new ObjectMapper();
        String resp = mapper.writeValueAsString(playInfoResponse);
        System.out.println("resp = " + resp);
    }

    private static void getVideoPlayAuth() throws UmiException, JsonProcessingException {
        AliYunService aliYunService = new AliYunService();
        aliYunService.init();
        GetVideoPlayAuthResponse getVideoPlayAuthResponse = aliYunService.getVideoPlayAuth("c6b8434cb63c4b96bc4d35f7bfdec707");
        ObjectMapper mapper = new ObjectMapper();
        String resp = mapper.writeValueAsString(getVideoPlayAuthResponse);
        System.out.println("resp = " + resp);
    }

    private static void createUploadVideo() throws UmiException {
        AliYunService aliYunService = new AliYunService();
        aliYunService.init();
        VodCreateUploadVideoResponse response =
                aliYunService.createUploadVideo("test.mp4", "test video upload", null, null, null, null);
        System.out.println("upload address = " + response.getUploadAddress());
        System.out.println("upload auth = " + response.getUploadAuth());
    }
}
