package com.ugoodtech.umi.core.domain.converter;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.Resource;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.beans.PropertyEditorSupport;

public class ResourceTypeConverter extends PropertyEditorSupport implements AttributeConverter<Resource.ResourceType, Integer> {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (!StringUtils.isNumeric(text)) {
            setValue(null);
        } else {
            Resource.ResourceType consultingStatus = convertToEntityAttribute(Integer.valueOf(text));
            setValue(consultingStatus);
        }
    }

    @Override
    public Integer convertToDatabaseColumn(Resource.ResourceType attribute) {
        return attribute.getCode();
    }

    @Override
    public Resource.ResourceType convertToEntityAttribute(Integer dbData) {
        return Resource.ResourceType.forValue(dbData);
    }
}
