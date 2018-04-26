package com.ugoodtech.umi.core.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */


import com.ugoodtech.umi.core.domain.Nation;

public interface NationService {
    Iterable<Nation> findAllProvinces();

    Iterable<Nation> findCitiesInProvince(String provinceCode);

    Iterable<Nation> findDistrictsInCity(String cityCode);

    Nation findByCode(String code);

    Iterable<Nation> findByName(String name);
}
