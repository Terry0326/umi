package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_tip_off")
public class UserTipOff extends BaseEntity {
    private User tipUser;
    private User user;
    private String reason;
    private boolean done=false;
    private String doneStr;

    @ManyToOne
    @JoinColumn(name = "tip_user_id")
    public User getTipUser() {
        return tipUser;
    }

    public void setTipUser(User tipUser) {
        this.tipUser = tipUser;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(length = 500)
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getDoneStr() {
        return doneStr;
    }

    public void setDoneStr(String doneStr) {
        this.doneStr = doneStr;
    }


}
