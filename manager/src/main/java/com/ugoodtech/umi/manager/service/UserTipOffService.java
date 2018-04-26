package com.ugoodtech.umi.manager.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.UserTipOff;
import com.ugoodtech.umi.core.domain.UserTipDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface UserTipOffService {

    Page<UserTipOff> getUserTipOff(Long tipUserId, String param, Boolean done, Date stDate, Date edDate, Pageable pageable);


    List<UserTipDto> getUserTipDto(Long tipUserId, String param, Boolean done, Date stDate, Date edDate, Pageable pageable);
    long gerTotal(Long tipUserId, String param, Boolean done, Date stDate, Date edDate);
}
