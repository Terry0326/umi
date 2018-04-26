package com.ugoodtech.umi.client.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * {
 * at =     (
 * {
 * nickname = "\U5927\U53d4";
 * place = 0;
 * username = 45;
 * }
 * );
 * msgtype = text;
 * text =     {
 * content = "\U5927\U5bb6\U597d";
 * };
 * }
 */
public class CommentContentBody implements Serializable {
    private String msgtype;
    private Map<String, String> text;
    private List<Map> at;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public Map<String, String> getText() {
        return text;
    }

    public void setText(Map<String, String> text) {
        this.text = text;
    }

    public List<Map> getAt() {
        return at;
    }

    public void setAt(List<Map> at) {
        this.at = at;
    }

    public String getContent() {
        return text.get("content");
    }
}
