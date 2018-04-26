package com.ugoodtech.umi.client.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 16/2/3
 */

import com.ugoodtech.umi.client.service.ClientVersionService;
import com.ugoodtech.umi.core.domain.ClientVersion;
import com.ugoodtech.umi.core.repository.ClientVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ClientVersionServiceImpl implements ClientVersionService {
    @Autowired
    private ClientVersionRepository versionRepository;

    @Override
    public void createClientVersion(ClientVersion clientVersion) {
        clientVersion.setCreationTime(new Date());
        versionRepository.save(clientVersion);
    }

    @Override
    public void updateClientVersion(ClientVersion clientVersion) {
        versionRepository.save(clientVersion);
    }

    @Override
    public ClientVersion getUpgradeVersionBasedOnVersionName(String oldVersionName, ClientVersion.Platform platform) {
        PageRequest pageRequest = new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "creationTime"));
        Page<ClientVersion> versionPage = versionRepository.getByPlatform(platform, pageRequest);
        if (versionPage.getContent().size() > 0) {
            ClientVersion latestVersion = versionPage.getContent().get(0);
            if (compareVersionNames(oldVersionName, latestVersion.getVersionName()) < 0) {
                return latestVersion;
            }
        }
        return null;
    }

    @Override
    public ClientVersion getUpgradeBasedOnVersionNum(Integer versionNum, ClientVersion.Platform platform) {
        PageRequest pageRequest = new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "creationTime"));
        Page<ClientVersion> versionPage = versionRepository.getByPlatformAndVersionNumGreaterThan(platform, versionNum, pageRequest);
        if (versionPage.getContent().size() > 0) {
            return versionPage.getContent().get(0);

        }
        return null;
    }

    public static int compareVersionNames(String oldVersionName, String newVersionName) {
        int res = 0;

        String[] oldNumbers = oldVersionName.split("\\.");
        String[] newNumbers = newVersionName.split("\\.");

        // To avoid IndexOutOfBounds
        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i++) {
            int oldVersionPart = Integer.valueOf(oldNumbers[i]);
            int newVersionPart = Integer.valueOf(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = -1;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = 1;
                break;
            }
        }

        // If versions are the same so far, but they have different length...
        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = (oldNumbers.length > newNumbers.length) ? 1 : -1;
        }

        return res;
    }


}
