package com.ugoodtech.umi.manager.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 16/3/17
 */


import com.ugoodtech.umi.core.domain.SystemUser;
import com.ugoodtech.umi.core.exception.UsernameAlreadyExistException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SystemUserService {

    void createSystemUser(SystemUser systemUser, String cityCode) throws UsernameAlreadyExistException;

    void updateSystemUser(SystemUser systemUser) throws UsernameAlreadyExistException;

    Page<SystemUser> querySystemUser(String param, Pageable pageable);

    Page<SystemUser> queryCustomerUser(Long customerId, Pageable pageable);

    void delete(Long userId);

    void delete(List<Long> userIds);

    boolean changePassword(SystemUser user, String password, String oldPassword);
}
