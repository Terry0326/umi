package com.ugoodtech.umi.client.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 15/12/17
 */

import com.ugoodtech.umi.client.dto.UserDetailDto;
import com.ugoodtech.umi.client.dto.UserDto;
import com.ugoodtech.umi.core.domain.Address;
import com.ugoodtech.umi.core.domain.Gender;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.exception.UmiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;

public interface UserManager {

    /**
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    User getUserByUsername(String username) throws UsernameNotFoundException;

    User getUser(Long usrId);

    void resetPassword(String phoneNumber, String newPwd);

    void updatePassword(String phoneNumber, String oldPwd, String newPwd) throws UmiException;

    void updatePassword(Long userId, String oldPwd, String newPwd) throws UmiException;

    User createUser(String dialingCode, String username, String password, User.RegistrationWay phoneNumberRegister);

    UserDetailDto updateUserDetail(User user, String nickname, Gender gender, Address address, String signature, String avatar, boolean completed) throws UmiException;

    boolean isNicknameExist(String nickname, Long exceptUserId);

    UserDetailDto getUserDetail(Long requestUserId, Long targetUserId) throws UmiException;

    UserDetailDto getUserDetail(Long requestUserId, User targetUser) throws UmiException;

    Page<User> query(User user, String qKey, Pageable pageable);

    public User queryUser(User user,String userName);

    UserDetailDto getUserDetail(Long id) throws UmiException;

    void configReceiveNotification(Long userId, boolean receive);

    Collection<Long> getNearByUserIds(Long userId, Double lat, Double lng, Integer distance) throws UmiException;
}
