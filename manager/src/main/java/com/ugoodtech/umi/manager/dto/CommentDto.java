package com.ugoodtech.umi.manager.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.TopicComment;
import com.ugoodtech.umi.core.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

@ApiModel("评论")
public class CommentDto implements Serializable {
    @ApiModelProperty("评论内容")
    String content;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("用户昵称")
    private String nickname;
    @ApiModelProperty("创建时间")
    private Date creationTime;
    private boolean burnable;

    public CommentDto(TopicComment comment) {
        this.content = comment.getContent();
        User publisher = comment.getPublisher();
        this.username = publisher.getUsername();
        this.nickname = publisher.getNickname();
        this.creationTime = comment.getCreationTime();
        this.burnable = comment.isBurnable();
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

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isBurnable() {
        return burnable;
    }

    public void setBurnable(boolean burnable) {
        this.burnable = burnable;
    }
}
