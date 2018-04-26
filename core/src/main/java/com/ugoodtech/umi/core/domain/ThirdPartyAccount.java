package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.converter.ThirdPartyConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "third_party_accounts")
public class ThirdPartyAccount extends BaseEntity {
    private User user;
    private ThirdParty thirdParty;
    private String accId;
    private String accName;
    private String token;
    private String extra;
    private AccountSource source;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "third_party")
    @Convert(converter = ThirdPartyConverter.class)
    public ThirdParty getThirdParty() {
        return thirdParty;
    }

    public void setThirdParty(ThirdParty thirdParty) {
        this.thirdParty = thirdParty;
    }

    @Column(name = "acc_id", length = 100)

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    @Column(name = "acc_name", length = 100)

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }


    @Column(length = 200)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Column(length = 200)
    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    public AccountSource getSource() {
        return source;
    }

    public void setSource(AccountSource source) {
        this.source = source;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum ThirdParty {
        J_PUSH(1, "极光推送"), J_MESSAGE(2, "极光聊天"), NET_EASE_IM(3, "网易云信"),
        RONG_YUN_IM(4, "融云IM"), QQ(5, "QQ"), WX(6, "WX");
        @JsonProperty
        private Integer code;
        @JsonProperty
        private String name;

        ThirdParty(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static final    Map<Integer, ThirdParty> dbValues = new HashMap<>();

        static {
            for (ThirdParty party : values()) {
                dbValues.put(party.code, party);
            }
        }

        @JsonCreator
        public static ThirdParty forValue(Integer value) {
            return dbValues.get(value);
        }
    }

    public enum AccountSource {
        iOS, Android, Web, Unkonwn;

        @JsonCreator
        public static AccountSource forValue(Integer value) {
            if (value >= 0 && value < AccountSource.values().length) {
                return AccountSource.values()[value];
            } else {
                return null;
            }
        }
    }
}
