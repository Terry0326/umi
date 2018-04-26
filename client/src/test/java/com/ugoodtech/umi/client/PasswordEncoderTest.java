package com.ugoodtech.umi.client;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderTest {
    @Test
    public void testEncode(){
        PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        String encoded=passwordEncoder.encode("111111");
        System.out.println("encoded = " + encoded);
        assert true;
       boolean matched= passwordEncoder.matches("111111",encoded);
        System.out.println("matched = " + matched);
    }
}
