package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 16/3/9
 */

import javax.persistence.*;
import java.io.Serializable;

/**
 * `id`, `code`, `province`, `city`, `district`, `parent`
 */
@Entity
@Table(name = "nation")
public class Nation implements Serializable {
    private Long id;
    private String code;
    private String province;
    private String city;
    private String district;
    private Nation parent;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(length = 100)
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Column(length = 100)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(length = 100)
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @ManyToOne
    @JoinColumn(name = "parent")
    public Nation getParent() {
        return parent;
    }

    public void setParent(Nation parent) {
        this.parent = parent;
    }
}
