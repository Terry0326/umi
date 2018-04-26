package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ugoodtech.umi.core.domain.converter.TopicTypeConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ApiModel("帖子")
@Entity
@Table(name = "topics")
public class Topic extends BaseEntity {
    @ApiModelProperty("帖子内容,图片key或者视频key")
    private String content;
    @ApiModelProperty("帖子封面")
    private String cover;
    @ApiModelProperty("说明文字")
    private String description;
    @ApiModelProperty("帖子内容类型")
    private TopicType topicType;
    @ApiModelProperty("是否焚烧帖")
    private boolean burnTopic;
    @ApiModelProperty(hidden = true)
    private boolean burned;
    @ApiModelProperty("是否定时帖")
    private boolean timeTopic;
    @ApiModelProperty("是否定时帖")
    private boolean timed;
    @ApiModelProperty("定时发送的日期")
    private Date timedTime;
    @ApiModelProperty("匿名")
    private boolean anonymous;
    @ApiModelProperty("生存时间,结束的日期")
    private String aliveTime;

    @ApiModelProperty("帖子创建者")
    private User user;

    private boolean enabled = true;
    private TopicAddress address;
    @ApiModelProperty("评论数量")
    private Long commentsNum = 0L;
    @ApiModelProperty("点赞数量")
    private Long likeNum = 0L;
    @ApiModelProperty("点击数量")
    private Long clickNum = 0L;
    private Integer weight;
    private String playUrls;

    public Date getTimedTime() {
        return timedTime;
    }

    public void setTimedTime(Date timedTime) {
        this.timedTime = timedTime;
    }

    public boolean isTimeTopic() {
        return timeTopic;
    }

    public void setTimeTopic(boolean timeTopic) {
        this.timeTopic = timeTopic;
    }

    public boolean isTimed() {
        return timed;
    }

    public void setTimed(boolean timed) {
        this.timed = timed;
    }

    @Column(length = 100)
    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Column(length = 100)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(length = 1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column
    @Convert(converter = TopicTypeConverter.class)
    public TopicType getTopicType() {
        return topicType;
    }

    public void setTopicType(TopicType topicType) {
        this.topicType = topicType;
    }

    public boolean isBurnTopic() {
        return burnTopic;
    }

    public void setBurnTopic(boolean burnTopic) {
        this.burnTopic = burnTopic;
    }

    public boolean isBurned() {
        return burned;
    }

    public void setBurned(boolean burned) {
        this.burned = burned;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getAliveTime() {
        return aliveTime;
    }

    public void setAliveTime(String aliveTime) {
        this.aliveTime = aliveTime;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Embedded
    public TopicAddress getAddress() {
        return address;
    }

    public void setAddress(TopicAddress address) {
        this.address = address;
    }

    public Long getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Long likeNum) {
        this.likeNum = likeNum;
    }

    public Long getCommentsNum() {
        return commentsNum;
    }

    public void setCommentsNum(Long commentsNum) {
        this.commentsNum = commentsNum;
    }

    public Long getClickNum() {
        return clickNum;
    }

    public void setClickNum(Long clickNum) {
        this.clickNum = clickNum;
    }

    @Column(name = "play_urls", length = 500)
    public String getPlayUrls() {
        return playUrls;
    }

    public void setPlayUrls(String playUrls) {
        this.playUrls = playUrls;
    }

    @Formula("(comments_num+like_num)")
    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum TopicType {
        Image(1, "图片"), Video(2, "视频");
        private Integer code;
        private String name;

        TopicType(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static final Map<Integer, TopicType> dbValues = new HashMap<>();

        static {
            for (TopicType topicType : values()) {
                dbValues.put(topicType.code, topicType);
            }
        }

        @JsonCreator
        public static TopicType forValue(Integer value) {
            return dbValues.get(value);
        }
    }


}
