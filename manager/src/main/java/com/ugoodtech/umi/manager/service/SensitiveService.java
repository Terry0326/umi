package com.ugoodtech.umi.manager.service;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 16/3/17
 */

import com.ugoodtech.umi.core.domain.SensitiveWord;

public interface SensitiveService {


    Iterable<SensitiveWord> query(String param);


}
