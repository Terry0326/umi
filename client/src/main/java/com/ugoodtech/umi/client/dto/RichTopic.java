package com.ugoodtech.umi.client.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.Topic;
import com.ugoodtech.umi.core.domain.TopicAddress;
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
    @ApiModelProperty("帖子说明")
    private String description;
    @ApiModelProperty("帖子内容类型")
    private Topic.TopicType topicType;
    @ApiModelProperty("是否焚烧帖")
    private boolean burnTopic;
    @ApiModelProperty("是否匿名")
    private boolean anonymous;
    @ApiModelProperty("生存时间")
    private String aliveTime;
    @ApiModelProperty("剩余生存时间,单位秒")
    private Integer remainTime;
    @ApiModelProperty("帖子创建者")
    private UserDetailDto user;
    @ApiModelProperty("评论数量")
    private Long commentsNum;
    private Long burnableCommentsNum;
    private List<CommentDto> comments = new ArrayList<>();
    @ApiModelProperty("创建时间")
    private Date creationTime;
    @ApiModelProperty("点赞数量")
    private Long likeNum;
    @ApiModelProperty("是否点赞过")
    private boolean liked;
    @ApiModelProperty("点击率")
     private Long clickNum;
    @ApiModelProperty("距离")
    private Double distance;
    private String imageUrlPrefix;
    private boolean owner;
    private String cover;
    private TopicAddress address;
    private String playUrls;

    public RichTopic() {
    }

    public RichTopic(Topic topic) {
        this.setId(topic.getId());
        this.setContent(topic.getContent());
        this.setDescription(topic.getDescription());
        this.setTopicType(topic.getTopicType());
        this.setAliveTime(topic.getAliveTime() );
        this.setBurnTopic(topic.isBurnTopic());
        this.setAnonymous(topic.isAnonymous());

        this.setUser(new UserDetailDto(topic.getUser()));
        this.setCreationTime(topic.getCreationTime());
        if (topic.isBurnTopic()) {
            Date now = new Date();
            Long remain = now.getTime() - this.getCreationTime().getTime();
            this.remainTime = Math.toIntExact(remain / 1000);
        }
        this.cover = topic.getCover();
        this.address = topic.getAddress();
        this.playUrls = topic.getPlayUrls();
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(Integer remainTime) {
        this.remainTime = remainTime;
    }

    public UserDetailDto getUser() {
        return user;
    }

    public void setUser(UserDetailDto user) {
        this.user = user;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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

    public Long getBurnableCommentsNum() {
        return burnableCommentsNum;
    }

    public void setBurnableCommentsNum(Long burnableCommentsNum) {
        this.burnableCommentsNum = burnableCommentsNum;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }


    public String getImageUrlPrefix() {
        return imageUrlPrefix;
    }

    public void setImageUrlPrefix(String imageUrlPrefix) {
        this.imageUrlPrefix = imageUrlPrefix;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public TopicAddress getAddress() {
        return address;
    }

    public void setAddress(TopicAddress address) {
        this.address = address;
    }

    public String getPlayUrls() {
        return playUrls;
    }

    public void setPlayUrls(String playUrls) {
        this.playUrls = playUrls;
    }
}
