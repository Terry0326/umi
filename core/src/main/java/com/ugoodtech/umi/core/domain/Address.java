package com.ugoodtech.umi.core.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.ugoodtech.umi.core.domain.view.View;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * This class is used to represent an address with address,
 * city, province and postal-code information.
 */
@ApiModel
@Embeddable
public class Address implements Serializable {
    @ApiModelProperty("纬度")
    private Double lat;
    @ApiModelProperty("经度")
    private Double lng;
    @JsonView(View.Summary.class)
    @ApiModelProperty("国家")
    private String country;//省
    @ApiModelProperty("国家代码")
    private String countryCode;//省
    @JsonView(View.Summary.class)
    @ApiModelProperty("省")
    private String province;//省
    @JsonIgnore
    private String provinceCode;
    @ApiModelProperty("地区")
    @JsonView(View.Summary.class)
    private String city;//地区
    @JsonIgnore
    private String cityCode;
    @ApiModelProperty("县级")
    @JsonView(View.Summary.class)
    private String district;//县
    @JsonIgnore
    private String districtCode;

    @JsonView(View.Summary.class)
    @ApiModelProperty("详细地址")
    private String address;

    public Address() {
        this.country = "";
        this.city = "";
    }
    public Address(String address) {
        this.address = address;
    }

    @Column(length = 100)
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name = "country_code", length = 100)
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Column(length = 100)
    public String getProvince() {
        return province;
    }

    @Column(length = 100)
    public String getCity() {
        return city;
    }

    @Column(length = 150)
    public String getAddress() {
        return address;
    }

    @Column(length = 50)
    public String getDistrict() {
        return district;
    }

    @Column(name = "province_code", length = 10)
    public String getProvinceCode() {
        return provinceCode;
    }

    @Column(name = "city_code", length = 10)
    public String getCityCode() {
        return cityCode;
    }

    @Column(name = "district_code", length = 10)
    public String getDistrictCode() {
        return districtCode;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    @Column
    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    @Column
    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    /**
     * Overridden equals method for object comparison. Compares based on hashCode.
     *
     * @param o Object to compare
     * @return true/false based on hashCode
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Address)) {
            return false;
        }

        final Address address1 = (Address) o;

        return this.hashCode() == address1.hashCode();
    }

    /**
     * Overridden hashCode method - compares on address, city, province, country and postal code.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        int result;
        result = (address != null ? address.hashCode() : 0);
        result = 29 * result + (district != null ? district.hashCode() : 0);
        result = 29 * result + (city != null ? city.hashCode() : 0);
        result = 29 * result + (province != null ? province.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {


        return super.toString();
    }
}
