package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 16/3/7
 */

import javax.persistence.*;
import java.io.Serializable;

/**
 * 代码表
 */
@Entity
@Table(name = "general_codes")
public class GeneralCode implements Serializable {
    private Long id;
    private String code;
    private String name;
    private GeneralCode parent;
    private Integer category;
    private boolean deleted;
    private Integer orderNum;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    public GeneralCode getParent() {
        return parent;
    }

    public void setParent(GeneralCode parent) {
        this.parent = parent;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name = "order_num")
    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

}
