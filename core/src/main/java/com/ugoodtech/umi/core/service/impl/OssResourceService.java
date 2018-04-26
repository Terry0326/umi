package com.ugoodtech.umi.core.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.aliyun.oss.OSSClient;
import com.ugoodtech.umi.core.domain.Resource;
import com.ugoodtech.umi.core.exception.ResourceNotFoundException;
import com.ugoodtech.umi.core.service.ResourceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class OssResourceService implements ResourceService {
    @Value("${oss.endpoint}")
    private String endpoint;
    @Value("${oss.accessKeyId}")
    private String accessKeyId;
    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${oss.bucketName}")
    private String bucketName;
    private OSSClient ossClient;

    @PostConstruct
    public void init() {
        ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
    }

    @Override
    public Resource save(String filename, String contentType, byte[] bytes) throws IOException {
        return null;
    }

    @Override
    public Resource save(Long possessorId, Resource.PossessorType possessorType, String filename, String contentType, byte[] bytes) throws IOException {
        return null;
    }

    @Override
    public void delete(Long resourceId) {

    }

    @Override
    public void readResourceBody(Long attachmentId, OutputStream os) throws IOException {

    }

    @Override
    public Resource getResource(Long resourceId) throws IOException {
        return null;
    }

    @Override
    public byte[] getPhotoThumbnailBytes(Long photoId, int width, int height) throws IOException, ResourceNotFoundException {
        return new byte[0];
    }

    @Override
    public byte[] getResourceBytes(Long resourceId) {
        return new byte[0];
    }

    @Override
    public void zipAndOutput(Long[] fileId, ServletOutputStream outputStream) throws IOException {

    }

    @Override
    public void delete(Long[] fileIds) {

    }
}
