package com.ugoodtech.umi.core.exception;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

public enum ErrorCode {
    USERNAME_EXIST(1001, "用户名已经存在"),PASSWORD_MISMATCH(1002, "原密码不正确"),RESOURCE_NOT_FOUND(1003,"资源未找到");
    private Integer errorCode;
    private String errorMsg;

    ErrorCode(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
