package com.ugoodtech.umi.core.domain.converter;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.ThirdPartyAccount;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.beans.PropertyEditorSupport;

public class ThirdPartyConverter extends PropertyEditorSupport implements AttributeConverter<ThirdPartyAccount.ThirdParty, Integer> {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (!StringUtils.isNumeric(text)) {
            setValue(null);
        } else {
            ThirdPartyAccount.ThirdParty thirdParty = convertToEntityAttribute(Integer.valueOf(text));
            setValue(thirdParty);
        }
    }

    @Override
    public Integer convertToDatabaseColumn(ThirdPartyAccount.ThirdParty attribute) {
        return attribute.getCode();
    }

    @Override
    public ThirdPartyAccount.ThirdParty convertToEntityAttribute(Integer dbData) {
        return ThirdPartyAccount.ThirdParty.forValue(dbData);
    }
}
