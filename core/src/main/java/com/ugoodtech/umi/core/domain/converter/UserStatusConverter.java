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

public class UserStatusConverter extends PropertyEditorSupport implements AttributeConverter<User.UserStatus, Integer> {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (!StringUtils.isNumeric(text)) {
            setValue(null);
        } else {
            User.UserStatus status = convertToEntityAttribute(Integer.valueOf(text));
            setValue(status);
        }
    }

    @Override
    public Integer convertToDatabaseColumn(User.UserStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public User.UserStatus convertToEntityAttribute(Integer dbData) {
        return User.UserStatus.forValue(dbData);
    }
}
