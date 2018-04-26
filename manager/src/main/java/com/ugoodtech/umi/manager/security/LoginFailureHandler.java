package com.ugoodtech.umi.manager.security;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 15/11/25
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ugoodtech.umi.core.dto.JsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        //todo:replace the followingexception throw with customized failure code.
//        throw new RuntimeException("LoginFailureHandler not implemented");
        ObjectMapper mapper = new ObjectMapper();
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setSuccess(false);
        if (e instanceof UsernameNotFoundException) {
            jsonResponse.setErrorDescription("用户名不存在");
        } else if (e instanceof DisabledException) {
            jsonResponse.setErrorDescription("用户被禁用");
        } else {
            logger.error(" error happend when login ,", e.toString());
            jsonResponse.setErrorDescription("登录失败,用户名或密码错误");

        }
        httpServletResponse.setContentType("text/html;charset=UTF-8");
        OutputStream out = httpServletResponse.getOutputStream();
        mapper.writeValue(out, jsonResponse);
    }

    private static Logger logger = LoggerFactory.getLogger(LoginFailureHandler.class);
}
