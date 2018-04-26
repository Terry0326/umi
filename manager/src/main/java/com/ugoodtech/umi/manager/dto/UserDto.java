package com.ugoodtech.umi.manager.dto;

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
    @ApiModelProperty("是否加入黑名单")
    private boolean enabled;
    public UserDto() {
    }

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.avatar = user.getAvatar();
        this.nickname = user.getNickname();
        this.address = user.getAddress();
        this.signature = user.getSignature();
        this.gender = user.getGender();
        this.enabled = user.isEnabled();
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
