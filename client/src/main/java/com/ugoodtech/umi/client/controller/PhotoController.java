package com.ugoodtech.umi.client.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 15/11/19
 */


import com.ugoodtech.umi.core.service.ResourceService;
import com.ugoodtech.umi.core.domain.Resource;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.exception.ResourceNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Api(description = "图片上传获取接口")
@Controller
@RequestMapping("/photos")
public class PhotoController {

    @Autowired
    ResourceService photoService;

    @ApiOperation("上传图片")
    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public JsonResponse handleFileUpload(MultipartHttpServletRequest request)
            throws IOException {
        List<String> uploadResponses = new ArrayList<>();
        Iterator<String> iterator = request.getFileNames();
        while (iterator.hasNext()) {
            String fileName = iterator.next();
            List<MultipartFile> multipartFiles = request.getFiles(fileName);
            for (MultipartFile multipartFile : multipartFiles) {
                Resource photo = photoService.save(fileName, multipartFile.getContentType(), multipartFile.getBytes());
                Long photoId = photo.getId();
                uploadResponses.add(photoId + "");
            }
        }
        return JsonResponse.successResponseWithData(uploadResponses);
    }


    @RequestMapping(value = "/uploadPhoto", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse uploadPhoto(@RequestParam(value = "image") MultipartFile image) throws Exception {
        Resource photo = photoService.save(image.getOriginalFilename(), image.getContentType(), image.getBytes());
        if (null != photo) {
            logger.debug("photoId : " + photo.getId());
            return JsonResponse.successResponse();
        } else {
            logger.debug("上传照片失败，数据异常");
            return JsonResponse.errorResponseWithError("上传照片失败，数据异常", "上传照片失败，数据异常");
        }

    }


    @RequestMapping("/getPhoto")
    public void getPhoto(HttpServletResponse response,
                         @RequestParam(value = "photoId", required = false) String photoId,
                         @RequestParam(value = "width", required = false) Integer width,
                         @RequestParam(value = "height", required = false) Integer height) throws Exception {

        byte[] bytes = new byte[0];
        try {
            if (!StringUtils.isNumeric(photoId)) {
//                bytes = photoService.getDefaultPhoto("atongmu.jpg");
            } else if (null != width && 0 != width && null != height && 0 != height) {
                bytes = photoService.getPhotoThumbnailBytes(Long.valueOf(photoId), width, height);
            } else {
                bytes = photoService.getResourceBytes(Long.valueOf(photoId));
            }
        } catch (IOException e) {
            logger.debug("获取图片异常");
            throw new Exception("获取图片异常");
        } catch (ResourceNotFoundException e) {
            logger.debug("图片不存在", e);
            throw new Exception("图片不存在");
        } catch (NumberFormatException e) {
            logger.debug("获取图片参数异常");
            throw new Exception("获取图片参数异常");
        }
        String extensionName = "jpg";
        response.setContentType("image/" + extensionName);
        IOUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream());

    }

    Logger logger = LoggerFactory.getLogger(PhotoController.class);
}
