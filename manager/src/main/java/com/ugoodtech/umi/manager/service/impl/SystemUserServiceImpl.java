package com.ugoodtech.umi.manager.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */


import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.core.domain.QSystemUser;
import com.ugoodtech.umi.core.domain.Role;
import com.ugoodtech.umi.core.domain.SystemUser;
import com.ugoodtech.umi.core.exception.UsernameAlreadyExistException;
import com.ugoodtech.umi.core.repository.RoleRepository;
import com.ugoodtech.umi.core.repository.SystemUserRepository;
import com.ugoodtech.umi.manager.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("systemUserService")
public class SystemUserServiceImpl implements SystemUserService, UserDetailsService {
    @Autowired
    private SystemUserRepository systemUserRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;



    @Override
    public void createSystemUser(SystemUser systemUser, String cityCode) throws UsernameAlreadyExistException {
        if (systemUserRepository.findByUsername(systemUser.getUsername()) != null) {
            throw new UsernameAlreadyExistException();
        }

        systemUser.setCreationTime(new Date());
        systemUser.setAccountExpired(false);
        systemUser.setAccountLocked(false);
        systemUser.setCredentialsExpired(false);
        systemUser.setEnabled(true);
//        systemUser.setPassword(passwordEncoder.encode(Constants.SYS_INIT_PASSWORD));
        //
        //
        Set<Role> roleSet=new HashSet<>();
        Role role = roleRepository.findByName(systemUser.getRoleName());
        roleSet.add(role);
        systemUser.setRoles(roleSet);
        systemUserRepository.save(systemUser);
    }



    @Override
    public void updateSystemUser(SystemUser systemUser) throws UsernameAlreadyExistException {
        SystemUser existUser = systemUserRepository.findOne(systemUser.getId());
        boolean usernameChanged = !existUser.getUsername().equals(systemUser.getUsername());
        if (usernameChanged && systemUserRepository.findByUsername(systemUser.getUsername()) != null) {
            throw new UsernameAlreadyExistException();
        }
        if (!"".equals(systemUser.getPassword()) && null != systemUser.getPassword()) {
            existUser.setPassword(passwordEncoder.encode(systemUser.getPassword()));
        }
        existUser.setUsername(systemUser.getUsername());
        existUser.setTelephone(systemUser.getTelephone());
        existUser.setRealName(systemUser.getRealName());
        existUser.setEnabled(systemUser.isEnabled());

        existUser.setDescription(systemUser.getDescription());
        systemUserRepository.save(existUser);
    }


    @Override
    public Page<SystemUser> querySystemUser(String param, Pageable pageable) {
        QSystemUser qSystemUser = QSystemUser.systemUser;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qSystemUser.enabled.eq(true));
//        builder.and(qSystemUser.roles.contains(roleRepository.findByName(Constants.ROLE_ADMIN)));
//        if (!StringUtils.isEmpty(param)) {
//            BooleanBuilder keyBuilder = new BooleanBuilder();
//
//            builder.and(
//                    keyBuilder.or(qSystemUser.username.like("%" + param + "%"))
//                            .or(qSystemUser.realName.like("%" + param + "%"))
//            );
//        }
        return systemUserRepository.findAll(builder, pageable);
    }

    @Override
    public Page<SystemUser> queryCustomerUser(Long customerId, Pageable pageable) {
        QSystemUser qSystemUser = QSystemUser.systemUser;
        BooleanBuilder builder = new BooleanBuilder();
//        builder.and(qSystemUser.roles.contains(roleRepository.findByName(Constants.ROLE_ADMIN)));
//        if (!StringUtils.isEmpty(param)) {
//            BooleanBuilder keyBuilder = new BooleanBuilder();
//
//            builder.and(
//                    keyBuilder.or(qSystemUser.username.like("%" + param + "%"))
//                            .or(qSystemUser.realName.like("%" + param + "%"))
//            );
//        }
        return systemUserRepository.findAll(builder, pageable);
    }


    @Override
    public void delete(Long userId) {
        systemUserRepository.delete(userId);
    }

    @Override
    public void delete(List<Long> userIds) {
        systemUserRepository.delete(userIds);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser systemUser = systemUserRepository.findByUsername(username);
        if (systemUser == null) {
            throw new UsernameNotFoundException("systemUser not found by the username:" + username);
        }
        return systemUser;
    }

    @Override
    public boolean changePassword(SystemUser user, String password, String oldPassword) {
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            SystemUser savedUser = systemUserRepository.findByUsername(user.getUsername());
            savedUser.setPassword(passwordEncoder.encode(password));
            systemUserRepository.save(savedUser);
//            log.info("系统用户的密码已经修改为"+password);
//            System.out.println(user.getPassword());
            return true;
        } else {
            return false;
        }
        //if old password do not match,then return

    }
}
