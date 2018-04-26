package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ugoodtech.umi.core.domain.converter.MessageTypeConverter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "messages")
public class Message extends BaseEntity {
    private String title;
    private String content;
    private User fromUser;
    private User toUser;
    private MessageType type;
    private Long linkId;
    private Long subLinkId;
    private Integer linkType;
    private Date notifyTime;
    private Date readTime;
    private boolean expired;
    private boolean read;
    private String countries;

    @Column(length = 200)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(length = 1000)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "message_type")
    @Convert(converter = MessageTypeConverter.class)
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    @Column(name = "link_id")
    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    @Column(name = "sub_link_id")
    public Long getSubLinkId() {
        return subLinkId;
    }

    public void setSubLinkId(Long subLinkId) {
        this.subLinkId = subLinkId;
    }

    public Integer getLinkType() {
        return linkType;
    }

    public void setLinkType(Integer linkType) {
        this.linkType = linkType;
    }

    @Column(name = "notify_time")
    public Date getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    @Column(name = "is_read")
    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public String getCountries() {
        return countries;
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum MessageType {
        NOTICE_MESSAGE(0, "公告"),
        FOLLOW_MESSAGE(1, "关注"),
        INSTANT_COMMENT_MESSAGE(2, "实时评论"),
        AT_MESSAGE(3, "@了你(除标记@外所有@)"),
        LIKE_MESSAGE(4, "点赞"),
        MARK_MESSAGE(6, "发帖标记@");

        @JsonProperty
        private Integer code;
        @JsonProperty
        private String name;

        MessageType(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static final Map<Integer, MessageType> dbValues = new HashMap<>();

        static {
            for (MessageType messageType : values()) {
                dbValues.put(messageType.code, messageType);
            }
        }

        @JsonCreator
        public static MessageType forValue(Integer value) {
            return dbValues.get(value);
        }
    }
}
