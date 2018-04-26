package com.ugoodtech.umi.core.domain.serializer;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ugoodtech.umi.core.Constants;
import com.ugoodtech.umi.core.domain.SystemUser;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class SystemUserSerializer extends JsonSerializer<SystemUser> {
    @Override
    public void serialize(SystemUser systemUser, JsonGenerator generator, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        generator.writeStartObject();
        generator.writeFieldName("userId");
        generator.writeNumber(systemUser.getId());
        generator.writeFieldName("username");
        generator.writeString(systemUser.getUsername());
        if (null != systemUser.getCreationTime()) {
            generator.writeFieldName("creationTime");
            generator.writeNumber(systemUser.getCreationTime().getTime());
        }
        generator.writeFieldName("remarks");
        generator.writeString(systemUser.getDescription());
        generator.writeFieldName("realName");
        String realName = systemUser.getRealName();
        generator.writeString(realName == null ? "" : realName);
        generator.writeFieldName("telephone");
        String telephone = systemUser.getTelephone();
        generator.writeString(telephone == null ? "" : telephone);
        //


        if (null != systemUser.getRoles()) {
            generator.writeFieldName("roles");
            generator.writeObject(systemUser.getRoles());
            generator.writeFieldName("roleName");
            generator.writeString(systemUser.getRoles().iterator().next().getName());
            generator.writeFieldName("roleNameStr");
            generator.writeString(systemUser.getRoles().iterator().next().getDescription());
        }



        //
        generator.writeFieldName("enabled");
        generator.writeBoolean(systemUser.isEnabled());
        generator.writeFieldName("enabledStr");
        generator.writeString(systemUser.isEnabled() ? "<font color='green' >激活</font>" : "<font color='red' >禁用</font>");

        if(null!=systemUser.getCreator()){
            generator.writeFieldName("creatorName");
            generator.writeString(systemUser.getCreator().getRealName()+"");
        }


        if(null!=systemUser.getCreationTime()){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
            generator.writeFieldName("creationTime");
            generator.writeString(simpleDateFormat.format(systemUser.getCreationTime()));
        }


        generator.writeEndObject();
    }
}
