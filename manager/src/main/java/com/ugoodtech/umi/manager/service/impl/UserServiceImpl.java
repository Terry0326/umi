package com.ugoodtech.umi.manager.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

import com.ugoodtech.umi.core.Constants;
import com.ugoodtech.umi.core.domain.QUser;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.domain.Role;
import com.ugoodtech.umi.core.exception.UsernameAlreadyExistException;
import com.ugoodtech.umi.core.repository.UserRepository;
import com.ugoodtech.umi.core.repository.RoleRepository;
import com.ugoodtech.umi.manager.service.UserService;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createClientUser(User user) throws UsernameAlreadyExistException {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UsernameAlreadyExistException();
        }

        Role role = roleRepository.findByName(Constants.ROLE_USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreationTime(new Date());
        user.addRole(role);
        user.setAccountExpired(false);
        user.setAccountLocked(false);
        user.setCredentialsExpired(false);
        user.setEnabled(true);
        user.setDeleted(false);
        userRepository.save(user);
    }

    @Override
    public void modifyClientUser(User user) throws UsernameAlreadyExistException {
        User existUser = userRepository.findOne(user.getId());
        boolean usernameChanged = !existUser.getUsername().equals(user.getUsername());
        if (usernameChanged && userRepository.findByUsername(user.getUsername()) != null) {
            throw new UsernameAlreadyExistException();
        }
        existUser.setUsername(user.getUsername());
        if (!StringUtils.isEmpty(user.getPassword())) {
            existUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        //
        userRepository.save(existUser);
    }

    @Override
    public Page<User> queryClientUser(String key, Boolean enabled, String countries, Role role, Pageable pageable) {
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.deleted.isFalse());
        if (!StringUtils.isEmpty(key)) {
            BooleanBuilder orBuilder = new BooleanBuilder();
            orBuilder.or(qUser.username.like("%" + key + "%"));
            builder.and(orBuilder);
        }
        if (enabled != null) {
            builder.and(qUser.enabled.eq(enabled));
        }
        if (!StringUtils.isEmpty(countries)) {
            String[] cs = countries.split(","); 
            builder.and(qUser.address.country.in(cs));
        }
        if (role != null) {
            builder.and(qUser.roles.contains(role));
        }
        return userRepository.findAll(builder, pageable);
    }

    @Override
    public boolean enableClientUser(Long clientUserId) {
        User user = userRepository.findOne(clientUserId);
        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean disableClientUser(Long clientUserId) {
        User user = userRepository.findOne(clientUserId);
        user.setEnabled(false);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean deleteClientUser(Long clientUserId) {
        User user = userRepository.findOne(clientUserId);
        user.setDeleted(true);
        user.setUsername(user.getUsername() + "_" + System.currentTimeMillis());
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean deleteClientUser(List<Long> clientUserIdList) {
        userRepository.delete(clientUserIdList);
        return true;
    }

    @Override
    public Page<User> getUsers(String param, Pageable pageable) {
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.deleted.isFalse());
//        builder.and(qUser.enabled.isTrue());
        if (!StringUtils.isEmpty(param)) {
            BooleanBuilder keyBuilder = new BooleanBuilder();
            builder.and(
                    keyBuilder.or(qUser.username.like("%" + param + "%"))
                            .or(qUser.nickname.like("%" + param + "%"))
            );
        }
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return userRepository.findAll(builder, pageable);
    }

    @Override
    public Page<User> getDisableUsers(String param, Pageable pageable) {
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.deleted.isFalse());
        builder.and(qUser.enabled.isFalse());
        if (!StringUtils.isEmpty(param)) {
            BooleanBuilder keyBuilder = new BooleanBuilder();
            builder.and(
                    keyBuilder.or(qUser.username.like("%" + param + "%"))
                            .or(qUser.nickname.like("%" + param + "%"))
            );
        }
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return userRepository.findAll(builder, pageable);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username + " not found");
        }
        return user;
    }
}
