package com.ugoodtech.umi.client.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ugoodtech.umi.core.domain.Address;
import com.ugoodtech.umi.core.domain.Gender;
import com.ugoodtech.umi.core.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel("用户")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements Serializable {
    @ApiModelProperty("用户ID")
    private Long id;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("性别")
    private Gender gender;
    @ApiModelProperty("用户头像Key")
    private String avatar;
    @ApiModelProperty("nickname")
    private String nickname;
    @ApiModelProperty("地址")
    private Address address;
    @ApiModelProperty("签名")
    private String signature;
    @ApiModelProperty("发帖数量")
    private Long publishTopicsNum;
    @ApiModelProperty("粉丝数量")
    private Long followersNum;
    @ApiModelProperty("关注数量")
    private Long followingNum;
    @ApiModelProperty("帖子点赞数量")
    private Long topicLikeNum;
    @ApiModelProperty("注册方式")
    private User.RegistrationWay registrationWay;
    private boolean infoCompleted;
    private String dialingCode;
    private boolean receiveNotification;

    public UserDto() {
    }

    public UserDto(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.avatar = user.getAvatar();
            this.nickname = user.getNickname();
            this.address = user.getAddress();
            this.signature = user.getSignature();
            this.gender = user.getGender();
            this.followersNum = user.getFollowersNum() == null ? 0 : user.getFollowersNum();
            this.followingNum = user.getFollowingNum() == null ? 0 : user.getFollowingNum();
            this.topicLikeNum = user.getTopicLikeNum() == null ? 0 : user.getTopicLikeNum();
            this.publishTopicsNum = user.getPublishTopicsNum() == null ? 0 : user.getPublishTopicsNum();
            this.registrationWay = user.getRegistrationWay();
            this.infoCompleted = user.isInfoCompleted();
            this.dialingCode = user.getDialingCode();
            this.receiveNotification = user.isReceiveNotification();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Long getFollowersNum() {
        return followersNum;
    }

    public void setFollowersNum(Long followersNum) {
        this.followersNum = followersNum;
    }

    public Long getFollowingNum() {
        return followingNum;
    }

    public void setFollowingNum(Long followingNum) {
        this.followingNum = followingNum;
    }

    public Long getPublishTopicsNum() {
        return publishTopicsNum;
    }

    public void setPublishTopicsNum(Long publishTopicsNum) {
        this.publishTopicsNum = publishTopicsNum;
    }

    public Long getTopicLikeNum() {
        return topicLikeNum;
    }

    public void setTopicLikeNum(Long topicLikeNum) {
        this.topicLikeNum = topicLikeNum;
    }

    public User.RegistrationWay getRegistrationWay() {
        return registrationWay;
    }

    public void setRegistrationWay(User.RegistrationWay registrationWay) {
        this.registrationWay = registrationWay;
    }

    public boolean isInfoCompleted() {
        return infoCompleted;
    }

    public void setInfoCompleted(boolean infoCompleted) {
        this.infoCompleted = infoCompleted;
    }

    public String getDialingCode() {
        return dialingCode;
    }

    public void setDialingCode(String dialingCode) {
        this.dialingCode = dialingCode;
    }

    public boolean isReceiveNotification() {
        return receiveNotification;
    }

    public void setReceiveNotification(boolean receiveNotification) {
        this.receiveNotification = receiveNotification;
    }
}
