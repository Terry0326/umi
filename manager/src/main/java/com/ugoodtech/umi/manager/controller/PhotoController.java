package com.ugoodtech.umi.manager.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 15/11/19
 */


import com.ugoodtech.umi.core.domain.Resource;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.repository.ResourceRepository;
import com.ugoodtech.umi.core.service.ResourceService;
import com.ugoodtech.umi.manager.dto.PhotoResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("/photos")
public class PhotoController {

    @Autowired
    ResourceService photoService;

    @Autowired
    ResourceRepository resourceRepository;

    @Value("${photos.basedir}")
    private String basePath;

    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public JsonResponse handleFileUpload(MultipartHttpServletRequest request)
            throws IOException {
        List<String> uploadResponses = new ArrayList<>();
        Iterator<String> iterator = request.getFileNames();
        while (iterator.hasNext()) {
            String fileName = iterator.next();
            System.out.println("upload fileName = " + fileName);
            List<MultipartFile> multipartFiles = request.getFiles(fileName);
            for (MultipartFile multipartFile : multipartFiles) {
//                System.out.println("upload fileSize = " + multipartFile.getTotalElements());
//                String originalFilename = multipartFile.getOriginalFilename();
                Resource photo = photoService.save( System.currentTimeMillis() + "", multipartFile.getContentType(), multipartFile.getBytes());
                Long photoId = photo.getId();
                uploadResponses.add(photoId + "");
//                Resource attachment = resourceService.save(originalFilename, multipartFile.getBytes());
//                uploadResponses.add(attachment.getId());
            }
        }
        return JsonResponse.successResponseWithData(uploadResponses);
    }


    @RequestMapping(value = "/uploadPhoto", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse uploadPhoto(
            @RequestParam(value = "appVersion", required = false) String appVersion,
            @RequestParam(value = "image") MultipartFile image,
            @RequestParam(value = "osType", required = false) String osType,
            @RequestParam(value = "osVersion", required = false) String osVersion
    ) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("--------------uploadPhoto---------------");
            logger.debug("appVersion  = " + appVersion);
            logger.debug("osType  = " + osType);
            logger.debug("osVersion : " + osVersion);

            logger.debug("imageSize  = " + image.getSize());
            logger.debug("imageOriginalFilename  = " + image.getOriginalFilename());
            logger.debug("imageContentType : " + image.getContentType());
        }

//        User user = userManager.getUserByUsername(phoneNo);
//
//        if (null == user) {
//            logger.debug("登录已失效,请重新登录");
//            logger.debug("--------------------------------------");
//            throw new JsonObjectException("登录已失效,请重新登录", Constants.LOGIN_INVALID);
//
//        } else {
//            logger.debug("当前登录用户id:" + user.getId());
//            logger.debug("当前登录用户手机号:" + user.getPhoneNumber());
//        }


        Resource photo = photoService.save( System.currentTimeMillis() + "", image.getContentType(), image.getBytes());
        if (null != photo) {
            logger.debug("photoId : " + photo.getId());
//            UserInfo userInfo=user.getUserInfo();
//            if(null!=userInfo){
//                userInfo.setPhoto(photo.getId()+"");
//                userInfoManager.updateUserInfo(userInfo);
//            }
            return JsonResponse.successResponseWithData(PhotoResponse.turnPhotoToResponse(photo));
        } else {
            logger.debug("上传照片失败，数据异常");
            logger.debug("--------------------------------------");
            return JsonResponse.errorResponseWithError("上传照片失败，数据异常", "上传照片失败，数据异常");
        }

    }


    @RequestMapping("/getPhoto")
    @ResponseBody
    public void getPhoto(HttpServletResponse response,
                         @RequestParam(value = "photoId") Long photoId,
                         @RequestParam(value = "width", required = false) Integer width,
                         @RequestParam(value = "height", required = false) Integer height) throws Exception {

        Resource resource = resourceRepository.findOne(photoId);
        if (resource.getMimeType() != null) {
            response.setContentType(resource.getMimeType());
        }
        Path path = Paths.get(basePath, resource.getPath());
        InputStream inputStream = Files.newInputStream(path);
        IOUtils.copy(inputStream, response.getOutputStream());
        inputStream.close();

    }

    Logger logger = LoggerFactory.getLogger(PhotoController.class);
}
