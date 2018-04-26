package com.ugoodtech.umi.core.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
* Unauthorized copying of this file, via any medium is strictly prohibited.
* Proprietary and confidential.
* Written by Stone Shaw.
*/

import com.ugoodtech.umi.core.domain.Nation;
import com.ugoodtech.umi.core.domain.QNation;
import com.ugoodtech.umi.core.repository.NationRepository;
import com.ugoodtech.umi.core.service.NationService;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class NationServiceImpl implements NationService {
    @Autowired
    private NationRepository nationRepository;


    @Override
    public Iterable<Nation> findAllProvinces(
    ) {
        QNation qNation = QNation.nation;
        BooleanBuilder builder = new BooleanBuilder();


        builder.and(qNation.id.ne(1L));
        builder.and(qNation.province.isNotNull()).and(qNation.province.isNotEmpty());
        Sort sort=new Sort(Sort.Direction.ASC,"province");
        Pageable pageable = new PageRequest(0, 1000,sort);
        return nationRepository.findAll(builder,pageable);

    }

    @Override
    public Iterable<Nation> findCitiesInProvince(
            String provinceCode) {
        QNation qNation = QNation.nation;
        BooleanBuilder builder = new BooleanBuilder();
        if (!StringUtils.isEmpty(provinceCode)) {
            builder.and(qNation.parent.code.eq(provinceCode));
        }
        Sort sort=new Sort(Sort.Direction.ASC,"province");
        Pageable pageable = new PageRequest(0, 1000,sort);
        builder.and(qNation.city.isNotNull()).and(qNation.city.isNotEmpty());
        return nationRepository.findAll(builder,pageable);
    }

    @Override
    public Iterable<Nation> findDistrictsInCity(
            String cityCode) {
        QNation qNation = QNation.nation;
        BooleanBuilder builder = new BooleanBuilder();
        if (!StringUtils.isEmpty(cityCode)) {
            builder.and(qNation.parent.code.eq(cityCode));
        }


        builder.and(qNation.district.isNotNull()).and(qNation.district.isNotEmpty());
        builder.and(qNation.district.ne("市辖区"));
        Sort sort=new Sort(Sort.Direction.ASC,"province");
        Pageable pageable = new PageRequest(0, 1000,sort);
        return nationRepository.findAll(builder,pageable);
    }

    @Override
    public Nation findByCode(String code) {
        QNation qNation = QNation.nation;
        BooleanBuilder builder = new BooleanBuilder();
        if (!StringUtils.isEmpty(code)) {
            builder.and(qNation.code.eq(code));
            builder.and(qNation.province.isEmpty());
        }
        Sort sort=new Sort(Sort.Direction.ASC,"province");
        Pageable pageable = new PageRequest(0, 1000,sort);
        Iterable<Nation> nations= nationRepository.findAll(builder,pageable);
        if(null!=nations){
            for(Nation nation:nations){
                return nation;
            }
        }
        return null;
    }
    @Override
    public Iterable<Nation> findByName(String name) {
        QNation qNation = QNation.nation;
        BooleanBuilder builder = new BooleanBuilder();
        if (!StringUtils.isEmpty(name)) {
            builder.and(qNation.district.eq(name));
            builder.and(qNation.province.isEmpty());
        }
        Iterable<Nation> nations = nationRepository.findAll(builder, new Sort(Sort.Direction.ASC, "code"));
        return nations;
    }
}
