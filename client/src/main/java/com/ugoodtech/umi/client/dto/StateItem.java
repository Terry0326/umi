package com.ugoodtech.umi.client.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import java.util.List;

public class StateItem extends LocationItem {
    public List<CityItem> city;

    public StateItem() {
    }

    public List<CityItem> getCity() {
        return city;
    }

    public void setCity(List<CityItem> city) {
        this.city = city;
    }
}
