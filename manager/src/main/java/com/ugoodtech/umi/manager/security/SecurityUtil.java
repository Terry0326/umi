package com.ugoodtech.umi.manager.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Copyright © 2013 All Rights Reserved, Ugood Technology, LLC.
 * <p/>
 * User: Stone
 */
public class SecurityUtil {
    public static Object getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                return authentication.getPrincipal();
            } else if (authentication.getDetails() instanceof UserDetails) {
                return authentication.getDetails();
            } else {
                return null;
            }
        }
        return null;
    }
}
