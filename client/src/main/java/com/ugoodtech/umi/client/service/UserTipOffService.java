package com.ugoodtech.umi.client.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.exception.UmiException;

public interface UserTipOffService {
    void tipOff(User user, Long targetUserId, String reason) throws UmiException;
}
