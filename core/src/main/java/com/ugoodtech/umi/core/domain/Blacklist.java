package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "blacklist")
public class Blacklist extends BaseEntity {
    private User user;
    private String reason;
    private String endTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    @Column(name="end_Time")
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String  endTime) {
        this.endTime = endTime;
    }
}
