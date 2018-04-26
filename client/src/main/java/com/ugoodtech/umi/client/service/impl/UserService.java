package com.ugoodtech.umi.client.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 15/12/16
 */

import ch.hsr.geohash.GeoHash;
import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.client.dto.UserDetailDto;
import com.ugoodtech.umi.client.dto.UserDto;
import com.ugoodtech.umi.client.service.BlockUserService;
import com.ugoodtech.umi.client.service.ChatService;
import com.ugoodtech.umi.client.service.FollowService;
import com.ugoodtech.umi.client.service.UserManager;
import com.ugoodtech.umi.core.domain.Address;
import com.ugoodtech.umi.core.domain.Gender;
import com.ugoodtech.umi.core.domain.QUser;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

@Service
public class UserService implements UserManager, UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BlockUserService blockUserService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    //
    @Autowired
    private FollowService followService;
    @Autowired
    private ChatService chatService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username);
        if (user == null || user.isDeleted()) {
            throw new UsernameNotFoundException(String.format("User with username %s doesn't exist.", username));
        }
        return user;
    }

    /**
     * {@inheritDoc}
     *
     * @param username the login name of the human
     * @return User the populated user object
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException thrown when username not found
     */
    @Override
    public User getUserByUsername(final String username) throws UsernameNotFoundException {
        return (User) this.loadUserByUsername(username);
    }

    @Override
    public void resetPassword(String phoneNumber, String newPwd) {
        User user = getUserByUsername(phoneNumber);
        if (user != null) {
            log.debug("Updating password (providing current password) for user:" + phoneNumber);
            user.setPassword(passwordEncoder.encode(newPwd));
            userRepository.save(user);
        } else {
            throw new UsernameNotFoundException("手机号不正确");
        }
    }

    @Override
    public void updatePassword(String phoneNumber, String oldPwd, String newPwd) throws UmiException {
        User user = getUserByUsername(phoneNumber);
        if(user==null){
            throw new UmiException(1000,"用户未找到");
        }
        if (passwordEncoder.matches(oldPwd, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPwd));
            userRepository.save(user);
        } else {
            throw new UmiException(1000, "原密码不正确");
        }
    }

    @Override
    public void updatePassword(Long userId, String oldPwd, String newPwd) throws UmiException {
        User user = userRepository.findOne(userId);
        if(user==null){
            throw new UmiException(1000,"用户未找到");
        }
        if (passwordEncoder.matches(oldPwd, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPwd));
            userRepository.save(user);
        } else {
            throw new UmiException(1000, "原密码不正确");
        }
    }

    @Override
    public User createUser(String dialingCode, String username, String password, User.RegistrationWay phoneNumberRegister) {
        String encodePwd = passwordEncoder.encode(password);
        User user = new User(username, encodePwd);
        if (dialingCode != null && dialingCode.startsWith("+")) {
            dialingCode = dialingCode.substring(1);
        }
        user.setDialingCode(dialingCode);
        user.setDeleted(false);
        user.setEnabled(true);
        user.setCredentialsExpired(false);
        user.setAccountLocked(false);
        user.setAccountExpired(false);
        user.setCreationTime(new Date());
        user.setGender(Gender.unknown);
        user.setStatus(User.UserStatus.NORMAL);
        user.setAddress(new Address());
        user.setRegistrationWay(phoneNumberRegister);
        user.setReceiveNotification(true);
        return userRepository.save(user);
    }

    @Override
    public UserDetailDto updateUserDetail(User user, String nickname, Gender gender, Address address, String signature, String avatar, boolean completed) throws UmiException {
        user = userRepository.findOne(user.getId());
        if (!StringUtils.isEmpty(nickname)) {
            if (isNicknameExist(nickname, user.getId())) {
                throw new UmiException(1000, "昵称已存在");
            } else {
                user.setNickname(nickname);
            }
        }
        if (gender != null) {
            user.setGender(gender);
        }
        if (address != null) {
            user.setAddress(address);
            if (address.getLat() != null && address.getLng() != null) {
                GeoHash geoHash = GeoHash.withCharacterPrecision(address.getLat(), address.getLng(), 5);
                user.setGeoHash(geoHash.toBase32());
            }
        }
        if (signature != null) {
            user.setSignature(signature);
        }
        if (!StringUtils.isEmpty(avatar)) {
            user.setAvatar(avatar);
        }
        user.setInfoCompleted(completed);
        user.setUpdateTime(new Date());
        user = userRepository.save(user);
        //
        chatService.refreshUser(user);
        //
        return getUserDetail(user.getId(), user);
    }

    @Override
    public boolean isNicknameExist(String nickname, Long exceptUserId) {
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.nickname.eq(nickname));
        if (exceptUserId != null) {
            builder.and(qUser.id.ne(exceptUserId));
        }
        return userRepository.count(builder) > 0L;
    }

    @Override
    public UserDetailDto getUserDetail(Long requestUserId, Long targetUserId) throws UmiException {
        User user = userRepository.findOne(targetUserId);
        return getUserDetail(requestUserId, user);
    }

    @Override
    public UserDetailDto getUserDetail(Long requestUserId, User targetUser) throws UmiException {
        User requestUser = userRepository.findOne(requestUserId);
        UserDetailDto userDetailDto = new UserDetailDto(targetUser);
        if (!requestUserId.equals(targetUser.getId())) {
            userDetailDto.setFollowing(followService.isFollowing(requestUserId, targetUser.getId()));
            userDetailDto.setBlockMe(blockUserService.isUserBlockedByOne(requestUser, targetUser.getId()));
            userDetailDto.setBlock(blockUserService.isUserBlockedByOne(targetUser, requestUser.getId()));
            userDetailDto.setSee(blockUserService.isSeeItsTopicByOne(targetUser, requestUser.getId()));
            System.out.println("userDetailDto.getSee()========"+userDetailDto.isSee());
        }
        return userDetailDto;

    }

    @Override
    public UserDetailDto getUserDetail(Long id) throws UmiException {
        return getUserDetail(id, id);
    }

    @Override
    public User getUser(Long usrId) {
        return userRepository.findOne(usrId);
    }

    @Override
    public Page<User> query(User user, String qKey, Pageable pageable) {
        if (pageable.getSort() == null) {
            Sort creationTime = new Sort(Sort.Direction.DESC, "creationTime");
            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), creationTime);
        }
        QUser qUser = QUser.user;
        String likeStr = "%" + qKey + "%";
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.deleted.isFalse());
        builder.and(qUser.enabled.isTrue());
        builder.and(qUser.id.ne(user.getId()));
        builder.and(new BooleanBuilder()
                .or(qUser.nickname.like(likeStr))
                .or(qUser.signature.like(likeStr))
                .or(qUser.address.city.like(likeStr)));
        return userRepository.findAll(builder, pageable);
    }
    @Override
    public User queryUser(User user,String userName){
        QUser qUser=QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.deleted.isFalse());
        builder.and(qUser.enabled.isTrue());
        builder.and(qUser.id.ne(user.getId()));
        builder.and(qUser.username.eq(userName));
        return userRepository.findOne(builder);
    }

    @Override
    public void configReceiveNotification(Long userId, boolean receive) {
        User user = userRepository.findOne(userId);
        user.setReceiveNotification(receive);
        userRepository.save(user);
    }

    @Override
    public Collection<Long> getNearByUserIds(Long userId, Double lat, Double lng, Integer distance) throws UmiException {
        User user = userRepository.findOne(userId);
        if (lat == null || lng == null) {
            Address address = user.getAddress();
            if (address != null && address.getLat() != null && address.getLng() != null) {
                lat = address.getLat();
                lng = address.getLng();
            } else {
                throw new UmiException(1000, "无法确定您当前的位置");
            }
        }
        GeoHash geoHash = GeoHash.withCharacterPrecision(lat, lng, 5);
        GeoHash[] adjacent = geoHash.getAdjacent();
        Collection<String> nearByGeoHashGrid = new HashSet<>();
        for (GeoHash hash : adjacent) {
            nearByGeoHashGrid.add(hash.toBase32());
        }
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.deleted.isFalse());
        builder.and(qUser.enabled.isTrue());
        builder.and(qUser.geoHash.in(nearByGeoHashGrid));
        Iterable<User> users = userRepository.findAll(builder);
        Collection<Long> nearByUserIds = new HashSet<>();
        for (User nearByUser : users) {
            nearByUserIds.add(nearByUser.getId());
        }
        return nearByUserIds;
    }

    Logger log = LoggerFactory.getLogger(UserService.class);
}
