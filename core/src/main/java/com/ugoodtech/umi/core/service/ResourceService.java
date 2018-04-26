package com.ugoodtech.umi.core.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */


import com.ugoodtech.umi.core.domain.Resource;
import com.ugoodtech.umi.core.exception.ResourceNotFoundException;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface ResourceService {

    Resource save(String filename, String contentType, byte[] bytes) throws IOException;

    Resource save(Long possessorId, Resource.PossessorType possessorType, String filename, String contentType, byte[] bytes) throws IOException;

    void delete(Long resourceId);

    void readResourceBody(Long attachmentId, OutputStream os) throws IOException;

    Resource getResource(Long resourceId) throws IOException;

    byte[] getPhotoThumbnailBytes(Long photoId, int width, int height) throws IOException, ResourceNotFoundException;

    byte[] getResourceBytes(Long resourceId);

    void zipAndOutput(Long[] fileId, ServletOutputStream outputStream) throws IOException;

    void delete(Long[] fileIds);

}
