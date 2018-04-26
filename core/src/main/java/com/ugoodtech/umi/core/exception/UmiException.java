package com.ugoodtech.umi.core.exception;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

public class UmiException extends Exception {
    private Integer errorCode=  1000;

    public Integer getErrorCode() {
        return errorCode;
    }

    public UmiException(Integer errorCode, String message) {
        super(message);
    }
}
