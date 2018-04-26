package com.ugoodtech.umi.core.exception;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

public class NoOperationPermission extends UmiException {
    public NoOperationPermission() {
        super(9500, "没有操作权限");
    }
}
