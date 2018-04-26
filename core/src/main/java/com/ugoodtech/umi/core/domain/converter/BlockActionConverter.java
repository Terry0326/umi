package com.ugoodtech.umi.core.domain.converter;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.BlockUser;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.beans.PropertyEditorSupport;

public class BlockActionConverter extends PropertyEditorSupport implements AttributeConverter<BlockUser.BlockAction, Integer> {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (!StringUtils.isNumeric(text)) {
            setValue(null);
        } else {
            BlockUser.BlockAction action = convertToEntityAttribute(Integer.valueOf(text));
            setValue(action);
        }
    }

    @Override
    public Integer convertToDatabaseColumn(BlockUser.BlockAction attribute) {
        return attribute.getCode();
    }

    @Override
    public BlockUser.BlockAction convertToEntityAttribute(Integer dbData) {
        return BlockUser.BlockAction.forValue(dbData);
    }
}
