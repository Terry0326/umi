package com.ugoodtech.umi.manager.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

import cn.jiguang.common.utils.StringUtils;
import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.core.domain.QSensitiveWord;
import com.ugoodtech.umi.core.domain.SensitiveWord;
import com.ugoodtech.umi.core.repository.SensitiveRepository;
import com.ugoodtech.umi.manager.service.SensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SensitiveServiceImpl implements SensitiveService {
    @Autowired
    private SensitiveRepository sensitiveRepository;


   

   


    @Override
    public Iterable<SensitiveWord> query(String param) {
        QSensitiveWord qSensitiveWord = QSensitiveWord.sensitiveWord;
        BooleanBuilder builder = new BooleanBuilder();
        if(!StringUtils.isEmpty(param)){
            builder.and(qSensitiveWord.name.like("%"+param+"%"));
        }
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        return sensitiveRepository.findAll(builder);
    }
}
