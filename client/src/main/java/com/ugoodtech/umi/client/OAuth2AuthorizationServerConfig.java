package com.ugoodtech.umi.client;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 15/11/20
 */

import com.ugoodtech.umi.client.oauth2.BadRequest2OkOAuth2ExceptionRenderer;
import com.ugoodtech.umi.client.oauth2.OKWebResponseExceptionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.http.converter.jaxb.JaxbOAuth2ExceptionMessageConverter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.OAuth2ExceptionRenderer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAuthorizationServer

public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private DataSource dataSource;

    @Override
    public void
    configure(AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {
        endpoints.tokenStore(tokenStore())
                .authenticationManager(this.authenticationManager)
                .exceptionTranslator(new OKWebResponseExceptionTranslator())
        ;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler());
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer
                                  clients) throws Exception {
        clients
                .jdbc(dataSource)
//                .withClient("kmd_client")
//                .secret("top_secret")
//                .authorizedGrantTypes("password","refresh_token")
//                .scopes("read", "write")
//                .accessTokenValiditySeconds(60 * 60 * 24 * 7)
//                .refreshTokenValiditySeconds(60 * 60 * 24 * 7)
//                .resourceIds("Kmd_Resources")
        ;
    }

    private AccessDeniedHandler accessDeniedHandler() {
        OAuth2AccessDeniedHandler accessDeniedHandler = new OAuth2AccessDeniedHandler();
        accessDeniedHandler.setExceptionRenderer(exceptionRenderer());
        return accessDeniedHandler;
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
        authenticationEntryPoint.setExceptionRenderer(exceptionRenderer());
        return authenticationEntryPoint;
    }

    private OAuth2ExceptionRenderer exceptionRenderer() {
        BadRequest2OkOAuth2ExceptionRenderer exceptionRenderer = new BadRequest2OkOAuth2ExceptionRenderer();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new JaxbOAuth2ExceptionMessageConverter());
        exceptionRenderer.setMessageConverters(converters);
        return exceptionRenderer;
    }


    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

}
