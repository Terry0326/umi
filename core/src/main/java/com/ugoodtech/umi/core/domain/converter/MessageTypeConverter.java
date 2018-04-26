package com.ugoodtech.umi.core.domain.converter;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.Message;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.beans.PropertyEditorSupport;

public class MessageTypeConverter extends PropertyEditorSupport implements AttributeConverter<Message.MessageType, Integer> {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (!StringUtils.isNumeric(text)) {
            setValue(null);
        } else {
            Message.MessageType messageType = convertToEntityAttribute(Integer.valueOf(text));
            setValue(messageType);
        }
    }

    @Override
    public Integer convertToDatabaseColumn(Message.MessageType attribute) {
        return attribute.getCode();
    }

    @Override
    public Message.MessageType convertToEntityAttribute(Integer dbData) {
        return Message.MessageType.forValue(dbData);
    }
}
