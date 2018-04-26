package com.ugoodtech.umi.client.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.client.service.ThirdPartyAccountService;
import com.ugoodtech.umi.core.domain.QThirdPartyAccount;
import com.ugoodtech.umi.core.domain.ThirdPartyAccount;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.repository.ThirdPartyAccountRepository;
import com.ugoodtech.umi.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ThirdPartyAccountServiceImpl implements ThirdPartyAccountService {
    @Autowired
    private UserRepository clientUserRepository;
    @Autowired
    private ThirdPartyAccountRepository thirdPartyAccountRepository;

    @Override 
    public ThirdPartyAccount getThirdPartyAccount(String name, ThirdPartyAccount.ThirdParty thirdParty
            , boolean createIfNotExist) {
        User clientUser = clientUserRepository.findByUsername(name);
        List<ThirdPartyAccount> thirdPartyAccounts = thirdPartyAccountRepository
                .findThirdPartyAccount(clientUser.getId(), thirdParty);
        return (thirdPartyAccounts.isEmpty() ? null : thirdPartyAccounts.get(0));
    }

    @Override
    public void addPushRegistrationID(User clientUser, String registrationId,
                                      ThirdPartyAccount.AccountSource accountSource) {
        removePushRegistrationID(clientUser);
        //
        ThirdPartyAccount thirdPartyAccount = new ThirdPartyAccount();
        thirdPartyAccount.setCreationTime(new Date());
        thirdPartyAccount.setDeleted(false);
        thirdPartyAccount.setAccId(registrationId);
        thirdPartyAccount.setAccName(clientUser.getUsername());
        thirdPartyAccount.setSource(accountSource);
        thirdPartyAccount.setThirdParty(ThirdPartyAccount.ThirdParty.J_PUSH);
        thirdPartyAccount.setToken(registrationId);
        thirdPartyAccount.setUser(clientUser);
        thirdPartyAccountRepository.save(thirdPartyAccount);
    }

    @Override
    public boolean createThirdPartyAccount(User user, String openId, String accessToken,
                                           ThirdPartyAccount.ThirdParty thirdParty) {
        ThirdPartyAccount thirdPartyAccount = getThirdPartyAccount(openId, thirdParty);
        if (thirdPartyAccount == null) {
            thirdPartyAccount = new ThirdPartyAccount();
            thirdPartyAccount.setCreationTime(new Date());
            thirdPartyAccount.setDeleted(false);
            thirdPartyAccount.setAccId(openId);
            thirdPartyAccount.setSource(ThirdPartyAccount.AccountSource.Unkonwn);
            thirdPartyAccount.setThirdParty(thirdParty);
            thirdPartyAccount.setToken(accessToken);
            thirdPartyAccount.setUser(user);
            thirdPartyAccountRepository.save(thirdPartyAccount);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ThirdPartyAccount getThirdPartyAccount(String openId, ThirdPartyAccount.ThirdParty thirdParty) {
        BooleanBuilder builder = new BooleanBuilder();
        QThirdPartyAccount qThirdPartyAccount = QThirdPartyAccount.thirdPartyAccount;
        builder.and(qThirdPartyAccount.accId.eq(openId))
                .and(qThirdPartyAccount.thirdParty.eq(thirdParty))
                .and(qThirdPartyAccount.deleted.isFalse());
        return thirdPartyAccountRepository.findOne(builder);
    }

    @Override
    public void checkThirdPartyAccount(String openId, String accessToken, ThirdPartyAccount.ThirdParty thirdParty, User user) {
        ThirdPartyAccount thirdPartyAccount = getThirdPartyAccount(openId, thirdParty);
        if (thirdPartyAccount == null) {
            createThirdPartyAccount(user, openId, accessToken, thirdParty);
        } else {
            thirdPartyAccount.setToken(accessToken);
            thirdPartyAccountRepository.save(thirdPartyAccount);
        }
    }

    @Override
    public void removePushRegistrationID(User user) {
        QThirdPartyAccount qThirdPartyAccount = QThirdPartyAccount.thirdPartyAccount;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qThirdPartyAccount.user.id.eq(user.getId()));
        builder.and(qThirdPartyAccount.thirdParty.eq(ThirdPartyAccount.ThirdParty.J_PUSH));
        Iterable<ThirdPartyAccount> thirdPartyAccounts = thirdPartyAccountRepository.findAll(builder);
        //delete old registrationId
        for (ThirdPartyAccount thirdPartyAccount : thirdPartyAccounts) {
            thirdPartyAccountRepository.delete(thirdPartyAccount);
        }
    }

    private static Logger logger = LoggerFactory.getLogger(ThirdPartyAccountServiceImpl.class);
}
