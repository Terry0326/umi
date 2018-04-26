package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@ApiModel("评论")
@Entity
@Table(name = "topic_comments")
public class TopicComment extends BaseEntity {
    //    @JsonIgnore
//    @ApiModelProperty(hidden = true)
    private Topic topic;
    @ApiModelProperty("评论内容")
    private String content;
    @ApiModelProperty("发布人")
    private User publisher;

    private boolean showDeleted;
    private boolean burnable;
    private boolean burned;

    @Column(length = 500)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    @ManyToOne
    @JoinColumn(name = "topic_id")
    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    @Column(name = "is_burnable")
    public boolean isBurnable() {
        return burnable;
    }

    public void setBurnable(boolean burnable) {
        this.burnable = burnable;
    }

    public boolean isBurned() {
        return burned;
    }

    public void setBurned(boolean burned) {
        this.burned = burned;
    }

    @Transient
    public boolean isShowDeleted() {
        return showDeleted;
    }

    public void setShowDeleted(boolean showDeleted) {
        this.showDeleted = showDeleted;
    }
}
