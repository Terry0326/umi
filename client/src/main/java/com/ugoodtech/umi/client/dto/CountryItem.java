package com.ugoodtech.umi.client.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import java.util.List;

public class CountryItem extends LocationItem {
    public List<StateItem> state;

    public CountryItem() {
    }

    public List<StateItem> getState() {
        return state;
    }

    public void setState(List<StateItem> state) {
        this.state = state;
    }
}
