package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.converter.ResourceTypeConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "resources")
public class Resource extends BaseEntity {
    private String mimeType;
    private String name;
    private String description;
    private Integer size;
    private ResourceType type;
    private String url;
    private String playUrl;
    private String downloadUrl;
    private String path;
    private Long possessorId;
    private PossessorType possessorType;
    private boolean snapshot;
    private boolean completed;

    @Column(name = "mime_type", length = 50)
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Column(length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(length = 500)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column
    @Convert(converter = ResourceTypeConverter.class)
    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Transient
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "play_url", length = 200)
    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    @Column(name = "download_url", length = 200)
    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Column(length = 100)
    @JsonIgnore
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Column(name = "possessor_id")
    public Long getPossessorId() {
        return possessorId;
    }

    public void setPossessorId(Long possessorId) {
        this.possessorId = possessorId;
    }

    @Column(name = "possessor_type")
    @Enumerated(EnumType.ORDINAL)
    public PossessorType getPossessorType() {
        return possessorType;
    }

    public void setPossessorType(PossessorType possessorType) {
        this.possessorType = possessorType;
    }

    public boolean isSnapshot() {
        return snapshot;
    }

    public void setSnapshot(boolean snapshot) {
        this.snapshot = snapshot;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum ResourceType {
        image(1, "照片"), video(2, "录影"), word(3, "word"), excel(4, "excel");
        private Integer code;
        private String name;

        ResourceType(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        @JsonProperty
        public Integer getCode() {
            return code;
        }

        @JsonProperty
        public String getName() {
            return name;
        }

        public static final Map<Integer, ResourceType> dbValues = new HashMap<>();

        static {
            for (ResourceType resourceType : values()) {
                dbValues.put(resourceType.code, resourceType);
            }
        }

        @JsonCreator
        public static ResourceType forValue(Integer value) {
            return dbValues.get(value);
        }
    }

    public enum PossessorType {
        DEMO, TRAINING, CONSULTATION, TRAINING_REPORT, CONSULTATION_REPORT
    }
}
