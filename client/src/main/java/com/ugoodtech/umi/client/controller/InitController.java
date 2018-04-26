package com.ugoodtech.umi.client.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import ch.hsr.geohash.GeoHash;
import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.core.domain.Address;
import com.ugoodtech.umi.core.domain.QUser;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/init")
public class InitController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/geohash")
    public JsonResponse initGeoHash() {
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.deleted.isFalse());
        Iterable<User> users = userRepository.findAll(builder);
        for (User user : users) {
            Address address = user.getAddress();
            if (address != null && address.getLat() != null && address.getLng() != null) {
                GeoHash geoHash = GeoHash.withCharacterPrecision(address.getLat(), address.getLng(), 5);
                user.setGeoHash(geoHash.toBase32());
                userRepository.save(user);
                logger.debug("init user " + user.getUsername() + " with geohash:" + geoHash.toBase32());
            }
        }
        return JsonResponse.successResponse();
    }

    Logger logger = LoggerFactory.getLogger(InitController.class);
}
