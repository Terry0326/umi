package com.ugoodtech.umi.core.repository;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 16/2/3
 */

import com.ugoodtech.umi.core.domain.ClientVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClientVersionRepository extends PagingAndSortingRepository<ClientVersion, Long> {

    Page<ClientVersion> getByPlatform(ClientVersion.Platform platform, Pageable pageable);

    Page<ClientVersion> getByPlatformAndVersionNumGreaterThan(ClientVersion.Platform platform, Integer versionNum, Pageable pageable);
}
