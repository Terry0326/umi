package com.ugoodtech.umi.core.exception;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

public class UsernameAlreadyExistException extends UmiException {
    public UsernameAlreadyExistException() {
        super(ErrorCode.USERNAME_EXIST.getErrorCode(), ErrorCode.USERNAME_EXIST.getErrorMsg());
    }
}
