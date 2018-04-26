package com.ugoodtech.umi.client.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.Message;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageDto implements Serializable {
    private Long id;
    private UserDetailDto fromUser;
    private UserDto toUser;
    private Message.MessageType messageType;
    private String content;
    private Date creationTime;
    private Long linkId;
    private Integer linkType;
    private Map<String, Object> extra = new HashMap<>();
    private Long total;
    private String interval;
    private boolean read;
    private Integer unreadNum;
    private boolean receiveAtMsg = false;
    private boolean deleted;
    public MessageDto() {
    }

    public MessageDto(Message message) {
        this.id = message.getId();
        fromUser = new UserDetailDto(message.getFromUser());
        toUser = new UserDto(message.getToUser());
        messageType = message.getType();
        content = message.getContent();
        creationTime = message.getCreationTime();
        long howLong = (System.currentTimeMillis() - creationTime.getTime()) / 1000;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
      //  if (howLong > 365 * 24 * 60 * 60) {
     //       interval = howLong / (365 * 24 * 60 * 60) + "年";
     //   } else if (howLong > 30 * 24 * 60 * 60) {
      //      interval = howLong / (30 * 24 * 60 * 60) + "月";
     //   }
        if(howLong >7* 24 * 60 * 60){
            interval =formatter.format(creationTime.getTime());
        }
        else if (howLong > 24 * 60 * 60) {
            interval = howLong / (24 * 60 * 60) + "天前";
        } else if (howLong > 60 * 60) {
            interval = howLong / (60 * 60) + "小时前";
        } else if (howLong > 60) {
            interval = howLong / 60 + "分钟前";
        } else {
            interval = howLong + "秒前";
        }
        linkId = message.getLinkId();
        linkType = message.getLinkType();
        this.read = message.isRead();
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDetailDto getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserDetailDto fromUser) {
        this.fromUser = fromUser;
    }

    public UserDto getToUser() {
        return toUser;
    }

    public void setToUser(UserDto toUser) {
        this.toUser = toUser;
    }

    public Message.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(Message.MessageType messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public Integer getLinkType() {
        return linkType;
    }

    public void setLinkType(Integer linkType) {
        this.linkType = linkType;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Integer getUnreadNum() {
        return unreadNum;
    }

    public void setUnreadNum(Integer unreadNum) {
        this.unreadNum = unreadNum;
    }

    public boolean isReceiveAtMsg() {
        return receiveAtMsg;
    }

    public void setReceiveAtMsg(boolean receiveAtMsg) {
        this.receiveAtMsg = receiveAtMsg;
    }
}
