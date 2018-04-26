package com.ugoodtech.umi.client;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ugoodtech.umi.client.dto.IpAddress;
import com.ugoodtech.umi.core.domain.City;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocListTest {
    public static void main(String[] args) throws IOException {
        readLocList();
      //  IpAddress ipAddress = getAddressByTaoBaoAPI("111.165.61.173");
      //  System.out.println(ipAddress.getCountry());
       // System.out.println(ipAddress.getCity());
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


    private static void testRegx() {
        String password = "12345a";
        final String PASSWORD_PATTERN = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,12}$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        boolean matches = matcher.matches();
        System.out.println("matches = " + matches);
    }

    private static void readLocList() throws IOException {
        String data = ReadJson("/LocList.json");
        Map map = new ObjectMapper().readValue(data, Map.class);
        List<Map> countries = (List<Map>) map.get("countryRegion");

        for (Map country : countries) {

            String countryName = (String) country.get("name");
            String countryCode = (String) country.get("code");
            Object o = country.get("state");
            if (o != null) {
                if (o instanceof List) {//state list
                    List states = (List) o;
                    for (Object state : states) {
                        if (state instanceof Map) {
//                            parseCities((Map) state, countryName, countryCode);
                            parseState((Map) state, countryCode, countryName);
                        } else {
                            System.out.println("state = " + state.getClass());
                        }
                    }
                } else if (o instanceof Map) {//only one state which contain city list
//                    parseCities((Map) o, countryName, countryCode);
                    parseState((Map) o, countryCode, countryName);
                } else {
                    System.out.println("o = " + o.getClass());

                }
            }
        }
    }

    private static void parseState(Map state, String countryCode, String countryName) {
        String stateName = (String) state.get("name");
        String stateCode = (String) state.get("code");
        if (stateName != null) {
            City location = new City();
            location.setCountryName(countryName);
            location.setCountryCode(countryCode);
            location.setStateName(stateName);
            location.setStateCode(stateCode);
            System.out.println(location);
        } else {
            List<Map> cityList = (List) state.get("city");
            for (Map city : cityList) {
                parseCity(countryName, countryCode, stateName, stateCode, city);
            }
        }

    }

    private static void parseCities(Map o, String countryName, String countryCode) {
        Map state = o;
        String stateName = (String) state.get("name");
        String stateCode = (String) state.get("code");
        Object cities = state.get("city");
        if (cities != null) {
            if (cities instanceof List) {
                List<Map> cityList = (List) cities;
                for (Map city : cityList) {
                    parseCity(countryName, countryCode, stateName, stateCode, city);
                }
            } else if (cities instanceof Map) {
                Map city = (Map) cities;
                parseCity(countryName, countryCode, stateName, stateCode, city);
            }
        } else {
            System.out.println("no cities ");
        }
    }

    private static void parseCity(String countryName, String countryCode, String stateName, String stateCode, Map city) {
        City location = new City();
        location.setCountryCode(countryCode);
        location.setCountryName(countryName);
        location.setStateCode(stateCode);
        location.setStateName(stateName);
        location.setCityName((String) city.get("name"));
        location.setCityCode((String) city.get("code"));
        System.out.println(location);
    }

    private static String ReadJson(String path) {
        //从给定位置获取文件
        InputStream stream = null;
        String data = null;
        try {
            stream = LocListTest.class.getResourceAsStream(path);
            data = IOUtils.toString(stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return data;
    }
}
