package com.ugoodtech.umi.manager.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.Topic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApiModel("帖子")
public class RichTopic {
    @ApiModelProperty("帖子ID")
    private Long id;
    @ApiModelProperty("帖子内容,图片key或者视频key")
    private String content;
    @ApiModelProperty("帖子内容类型")
    private Topic.TopicType topicType;
    @ApiModelProperty("是否焚烧帖")
    private boolean burnTopic;
    @ApiModelProperty(hidden = true)
    private boolean burned;
    @ApiModelProperty("是否匿名")
    private boolean anonymous;
    @ApiModelProperty("生存时间,单位秒")
    private String aliveTime;
    @ApiModelProperty("帖子创建者")
    private UserDto user;
    @ApiModelProperty("评论数量")
    private Long commentsNum;
    private List<CommentDto> comments = new ArrayList<>();
    @ApiModelProperty("创建时间")
    private Date creationTime;
    @ApiModelProperty("点赞数量")
    private Long likeNum;
    @ApiModelProperty("激活禁用")
    private boolean enabled;
    @ApiModelProperty("帖子标题")
    private String title;
    @ApiModelProperty("帖子点击率")
    private Long clickNum;
    @ApiModelProperty("帖子定时发布时间")
    private Date timedTime;
    @ApiModelProperty("定时帖是否已发布")
    private Boolean timed;
    public RichTopic() {
    }

    public RichTopic(Topic topic) {
        this.setTimed(topic.isTimed());
        this.setId(topic.getId());
        this.setContent(topic.getContent());
        this.setTopicType(topic.getTopicType());
        this.setAliveTime(topic.getAliveTime());
        this.setBurnTopic(topic.isBurnTopic());
        this.setBurned(topic.isBurned());
        this.setAnonymous(topic.isAnonymous());
        this.setUser(new UserDto(topic.getUser()));
        this.setCreationTime(topic.getCreationTime());
        this.setEnabled(topic.isEnabled());
        this.setTitle(topic.getDescription());
        this.setClickNum(topic.getClickNum());
        this.setTimedTime(topic.getTimedTime());
    }

    public Boolean getTimed() {
        return timed;
    }

    public void setTimed(Boolean timed) {
        this.timed = timed;
    }

    public Date getTimedTime() {
        return timedTime;
    }

    public void setTimedTime(Date timedTime) {
        this.timedTime = timedTime;
    }

    public Long getClickNum() {
        return clickNum;
    }

    public void setClickNum(Long clickNum) {
        this.clickNum = clickNum;
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

    public Topic.TopicType getTopicType() {
        return topicType;
    }

    public void setTopicType(Topic.TopicType topicType) {
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

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
