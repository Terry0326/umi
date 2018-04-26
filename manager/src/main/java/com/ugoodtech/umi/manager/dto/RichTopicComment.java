package com.ugoodtech.umi.manager.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.User;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;


public class RichTopicComment {
    private RichTopic topic;
    @ApiModelProperty("评论内容")
    private String content;
    private Date creationTime;
    @ApiModelProperty("发布人")
    private User publisher;

    private boolean showDeleted;
    private boolean burnable;

    public RichTopic getTopic() {
        return topic;
    }

    public void setTopic(RichTopic topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    public boolean isShowDeleted() {
        return showDeleted;
    }

    public void setShowDeleted(boolean showDeleted) {
        this.showDeleted = showDeleted;
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
