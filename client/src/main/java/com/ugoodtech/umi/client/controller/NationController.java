package com.ugoodtech.umi.client.controller;


import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.service.NationService;
import com.ugoodtech.umi.core.domain.Nation;
import com.ugoodtech.umi.core.dto.LabelValue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright © 2013 All Rights Reserved, Ugood Technology, Inc.
 */
@Api("中国城市查询接口")
@Controller
@RequestMapping("/nation")
public class NationController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(NationController.class);

    @Autowired
    private NationService nationService;

    @RequestMapping(value = "/getProvinces", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse getProvinces(
            boolean addAll
    ) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("--------------getProvinces---------------");
        }
        Iterable<Nation> nations = nationService.findAllProvinces();
        List<LabelValue> labelValues = new ArrayList<>();


        if (null != nations) {
            for (Nation nation : nations) {
                LabelValue labelValue = new LabelValue(nation.getProvince(), nation.getCode() + "");
                labelValues.add(labelValue);

            }
        }
        return JsonResponse.successResponseWithData(labelValues);

    }

    @ApiOperation("获取城市列表")
    @RequestMapping(value = "/getCities", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse getCities(
            @ApiParam(value = "上级地区代码,不传则查全部", required = false) @RequestParam(required = false) String code
    ) throws Exception {
        Iterable<Nation> nations = nationService.findCitiesInProvince(code);
        List<LabelValue> labelValues = new ArrayList<>();
        if (null != nations) {
            for (Nation nation : nations) {
                LabelValue labelValue = new LabelValue(nation.getCity(), nation.getCode() + "");
                labelValues.add(labelValue);
            }
        }
        return JsonResponse.successResponseWithData(labelValues);

    }

    @RequestMapping(value = "/getDistricts", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse getDistricts(
            String code,
            @RequestParam(required = false) Boolean addAll
    ) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("--------------getDistricts---------------");
        }
        if (null == addAll) {
            addAll = false;
        }
        Iterable<Nation> nations = nationService.findDistrictsInCity(code);
        List<LabelValue> labelValues = new ArrayList<>();
        if (null != nations) {
            for (Nation nation : nations) {
                LabelValue labelValue = new LabelValue(nation.getDistrict(), nation.getCode() + "");
                labelValues.add(labelValue);
            }
        }
        return JsonResponse.successResponseWithData(labelValues);

    }
}
