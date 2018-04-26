package com.ugoodtech.umi.manager.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.TopicTipOff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

public interface TopicTipOffService {

    Page<TopicTipOff> getTopicTipOff(String param, Boolean enabled, Date stDate, Date edDate, Pageable pageable);



}
