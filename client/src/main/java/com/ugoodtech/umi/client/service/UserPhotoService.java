package com.ugoodtech.umi.client.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.Topic;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.domain.UserPhoto;
import com.ugoodtech.umi.core.exception.UmiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface UserPhotoService {
    Map<String,String> createUserPhoto(User user, String path);

    void deleteUserPhoto(User user,Long photoId);

    Page<UserPhoto> getUserPhoto(Long userId, Pageable pageable);
}
