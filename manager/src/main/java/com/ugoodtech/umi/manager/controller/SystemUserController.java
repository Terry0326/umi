package com.ugoodtech.umi.manager.controller;


import com.ugoodtech.umi.core.domain.SystemUser;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.repository.RoleRepository;
import com.ugoodtech.umi.core.repository.SystemUserRepository;
import com.ugoodtech.umi.manager.service.SystemUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright © 2013 All Rights Reserved, Ugood Technology, Inc.
 */
@RestController
@RequestMapping("/systemUser")
public class SystemUserController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SystemUserController.class);
    @Autowired
    SystemUserRepository systemUserRepository;
    @Autowired
    SystemUserService systemUserService;
    @Autowired
    RoleRepository roleRepository; 

    @Autowired
    private PasswordEncoder passwordEncoder;

 
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public JsonResponse detail(Authentication authentication) throws Exception {
        if(null!=authentication){
            SystemUser user = (SystemUser) authentication.getPrincipal();
            return JsonResponse.successResponseWithData(user);
        }else{
            return JsonResponse.errorResponseWithError("","notLogin");
        }
    }



    @RequestMapping(value = "/changeUserPwd", method = RequestMethod.POST)
    public JsonResponse changeUserPwd(
                                      Authentication authentication,
                                      @RequestParam(value = "oldPwd") String oldPwd,
                                      @RequestParam(value = "newPwd") String newPwd) throws Exception {

        SystemUser currentUser = (SystemUser) authentication.getPrincipal();
        if (null == currentUser) {
            return JsonResponse.errorResponseWithError("", "please login again");
        } else {
            logger.debug("当前登录用户id:" + currentUser.getId());
        }
        if (oldPwd.equals(newPwd)) {
            return JsonResponse.errorResponseWithError("", "new password can not be the same as the original password ");
        }
        try {
            boolean result = systemUserService.changePassword(currentUser, newPwd, oldPwd);
            if (result) {
                return JsonResponse.successResponseWithData("success");
            } else {
                return JsonResponse.errorResponseWithError("", "原密码输入错误，请重新输入");
            }


        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("failed,Original password error");
            return JsonResponse.errorResponseWithError("", "failed,Original password error");

        }
    }

}
