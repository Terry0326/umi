package com.ugoodtech.umi.manager.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.ugoodtech.umi.core.domain.QUserTipOff;
import com.ugoodtech.umi.core.domain.UserTipOff;
import com.ugoodtech.umi.core.repository.UserTipOffRepository;
import com.ugoodtech.umi.core.domain.UserTipDto;
import com.ugoodtech.umi.manager.service.UserTipOffService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.math.BigInteger;
import java.util.*;

@Service
public class UserTipOffServiceImpl implements UserTipOffService{

    @Autowired
    private UserTipOffRepository userTipOffRepository;

    @Override
    public Page<UserTipOff> getUserTipOff(Long tipUserId, String param, Boolean done, Date stDate, Date edDate, Pageable pageable) {
        QUserTipOff qUserTipOff = QUserTipOff.userTipOff;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qUserTipOff.deleted.isFalse());
        if (!StringUtils.isEmpty(param)) {
            BooleanBuilder keyBuilder = new BooleanBuilder();
            keyBuilder.or(qUserTipOff.tipUser.username.like("%" + param + "%"));
            keyBuilder.or(qUserTipOff.tipUser.nickname.like("%" + param + "%"));
            keyBuilder.or(qUserTipOff.user.username.like("%" + param + "%"));
            keyBuilder.or(qUserTipOff.user.nickname.like("%" + param + "%"));
            keyBuilder.or(qUserTipOff.reason.like("%" + param + "%"));
            builder.and(keyBuilder);
        }
        if(null!=done){
            builder.and(qUserTipOff.done.eq(done));
        }
        if(null!=stDate){
            builder.and(qUserTipOff.creationTime.before(stDate).not());
        }
        if(null!=edDate){
            builder.and(qUserTipOff.creationTime.after(edDate).not());
        }

        Sort sort =  new Sort(Sort.Direction.DESC, "creationTime");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return userTipOffRepository.findAll(builder,pageable);
    }
    @Override
    public List<UserTipDto> getUserTipDto(Long tipUserId, String param, Boolean done, Date stDate, Date edDate, Pageable pageable) {

        System.out.println("newDate"+new Date());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String std="1990-01-01",edd=sdf.format(new Date())+" 23:59";
//        if (!StringUtils.isEmpty(param)) {
//            sb.append(" AND u.`username` LIKE '%"+param+"%' or u.`nickname` LIKE '%"+param+"%' or f.`reason` LIKE '%"+param+"%' ");
//        }
//        if(null!=done){
//            sb.append(" AND f.done="+done);
//        }
        if(null!=stDate){
            std=sdf.format(stDate);
        }
        if(null!=edDate){
            edd=sdf.format(edDate);
        }
        param= (null==param?"":param);
        done=(null==done?false:done);
         List array= userTipOffRepository.getUserTipDto(param,done,std,edd);
        List<UserTipDto> topicList2 = new ArrayList<UserTipDto>();
//
    System.out.println("topicList.length=="+topicList2.size()+"std==="+std+"param==="+param+"done==="+done);
        for (int i = 0; i < array.size(); i++) {
            UserTipDto tip = new UserTipDto();
            Object[] obj = (Object[])array.get(i);
            tip.setUserName(obj[0].toString());

            tip.setNickName(obj[1]!=null?obj[1].toString():" ");
            tip.setDone( (Boolean)obj[2]);
            tip.setDoneStr(obj[3]!=null?obj[3].toString():" ");
            tip.setCreationTime((Date) obj[4]);
            tip.setTipNum((BigInteger)obj[5]);
            BigInteger bi=(BigInteger)obj[6];
            tip.setId(bi.longValue());
            topicList2.add(tip);
        }
        return topicList2;
    }

    @Override
    public long gerTotal(Long tipUserId, String param, Boolean done, Date stDate, Date edDate) {
        System.out.println("newDate"+new Date());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String std="1990-01-01",edd=sdf.format(new Date())+" 23:59";
        if(null!=stDate){
            std=sdf.format(stDate);
        }
        if(null!=edDate){
            edd=sdf.format(edDate);
        }
        param= (null==param?"":param);
        done=(null==done?false:done);
        return userTipOffRepository.getTotal(param,done,std,edd);
    }


    Logger logger = LoggerFactory.getLogger(UserTipOffServiceImpl.class);
}
