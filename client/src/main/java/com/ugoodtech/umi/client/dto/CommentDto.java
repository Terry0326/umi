package com.ugoodtech.umi.client.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ugoodtech.umi.core.domain.TopicComment;
import com.ugoodtech.umi.core.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

@ApiModel("评论")
public class CommentDto implements Serializable {
    private Long id;
    @ApiModelProperty("评论内容")
    String content;
    @ApiModelProperty("发帖人")
    User user;
    @ApiModelProperty("用户ID")
    private Long userId;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("用户昵称")
    private String nickname;
    @ApiModelProperty("创建时间")
    private Date creationTime;
    @ApiModelProperty("用户头像")
    private String avatar;
    private boolean burnable;
    private CommentContentBody contentObject;

    public CommentDto(TopicComment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        User publisher = comment.getPublisher();
        this.user=publisher;
        this.userId = publisher.getId();
        this.username = publisher.getUsername();
        this.nickname = publisher.getNickname();
        this.burnable = comment.isBurnable();
        this.creationTime = comment.getCreationTime();
        this.avatar=publisher.getAvatar();
        if (comment.getContent().startsWith("{")) {
            try {
                this.contentObject = new ObjectMapper().readValue(comment.getContent(), CommentContentBody.class);
            } catch (IOException ignored) {
            }
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isBurnable() {
        return burnable;
    }

    public void setBurnable(boolean burnable) {
        this.burnable = burnable;
    }

    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public CommentContentBody getContentObject() {
        return contentObject;
    }
}
