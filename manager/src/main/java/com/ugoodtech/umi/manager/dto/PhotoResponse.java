package com.ugoodtech.umi.manager.dto;


import com.ugoodtech.umi.core.domain.Resource;

/**
 * Copyright © 2013 All Rights Reserved, Ugood Technology, Inc.
 */
public class PhotoResponse {
    private Long photoId;
    private String category;
    private String creationTime;
    private String uploadUser;
    private String description;

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(String uploadUser) {
        this.uploadUser = uploadUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static PhotoResponse turnPhotoToResponse(Resource photo) {
        if (null != photo) {

            PhotoResponse photoResponse = new PhotoResponse();
            photoResponse.setPhotoId(photo.getId());

            photoResponse.setDescription(photo.getDescription());
            return photoResponse;
        } else {
            return null;
        }
    }
}
