package com.ugoodtech.umi.client.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.client.dto.CommentContentBody;
import com.ugoodtech.umi.client.dto.MessageDto;
import com.ugoodtech.umi.client.dto.UserDetailDto;
import com.ugoodtech.umi.client.service.BlockUserService;
import com.ugoodtech.umi.client.service.FollowService;
import com.ugoodtech.umi.client.service.MessageService;
import com.ugoodtech.umi.client.service.PushService;
import com.ugoodtech.umi.core.domain.*;
import com.ugoodtech.umi.core.domain.Message.MessageType;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.MessageRepository;
import com.ugoodtech.umi.core.repository.TopicRepository;
import com.ugoodtech.umi.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private PushService pushService;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private FollowService followService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BlockUserService blockUserService;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        logger.debug("DATASOURCE = " + dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /** @Override
    public Map<Integer, List<MessageDto>> getUserNotificationMessages(User user) {
    Map<Integer, List<MessageDto>> messageListMap = new HashMap<>();
    //at列表
    List<Message> atMessagePage = (List<Message>) getUserMessages(user, MessageType.MARK_MESSAGE);
    messageListMap.put(MessageType.MARK_MESSAGE.getCode(), convertToMessageDtoList(atMessagePage, false));
    //评论列表
    List<MessageDto> commentMessagePage = getInstantCommentMessages(user);
    messageListMap.put(MessageType.INSTANT_COMMENT_MESSAGE.getCode(), commentMessagePage);
    //系统公告列表
    List<MessageDto> systemNoticeList = new ArrayList<>();
    //点赞列表
    List<Message> likeMessagePage = (List<Message>) getUserMessages(user, MessageType.LIKE_MESSAGE);
    MessageDto mergedLikeMsg = getMergedLikeMessage(likeMessagePage);
    if (mergedLikeMsg != null) {
    systemNoticeList.add(mergedLikeMsg);
    }
    //关注列表
    List<Message> followMessagePage = (List<Message>) getUserMessages(user, MessageType.FOLLOW_MESSAGE);
    MessageDto mergedFollowMsg = getMergedFollowMessage(followMessagePage);
    if (mergedFollowMsg != null) {
    systemNoticeList.add(mergedFollowMsg);
    }
    //
    messageListMap.put(MessageType.NOTICE_MESSAGE.getCode(), systemNoticeList);
    //获取未读总数
    MessageDto unreadMessage = new MessageDto();
    unreadMessage.setUnreadNum(getUserUnreadMessagesNum(user));
    //获取是否接收at通知
    User dbUser = userRepository.findOne(user.getId());
    unreadMessage.setReceiveAtMsg(dbUser.isReceiveNotification());
    ArrayList<MessageDto> unreadNumList = new ArrayList<>();
    unreadNumList.add(unreadMessage);
    messageListMap.put(100, unreadNumList);
    return messageListMap;
    }**/
    @Override
    public Map<Integer, Map<Integer,List<MessageDto>>> getUserNotificationMessages(User user) {
        Map<Integer, Map<Integer,List<MessageDto>>> messageListMap = new HashMap<>();
        //at列表
        Map<Integer,List<MessageDto>> atMessagePage=new HashMap<>();
        atMessagePage.put(getUserUnreadMessagesNumByType(user,MessageType.MARK_MESSAGE),null);
        messageListMap.put(MessageType.MARK_MESSAGE.getCode(),atMessagePage);
        //评论列表
        Map<Integer,List<MessageDto>>  commentMessagePage = getInstantCommentMessages(user,0,10);
        messageListMap.put(MessageType.INSTANT_COMMENT_MESSAGE.getCode(), commentMessagePage);
        //系统公告列表
        //List<MessageDto> systemNoticeList = new ArrayList<>();
        //点赞列表
        //  List<Message> likeMessagePage = (List<Message>) getUserMessages(user, MessageType.LIKE_MESSAGE);
        Map<Integer,List<MessageDto>> likeMessagePage=new HashMap<>();
        likeMessagePage.put(getUserUnreadMessagesNumByType(user,MessageType.LIKE_MESSAGE),null);
        messageListMap.put(MessageType.LIKE_MESSAGE.getCode(),likeMessagePage);
//       if (mergedLikeMsg != null) {
//           systemNoticeList.add(mergedLikeMsg);
//       }
        //关注列表
        Map<Integer,List<MessageDto>> followMessagePage=new HashMap<>();
        followMessagePage.put(getUserUnreadMessagesNumByType(user,MessageType.FOLLOW_MESSAGE),null);
        messageListMap.put(MessageType.FOLLOW_MESSAGE.getCode(),followMessagePage);

//       if (mergedFollowMsg != null) {
//           systemNoticeList.add(mergedFollowMsg);
//       }
        //获取未读总数
        MessageDto unreadMessage = new MessageDto();
        unreadMessage.setUnreadNum(getUserUnreadMessagesNum(user));
        //获取是否接收at通知
        User dbUser = userRepository.findOne(user.getId());
        unreadMessage.setReceiveAtMsg(dbUser.isReceiveNotification());
        ArrayList<MessageDto> unreadNumList = new ArrayList<>();
        unreadNumList.add(unreadMessage);
        Map<Integer,List<MessageDto>> unreadNumMap=new HashMap<>();
        unreadNumMap.put(100,unreadNumList);
        messageListMap.put(100, unreadNumMap);
        return messageListMap;
    }
    static String queryUniqueTopicFirstCommentMsg = "select * from (select * from messages where to_user_id=? and `from_user_id` !=?" +
            " and message_type=2 and deleted=false order by creation_time desc,id desc) temp  group by link_id" +
            " order by creation_time desc  limit ?,? ";
    @Override
    public Map<Integer,List<MessageDto>> getInstantCommentMessages(final User user, Integer page, Integer size) {
        final List<MessageDto> messages = new ArrayList<>();
        final Map<Integer,List<MessageDto>> map=new HashMap<>();

        jdbcTemplate.query(queryUniqueTopicFirstCommentMsg, new Object[]{user.getId(),user.getId(),page,size}, new RowCallbackHandler() {
            int unreadNum = 0;
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                Message message = new Message();
                message.setType(MessageType.forValue(resultSet.getInt("message_type")));
                message.setId(resultSet.getLong("id"));
                message.setContent(resultSet.getString("content"));
                message.setCreationTime(resultSet.getTimestamp("creation_time"));
                message.setTitle(resultSet.getString("title"));
                message.setLinkType(resultSet.getInt("link_type"));
                message.setLinkId(resultSet.getLong("link_id"));
                message.setRead(resultSet.getBoolean("is_read"));
                if(false==message.isRead()){
                    unreadNum++;
                }
                Long fromUserId = resultSet.getLong("from_user_id");
                User fromUser = userRepository.findOne(fromUserId);
                message.setFromUser(fromUser);
                MessageDto dto = new MessageDto(message);
                dto.setDeleted(resultSet.getBoolean("deleted"));
                fulfillWithTopic(dto);
                messages.add(dto);
                System.out.println("=========common==============="+dto.isRead());
                if(resultSet.isLast()){
                    map.put(unreadNum,messages);
                }
            }
        });

        return map;
    }

    private MessageDto getMergedLikeMessage(List<Message> likeMessages) {
        if (likeMessages == null || likeMessages.isEmpty()) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder("");
        Integer unreadNum = 0;
        for (int i = 0; i < likeMessages.size(); ++i) {
            Message followMessage = likeMessages.get(i);
            stringBuilder.append(followMessage.getFromUser().getNickname()).append(",");
            if (!followMessage.isRead()) {
                unreadNum++;
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        MessageDto mergedLikeMsg = new MessageDto();
        mergedLikeMsg.setContent(stringBuilder.toString());
        mergedLikeMsg.setMessageType(Message.MessageType.LIKE_MESSAGE);
        mergedLikeMsg.setTotal(Long.valueOf(likeMessages.size()));
        mergedLikeMsg.setUnreadNum(unreadNum);
        return mergedLikeMsg;
    }

    private MessageDto getMergedFollowMessage(List<Message> followMessages) {
        if (followMessages == null || followMessages.isEmpty()) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder("");
        Integer unreadNum = 0;
        for (int i = 0; i < followMessages.size(); ++i) {
            Message followMessage = followMessages.get(i);
            stringBuilder.append(followMessage.getFromUser().getNickname()).append(",");
            if (!followMessage.isRead()) {
                unreadNum++;
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        MessageDto allFollowMessage = new MessageDto();
        allFollowMessage.setContent(stringBuilder.toString());
        allFollowMessage.setMessageType(Message.MessageType.FOLLOW_MESSAGE);
        allFollowMessage.setTotal(Long.valueOf(followMessages.size()));
        allFollowMessage.setUnreadNum(unreadNum);
        return allFollowMessage;
    }

    @Override
    public Page<Message> getUserUnreadMessages(User user, Message.MessageType messageType, int page, Integer size) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.toUser.id.eq(user.getId()));
        builder.and(qMessage.type.eq(messageType));
        builder.and(qMessage.read.isFalse());
        builder.and(qMessage.deleted.isFalse());
        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.DESC, "creationTime"));
        return messageRepository.findAll(builder, pageable);
    }

    private List<Message> getUserUnreadMessages(User user, Message.MessageType messageType) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.toUser.id.eq(user.getId()));
        builder.and(qMessage.type.eq(messageType));
        builder.and(qMessage.read.isFalse());
        builder.and(qMessage.deleted.isFalse());
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        return (List<Message>) messageRepository.findAll(builder, sort);
    }

    private Iterable<Message> getUserMessages(User user, Message.MessageType messageType) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.toUser.id.eq(user.getId()));
        if (messageType.getCode()==6){
            builder.and(qMessage.type.eq(messageType));
            builder.and(qMessage.type.eq( MessageType.AT_MESSAGE));
        }
        builder.and(qMessage.deleted.isFalse());
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        return messageRepository.findAll(builder, sort);
    }

    @Override
    public Message createMessage(Message.MessageType messageType, User fromUser, User targetUser) {
        return createMessage(messageType, fromUser, targetUser, null, null, null);
    }

    @Override
    public Message createMessage(Message.MessageType messageType, User fromUser, User targetUser,
                                 Long linkId, Integer linkType, String content) {
        Message message = new Message();
        message.setType(messageType);
        message.setFromUser(fromUser);
        message.setToUser(targetUser);
        if(fromUser==targetUser){
            message.setRead(true);
        }else{
            message.setRead(false);
        }
        message.setExpired(false);
        message.setDeleted(false);
        String title = message.getType().getName() + "通知";
        message.setTitle(title);
        if (content == null) {
            content = getMessageContentByType(messageType, fromUser);
        }
        message.setContent(content);
        message.setLinkId(linkId);
        message.setLinkType(linkType);
        messageRepository.save(message);
        //
        System.out.println("fromUser.getId()==="+fromUser.getId());
        System.out.println("targetUser.getId()==="+targetUser.getId());
        if(!fromUser.getId().equals(targetUser.getId())){
            if (!messageType.equals(MessageType.AT_MESSAGE) || (
                    messageType.equals(MessageType.AT_MESSAGE) && fromUser.isReceiveNotification())) {
                pushMessage(messageType, targetUser, linkId, message);
            }
        }

        return message;
    }

    private void pushMessage(MessageType messageType, User targetUser, Long linkId, Message message) {
        Integer badge = getUserUnreadMessagesNum2(targetUser);// TODO: 2017/10/11
        Map<String, String> extra = new HashMap<>();
        extra.put("id", message.getId() + "");
        extra.put("type", messageType.getCode() + "");
        extra.put("linkId", linkId + "");
        pushService.sendNotification(targetUser, message.getTitle(), message.getContent(), extra, badge);
    }
    @Override
    public Integer getUserUnreadMessagesNum(User user) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.toUser.id.eq(user.getId()));
        builder.and(qMessage.fromUser.id.ne(user.getId()));
        builder.and(qMessage.read.isFalse());
        builder.and(qMessage.deleted.isFalse());
        builder.and(qMessage.expired.isFalse());
        builder.and(qMessage.type.ne(MessageType.AT_MESSAGE));
        return Math.toIntExact(messageRepository.count(builder));
    }
    public Integer getUserUnreadMessagesNum2(User user) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.toUser.id.eq(user.getId()));
        builder.and(qMessage.read.isFalse());
        builder.and(qMessage.fromUser.id.ne(user.getId()));
        builder.and(qMessage.deleted.isFalse());
        builder.and(qMessage.expired.isFalse());
        builder.and(qMessage.type.ne(MessageType.INSTANT_COMMENT_MESSAGE));
        builder.and(qMessage.type.ne(MessageType.AT_MESSAGE));
        return Math.toIntExact(messageRepository.count(builder));
    }

    @Override
    public Integer getUserUnreadMessagesNumByType(User user, MessageType messageType) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.toUser.id.eq(user.getId()));
        builder.and(qMessage.read.isFalse());
        builder.and(qMessage.deleted.isFalse());
        builder.and(qMessage.expired.isFalse());
        builder.and(qMessage.type.eq(messageType));
        builder.and(qMessage.fromUser.id.ne(user.getId()));
        return Math.toIntExact(messageRepository.count(builder));
    }

    @Override
    public void createCommentMessage(TopicComment comment, Long toUserId) {
        Message message = new Message();
        message.setType(MessageType.INSTANT_COMMENT_MESSAGE);
        message.setFromUser(comment.getPublisher());
        User user = userRepository.findOne(toUserId);
        message.setToUser(user);
        message.setRead(false);
        message.setExpired(false);
        message.setDeleted(false);
        String title = message.getType().getName() + "通知";
        message.setTitle(title);
        message.setLinkId(comment.getTopic().getId());
        message.setLinkType(comment.getTopic().getTopicType().getCode());
        message.setSubLinkId(comment.getId());
        //
        try {
            CommentContentBody contentBody = new ObjectMapper().readValue(comment.getContent(), CommentContentBody.class);
            message.setContent(contentBody.getContent());
        } catch (IOException e) {
            message.setContent(comment.getContent());
            logger.error("parse comment content body error!", e);
        }
        messageRepository.save(message);
//        pushMessage(message.getType(), comment.getTopic().getUser(), comment.getTopic().getId(), message);
    }

    @Override
    public void markUserMessageRead(User user, Long messageId) {
        Message message = messageRepository.findOne(messageId);
        if (message == null || message.isRead()) {
            return;
        }
        if (message.getToUser().getId().equals(user.getId())) {
            message.setRead(true);
            messageRepository.save(message);
            System.out.println("markRead==="+message.isRead()+"==id"+messageId);
        } else {
            logger.warn("messsage[" + message + "] is not belong to the user[" + user.getId() + "]");
        }

    }

    @Override
    public void markUserMessagesRead(User user, MessageType type) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.toUser.id.eq(user.getId()))
                .and(qMessage.deleted.isFalse())
                .and(qMessage.type.eq(type))
                .and(qMessage.read.isFalse());
        Iterable<Message> messages = messageRepository.findAll(builder);
        for (Message message : messages) {
            message.setRead(true);
            messageRepository.save(message);
        }
    }

    @Override
    public void markUserMessagesReadByLinkId(User user, MessageType type, Long linkId) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.toUser.id.eq(user.getId()))
                .and(qMessage.deleted.isFalse())
                .and(qMessage.type.eq(type))
                .and(qMessage.read.isFalse())
                .and(qMessage.linkId.eq(linkId));
        Iterable<Message> messages = messageRepository.findAll(builder);
        for (Message message : messages) {
            message.setRead(true);
            messageRepository.save(message);
        }
    }

    Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private String getMessageContentByType(Message.MessageType messageType, User fromUser) {
        String nickname = fromUser.getNickname();

        if (nickname == null) {
            nickname = "";
        }
        switch (messageType) {
            case INSTANT_COMMENT_MESSAGE:
                return nickname + "发的帖子正在进行实时评论";
            case FOLLOW_MESSAGE:
                return nickname + "关注了你";
            case LIKE_MESSAGE:
                return nickname + "赞了你的帖子";
            case AT_MESSAGE:
                return nickname + "@了你";
            case MARK_MESSAGE:
                return nickname + "@了你";
            default:
                return messageType.getName();
        }
    }


    @Override
    public Map<Integer,List<MessageDto>>
    convertToMessageDtoList(List<Message> messages, boolean queryFollowing) {
        Map<Integer,List<MessageDto>> map = new HashMap<>();
        if (messages == null || messages.isEmpty()) {
            return map;
        }
        List<MessageDto> dtos = new ArrayList<>();
        Integer unreadNum=0;
        for (Message message : messages) {
            MessageDto dto = new MessageDto(message);
            MessageType messageType = dto.getMessageType();
            if(dto.isRead()==false){
                unreadNum++;
            }
            if (queryFollowing) {
                UserDetailDto fromUser = dto.getFromUser();
                fromUser.setFollowing(followService.isFollowing(dto.getToUser().getId(), fromUser.getId()));
                fromUser.setBlockMe(blockUserService.isUserBlockedByOne(message.getToUser(), fromUser.getId()));
                fromUser.setBlock(blockUserService.isUserBlockedByOne(message.getFromUser(), message.getToUser().getId()));
            }
            if (messageType.equals(MessageType.INSTANT_COMMENT_MESSAGE) ||
                    messageType.equals(MessageType.AT_MESSAGE) ||
                    messageType.equals(MessageType.MARK_MESSAGE) ||
                    messageType.equals(Message.MessageType.LIKE_MESSAGE)) {
                fulfillWithTopic(dto);
            }
            dtos.add(dto);
        }
        map.put(unreadNum,dtos);
        return map;
    }

    private void fulfillWithTopic(MessageDto dto) {
        Long topicId = dto.getLinkId();
        if (topicId != null) {
            System.out.println("=======fulfillWithTopic======"+topicId);
            Topic topic = topicRepository.findOne(topicId);
            String cover = topic.getCover();
            if (cover == null) {
                cover = topic.getContent().split(",")[0];
            }
            dto.setLinkType(topic.getTopicType().getCode());
            dto.getExtra().put("cover", cover);
            dto.getExtra().put("type", topic.getTopicType().getCode());
            dto.getExtra().put("content", topic.getContent());
            dto.getExtra().put("burnTopic", topic.isBurnTopic());
            dto.getExtra().put("creationTime", topic.getCreationTime());
            dto.getExtra().put("aliveTime", topic.getAliveTime());
        }
    }

    @Override
    public Page<MessageDto> getUserMessageDtos(User user, MessageType type, Integer page, Integer size) {
        Page<Message> messagePage = null;
        messagePage = getUserMessages(user, type, page, size);
        assert messagePage != null;
        Map<Integer,List<MessageDto>>  map = convertToMessageDtoList(messagePage.getContent(), true);
        List<MessageDto> list=new ArrayList<>();
        for (Integer key : map.keySet()) {
            list=map.get(key);
        }
        return new PageImpl<>(list, new PageRequest(page, size), messagePage.getTotalElements());
    }

    @Override
    public Page<Message> getUserMessages(User user, MessageType type, Integer page, Integer size) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.toUser.id.eq(user.getId()));
        builder.and(qMessage.fromUser.id.ne(user.getId()));
        builder.and(qMessage.type.eq(type));
        builder.and(qMessage.deleted.isFalse());
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        Pageable pageable = new PageRequest(page, size, sort);
        return messageRepository.findAll(builder, pageable);
    }

    @Override
    public void deleteMessage(User user, Long messageId) throws UmiException {
        Message message = messageRepository.findOne(messageId);
        if (message != null) {
            if (message.getToUser().getId().equals(user.getId()) ||
                    message.getFromUser().getId().equals(user.getId())) {
                message.setDeleted(true);
                messageRepository.save(message);
            } else {
                throw new UmiException(1000, "您没有删除该通知的权限");
            }
        } else {
            throw new UmiException(1000, "通知不存在");
        }
    }

    @Override
    public void deleteMessageForLink(User user, Long linkId, MessageType type) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.toUser.id.eq(user.getId()))
                .and(qMessage.deleted.isFalse())
                .and(qMessage.type.eq(type))
                .and(qMessage.linkId.eq(linkId));
        Iterable<Message> messages = messageRepository.findAll(builder);
        for (Message message : messages) {
            message.setDeleted(true);
            messageRepository.save(message);
        }
    }

    @Override
    public void deleteMessageForType(User user, MessageType type) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.toUser.id.eq(user.getId()))
                .and(qMessage.deleted.isFalse())
                .and(qMessage.type.eq(type));
        Iterable<Message> messages = messageRepository.findAll(builder);
        for (Message message : messages) {
            message.setDeleted(true);
            messageRepository.save(message);
        }
    }

    @Override
    public void deleteCommentMessageByTopic(Long topicId) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.type.eq(MessageType.INSTANT_COMMENT_MESSAGE));
        builder.and(qMessage.linkId.eq(topicId));
        builder.and(qMessage.deleted.isFalse());
        Iterable<Message> messages = messageRepository.findAll(builder);
        for (Message message : messages) {
            message.setDeleted(true);
            message.setUpdateTime(new Date());
            messageRepository.save(message);
        }
    }

    @Override
    public void deleteCommentMessage(Long commentId) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.type.eq(MessageType.INSTANT_COMMENT_MESSAGE));
        builder.and(qMessage.subLinkId.eq(commentId));
        builder.and(qMessage.deleted.isFalse());
        Iterable<Message> messages = messageRepository.findAll(builder);
        for (Message message : messages) {
            message.setDeleted(true);
            message.setUpdateTime(new Date());
            messageRepository.save(message);
        }
    }

    @Override
    public void deleteFollowMessage(Long fromUserId, Long targetUserId) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.type.eq(MessageType.FOLLOW_MESSAGE));
        builder.and(qMessage.fromUser.id.eq(fromUserId));
        builder.and(qMessage.toUser.id.eq(targetUserId));
        builder.and(qMessage.deleted.isFalse());
        Iterable<Message> messages = messageRepository.findAll(builder);
        for (Message message : messages) {
            message.setDeleted(true);
            message.setUpdateTime(new Date());
            messageRepository.save(message);
        }
    }

    @Override
    public Message getMessageByUserAndLink(User user, Long topicId,Message.MessageType type) {
        QMessage qMessage = QMessage.message;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMessage.type.eq(type));
        builder.and(qMessage.fromUser.id.eq(user.getId()));
        builder.and(qMessage.linkId.eq(topicId));
        builder.and(qMessage.deleted.isFalse());
        Message messages = messageRepository.findOne(builder);
        return messages;
    }
}
