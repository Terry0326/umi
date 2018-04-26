package com.ugoodtech.umi.manager;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 15/11/20
 */

import com.ugoodtech.umi.manager.security.LoginFailureHandler;
import com.ugoodtech.umi.manager.security.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

//import org.springframework.security.core.userdetails.UserDetailsService;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    @Qualifier("systemUserService")
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authorizeRequests()
                .antMatchers("/", "/login", "/main", "/getValidationCode**", "/scripts/**", "/images/**").permitAll()
//                .antMatchers("/need_authenticated_url/**").authenticated()
                .and()
                .formLogin().loginPage("/login")
                .successHandler(new LoginSuccessHandler())
                .failureHandler(new LoginFailureHandler())
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("remember-me")
                .permitAll()
                .and()
                .csrf()
                .disable()
                .headers().frameOptions().sameOrigin();
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
////        http.sessionManagement()
////                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
////                .and()
////                .authorizeRequests()
////                .antMatchers("/","/login", "/main", "/getValidationCode**", "/scripts/**", "/images/**").permitAll()
//////                .antMatchers("/need_authenticated_url/**").authenticated()
////                .and()
////                .formLogin().loginPage("/login")
////                .successHandler(new LoginSuccessHandler())
////                .failureHandler(new LoginFailureHandler())
////                .and()
////                .logout()
////                .logoutUrl("/logout")
////                .logoutSuccessUrl("/login")
////                .deleteCookies("remember-me")
////                .permitAll()
////                .and()
////                .csrf()
////                .disable()
////                .headers().frameOptions().sameOrigin();
//        http.sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                .and()
//                .requestMatchers()
//                .antMatchers("/so/**")
//                .and()
//                .authorizeRequests()
//                .antMatchers("/so/login","/so/getValidationCode**").permitAll()
////                .antMatchers("/welcome").permitAll()
//                .antMatchers("/so/**").authenticated()
////                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .loginProcessingUrl("/so/login")
//                .loginPage("/so/login")
//                .successHandler(new LoginSuccessHandler())
//                .failureHandler(new LoginFailureHandler())
//                .and()
//                .logout()
//                .logoutUrl("/so/logout")
//                .logoutSuccessUrl("/so/login")
//                .deleteCookies("remember-me")
//                .permitAll()
//                .and()
//                .csrf()
//                .disable()
//        ;
//    }


    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }


}
