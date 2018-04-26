package com.ugoodtech.umi.core.exception;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

public class ResourceNotFoundException extends UmiException {
    public ResourceNotFoundException() {
        super(ErrorCode.RESOURCE_NOT_FOUND.getErrorCode(), ErrorCode.RESOURCE_NOT_FOUND.getErrorMsg());
    }
}
