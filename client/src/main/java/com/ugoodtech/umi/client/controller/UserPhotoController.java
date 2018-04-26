package com.ugoodtech.umi.client.controller;

import com.ugoodtech.umi.client.service.UserPhotoService;
import com.ugoodtech.umi.core.domain.Topic;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.domain.UserPhoto;
import com.ugoodtech.umi.core.domain.converter.TopicTypeConverter;
import com.ugoodtech.umi.core.dto.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Api("用户相册接口")
@RestController
@RequestMapping("/userPhoto")
public class UserPhotoController {
    @Autowired
    private UserPhotoService userPhotoService;


    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
        binder.registerCustomEditor(Topic.TopicType.class, new TopicTypeConverter());
    }
    @ApiOperation("获取用户相册列表")
    @RequestMapping(value = "/getUserPhotos", method = RequestMethod.GET)
    public JsonResponse<List<UserPhoto>> getUserPhotos(@ApiIgnore Authentication authentication,
                                                        @ApiParam("用户id") @RequestParam Long userId,
                                                         @ApiParam("页码,从0开始,默认0")
                                                             @RequestParam(defaultValue = "0") Integer page,
                                                         @ApiParam("每页条数,默认10")
                                                             @RequestParam(defaultValue = "10") Integer size){
        Pageable pageable = new PageRequest(page, size);
        Page<UserPhoto> userPhotoList =userPhotoService.getUserPhoto(userId,pageable);
        return  JsonResponse.successResponseWithPageData(userPhotoList);
    }
    @ApiOperation("删除相册图片")
    @RequestMapping(value = "/deleteUserPhoto", method = RequestMethod.POST)
    public JsonResponse deleteUserPhoto(@ApiIgnore Authentication authentication,
                                                         @ApiParam("相册ID")  @RequestParam   Long photoId){
        System.out.println("============deleteUserPhoto================"+photoId);
        User user = (User) authentication.getPrincipal();
        userPhotoService.deleteUserPhoto(user,photoId);
        return  JsonResponse.successResponse();
    }
    @ApiOperation("上传相册图片")
    @RequestMapping(value = "/createUserPhoto", method = RequestMethod.POST)
    public JsonResponse< Map<String,String> > createUserPhoto(@ApiIgnore Authentication authentication,
                                                         @ApiParam("相册图片内容")   @RequestParam  String path){
        User user = (User) authentication.getPrincipal();
        Map<String,String> map= userPhotoService.createUserPhoto(user,path);
        return  JsonResponse.successResponseWithData(map);
    }
}
