package com.ugoodtech.umi.manager.dto;

import com.ugoodtech.umi.core.domain.Address;
import com.ugoodtech.umi.core.repository.NationRepository;
import com.ugoodtech.umi.core.utils.BeanUtilEx;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

/**
 * Copyright © 2013 All Rights Reserved, Ugood Technology, Inc.
 */

public class AddressResponse {
    private String province;//省
    private String city;//地区
    private String district;//县
    private String address;
    private String provinceCode;
    private String cityCode;
    private String districtCode;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public static AddressResponse turnAddressToResponse(Address address) throws InvocationTargetException, IllegalAccessException {
        if (null != address) {
            AddressResponse addressResponse = new AddressResponse();
            BeanUtilEx.copyProperties(addressResponse, address);
            return addressResponse;
        } else {
            return null;
        }
    }

    public static Address turnResponseToAddress(AddressResponse addressResponse, NationRepository nationRepository) throws ParseException, InvocationTargetException, IllegalAccessException {
        if (null != addressResponse) {
            Address address = new Address();
            BeanUtilEx.copyProperties(address, addressResponse);
            if (null != address.getProvinceCode()) {
                address.setProvince(nationRepository.findProvinceByCode(address.getProvinceCode()));
            }
            if (null != address.getCityCode()) {
                address.setCity(nationRepository.findCityByCode(address.getCityCode()));
            }
            if (null != address.getDistrictCode()) {
                address.setDistrict(nationRepository.findDistrictByCode(address.getDistrictCode()));
            }
            return address;
        } else {
            return new Address();
        }
    }
}
