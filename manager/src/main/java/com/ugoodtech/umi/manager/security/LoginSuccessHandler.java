package com.ugoodtech.umi.manager.security;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 15/11/25
 */


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ugoodtech.umi.core.domain.SystemUser;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.dto.JsonResponse;
import nl.captcha.Captcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;


public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        //todo:replace the followingexception throw with customized failure code.
        Captcha captcha = (Captcha) httpServletRequest.getSession().getAttribute(Captcha.NAME);
        // Or, for an AudioCaptcha:
        // AudioCaptcha captcha = (AudioCaptcha) session.getAttribute(Captcha.NAME);
        httpServletRequest.setCharacterEncoding("UTF-8"); // Do this so we can capture non-Latin chars
        String answer = httpServletRequest.getParameter("verification");
        ObjectMapper mapper = new ObjectMapper();
        JsonResponse jsonResponse = new JsonResponse<User>();
        if (captcha.isCorrect(answer)) {
            SystemUser user = (SystemUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            logger.debug("--------------loginUser---------------");
            logger.debug("username  = " + user.getUsername());
            logger.debug("--------------------------------------");
            jsonResponse.setSuccess(true);
            jsonResponse.setData(user);
            jsonResponse.setErrorDescription("登录成功");
        } else {
            jsonResponse.setSuccess(false);
            jsonResponse.setErrorDescription("验证码错误");
        }
        httpServletResponse.setContentType("text/html;charset=UTF-8");
        OutputStream out = httpServletResponse.getOutputStream();

        mapper.writeValue(out, jsonResponse);

//        throw new RuntimeException("LoginSuccessHandler not implemented");
    }

    private static Logger logger = LoggerFactory.getLogger(LoginSuccessHandler.class);
}
