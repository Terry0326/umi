package com.ugoodtech.umi.manager.controller;

import com.ugoodtech.umi.core.dto.JsonException;
import com.ugoodtech.umi.core.dto.JsonResponse;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Copyright © 2013 All Rights Reserved, Ugood Technology, LLC.
 * <p/>
 * User: Stone
 */
@ApiIgnore
public class BaseController {
//    @Value("${logger.skip}")
//    public Boolean loggerSkip;

    public static final Logger log = LoggerFactory.getLogger(BaseController.class);
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public JsonResponse resolveException(HttpServletRequest req, HttpServletResponse resp, Exception exp) {
        JsonResponse jsonResponse = new JsonResponse();
        if (exp instanceof JsonException) {
            jsonResponse.setSuccess(true);
            jsonResponse.setErrorDescription(exp.getMessage());
            if (((JsonException) exp).getErrCode() != null) {
                jsonResponse.setError(((JsonException) exp).getErrCode());
            }
            resp.setStatus(HttpServletResponse.SC_OK);
//            exp.printStackTrace();
        } else if (exp instanceof HibernateException) {
            jsonResponse.setSuccess(false);
            jsonResponse.setErrorDescription(exp.getMessage());
            resp.setStatus(HttpServletResponse.SC_OK);
            exp.printStackTrace();
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.setSuccess(false);
            jsonResponse.setErrorDescription(exp.getMessage());
            exp.printStackTrace();
        }
        return jsonResponse;
    }


    public void debugException(String methodName, Exception e, Logger logger) {
        logger.debug(methodName + e.toString());
        if (null != e.getStackTrace()) {
            if (e.getStackTrace().length != 0) {
                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                    logger.debug("at " + stackTraceElement);
                }
            }
        }
    }
}
