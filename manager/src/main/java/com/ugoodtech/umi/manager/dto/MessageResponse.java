package com.ugoodtech.umi.manager.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 15/12/24
 */

import com.ugoodtech.umi.core.domain.Message;
import com.ugoodtech.umi.core.utils.BeanUtilEx;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageResponse implements Serializable {
    private Long messageId;
    private String title;
    private String content;
    private Integer typeInteger;
    private Long linkedId;
    private Integer receiverTypeInteger;
    private String receiverTypeStr;
    private String receiverIds;
    private String notifyTimeStr;
    private String creator;

    public MessageResponse() {
    }

//    public MessageResponse(Message message) throws InvocationTargetException, IllegalAccessException {
//        this.messageId = message.getId();
//        this.addressResponse=AddressResponse.turnAddressToResponse(message.getAddress());
//        this.messageName=message.getName();
//        this.messageShortName=message.getShortName();
//        if (null!=message.getAddress()) {
//            this.messageAddress=message.getAddress().getProvince()
//                    +message.getAddress().getCity()
//                    +message.getAddress().getDistrict()
//                    +message.getAddress().getAddress();
//        }
//    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public Long getLinkedId() {
        return linkedId;
    }

    public void setLinkedId(Long linkedId) {
        this.linkedId = linkedId;
    }


    public String getReceiverTypeStr() {
        return receiverTypeStr;
    }

    public void setReceiverTypeStr(String receiverTypeStr) {
        this.receiverTypeStr = receiverTypeStr;
    }

    public String getReceiverIds() {
        return receiverIds;
    }

    public void setReceiverIds(String receiverIds) {
        this.receiverIds = receiverIds;
    }

    public Integer getTypeInteger() {
        return typeInteger;
    }

    public void setTypeInteger(Integer typeInteger) {
        this.typeInteger = typeInteger;
    }

    public Integer getReceiverTypeInteger() {
        return receiverTypeInteger;
    }

    public void setReceiverTypeInteger(Integer receiverTypeInteger) {
        this.receiverTypeInteger = receiverTypeInteger;
    }

    public String getNotifyTimeStr() {
        return notifyTimeStr;
    }

    public void setNotifyTimeStr(String notifyTimeStr) {
        this.notifyTimeStr = notifyTimeStr;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public static MessageResponse turnMessageToResponse(Message message) throws InvocationTargetException, IllegalAccessException {
        if (null != message) {
            MessageResponse messageResponse = new MessageResponse();
            BeanUtilEx.copyProperties(messageResponse, message);
            messageResponse.setMessageId(message.getId());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (null != message.getNotifyTime()) {
                messageResponse.setNotifyTimeStr(simpleDateFormat.format(message.getNotifyTime()));
            }
            if (null != message.getType()) {
                messageResponse.setReceiverTypeInteger(message.getType().getCode());
                messageResponse.setReceiverTypeStr(message.getType().getName());
            }
            return messageResponse;
        } else {
            return null;
        }
    }

    public static Message turnResponseToMessage(MessageResponse messageResponse) throws ParseException, InvocationTargetException, IllegalAccessException {
        if (null != messageResponse) {
            Message message = new Message();
            message.setId(messageResponse.getMessageId());
//            message.setAddress(AddressResponse.turnResponseToAddress(messageResponse.getAddressResponse()));
            BeanUtilEx.copyProperties(message, messageResponse);
            message.setType(Message.MessageType.NOTICE_MESSAGE);
            message.setNotifyTime(new Date());
//            message.setType();Message.MessageType.forValue(messageResponse.getReceiverTypeInteger()));
            return message;
        } else {
            return new Message();
        }
    }
}
