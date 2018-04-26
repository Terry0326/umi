package com.ugoodtech.umi.manager.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 16/3/17
 */

import com.ugoodtech.umi.core.domain.Role;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.exception.UsernameAlreadyExistException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    void createClientUser(User user) throws UsernameAlreadyExistException;

    void modifyClientUser(User user) throws UsernameAlreadyExistException;

    Page<User> queryClientUser(String key, Boolean enabled, String countries,Role role, Pageable pageable);

    boolean enableClientUser(Long clientUserId);

    boolean disableClientUser(Long clientUserId);

    boolean deleteClientUser(Long clientUserId);

    boolean deleteClientUser(List<Long> clientUserIdList);

    Page<User> getUsers(String param,Pageable pageable);

    Page<User> getDisableUsers(String param,Pageable pageable);
}
