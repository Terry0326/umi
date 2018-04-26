package com.ugoodtech.umi.client.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import java.io.Serializable;

public class CommentMessage implements Serializable {
    private Long userId;
    private String userNickname;
    private Long topicId;
    private boolean burnable;
    private CommentContentBody contentBody;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public CommentContentBody getContentBody() {
        return contentBody;
    }

    public void setContentBody(CommentContentBody contentBody) {
        this.contentBody = contentBody;
    }

    public boolean isBurnable() {
        return burnable;
    }

    public void setBurnable(boolean burnable) {
        this.burnable = burnable;
    }
}
