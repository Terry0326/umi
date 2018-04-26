package com.ugoodtech.umi.client;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 15/11/21
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("umi_resources");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.
                requestMatcher(new OAuthRequestMatcher())
                .authorizeRequests()
                .antMatchers("/swagger-ui.html", "/sms/*", "/users/resetPwd","/users/loginByQQ").permitAll()
                .antMatchers("/users/detail", "/users/pushToken", "/topics/**", "/discovery/**",
                        "/chats/**", "/stsToken").authenticated();
        http.sessionManagement().maximumSessions(1).expiredUrl("/logout");
    }

    private static class OAuthRequestMatcher implements RequestMatcher {
        @Override
        public boolean matches(HttpServletRequest request) {
            String auth = request.getHeader("Authorization");
            return (auth != null && auth.startsWith("Bearer"));
        }
    }
}
