package com.ugoodtech.umi.client.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 15/12/16
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ugoodtech.umi.client.dto.UserDetailDto;
import com.ugoodtech.umi.client.dto.UserDto;
import com.ugoodtech.umi.client.service.*;
import com.ugoodtech.umi.core.Constants;
import com.ugoodtech.umi.core.domain.Address;
import com.ugoodtech.umi.core.domain.Gender;
import com.ugoodtech.umi.core.domain.ThirdPartyAccount;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.domain.converter.GenderConverter;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.UserRepository;
import io.swagger.annotations.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Api("用户接口")
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserManager userManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ThirdPartyAccountService thirdPartyAccountService;

    @Autowired
    private FollowService followService;
    @Autowired
    private BlockUserService blockUserService;
    @Autowired
    private UserTipOffService userTipOffService;

    @Value("${client.app.contextPath}")
    private String urlPrefix;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
        binder.registerCustomEditor(Gender.class, new GenderConverter());
    }

    @ApiOperation("QQ登录")
    @RequestMapping(value = "/loginByQQ", method = RequestMethod.POST)
    public JsonResponse loginByQQ(@ApiParam("openId") @RequestParam String openId,
                                  @ApiParam("accessToken") @RequestParam String accessToken,
                                  @ApiParam("pf") @RequestParam String pf,
                                  @ApiParam("nickname") @RequestParam String nickname,
                                  @ApiParam(value = "头像key") @RequestParam String avatar,
                                  @ApiParam("性别,0:男,1:女") @RequestParam Gender gender
    ) throws UmiException {
        logger.debug("nickname:" + nickname);
        logger.debug("avatar:" + avatar);
        ThirdPartyAccount.ThirdParty qq = ThirdPartyAccount.ThirdParty.QQ;
        String username = qq.getName() + "_" + openId;
        String password = openId + "_" + qq.getName() + qq.getCode();
        User user = null;
        try {
            user = userManager.getUserByUsername(username);
        } catch (Exception e) {
        }
        if (user == null) {
            user = userManager.createUser("86", username, password, User.RegistrationWay.QQ_REGISTER);
            String randomName = nickname;
            while (userManager.isNicknameExist(randomName, null)) {
                randomName = nickname + "_" + RandomStringUtils.randomNumeric(6);
            }
            userManager.updateUserDetail(user, randomName, gender, new Address(), "", avatar, false);
        }
        thirdPartyAccountService.checkThirdPartyAccount(openId, accessToken, qq, user);
        return getAccessToken(user, password);
    }

    @ApiOperation("微信登录")
    @RequestMapping(value = "/loginByWX", method = RequestMethod.POST)
    public JsonResponse loginByWX(@ApiParam("openId") @RequestParam String openId,
                                  @ApiParam("accessToken") @RequestParam String accessToken,
                                  @ApiParam("nickname") @RequestParam String nickname,
                                  @ApiParam(value = "头像key") @RequestParam String avatar,
                                  @ApiParam("性别,0:男,1:女") @RequestParam Gender gender
    ) throws UmiException {
        System.out.println("===============微信登录===============");
        logger.debug("nickname:" + nickname);
        logger.debug("avatar:" + avatar);
        ThirdPartyAccount.ThirdParty wx = ThirdPartyAccount.ThirdParty.WX;
        String username = wx.getName() + "_" + openId;
        String password = openId + "_" + wx.getName() + wx.getCode();
        User user = null;
        try {
            user = userManager.getUserByUsername(username);
        } catch (Exception e) {
        }
        if (user == null) {
            user = userManager.createUser("86", username, password, User.RegistrationWay.WEI_XIN_REGISTER);
            String randomName = nickname;
            while (userManager.isNicknameExist(randomName, null)) {
                randomName = nickname + "_" + RandomStringUtils.randomNumeric(6);
            }
            userManager.updateUserDetail(user, randomName, gender, new Address(), "", avatar, false);
        }
        thirdPartyAccountService.checkThirdPartyAccount(openId, accessToken, wx, user);
        return getAccessToken(user, password);
    }

    @ApiOperation("退出")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public JsonResponse logout(@AuthenticationPrincipal User user) throws UmiException {
        // TODO:
        thirdPartyAccountService.removePushRegistrationID(user);
        return JsonResponse.successResponse();
    }

    private JsonResponse getAccessToken(User user, String password) {
        String result = oauthLogin(user.getUsername(), password);
        try {
            Map tokenData = new ObjectMapper().readValue(result, Map.class);
            UserDetailDto userDetail = null;
            try {
                userDetail = userManager.getUserDetail(user.getId());
            } catch (UmiException e) {
                e.printStackTrace();
            }
            Map<String, Object> data = new HashMap<>();
            data.put("token", tokenData);
            data.put("user", userDetail);
            return JsonResponse.successResponseWithData(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonResponse.errorResponse(1000, "登录失败,请重试");
    }

    @ApiOperation("注册-在手机号验证通过后,将手机号,密码和验证码一起提交,后台重新验证一遍")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public JsonResponse register(@ApiParam("国家电话区号,默认为中国86")
                                 @RequestParam(value = "dialingCode", defaultValue = "86", required = false)
                                         String dialingCode,
                                 @ApiParam("手机号") @RequestParam("username") String username,
                                 @ApiParam("密码") @RequestParam("password") String password,
                                 @ApiParam("短信验证码") @RequestParam("validationCode") String validationCode,
                                 HttpServletRequest request) {
        HttpSession session = request.getSession();
        logger.debug("session id is:" + session.getId());
        String phoneNumber = (String) session.getAttribute(SmsController.SmsValidationPhoneParamName);
        if (!username.equals(phoneNumber)) {
            logger.warn("注册的手机号码{" + username + "}和接受验证码的手机号码{" + phoneNumber + "}不匹配");
            return JsonResponse.errorResponseWithError("失败", "手机号不正确");
        }
        String sendCode = (String) session.getAttribute(SmsController.SmsValidationCodeParamName);
        if (!validationCode.equals(sendCode)) {
            return JsonResponse.errorResponseWithError("失败", "验证码错误");
        }
        if (userRepository.findByUsername(username) != null) {
            return JsonResponse.errorResponseWithError("失败", "手机号码已经存在");
        }
        User user = userManager.createUser(dialingCode, username, password, User.RegistrationWay.PHONE_NUMBER_REGISTER);
        return getAccessToken(user, password);
    }

    @ApiOperation("重置密码")
    @RequestMapping(value = "/resetPwd", method = RequestMethod.POST)
    public JsonResponse resetPwd(@ApiParam("手机号码") @RequestParam String phoneNumber,
                                 @ApiParam("验证码") @RequestParam String validationCode,
                                 @ApiParam("新密码") @RequestParam String newPwd,
                                 HttpServletRequest request) {
        logger.debug("update pwd with validation code:" + validationCode + " for phone:" + phoneNumber);
        String sentPhoneNumber = (String) request.getSession().getAttribute(SmsController.SmsValidationPhoneParamName);
        String sentCode = (String) request.getSession().getAttribute(SmsController.SmsValidationCodeParamName);
        if (!phoneNumber.equals(sentPhoneNumber)) {
            return JsonResponse.errorResponseWithError("fail", "手机号码不正确");
        }
        if (!validationCode.equals(sentCode)) {
            return JsonResponse.errorResponseWithError("fail", "验证码不正确");
        } else {
            userManager.resetPassword(phoneNumber, newPwd);
            return JsonResponse.successResponse();
        }
    }

    @ApiOperation("修改密码")
    @RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    public JsonResponse updatePwd2(@AuthenticationPrincipal User user,
                                   @ApiParam("原密码") @RequestParam String oldPwd,
                                   @ApiParam("新密码") @RequestParam String newPwd) throws UmiException {
        userManager.updatePassword(user.getId(), oldPwd, newPwd);
        return JsonResponse.successResponse();
    }

    @ApiOperation("完善用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address", value = "具体地址", paramType = "query"),
            @ApiImplicitParam(name = "country", value = "国家", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "省份", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "城市", paramType = "query"),
            @ApiImplicitParam(name = "lat", value = "纬度", paramType = "query"),
            @ApiImplicitParam(name = "lng", value = "经度", paramType = "query")})
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public JsonResponse<UserDetailDto> completeDetail(@ApiIgnore Authentication authentication,
                                                      @ApiParam("昵称") @RequestParam String nickname,
                                                      @ApiParam("性别,0:男,1:女,2:未知") @RequestParam(defaultValue = "2") Gender gender,
                                                      @ApiIgnore Address addr,
                                                      @ApiParam(value = "签名") @RequestParam(required = false) String signature,
                                                      @ApiParam(value = "头像key") @RequestParam(required = false) String avatar
    ) throws UmiException {
        User user = (User) authentication.getPrincipal();
        UserDetailDto userDetail = userManager.updateUserDetail(user, nickname, gender, addr, signature, avatar, true);
        return JsonResponse.successResponseWithData(userDetail);
    }


    @ApiOperation("获取用户详情,包含关注和被关注数据,不传userId属性则返回当前登录用户数据")
    @ApiResponse(code = 200, response = UserDetailDto.class, message = "获取用户详情,包含关注和被关注数据")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public JsonResponse<UserDetailDto> getUserDetail(@AuthenticationPrincipal User user,
                                                     @ApiParam(value = "用户id,不传该属性则返回当前登录用户数据")
                                                     @RequestParam(required = false) Long userId) throws UmiException {
        if (userId == null) {
            userId = user.getId();
        }
        UserDetailDto userDetail = userManager.getUserDetail(user.getId(), userId);
        if (userDetail.isBlockMe()) {
            throw new UmiException(1000, "您无权查看他的主页");
        }
        return JsonResponse.successResponseWithData(userDetail);
    }

    @ApiOperation("设置推送通知是否接收")
    @RequestMapping(value = "/configNotification", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public JsonResponse stopReceiveNotification(@ApiIgnore @AuthenticationPrincipal User user,
                                                @ApiParam @RequestParam boolean receive) {
        userManager.configReceiveNotification(user.getId(), receive);
        return JsonResponse.successResponse();
    }

    @ApiOperation("保存推送id")
    @RequestMapping(value = "/pushToken", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public JsonResponse registerPushToken(@ApiIgnore @AuthenticationPrincipal User user,
                                          @ApiParam("推送ID") @RequestParam String registrationID,
                                          @ApiParam("来源,0:iOS,1:Android,2:Web") @RequestParam Integer source) {
        logger.debug("save sendNotification token " + registrationID + " for user " + user.getUsername());
        ThirdPartyAccount.AccountSource accountSource = ThirdPartyAccount.AccountSource.forValue(source);
        thirdPartyAccountService.addPushRegistrationID(user, registrationID, accountSource);
        return JsonResponse.successResponse();
    }

    @ApiOperation("关注")
    @RequestMapping(value = "/follow", method = RequestMethod.POST)
    public JsonResponse follow(
            @ApiIgnore Authentication authentication,
            @ApiParam("被关注用户id") @RequestParam Long followUserId) throws UmiException {
        User user = (User) authentication.getPrincipal();
        followService.addFollow(user.getId(), followUserId);
        return JsonResponse.successResponse();
    }

    @ApiOperation("拉黑")
    @RequestMapping(value = "/blocks", method = RequestMethod.POST)
    public JsonResponse block(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("拉黑用户id") @RequestParam Long followUserId) throws UmiException {
        blockUserService.block(user.getId(), followUserId);
        return JsonResponse.successResponse();
    }

    @ApiOperation("是否被拉黑")
    @RequestMapping(value = "/isBlocked", method = RequestMethod.GET)
    public JsonResponse<Boolean> isBlocked(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam @RequestParam Long userId) throws UmiException {
        boolean result = blockUserService.isUserBlockedByOne(user, userId);
        return JsonResponse.successResponseWithData(result);
    }

    @ApiOperation("拉黑名单列表")
    @RequestMapping(value = "/blocks", method = RequestMethod.GET)
    public JsonResponse<List<UserDto>> blockList(
            @ApiIgnore @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable) throws UmiException {
        Page<User> blockUsers = blockUserService.getBlockList(user.getId(), pageable);
        return JsonResponse.successResponseWithPageData(getUserDtos(pageable, blockUsers));
    }

    @ApiOperation("移除黑名单")
    @RequestMapping(value = "/unblock", method = RequestMethod.POST)
    public JsonResponse unblock(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("拉黑用户id") @RequestParam Long blockUserId) throws UmiException {
        blockUserService.unblock(user.getId(), blockUserId);
        return JsonResponse.successResponse();
    }
    @ApiOperation("看还是不看Ta的帖子")
    @RequestMapping(value = "/isSeeItsTopics", method = RequestMethod.GET)
    public JsonResponse<Boolean> isSeeItsTopics(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam @RequestParam Long userId) throws UmiException {
        boolean result = blockUserService.isSeeItsTopicByOne(user, userId);
        return JsonResponse.successResponseWithData(result);
    }
    @ApiOperation("不看Ta的帖子")
    @RequestMapping(value = "/unSeeItsTopics", method = RequestMethod.POST)
    public JsonResponse unSeeItsTopics(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("帖子用户id") @RequestParam Long blockUserId) throws UmiException {
        System.out.println("=====================unSeeItsTopics===============");
        blockUserService.unSeeItsTopics(user.getId(), blockUserId);
        return JsonResponse.successResponse();
    }
    @ApiOperation("取消不看Ta的帖子")
    @RequestMapping(value = "/seeItsTopics", method = RequestMethod.POST)
    public JsonResponse seeItsTopics(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("帖子用户id") @RequestParam Long blockUserId) throws UmiException {
        System.out.println("=====================seeItsTopics===============");
        blockUserService.seeItsTopics(user.getId(), blockUserId);
        return JsonResponse.successResponse();
    }

    @ApiOperation("粉丝列表")
    @RequestMapping(value = "/followers", method = RequestMethod.GET)
    public JsonResponse<List<UserDetailDto>> getFollowers(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("获取粉丝列表的用户id,不传取当前用户") @RequestParam(required = false) Long userId,
            @ApiParam("分页页码,从0开始") @RequestParam(required = false) Integer page,
            @ApiParam("每页条数") @RequestParam(required = false) Integer size

    ) throws UmiException {
        if (userId == null) {
            userId = user.getId();
        }

        if (page != null && size != null) {
            Page<User> userPage = followService.getFollowers(userId, new PageRequest(page, size));
            List<UserDetailDto> userDtoPage = getUserDetailDtos(user, userPage.getContent());
            return JsonResponse.successResponseWithPageData(userDtoPage, userPage.getTotalElements());
        } else {
            List<User> users = followService.getFollowers(userId);
            List<UserDetailDto> userDtoPage = getUserDetailDtos(user, users);
            return JsonResponse.successResponseWithData(userDtoPage);
        }
    }

    private Page<UserDto> getUserDtos(@PageableDefault Pageable pageable, Page<User> users) {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User follower : users) {
            userDtoList.add(new UserDto(follower));
        }
        return new PageImpl<>(userDtoList, pageable, users.getTotalElements());
    }

    @ApiOperation("关注的人列表")
    @RequestMapping(value = "/following", method = RequestMethod.GET)
    public JsonResponse<List<UserDetailDto>> getFollowing(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("获取关注人列表的用户id,不传取当前用户") @RequestParam(required = false) Long userId,
            @ApiParam("分页页码,从0开始") @RequestParam(required = false) Integer page,
            @ApiParam("每页条数") @RequestParam(required = false) Integer size) throws UmiException {
        if (userId == null) {
            userId = user.getId();
        }
        if (page != null && size != null) {
            Page<User> userPage = followService.getFollowingUsers(userId, new PageRequest(page, size));
            List<UserDetailDto> userDtoPage = getUserDetailDtos(user, userPage.getContent());
            return JsonResponse.successResponseWithPageData(userDtoPage, userPage.getTotalElements());
        } else {
            List<User> users = followService.getFollowingUsers(userId);
            List<UserDetailDto> userDtoPage = getUserDetailDtos(user, users);
            return JsonResponse.successResponseWithData(userDtoPage);
        }
    }

    private Page<UserDetailDto> getUserDetailDtos(User user, Pageable pageable, Page<User> users) throws UmiException {
        List<UserDetailDto> detailDtos = new ArrayList<>();
        for (User followingUser : users) {
            detailDtos.add(userManager.getUserDetail(user.getId(), followingUser));
        }
        return new PageImpl<>(detailDtos, pageable, users.getTotalElements());
    }

    private List<UserDetailDto> getUserDetailDtos(User user, List<User> users) throws UmiException {
        List<UserDetailDto> detailDtos = new ArrayList<>();
        for (User followingUser : users) {
            detailDtos.add(userManager.getUserDetail(user.getId(), followingUser));
        }
        return detailDtos;
    }

    @ApiOperation("取消关注")
    @RequestMapping(value = "/unfollow", method = RequestMethod.POST)
    public JsonResponse unfollow(
            @ApiIgnore Authentication authentication,
            @ApiParam("被关注用户id") @RequestParam Long followUserId) throws UmiException {
        User user = (User) authentication.getPrincipal();
        followService.unFollow(user.getId(), followUserId);
        return JsonResponse.successResponse();
    }

    @ApiOperation("搜索用户")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public JsonResponse<List<UserDetailDto>> search(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("搜索关键字") @RequestParam String qKey,
            @ApiParam("分页参数,传两个参数page和size") @PageableDefault Pageable pageable) throws UmiException {
        Page<User> userPage = userManager.query(user, qKey, pageable);
        List<UserDetailDto> userDtos = new ArrayList<>();
        for (User user1 : userPage) {
            UserDetailDto detailDto = new UserDetailDto(user1);
            detailDto.setFollowing(followService.isFollowing(user.getId(), user1.getId()));
            userDtos.add(detailDto);
        }
        Page<UserDetailDto> userDtoPage = new PageImpl<>(userDtos, pageable, userPage.getTotalElements());
        return JsonResponse.successResponseWithPageData(userDtoPage);
    }

    @ApiOperation("搜索粉丝")
    @RequestMapping(value = "/searchFollowers", method = RequestMethod.GET)
    public JsonResponse<List<UserDto>> searchFollowers(
            @ApiIgnore Authentication authentication,
            @ApiParam("搜索关键字") @RequestParam String qKey,
            @ApiParam("分页参数,传两个参数page和size") @PageableDefault Pageable pageable) throws UmiException {
        User user = (User) authentication.getPrincipal();
        Page<User> userPage = followService.queryFollowers(user, qKey, pageable);
        List<UserDto> userDtos = new ArrayList<>();
        for (User user1 : userPage) {
            userDtos.add(new UserDto(user1));
        }
        Page<UserDto> userDtoPage = new PageImpl<UserDto>(userDtos, pageable, userPage.getTotalElements());
        return JsonResponse.successResponseWithPageData(userDtoPage);
    }

    @ApiOperation("搜索关注的人")
    @RequestMapping(value = "/searchFollowingUsers", method = RequestMethod.GET)
    public JsonResponse<List<UserDto>> searchFollowingUsers(
            @ApiIgnore Authentication authentication,
            @ApiParam("搜索关键字") @RequestParam String qKey,
            @ApiParam("分页参数,传两个参数page和size") @PageableDefault Pageable pageable) throws UmiException {
        User user = (User) authentication.getPrincipal();
        Page<User> userPage = followService.queryFollowingUsers(user, qKey, pageable);
        List<UserDto> userDtos = new ArrayList<>();
        for (User user1 : userPage) {
            userDtos.add(new UserDto(user1));
        }
        Page<UserDto> userDtoPage = new PageImpl<UserDto>(userDtos, pageable, userPage.getTotalElements());
        return JsonResponse.successResponseWithPageData(userDtoPage);
    }

    @ApiOperation("举报用户")
    @RequestMapping(value = "/tipOff", method = RequestMethod.POST)
    public JsonResponse tipOff(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("举报的用户id") @RequestParam Long targetUserId,
            @ApiParam("举报原因") @RequestParam String reason) throws UmiException {
        userTipOffService.tipOff(user, targetUserId, reason);
        return JsonResponse.successResponse();
    }

    private String oauthLogin(String username, String password) {
        HttpClient httpclient = HttpClientBuilder.create().build();
        String encoding = oauthBasicAuthBase64();
        HttpPost httppost = new HttpPost(urlPrefix + "/oauth/token?username=" + username
                + "&password=" + password + "&grant_type=password");
        httppost.setHeader("Authorization", "Basic " + encoding);
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String oauthBasicAuthBase64() {
        return Base64.encodeBase64String((Constants.clientID + ":" + Constants.clientSecret).getBytes());
    }

    Logger logger = LoggerFactory.getLogger(UserController.class);
}

