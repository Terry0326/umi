package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "cities")
public class City implements Serializable {
    private Long id;
    private String countryName;
    private String countryCode;
    private String stateName;
    private String stateCode;
    private String cityName;
    private String cityCode;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getCityName() {
        if (cityName != null) {
            return cityName;
        } else if (stateName != null) {
            return stateName;
        }
        return null;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        if (cityCode != null) {
            return cityCode;
        } else if (stateCode != null) {
            return stateCode;
        }
        return null;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    @Override
    public String toString() {
        return "insert into locations(country_name,country_code,state_name,state_code,city_name,city_code) values(" +
                "'" + countryName + '\'' +
                ", '" + countryCode + '\'' +
                ", '" + stateName + '\'' +
                ", '" + stateCode + '\'' +
                ", '" + cityName + '\'' +
                ", '" + cityCode + '\'' +
                ')';
    }
}
