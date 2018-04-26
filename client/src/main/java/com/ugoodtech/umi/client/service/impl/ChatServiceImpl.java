package com.ugoodtech.umi.client.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.client.service.ChatService;
import com.ugoodtech.umi.core.domain.QThirdPartyAccount;
import com.ugoodtech.umi.core.domain.ThirdPartyAccount;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.ThirdPartyAccountRepository;
import com.ugoodtech.umi.core.repository.UserRepository;
import io.rong.RongCloud;
import io.rong.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ThirdPartyAccountRepository thirdPartyAccountRepository;
    @Autowired
    private UserRepository userRepository;
    @Value("${rongyun.appKey}")
    private String appKey;
    @Value("${rongyun.appSecret}")
    String appSecret = "secret";
    @Value("${oss.image.urlPrefix}")
    private String avatarUrl;
    //
    private RongCloud rongCloud;

    @PostConstruct
    public void init() {
        rongCloud = RongCloud.getInstance(appKey, appSecret);
    }

    @Override
    public String getUserImToken(User user) throws UmiException {

        String username = user.getUsername();
        String realName = user.getNickname();
        ThirdPartyAccount thirdPartyAccount = getThirdPartyAccount(username, ThirdPartyAccount.ThirdParty.RONG_YUN_IM);
        if (thirdPartyAccount == null) {
            if (realName == null) {
                realName = username;
            }
            String portraitUri = getAvatarUrl(user);
            try {
                TokenResult tokenResult = rongCloud.user.getToken(username, realName, portraitUri);
                logger.debug(username + " im token  " + tokenResult.getToken());
                thirdPartyAccount = new ThirdPartyAccount();
                thirdPartyAccount.setAccId(username);
                thirdPartyAccount.setAccName(realName);
                thirdPartyAccount.setToken(tokenResult.getToken());
                thirdPartyAccount.setThirdParty(ThirdPartyAccount.ThirdParty.RONG_YUN_IM);
                thirdPartyAccount.setSource(ThirdPartyAccount.AccountSource.Unkonwn);
                thirdPartyAccount.setUser(user);
                thirdPartyAccountRepository.save(thirdPartyAccount);
            } catch (Exception e) {
                logger.error("获取token出错", e);
                throw new UmiException(1000, e.getMessage());
            }
        }
        return thirdPartyAccount.getToken();
    }

    private ThirdPartyAccount getThirdPartyAccount(String username, ThirdPartyAccount.ThirdParty thirdParty) {
        QThirdPartyAccount qThirdPartyAccount = QThirdPartyAccount.thirdPartyAccount;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qThirdPartyAccount.deleted.isFalse());
        builder.and(qThirdPartyAccount.accId.eq(username));
        builder.and(qThirdPartyAccount.thirdParty.eq(thirdParty));
        return thirdPartyAccountRepository.findOne(builder);
    }

    @Override
    public boolean createChatRoom(String roomId, String roomName) {
        ChatRoomInfo[] chatRoomInfo = {new ChatRoomInfo(roomId, roomName)};
        try {
            CodeSuccessResult successResult = rongCloud.chatroom.create(chatRoomInfo);
            return (successResult.getCode() == 200);
        } catch (Exception e) {
            logger.error("create chat room error", e);
        }
        return false;
    }

    @Override
    public boolean joinChatRoom(String[] userIds, String roomId) {
        try {
            CodeSuccessResult successResult = rongCloud.chatroom.join(userIds, roomId);
            return (successResult.getCode() == 200);
        } catch (Exception e) {
            logger.error("join chat room error", e);
        }
        return false;
    }

    @Override
    public String getPrivateMessageRoomId(Long userOneId, Long userTwoId) throws UmiException {

        String roomSuffix = userOneId < userTwoId ? userOneId + "_" + userTwoId : userTwoId + "_" + userOneId;
        String privateRoomId = "private_message_" + roomSuffix;
        try {
            ChatroomQueryResult queryResult = rongCloud.chatroom.query(new String[]{privateRoomId});
            if (queryResult.getCode() == 200) {
                List<ChatRoom> chatRooms = queryResult.getChatRooms();
                if (chatRooms.size() > 0) {
                    return chatRooms.get(0).getChrmId();
                } else {
                    if (createChatRoom(privateRoomId, "私信聊天室")) {
                        User one = userRepository.findOne(userOneId);
                        User two = userRepository.findOne(userTwoId);
                        joinChatRoom(new String[]{one.getUsername(), two.getUsername()}, privateRoomId);
                        return privateRoomId;
                    } else {
                        throw new UmiException(1000, "不能创建聊天室");
                    }
                }
            } else {
                throw new UmiException(1000, "不能创建聊天室");
            }
        } catch (Exception e) {
            logger.error("创建聊天室失败", e);
            throw new UmiException(1000, "不能创建聊天室");
        }
    }

    @Override
    public void block(Long userId, Long blackUserId) throws UmiException {
        User user = userRepository.findOne(userId);
        User blackUser = userRepository.findOne(blackUserId);
        try {
            rongCloud.user.addBlacklist(user.getUsername(), blackUser.getUsername());
        } catch (Exception e) {
            logger.error("拉黑失败", e);
            throw new UmiException(1000, "聊天拉黑失败");
        }
    }

    @Override
    public void unblock(Long executorId, Long blockerId) {
        User user = userRepository.findOne(executorId);
        User blackUser = userRepository.findOne(blockerId);
        try {
            rongCloud.user.removeBlacklist(user.getUsername(), blackUser.getUsername());
        } catch (Exception e) {
            logger.error("取消拉黑失败", e);
        }
    }
    @Override
    public void seeItsTopics(Long executorId, Long blockerId) {
        User user = userRepository.findOne(executorId);
        User blackUser = userRepository.findOne(blockerId);
        try {
            rongCloud.user.removeBlacklist(user.getUsername(), blackUser.getUsername());
        } catch (Exception e) {
            logger.error("取消不看Ta的帖子失效", e);
        }
    }

    @Override
    public void unSeeItsTopics(Long executorId, Long blockerId){
        User user=userRepository.findOne(executorId);
        User blackUser=userRepository.findOne(blockerId);
        try {
            rongCloud.user.removeBlacklist(user.getUsername(),blackUser.getUsername());
        }catch (Exception e){
            logger.error("不看Ta的帖子失败",e);
        }

    }

    @Override
    public void removeNotSeeItsTopics(Long executorId, Long blockerId) {
        User user=userRepository.findOne(executorId);
        User blackUser=userRepository.findOne(blockerId);
        try {
            rongCloud.user.removeBlacklist(user.getUsername(),blackUser.getUsername());
        }catch (Exception e){
            logger.error("移除不看他的帖子失败",e);
        }
    }



    @Override
    public void refreshUsers() {
        QThirdPartyAccount qThirdPartyAccount = QThirdPartyAccount.thirdPartyAccount;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qThirdPartyAccount.thirdParty.eq(ThirdPartyAccount.ThirdParty.RONG_YUN_IM));
        builder.and(qThirdPartyAccount.deleted.isFalse());
        List<ThirdPartyAccount> thirdPartyAccounts = (List<ThirdPartyAccount>) thirdPartyAccountRepository.findAll(builder);
        logger.debug("get rongyun im account :" + thirdPartyAccounts.size());
        int count = 0;
        for (ThirdPartyAccount thirdPartyAccount : thirdPartyAccounts) {
            User user = userRepository.findByUsername(thirdPartyAccount.getAccId());
            if (refreshUser(user)) {
                count++;
            }
        }
        logger.info("成功刷新 " + count + " 用户数据");
    }

    @Override
    public boolean refreshUser(User user) {
        String username = user.getUsername();
        String realName = user.getNickname();
        if (realName == null) {
            realName = username;
        }
        String portraitUri = getAvatarUrl(user);
        try {
            CodeSuccessResult result = rongCloud.user.refresh(username, realName, portraitUri);
            if (result.getCode() == 200) {
                logger.debug("更新用户聊天信息成功:" + username);
                return true;
            }
        } catch (Exception e) {
            logger.error("刷新聊天账号失败", e);
        }
        return false;
    }

    private String getAvatarUrl(User user) {
        if (user.getAvatar() != null) {
            if (user.getAvatar().startsWith("http")) {
                return user.getAvatar();
            } else {
                return avatarUrl + user.getId() + "-" + user.getAvatar() + "?x-oss-process=image/resize,m_fill,h_80,w_80";
            }
        } else {
            return null;
        }
    }

    Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);
}
