package com.ugoodtech.umi.manager.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.User;

import java.util.Date;


public class RichTopicTipOff  {
    private Long id;
    private RichTopic topic;
    private User user;
    private String reason;
    private boolean done=false;
    private String doneStr;
    private Date creationTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RichTopic getTopic() {
        return topic;
    }

    public void setTopic(RichTopic topic) {
        this.topic = topic;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getDoneStr() {
        return doneStr;
    }

    public void setDoneStr(String doneStr) {
        this.doneStr = doneStr;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
