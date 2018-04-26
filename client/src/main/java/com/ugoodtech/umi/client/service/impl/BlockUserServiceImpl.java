package com.ugoodtech.umi.client.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.client.service.BlockUserService;
import com.ugoodtech.umi.client.service.ChatService;
import com.ugoodtech.umi.core.domain.*;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.BlockUserRepository;
import com.ugoodtech.umi.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class BlockUserServiceImpl implements BlockUserService {
    @Autowired
    private BlockUserRepository blockUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatService chatService;

    @Override
    public void block(Long executorId, Long blockerId) {
        System.out.println("==============ss==================");
        BlockUser.BlockAction blockAction = BlockUser.BlockAction.BLOCK;
        createBlock(executorId, blockerId, blockAction);
    }

    @Override
    public void unSeeItsTopics(Long executorId, Long blockerId) {
        BlockUser.BlockAction blockAction = BlockUser.BlockAction.DONT_SEE_ITS_TOPICS;
        createBlock(executorId, blockerId, blockAction);
    }
    @Override
    public void seeItsTopics(Long executorId, Long blockerId) {
        BlockUser blockUser = getBlockUser(executorId, blockerId);
        if (blockUser != null) {
            BlockUser.BlockAction blockAction = blockUser.getAction().unmerge(BlockUser.BlockAction.DONT_SEE_ITS_TOPICS);
            if (blockAction.equals(BlockUser.BlockAction.NOOP)) {
                blockUserRepository.delete(blockUser);
            } else {
                blockUser.setAction(blockAction);
                blockUserRepository.save(blockUser);
            }
            chatService.seeItsTopics(executorId, blockerId);
        }
    }

    private void createBlock(Long executorId, Long blockerId, BlockUser.BlockAction blockAction) {
        BlockUser blockUser = getBlockUser(executorId, blockerId);
        if (blockUser != null) {
            blockAction = blockUser.getAction().merge(blockAction);
            if (blockAction == null) {
                logger.error("blocking user " + blockerId + " by user " + executorId
                        + " is error,caused by error db action code " + blockUser.getAction().getCode());
            }
            assert blockAction != null;
            blockUser.setAction(blockAction);
            blockUserRepository.save(blockUser);
        } else {
            blockUser = new BlockUser();
            blockUser.setExecutor(userRepository.findOne(executorId));
            blockUser.setBlocker(userRepository.findOne(blockerId));
            blockUser.setAction(blockAction);
            blockUserRepository.save(blockUser);
        }
        if (blockAction.equals(BlockUser.BlockAction.BLOCK)
                || blockAction.equals(BlockUser.BlockAction.BLOCK_AND_NOT_SEE_ITS_TOPICS)) {
            try {
                chatService.block(executorId, blockerId);
            } catch (UmiException e) {
                logger.error("IM拉黑失败", e);
            }
        }else if(blockAction.equals(BlockUser.BlockAction.DONT_SEE_ITS_TOPICS)){
                chatService.unSeeItsTopics(executorId, blockerId);
        }
    }

    private BlockUser getBlockUser(Long executorId, Long blockerId) {
        QBlockUser qBlockUser = QBlockUser.blockUser;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBlockUser.executor.id.eq(executorId));
        builder.and(qBlockUser.blocker.id.eq(blockerId));
        builder.and(qBlockUser.deleted.isFalse());
        return blockUserRepository.findOne(builder);
    }
    @Override
    public Page<User> getBlockList(Long userId, Pageable pageable) {
        QBlockUser qBlockUser = QBlockUser.blockUser;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBlockUser.executor.id.eq(userId));
        builder.and(qBlockUser.action.in(BlockUser.BlockAction.BLOCK, BlockUser.BlockAction.BLOCK_AND_NOT_SEE_ITS_TOPICS));
        builder.and(qBlockUser.deleted.isFalse());
        Page<BlockUser> blockPage = blockUserRepository.findAll(builder, pageable);
        return extractBlockers(pageable, blockPage);
    }

    @Override
    public Collection<Long> getBlockUserExecutorIds(Long userId) {
        QBlockUser qBlockUser = QBlockUser.blockUser;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBlockUser.blocker.id.eq(userId));
        builder.and(qBlockUser.action.in(BlockUser.BlockAction.BLOCK, BlockUser.BlockAction.BLOCK_AND_NOT_SEE_ITS_TOPICS));
        builder.and(qBlockUser.deleted.isFalse());
        Iterable<BlockUser> blockUsers = blockUserRepository.findAll(builder);
        Collection<Long> executorIds = new HashSet<>();
        for (BlockUser blockUser : blockUsers) {
            executorIds.add(blockUser.getExecutor().getId());
        }
        return executorIds;
    }

    @Override
    public Collection<Long> getNotSeeItsTopicsUserIds(Long executorId) {
        QBlockUser qBlockUser = QBlockUser.blockUser;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBlockUser.executor.id.eq(executorId));
        builder.and(qBlockUser.action.in(BlockUser.BlockAction.DONT_SEE_ITS_TOPICS, BlockUser.BlockAction.BLOCK_AND_NOT_SEE_ITS_TOPICS));
        builder.and(qBlockUser.deleted.isFalse());
        Iterable<BlockUser> blockUsers = blockUserRepository.findAll(builder);
        Collection<Long> notSeeItsTopicsUserIds = new HashSet<>();
        for (BlockUser blockUser : blockUsers) {
            notSeeItsTopicsUserIds.add(blockUser.getBlocker().getId());
        }
        return notSeeItsTopicsUserIds;
    }

    @Override
    public boolean isUserBlockedByOne(User user, Long executorId) {
        QBlockUser qBlockUser = QBlockUser.blockUser;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBlockUser.blocker.id.eq(user.getId()));
        builder.and(qBlockUser.executor.id.eq(executorId));
        builder.and(qBlockUser.action.in(BlockUser.BlockAction.BLOCK, BlockUser.BlockAction.BLOCK_AND_NOT_SEE_ITS_TOPICS));
        builder.and(qBlockUser.deleted.isFalse());
        return blockUserRepository.count(builder) > 0L;
    }
    @Override
    public boolean isSeeItsTopicByOne(User user, Long executorId) {
        QBlockUser qBlockUser = QBlockUser.blockUser;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBlockUser.blocker.id.eq(user.getId()));
        builder.and(qBlockUser.executor.id.eq(executorId));
        builder.and(qBlockUser.action.in(BlockUser.BlockAction.DONT_SEE_ITS_TOPICS));
        builder.and(qBlockUser.deleted.isFalse());
        return blockUserRepository.count(builder) > 0L;
    }
    @Override
    public Collection<Long> getBlockUserIds(Long executorId) {
        QBlockUser qBlockUser = QBlockUser.blockUser;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBlockUser.executor.id.eq(executorId));
        builder.and(qBlockUser.action.in(BlockUser.BlockAction.BLOCK, BlockUser.BlockAction.BLOCK_AND_NOT_SEE_ITS_TOPICS));
        builder.and(qBlockUser.deleted.isFalse());
        Iterable<BlockUser> blockUsers = blockUserRepository.findAll(builder);
        Collection<Long> blockeIds = new ArrayList<>();
        for (BlockUser blockUser : blockUsers) {
            blockeIds.add(blockUser.getBlocker().getId());
        }
        return blockeIds;
    }

    private Page<User> extractBlockers(Pageable pageable, Page<BlockUser> blockUsers) {
        List<User> blacklist = new ArrayList<>();
        for (BlockUser blockUser : blockUsers) {
            blacklist.add(blockUser.getBlocker());
        }
        return new PageImpl<>(blacklist, pageable, blockUsers.getTotalElements());
    }

    @Override
    public void unblock(Long executorId, Long blockerId) {
        BlockUser blockUser = getBlockUser(executorId, blockerId);
        if (blockUser != null) {
            BlockUser.BlockAction blockAction = blockUser.getAction().unmerge(BlockUser.BlockAction.BLOCK);
            if (blockAction.equals(BlockUser.BlockAction.NOOP)) {
                blockUserRepository.delete(blockUser);
            } else {
                blockUser.setAction(blockAction);
                blockUserRepository.save(blockUser);
            }
            chatService.unblock(executorId, blockerId);
        }
    }


    Logger logger = LoggerFactory.getLogger(BlockUserServiceImpl.class);
}
