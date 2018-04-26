package com.ugoodtech.umi.client.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */


import java.util.List;

public class ManualLocation {
    public List<CountryItem> countryRegion;

    public List<CountryItem> getCountryRegion() {
        return countryRegion;
    }

    public void setCountryRegion(List<CountryItem> countryRegion) {
        this.countryRegion = countryRegion;
    }

}
