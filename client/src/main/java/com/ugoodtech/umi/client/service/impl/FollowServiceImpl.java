package com.ugoodtech.umi.client.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.client.service.FollowService;
import com.ugoodtech.umi.client.service.MessageService;
import com.ugoodtech.umi.core.domain.*;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.UserFollowRepository;
import com.ugoodtech.umi.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class FollowServiceImpl implements FollowService {
    @Autowired
    private UserFollowRepository followRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageService messageService;
    private Lock lock = new ReentrantLock();

    @Override
    public void addFollow(Long followerId, Long targetUserId) throws UmiException {
        if (followerId.equals(targetUserId)) {
            throw new UmiException(1000, "您自己不能关注自己");
        }
        User follower = userRepository.findOne(followerId);
        User targetUser = userRepository.findOne(targetUserId);
        if (follower == null || targetUser == null) {
            throw new UmiException(1000, "用户不存在");
        }
        try {
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
                try {
                    boolean isFollowing = isFollowing(followerId, targetUserId);
                    if (isFollowing) {
                        throw new UmiException(1000, "您已经关注该用户");
                    }
                    UserFollow userFollow = new UserFollow();
                    userFollow.setFollower(follower);
                    userFollow.setTargetUser(targetUser);
                    followRepository.save(userFollow);
                    //
                    messageService.createMessage(Message.MessageType.FOLLOW_MESSAGE, follower, targetUser);
                    //
                    updateUserFollowersNum(targetUser);
                    updateUserFollowingNum(follower);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new UmiException(1000, "操作超时,请稍后重试");
            }
        } catch (InterruptedException e) {
            logger.warn(e.getMessage());
            throw new UmiException(1000, "操作被中断");
        }
    }

    private void updateUserFollowingNum(User follower) {
        follower.setFollowingNum(getFollowingUsersNum(follower.getId()));
        userRepository.save(follower);
    }

    private void updateUserFollowersNum(User targetUser) {
        targetUser.setFollowersNum(getFollowersNum(targetUser.getId()));
        userRepository.save(targetUser);
    }


    private UserFollow getFollow(Long followerId, Long targetUserId) {
        QUserFollow qUserFollow = QUserFollow.userFollow;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserFollow.follower.id.eq(followerId));
        builder.and(qUserFollow.targetUser.id.eq(targetUserId));
        builder.and(qUserFollow.deleted.isFalse());
        return followRepository.findOne(builder);
    }

    @Override
    public boolean isFollowing(Long followerId, Long targetUserId) {
        QUserFollow qUserFollow = QUserFollow.userFollow;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserFollow.follower.id.eq(followerId));
        builder.and(qUserFollow.targetUser.id.eq(targetUserId));
        builder.and(qUserFollow.deleted.isFalse());
        return followRepository.count(builder) > 0;
    }

    @Override
    public Long getFollowersNum(Long userId) {
        QUserFollow qUserFollow = QUserFollow.userFollow;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserFollow.targetUser.id.eq(userId));
        builder.and(qUserFollow.deleted.isFalse());
        Long followerNum = followRepository.count(builder);
        logger.debug("getFollowersNum " + followerNum + " for user:" + userId);
        return followerNum;
    }

    @Override
    public Long getFollowingUsersNum(Long userId) {
        QUserFollow qUserFollow = QUserFollow.userFollow;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserFollow.follower.id.eq(userId));
        builder.and(qUserFollow.deleted.isFalse());
        Long followingNum = followRepository.count(builder);
        logger.debug("getFollowingUsersNum " + followingNum + " for user:" + userId);
        return followingNum;
    }

    @Override
    public Page<User> getFollowers(Long userId, Pageable pageable) {
        QUserFollow qUserFollow = QUserFollow.userFollow;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserFollow.targetUser.id.eq(userId));
        builder.and(qUserFollow.deleted.isFalse());
        pageable = getPageableWithSort(pageable);
        Page<UserFollow> userFollows = followRepository.findAll(builder, pageable);
        return extractFollowers(pageable, userFollows);
    }

    private Page<User> extractFollowers(Pageable pageable, Page<UserFollow> userFollows) {
        List<User> followers = new ArrayList<>();
        for (UserFollow userFollow : userFollows) {
            followers.add(userFollow.getFollower());
        }
        return new PageImpl<>(followers, pageable, userFollows.getTotalElements());
    }

    private List<User> extractFollowers(Iterable<UserFollow> userFollows) {
        List<User> followers = new ArrayList<>();
        for (UserFollow userFollow : userFollows) {
            followers.add(userFollow.getFollower());
        }
        return followers;
    }

    private Pageable getPageableWithSort(Pageable pageable) {
        if (pageable.getSort() == null) {
            Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }
        return pageable;
    }

    @Override
    public void unFollow(Long fromUserId, Long targetUserId) throws UmiException {
        QUserFollow qUserFollow = QUserFollow.userFollow;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserFollow.follower.id.eq(fromUserId));
        builder.and(qUserFollow.targetUser.id.eq(targetUserId));
        builder.and(qUserFollow.deleted.isFalse());
        UserFollow follow = followRepository.findOne(builder);
        if (follow != null) {
            follow.setDeleted(true);
            followRepository.save(follow);
            messageService.deleteFollowMessage(fromUserId, targetUserId);
        } else {
            throw new UmiException(1000, "您没有关注此人");
        }
        updateUserFollowersNum(follow.getTargetUser());
        updateUserFollowingNum(follow.getFollower());
    }

    @Override
    public Page<User> getFollowingUsers(Long userId, Pageable pageable) {
        QUserFollow qUserFollow = QUserFollow.userFollow;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserFollow.follower.id.eq(userId));
        builder.and(qUserFollow.deleted.isFalse());
        Page<UserFollow> userFollows = followRepository.findAll(builder, pageable);
        return extractTargetUsers(pageable, userFollows);
    }

    private Page<User> extractTargetUsers(Pageable pageable, Page<UserFollow> userFollows) {
        List<User> followingUsers = new ArrayList<>();
        for (UserFollow userFollow : userFollows) {
            followingUsers.add(userFollow.getTargetUser());
        }
        return new PageImpl<>(followingUsers, pageable, userFollows.getTotalElements());
    }

    private List<User> extractTargetUsers(Iterable<UserFollow> userFollows) {
        List<User> followingUsers = new ArrayList<>();
        for (UserFollow userFollow : userFollows) {
            followingUsers.add(userFollow.getTargetUser());
        }
        return followingUsers;
    }

    @Override
    public Collection<Long> getFollowingUserIds(Long userId) {
        QUserFollow qUserFollow = QUserFollow.userFollow;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserFollow.follower.id.eq(userId));
        builder.and(qUserFollow.deleted.isFalse());
        Iterable<UserFollow> users = followRepository.findAll(builder);
        Collection<Long> followingUserIds = new ArrayList<>();
        for (UserFollow follow : users) {
            followingUserIds.add(follow.getTargetUser().getId());
        }
        return followingUserIds;
    }

    @Override
    public Page<User> queryFollowers(User user, String qKey, Pageable pageable) {
        if (pageable.getSort() == null) {
            Sort creationTime = new Sort(Sort.Direction.DESC, "creationTime");
            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), creationTime);
        }
        QUserFollow qUserFollow = QUserFollow.userFollow;
        String likeStr = "%" + qKey + "%";
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserFollow.deleted.isFalse());
        builder.and(qUserFollow.targetUser.id.eq(user.getId()));
        builder.and(new BooleanBuilder()
                .or(qUserFollow.follower.nickname.like(likeStr))
                .or(qUserFollow.follower.signature.like(likeStr))
                .or(qUserFollow.follower.address.city.like(likeStr)));
        Page<UserFollow> userFollows = followRepository.findAll(builder, pageable);
        List<User> users = new ArrayList<>();
        for (UserFollow follow : userFollows) {
            users.add(follow.getFollower());
        }
        Page<User> userPage = new PageImpl<>(users, pageable, userFollows.getTotalElements());
        return userPage;
    }

    @Override
    public Page<User> queryFollowingUsers(User user, String qKey, Pageable pageable) {
        if (pageable.getSort() == null) {
            Sort creationTime = new Sort(Sort.Direction.DESC, "creationTime");
            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), creationTime);
        }
        QUserFollow qUserFollow = QUserFollow.userFollow;
        String likeStr = "%" + qKey + "%";
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserFollow.deleted.isFalse());
        builder.and(qUserFollow.follower.id.eq(user.getId()));
        builder.and(new BooleanBuilder()
                .or(qUserFollow.targetUser.nickname.like(likeStr))
                .or(qUserFollow.targetUser.signature.like(likeStr))
                .or(qUserFollow.targetUser.address.city.like(likeStr)));
        Page<UserFollow> userFollows = followRepository.findAll(builder, pageable);
        List<User> users = new ArrayList<>();
        for (UserFollow follow : userFollows) {
            users.add(follow.getTargetUser());
        }
        Page<User> userPage = new PageImpl<>(users, pageable, userFollows.getTotalElements());
        return userPage;

    }

    @Override
    public List<User> getFollowers(Long userId) {
        QUserFollow qUserFollow = QUserFollow.userFollow;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserFollow.targetUser.id.eq(userId));
        builder.and(qUserFollow.deleted.isFalse());
        Iterable<UserFollow> userFollows = followRepository.findAll(builder);
        return extractFollowers(userFollows);
    }

    @Override
    public List<User> getFollowingUsers(Long userId) {
        QUserFollow qUserFollow = QUserFollow.userFollow;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserFollow.follower.id.eq(userId));
        builder.and(qUserFollow.deleted.isFalse());
        Iterable<UserFollow> userFollows = followRepository.findAll(builder);
        return extractTargetUsers(userFollows);
    }

    Logger logger = LoggerFactory.getLogger(FollowServiceImpl.class);
}
