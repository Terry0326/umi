package com.ugoodtech.umi.core.domain.converter;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.Gender;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.beans.PropertyEditorSupport;

public class GenderConverter extends PropertyEditorSupport implements AttributeConverter<Gender, Integer> {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (!StringUtils.isNumeric(text)) {
            setValue(null);
        } else {
            Gender gender = convertToEntityAttribute(Integer.valueOf(text));
            setValue(gender);
        }
    }

    @Override
    public Integer convertToDatabaseColumn(Gender attribute) {
        return attribute.getFlag();
    }

    @Override
    public Gender convertToEntityAttribute(Integer dbData) {
        return Gender.forValue(dbData);
    }
}
