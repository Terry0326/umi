package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Gender {
    male(0, "男"), female(1, "女"), unknown(2, "未知");
    @JsonProperty
    private Integer flag;
    @JsonProperty
    private String name;

    Gender(Integer flag, String name) {
        this.flag = flag;
        this.name = name;
    }

    public Integer getFlag() {
        return flag;
    }

    public String getName() {
        return name;
    }

    public static final Map<Integer, Gender> dbValues = new HashMap<>();

    static {
        for (Gender gender : values()) {
            dbValues.put(gender.flag, gender);
        }
    }

    @JsonCreator
    public static Gender forValue(Integer value) {
        return dbValues.get(value);
    }
}
