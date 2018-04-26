package com.ugoodtech.umi.client.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.client.service.UserTipOffService;
import com.ugoodtech.umi.core.domain.QUserTipOff;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.domain.UserTipOff;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.UserRepository;
import com.ugoodtech.umi.core.repository.UserTipOffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserTipOffServiceImpl implements UserTipOffService {
    @Autowired
    private UserTipOffRepository userTipOffRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void tipOff(User user, Long targetUserId, String reason) throws UmiException {
        boolean lastNotProcessed = isLastTipOffNotProcessed(user.getId(), targetUserId);
        if (lastNotProcessed) {
            throw new UmiException(1000, "您上次对该用户的举报还未处理,不能继续举报");
        }
        UserTipOff tipOff = new UserTipOff();
        tipOff.setUser(user);
        User tipUser = userRepository.findOne(targetUserId);
        tipOff.setTipUser(tipUser);
        tipOff.setDone(false);
        tipOff.setReason(reason);
        tipOff.setDeleted(false);
        tipOff.setCreationTime(new Date());
        userTipOffRepository.save(tipOff);
    }

    private boolean isLastTipOffNotProcessed(Long userId, Long targetUserId) {
        QUserTipOff qUserTipOff = QUserTipOff.userTipOff;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserTipOff.deleted.isFalse());
        builder.and(qUserTipOff.user.id.eq(userId));
        builder.and(qUserTipOff.tipUser.id.eq(targetUserId));
        builder.and(qUserTipOff.done.isFalse());
        return userTipOffRepository.count(builder) > 0;
    }
}
