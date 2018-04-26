package com.ugoodtech.umi.core.exception;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

public class PasswordMismatchException extends UmiException {
    public PasswordMismatchException() {
        super(ErrorCode.PASSWORD_MISMATCH.getErrorCode(), ErrorCode.PASSWORD_MISMATCH.getErrorMsg());
    }
}