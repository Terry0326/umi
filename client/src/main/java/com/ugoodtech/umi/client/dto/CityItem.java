package com.ugoodtech.umi.client.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import java.util.List;

public class CityItem extends LocationItem {
    public List<LocationItem> region;

    public CityItem() {
    }

    public List<LocationItem> getRegion() {
        return region;
    }

    public void setRegion(List<LocationItem> region) {
        this.region = region;
    }
}
