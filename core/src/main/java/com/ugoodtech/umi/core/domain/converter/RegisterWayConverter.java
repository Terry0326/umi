package com.ugoodtech.umi.core.domain.converter;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.User;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.beans.PropertyEditorSupport;

public class RegisterWayConverter extends PropertyEditorSupport implements AttributeConverter<User.RegistrationWay, Integer> {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (!StringUtils.isNumeric(text)) {
            setValue(null);
        } else {
            User.RegistrationWay way = convertToEntityAttribute(Integer.valueOf(text));
            setValue(way);
        }
    }

    @Override
    public Integer convertToDatabaseColumn(User.RegistrationWay attribute) {
        return attribute.getCode();
    }

    @Override
    public User.RegistrationWay convertToEntityAttribute(Integer dbData) {
        return User.RegistrationWay.forValue(dbData);
    }
}
