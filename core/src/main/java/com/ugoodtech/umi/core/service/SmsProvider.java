package com.ugoodtech.umi.core.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

public interface SmsProvider {
    boolean sendSms(String mobilePhone, String message);
}
