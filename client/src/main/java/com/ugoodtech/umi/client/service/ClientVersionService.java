package com.ugoodtech.umi.client.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 16/2/3
 */


import com.ugoodtech.umi.core.domain.ClientVersion;

public interface ClientVersionService {
    void createClientVersion(ClientVersion clientVersion);

    void updateClientVersion(ClientVersion clientVersion);

    ClientVersion getUpgradeVersionBasedOnVersionName(String oldVersionName, ClientVersion.Platform platform);

    ClientVersion getUpgradeBasedOnVersionNum(Integer versionNum, ClientVersion.Platform platform);
}
