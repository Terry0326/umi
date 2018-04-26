package com.ugoodtech.umi.client.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.repository.UserRepository;
import com.ugoodtech.umi.core.service.SmsProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Api(description = "短信接口")
@RestController
@RequestMapping("/sms")
public class SmsController {
    @Autowired
    @Qualifier("chinaSmsProvider")
    private SmsProvider chinaSmsProvider;
    @Autowired
    @Qualifier("i18nSmsProvider")
    private SmsProvider i18nSmsProvider;
    @Autowired
    private UserRepository userRepository;
    @Value("${sms.validatePhone.template}")
    private String smsValidateTemplate;
    @Value("${sms.forgotPassword.template}")
    private String smsForgotPwdTemplate;
    public static String SmsValidationCodeParamName = "SmsValidationCodeParamName";
    public static String SmsValidationPhoneParamName = "SmsValidationPhoneParamName";
    public static String SmsValidationDialingCodeParamName = "SmsValidationDialingCodeParamName";

    @ApiOperation(value = "获取注册短信验证码")
    @RequestMapping(value = "/sendSmsValidationCode", method = RequestMethod.GET)
    public JsonResponse getSmsValidationCode(@ApiParam("手机国家号码")
                                             @RequestParam(required = false, defaultValue = "86")
                                             String dialingCode,
                                             @ApiParam("手机号码") @RequestParam String phoneNumber,
                                             HttpServletRequest request) {
        if (dialingCode.startsWith("+")) {
            dialingCode = dialingCode.substring(1);
        }
        if (userRepository.findByUsername(phoneNumber) != null) {
            return JsonResponse.errorResponse(1000, "手机号已注册");
        }
        String validationCode = RandomStringUtils.randomNumeric(4);
        logger.debug("generate validation code:" + validationCode + " for phone " + phoneNumber);
        HttpSession session = request.getSession();
        session.setAttribute(SmsValidationCodeParamName, validationCode + "");
        session.setAttribute(SmsValidationPhoneParamName, phoneNumber);
        session.setAttribute(SmsValidationDialingCodeParamName, dialingCode);
        String message = smsValidateTemplate.replace("@", validationCode + "");
        boolean success;
        if ("86".equals(dialingCode)) {
            success = chinaSmsProvider.sendSms(phoneNumber, message);
        } else {
            success = i18nSmsProvider.sendSms(dialingCode + phoneNumber, message);
        }
        if (success) {
            return JsonResponse.successResponse();
        } else {
            return JsonResponse.errorResponseWithError("fail", "验证码发送失败");
        }
    }

    @ApiOperation("注册-验证手机号")
    @RequestMapping(value = "/validatePhone", method = RequestMethod.GET)
    public JsonResponse validatePhone(@ApiParam("手机国家号码")
                                      @RequestParam(required = false, defaultValue = "86")
                                      String dialingCode,
                                      @ApiParam("手机号") @RequestParam("phoneNumber") String username,
                                      @ApiParam("短信验证码") @RequestParam("validationCode") String validationCode,
                                      HttpServletRequest request) {
        if (dialingCode.startsWith("+")) {
            dialingCode = dialingCode.substring(1);
        }
        HttpSession session = request.getSession();
        String savedDialingCode = (String) session.getAttribute(SmsController.SmsValidationDialingCodeParamName);
        String phoneNumber = (String) session.getAttribute(SmsController.SmsValidationPhoneParamName);
        logger.debug("received dialingcoe:" + dialingCode + ",phoneNumber:" + username);
        logger.debug("saved dialingcoe:" + savedDialingCode + ",phoneNumber:" + phoneNumber);
        if (!dialingCode.equals(savedDialingCode) || !username.equals(phoneNumber)) {
            logger.warn("注册的国家区号{" + savedDialingCode + "}和接受验证码的手机号码{" + dialingCode + "}不匹配");
            return JsonResponse.errorResponseWithError("失败", "验证码与国家或手机号不匹配");
        }
        String sendCode = (String) session.getAttribute(SmsController.SmsValidationCodeParamName);
        if (!validationCode.equals(sendCode)) {
            return JsonResponse.errorResponseWithError("失败", "验证码错误");
        }
        if (userRepository.findByUsername(username) != null) {
            return JsonResponse.errorResponseWithError("失败", "手机号码已经存在");
        }
        return JsonResponse.successResponse();
    }

    @ApiOperation("验证忘记密码的验证码")
    @RequestMapping(value = "/validateForgotPwd", method = RequestMethod.GET)
    public JsonResponse validateForgotPwd(@ApiParam("手机国家号码")
                                          @RequestParam(required = false, defaultValue = "86")
                                          String dialingCode,
                                          @ApiParam("手机号") @RequestParam("phoneNumber") String username,
                                          @ApiParam("短信验证码") @RequestParam("validationCode") String validationCode,
                                          HttpServletRequest request) {
        if (dialingCode.startsWith("+")) {
            dialingCode = dialingCode.substring(1);
        }
        HttpSession session = request.getSession();
        String savedDialingCode = (String) session.getAttribute(SmsController.SmsValidationDialingCodeParamName);
        String phoneNumber = (String) session.getAttribute(SmsController.SmsValidationPhoneParamName);
        logger.debug("received dialingcoe:" + dialingCode + ",phoneNumber:" + username);
        logger.debug("saved dialingcoe:" + savedDialingCode + ",phoneNumber:" + phoneNumber);
        //
        if (!dialingCode.equals(savedDialingCode) || !username.equals(phoneNumber)) {
            logger.warn("注册的国家区号{" + savedDialingCode + "}和接受验证码的手机号码{" + dialingCode + "}不匹配");
            return JsonResponse.errorResponseWithError("失败", "验证码与国家或手机号不匹配");
        }
        String sendCode = (String) session.getAttribute(SmsController.SmsValidationCodeParamName);
        if (!validationCode.equals(sendCode)) {
            return JsonResponse.errorResponseWithError("失败", "验证码错误");
        }
        if (userRepository.findByUsername(username) == null) {
            return JsonResponse.errorResponseWithError("失败", "该手机号码还未注册");
        }
        return JsonResponse.successResponse();
    }

    @ApiOperation("发送忘记密码验证码")
    @RequestMapping(value = "/forgotPwd", method = RequestMethod.GET)
    public JsonResponse forgotPwd(@ApiParam("手机国家号码")
                                  @RequestParam(required = false, defaultValue = "86")
                                  String dialingCode,
                                  @ApiParam("手机号码") @RequestParam String phoneNumber,
                                  HttpServletRequest request) {
        if (dialingCode.startsWith("+")) {
            dialingCode = dialingCode.substring(1);
        }
        if (userRepository.findByUsername(phoneNumber) == null) {
            return JsonResponse.errorResponse(1000, "该手机号码还未注册");
        }
        String validationCode = RandomStringUtils.randomNumeric(4);
        logger.debug("generate forgot pwd validation code:" + validationCode + " for phone " + phoneNumber);
        HttpSession session = request.getSession();
        session.setAttribute(SmsValidationCodeParamName, validationCode);
        session.setAttribute(SmsValidationPhoneParamName, phoneNumber);
        session.setAttribute(SmsValidationDialingCodeParamName, dialingCode);
        //
        String message = smsForgotPwdTemplate.replace("@", validationCode);
        boolean success = chinaSmsProvider.sendSms(phoneNumber, message);
        if (success) {
            return JsonResponse.successResponseWithData(validationCode);
        } else {
            return JsonResponse.errorResponseWithError("fail", "验证码发送失败");
        }
    }

    Logger logger = LoggerFactory.getLogger(SmsController.class);
}
