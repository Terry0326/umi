package com.ugoodtech.umi.client.dto;

import com.ugoodtech.umi.core.domain.Message;

import java.util.HashMap;
import java.util.Map;

public class Notification {

    private String message;
    private Message.MessageType messageType;
    private Map<String, Object> extra = new HashMap<>();

    public Notification() {
        this.messageType = Message.MessageType.NOTICE_MESSAGE;
    }

    public Message.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(Message.MessageType messageType) {
        this.messageType = messageType;
    }

    public Notification(String content) {
        this();
        this.message = content;
    }

    public String getContent() {
        return message;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
