package com.ugoodtech.umi.client.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 15/12/17
 *
 * Provides {@link PasswordTokenManager} functionality generating and persisting
 * random tokens to the db as an extra security check.
 *
 * You will need to create a db table with the following structure:
 *
 * <pre>
 * <code>
 * create table password_reset_token (
 *     username varchar(50) NOT NULL,
 *     token varchar(255) NOT NULL,
 *     expiration_time timestamp NOT NULL,
 *     PRIMARY KEY (username, token)
 * )
 * </code>
 * </pre>
 *
 * and configure this alternative PasswordTokenManager in the spring
 * BeanFactory.
 *
 */


import com.ugoodtech.umi.client.service.PasswordTokenManager;
import com.ugoodtech.umi.core.domain.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Date;

@Component
public class PasswordTokenManagerImpl implements PasswordTokenManager {
    private JdbcTemplate jdbcTemplate;

    private String deleteTokenSql = "delete from password_reset_token where username=?";
    private String insertTokenSql = "insert into password_reset_token (username, token, expiration_time) values (?, ?, ?)";
    private String selectTokenSql = "select count(token) from password_reset_token where username=? and token=? and expiration_time > NOW()";

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setDeleteTokenSql(String deleteTokenSql) {
        this.deleteTokenSql = deleteTokenSql;
    }

    public void setInsertTokenSql(String insertTokenSql) {
        this.insertTokenSql = insertTokenSql;
    }

    public void setSelectTokenSql(String selectTokenSql) {
        this.selectTokenSql = selectTokenSql;
    }

    @Override
    public String generateRecoveryToken(final User user) {
        String token = RandomStringUtils.randomAlphanumeric(16);
        persistToken(user, token);
        return token;
    }

    @Override
    public boolean isRecoveryTokenValid(final User user, final String token) {
        return isRecoveryTokenPersisted(user, token);
    }

    @Override
    public void invalidateRecoveryToken(User user, String token) {
        jdbcTemplate.update(deleteTokenSql, user.getUsername());
    }

    protected void persistToken(User user, String token) {
        jdbcTemplate.update(deleteTokenSql, user.getUsername());
        jdbcTemplate.update(insertTokenSql, user.getUsername(), token, getExpirationTime());
    }

    protected boolean isRecoveryTokenPersisted(final User user, final String token) {
        Number count = jdbcTemplate.queryForObject(
                selectTokenSql,
                new Object[]{user.getUsername(), token}, Integer.class);
        return count != null && count.intValue() == 1;
    }

    /**
     * Return tokens expiration time, now + 1 day.
     *
     * @return
     */
    private Date getExpirationTime() {
        return DateUtils.addDays(new Date(), 1);
    }

}
