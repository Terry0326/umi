package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 16/2/3
 */

import javax.persistence.*;
import java.io.Serializable;
@Entity
@Table(name = "client_version")
public class ClientVersion extends BaseEntity implements Serializable {
    private String appId;
    private String description;
    private String versionName;
    private Integer versionNum;
    private Platform platform;
    private String downloadUrl;

    @Column(name = "app_id", length = 50)
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "version_name", length = 50)
    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @Column(name = "version_num")
    public Integer getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(Integer versionNum) {
        this.versionNum = versionNum;
    }

    @Enumerated(EnumType.ORDINAL)
    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    @Column(name = "download_url", length = 100)
    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public enum Platform {
        iOS, Android
    }

}
