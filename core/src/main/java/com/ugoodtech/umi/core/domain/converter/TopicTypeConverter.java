package com.ugoodtech.umi.core.domain.converter;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.Topic;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.beans.PropertyEditorSupport;

public class TopicTypeConverter extends PropertyEditorSupport implements AttributeConverter<Topic.TopicType, Integer> {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (!StringUtils.isNumeric(text)) {
            setValue(null);
        } else {
            Topic.TopicType type = convertToEntityAttribute(Integer.valueOf(text));
            setValue(type);
        }
    }

    @Override
    public Integer convertToDatabaseColumn(Topic.TopicType attribute) {
        return attribute.getCode();
    }

    @Override
    public Topic.TopicType convertToEntityAttribute(Integer dbData) {
        return Topic.TopicType.forValue(dbData);
    }
}
