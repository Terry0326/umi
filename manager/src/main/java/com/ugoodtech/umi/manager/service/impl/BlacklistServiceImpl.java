package com.ugoodtech.umi.manager.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.core.domain.Blacklist;
import com.ugoodtech.umi.core.domain.QBlacklist;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.repository.BlackListRepository;
import com.ugoodtech.umi.core.repository.UserRepository;
import com.ugoodtech.umi.manager.service.BlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BlacklistServiceImpl implements BlacklistService {
    @Autowired
    private BlackListRepository blackListRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void addToBlackList(Long userId,String reason,String endTime) {
        User one = userRepository.findOne(userId);
        one.setEnabled(false);

        userRepository.save(one);
        //
        Blacklist blacklist =blackListRepository.findBlacklistByUser(userId);
        if(null!=blacklist){
            blacklist.setUpdateTime(new Date());
        }else {
            blacklist=new Blacklist();
            blacklist.setUser(one);
            blacklist.setDeleted(false);
            blacklist.setCreationTime(new Date());
        }
        blacklist.setEndTime(endTime);
        blacklist.setReason(reason);
        blackListRepository.save(blacklist);
    }

    @Override
    public Blacklist getAddBlack(Long userId) {
        QBlacklist qBlacklist = QBlacklist.blacklist;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBlacklist.deleted.eq(false));
        builder.and(qBlacklist.user.id.eq(userId));
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        Pageable pageable = new PageRequest(0, 1, sort);
        Page<Blacklist> blacklists= blackListRepository.findAll(builder, pageable);
        if(blacklists.getTotalElements()>0){
            return blacklists.getContent().get(0);
        }else{
            return null;
        }
    }


}
