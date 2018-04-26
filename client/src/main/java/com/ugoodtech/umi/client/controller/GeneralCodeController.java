package com.ugoodtech.umi.client.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.client.dto.IpAddress;
import com.ugoodtech.umi.core.domain.Country;
import com.ugoodtech.umi.core.domain.GeneralCode;
import com.ugoodtech.umi.core.domain.QCountry;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.repository.CountryRepository;
import com.ugoodtech.umi.core.repository.GeneralCodeRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "代码表操作api")
@RestController
@RequestMapping("/codes")
public class GeneralCodeController {
    @Autowired
    private GeneralCodeRepository generalCodeRepository;

    @Autowired
    private CountryRepository countryRepository;

    @ApiOperation("根据ip获取地址,不需要传递ip,ip由后台来判断")
    @RequestMapping(value = "addressByIp", method = RequestMethod.GET)
    public JsonResponse<IpAddress> getAddressByIp(@ApiIgnore HttpServletRequest request) {
        String ip = getClientIpAddr(request);
        logger.debug("the ip is :" + ip);
        if (ip == null) {
            return JsonResponse.errorResponse(1000, "无法获取ip");
        }
        try {
            IpAddress ipAddress = getAddressByTaoBaoAPI(ip);
            if (ipAddress == null) {
                return JsonResponse.errorResponse(1000, "无法获取地址");
            } else {
                return JsonResponse.successResponseWithData(ipAddress);
            }
        } catch (IOException e) {
            logger.warn("无法根据ip获取地址", e);
            return JsonResponse.errorResponse(1000, "无法获取地址");
        }
    }

    public static IpAddress getAddressByTaoBaoAPI(String ip) throws IOException {
        String url = "http://ip.taobao.com/service/getIpInfo.php?ip=" + ip;
        HttpClient client = HttpClientBuilder.create().build();

        HttpGet request = new HttpGet(url);
        request.setHeader("Accept", "Accept text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
        request.setHeader("Accept-Encoding", "gzip, deflate");
        request.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
        request.setHeader("Connection", "keep-alive");
        request.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");

        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            String strResult = EntityUtils.toString(response.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readValue(strResult, JsonNode.class);
            if (jsonNode.get("code").asInt() == 0) {
                return mapper.readValue(jsonNode.get("data").toString(), IpAddress.class);
            }
        }
        return null;
    }

    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED");
        }
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_VIA");
        }
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @ApiOperation("根据类别查询代码表")
    @RequestMapping(method = RequestMethod.GET)
    public JsonResponse<List<GeneralCode>> findGeneralCodes(@ApiParam("代码类别") @RequestParam Integer category) {
        List<GeneralCode> generalCodes = generalCodeRepository.findByCategory(category);
        if (generalCodes != null) {
            return JsonResponse.successResponseWithData(generalCodes);
        } else {
            return JsonResponse.errorResponseWithError("失败", "没有找到代码数据");
        }
    }

    @ApiOperation("查询国家列表")
    @RequestMapping(value = "/countries", method = RequestMethod.GET)
    public JsonResponse<Iterable<Country>> findCountries(@ApiParam("搜索关键字,可以不传搜索全部")
                                                         @RequestParam(required = false) String qKey) {
        QCountry qCountry = QCountry.country;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCountry.dialingCode.isNotNull());
        if (qKey != null) {
            builder.and(qCountry.chineseName.like("%" + qKey + "%"));
        }
        Iterable<Country> countries = countryRepository.findAll(builder);
        List<Country> countrys =  new ArrayList<>();
        for (Country country:countries) {
            if(!country.getChineseName().equals("香港")&&!country.getChineseName().equals("台湾")){
                countrys.add(country);
            }
        }
        return JsonResponse.successResponseWithData(countries);
    }

    @ApiOperation("根据首字母排序的国家字典")
    @RequestMapping(value = "/countriesDict", method = RequestMethod.GET)
    public JsonResponse<Map<String, List<Country>>> findCountriesDict(@ApiParam("搜索关键字,可以不传搜索全部")
                                                                      @RequestParam(required = false) String qKey) {
        QCountry qCountry = QCountry.country;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCountry.dialingCode.isNotNull());
        if (qKey != null) {
            builder.and(qCountry.chineseName.like("%" + qKey + "%"));
        }
        Iterable<Country> countries = countryRepository.findAll(builder, new Sort(Sort.Direction.ASC, "englishFullName"));
        Map<String, List<Country>> countryMap = new HashMap<>();
        for (Country country : countries) {
            if(!country.getChineseName().equals("香港")&&!country.getChineseName().equals("台湾")) {
                String englishName = country.getEnglishName();
                String firstLetter = englishName.substring(0, 1);
                List<Country> countryList;
                if (countryMap.containsKey(firstLetter)) {
                    countryList = countryMap.get(firstLetter);
                } else {
                    countryList = new ArrayList<>();
                    countryMap.put(firstLetter, countryList);
                }
                countryList.add(country);
            }
        }
        return JsonResponse.successResponseWithData(countryMap);
    }

    Logger logger = LoggerFactory.getLogger(GeneralCodeController.class);

}
