package com.ugoodtech.umi.core.exception;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

public class ThirdPartyAccountAlreadyException extends UmiException {
    public ThirdPartyAccountAlreadyException(Integer errorCode, String message) {
        super(errorCode, message);
    }
}
