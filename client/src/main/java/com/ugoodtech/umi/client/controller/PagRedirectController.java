package com.ugoodtech.umi.client.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 15/11/24
 */

import com.ugoodtech.umi.core.dto.JsonResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


@RestController
@Controller
public class PagRedirectController {

    static {
        System.setProperty("java.awt.headless","true");
    }

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView welcome() {
        return new ModelAndView("welcome");
    }


    @RequestMapping(value = "/so/login", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView login() {
        return new ModelAndView("admin-login");
    }


//    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_DRIVER','ROLE_DISPATCHER')")
    @RequestMapping(value = "/so/main", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView dashMain() {
        return new ModelAndView("admin-main");
    }



    @RequestMapping(value = "/so/department", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView dashDepartment(Long hospitalId, HttpServletRequest request) {
        ModelAndView modelAndView=new ModelAndView("admin-department");
        request.setAttribute("hospitalId",hospitalId);
        return modelAndView;
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_DRIVER','ROLE_DISPATCHER')")
    @RequestMapping(value = "/so/password", method = RequestMethod.GET)
    public ModelAndView password() {
        return new ModelAndView("dashboard_password");
    }

    /**
     * 获取系统版本
     *
     * @param
     */
    @RequestMapping("/so/getSystemVersion")
    @ResponseBody
    public JsonResponse getSystemVersion() {
        String version = "1.0.0";
        return JsonResponse.successResponseWithData(version);
    }


}
