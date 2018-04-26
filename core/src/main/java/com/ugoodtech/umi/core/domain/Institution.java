package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "institutions")
public class Institution extends BaseEntity {
    private String name;
}
