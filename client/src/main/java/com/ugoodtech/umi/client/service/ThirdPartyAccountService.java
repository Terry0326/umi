package com.ugoodtech.umi.client.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */


import com.ugoodtech.umi.core.domain.ThirdPartyAccount;
import com.ugoodtech.umi.core.domain.User;

public interface ThirdPartyAccountService {


    ThirdPartyAccount getThirdPartyAccount(String name, ThirdPartyAccount.ThirdParty thirdParty,
                                           boolean createIfNotExist);

    void addPushRegistrationID(User clientUser, String registrationId, ThirdPartyAccount.AccountSource accountSource);

    boolean createThirdPartyAccount(User user,String openId, String accessToken, ThirdPartyAccount.ThirdParty thirdParty);

    ThirdPartyAccount getThirdPartyAccount(String openId, ThirdPartyAccount.ThirdParty thirdParty);

    void checkThirdPartyAccount(String openId, String accessToken, ThirdPartyAccount.ThirdParty thirdParty, User user);

    void removePushRegistrationID(User user);
}
