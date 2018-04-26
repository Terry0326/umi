package com.ugoodtech.umi.client.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.core.domain.City;
import com.ugoodtech.umi.core.domain.QCity;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.repository.CityRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api("城市选择接口")
@RestController
@RequestMapping("/cities")
public class CityController {

    @Autowired
    private CityRepository cityRepository;

    @ApiOperation("查询城市")
    @RequestMapping(method = RequestMethod.GET)
    public JsonResponse<List<City>> getCities(@ApiParam("国家名字") @RequestParam String countryName,
                                              @ApiParam("查询城市关键字,不传查全部") @RequestParam(required = false) String qCityKey) {
        QCity qCity = QCity.city;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCity.countryName.eq(countryName));
        if (!StringUtils.isEmpty(qCityKey)) {
            builder.and(new BooleanBuilder()
                    .or(qCity.stateName.like("%" + qCityKey + "%")
                            .or(qCity.cityName.like("%" + qCityKey + "%"))));
        }
        List<City> cities = (List<City>) cityRepository.findAll(builder, new Sort(Sort.Direction.ASC, "id"));
        return JsonResponse.successResponseWithData(cities);
    }
}
