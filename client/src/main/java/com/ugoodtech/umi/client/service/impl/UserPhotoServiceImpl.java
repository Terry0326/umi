package com.ugoodtech.umi.client.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.client.service.UserPhotoService;
import com.ugoodtech.umi.core.domain.QUserPhoto;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.domain.UserPhoto;
import com.ugoodtech.umi.core.repository.UserPhotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserPhotoServiceImpl implements UserPhotoService {

    @Autowired
    private UserPhotoRepository userPhotoRepository;
    //
    private JdbcTemplate jdbcTemplate;
    Logger logger = LoggerFactory.getLogger(UserPhotoServiceImpl.class);
    @Autowired
    public void setDataSource(DataSource dataSource) {
        logger.debug("DATASOURCE = " + dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @Override
    public Map<String,String> createUserPhoto(User user, String path) {
      String []conList=path.split(",");
      Map<String,String> map=new HashMap<>();
        for (String con:conList) {
            UserPhoto userPhoto=new UserPhoto();
            userPhoto.setUser(user);
            userPhoto.setCreationTime(new Date());
            userPhoto= userPhotoRepository.save(userPhoto);
            userPhoto.setContent("u"+user.getId()+"-img"+userPhoto.getId());
            userPhotoRepository.save(userPhoto);
            map.put(userPhoto.getContent(),con);
        }
        return map;
    }

    @Override
    public void deleteUserPhoto(User user, Long photoId) {
        userPhotoRepository.delete(photoId);
    }

    @Override
    public Page<UserPhoto> getUserPhoto(Long userId, Pageable pageable) {
         QUserPhoto userPhoto = QUserPhoto.userPhoto;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(userPhoto.user.id.eq(userId));
        return userPhotoRepository.findAll(builder,pageable);
    }
}
