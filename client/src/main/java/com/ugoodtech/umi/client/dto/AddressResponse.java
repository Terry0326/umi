package com.ugoodtech.umi.client.dto;


import com.ugoodtech.umi.core.domain.Address;
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
    

    public static AddressResponse turnAddressToResponse(Address address) throws InvocationTargetException, IllegalAccessException {
        if(null!=address){
            AddressResponse addressResponse=new AddressResponse();
            BeanUtilEx.copyProperties(addressResponse, address);
            return addressResponse;
        }else {
            return null;
        }
    }

    public static Address turnResponseToAddress(AddressResponse addressResponse) throws ParseException, InvocationTargetException, IllegalAccessException {
        if(null!=addressResponse){
            Address address=new Address();
            BeanUtilEx.copyProperties(address,addressResponse);
            return address;
        }else {
            return new Address();
        }
    }
}
