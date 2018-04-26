package com.ugoodtech.umi.client.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.client.SocketServer;
import com.ugoodtech.umi.client.dto.CommentContentBody;
import com.ugoodtech.umi.client.dto.CommentDto;
import com.ugoodtech.umi.client.dto.Location;
import com.ugoodtech.umi.client.dto.RichTopic;
import com.ugoodtech.umi.client.service.*;
import com.ugoodtech.umi.client.util.LatitudeLontitudeUtil;
import com.ugoodtech.umi.client.util.StringMatcher;
import com.ugoodtech.umi.core.domain.*;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.*;

import com.ugoodtech.umi.core.utils.DateUtil;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TopicServiceImpl implements TopicService {
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private TopicTipOffRepository topicTipOffRepository;
    @Autowired
    private FollowService followService;
    @Autowired
    private BlockUserService blockUserService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserManager userService;
    @Autowired
    private SensitiveRepository sensitiveRepository;
    @Autowired
    private SocketServer socketServer;
    @Autowired
    private AliYunService aliYunService;
    //
    private JdbcTemplate jdbcTemplate;
    static String  burnParam=null;
    static String  param=null;
    @Autowired
    public void setDatasource(DataSource datasource) {
        jdbcTemplate = new JdbcTemplate(datasource);
    }

    @Override
    @Transactional
    public Topic createTopic(User user, Topic topic, String markUserIds) throws UmiException {
        logger.debug("create topic with content:" + topic.getContent());
        if (!StringUtils.isEmpty(topic.getDescription())) {
            Iterable<SensitiveWord> sensitiveWords = sensitiveRepository.findAll();
            for (SensitiveWord sensitiveWord : sensitiveWords) {
                if (StringMatcher.indexOf(topic.getDescription(), sensitiveWord.getName()) >= 0) {
                    throw new UmiException(1000, "包含敏感字,无法发布该帖");
                }
            }
        }
        topic.setEnabled(true);
        topicRepository.save(topic);
        //
        User dbUser = userRepository.findOne(user.getId());
        dbUser.setPublishTopicsNum(getUserTopicNum(user));
        userRepository.save(dbUser);
        //
        if (!StringUtils.isEmpty(markUserIds)) {
            String[] markUserIdArray = markUserIds.split(",");
            for (String userId : markUserIdArray) {
                Long id = Long.valueOf(userId);
                User atUser = userRepository.findOne(id);
                String content = user.getNickname() + "在帖子中@了你";
                messageService.createMessage(Message.MessageType.MARK_MESSAGE, dbUser, atUser,
                        topic.getId(), topic.getTopicType().getCode(), content);
            }
        }
        return topic;
    }


    private Long getUserTopicNum(User user) {
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopic.user.id.eq(user.getId()));
        builder.and(qTopic.deleted.isFalse());
        return topicRepository.count(builder);
    }

    @Override
    public void deleteTopic(User user, Long topicId) throws UmiException {
        Topic topic = topicRepository.findOne(topicId);
        if (topic.getUser().getId().equals(user.getId())) {
            topic.setDeleted(true);
            topic.setUpdateTime(new Date());
            topicRepository.save(topic);
            messageService.deleteCommentMessageByTopic(topicId);
        } else {
            throw new UmiException(1000, "不能删除别人创建的帖子");
        }
    }

    @Override
    public TopicComment addComment(Long commentUserId, Long topicId, String content, boolean burnable) throws UmiException {
        //
        User user = userRepository.findOne(commentUserId);
        CommentContentBody contentBody = null;
        try {
            contentBody = new ObjectMapper().readValue(content, CommentContentBody.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert contentBody != null;
        String plainText = contentBody.getContent();
        if (StringUtils.isEmpty(plainText)) {
            throw new UmiException(1000, "不能提交内容为空的评论");
        }
        String msg = plainText;
        Iterable<SensitiveWord> sensitiveWords = sensitiveRepository.findAll();
        for (SensitiveWord sensitiveWord : sensitiveWords) {
            if (StringMatcher.indexOf(msg, sensitiveWord.getName()) >= 0) {
                throw new UmiException(1000, "包含敏感字,无法发布该评论");
            }
        }
        //
        Topic topic = topicRepository.findOne(topicId);
        TopicComment comment = new TopicComment();
        comment.setContent(content);
        comment.setPublisher(user);
        comment.setDeleted(false);
        comment.setTopic(topic);
        comment.setBurnable(burnable);
        comment.setBurned(false);
        commentRepository.save(comment);
        countUserCommentNum(user.getId());
        //
//        notificationService.sendComment(new CommentDto(comment), topicId);
        //
        topic.setCommentsNum(getTopicCommentsNum(topic.getId()));
        topicRepository.save(topic);
        //
        socketServer.sendComment(comment);
        //发送评论通知给所有评论者,当前评论用户除外
//        Collection<Long> notificationUserIds = getTopicCommentUserIds(topic.getId());
//        notificationUserIds.remove(user.getId());
//        for (Long userId : notificationUserIds) {
//            messageService.createCommentMessage(comment, userId);
//        }
        //发送评论通知给帖子作者
        if (!blockUserService.isUserBlockedByOne(topic.getUser(), user.getId())) {
            messageService.createCommentMessage(comment, topic.getUser().getId());
        }
        //
        if (contentBody.getAt() != null) {
            List<Map> atList = contentBody.getAt();
            for (Map map : atList) {
                Long userId = Long.valueOf((Integer) map.get("username"));
                User atUser = userRepository.findOne(userId);
                Integer linkType = topic.getTopicType().getCode();
                messageService.createMessage(Message.MessageType.AT_MESSAGE, user, atUser, topicId, linkType, plainText);
            }
        }
        //
        return comment;
    }

    final String queryCommentsUserIds = "select distinct publisher_id from topic_comments where topic_id=? and deleted=false";

    private Collection<Long> getTopicCommentUserIds(Long topicId) {
        final Set<Long> userIds = new HashSet<>();
        jdbcTemplate.query(queryCommentsUserIds, new Object[]{topicId}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                userIds.add(resultSet.getLong("publisher_id"));
            }
        });
        return userIds;
    }

    private Long getTopicCommentsNum(Long topicId) {
        QTopicComment qTopicComment = QTopicComment.topicComment;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopicComment.deleted.isFalse());
        builder.and(qTopicComment.burned.isFalse());
        builder.and(qTopicComment.topic.id.eq(topicId));
        return commentRepository.count(builder);
    }

    private void countUserCommentNum(Long userId) {
        QTopicComment qTopicComment = QTopicComment.topicComment;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopicComment.deleted.isFalse());
        builder.and(qTopicComment.publisher.id.eq(userId));
        Long count = commentRepository.count(builder);
        User user = userRepository.findOne(userId);
        user.setCommentNum(count);
        userRepository.save(user);
    }

    @Override
    public void like(User user, Long topicId) throws UmiException{
        logger.debug(user.getUsername() + " make a like to topic with id:" + topicId);
        Topic topic = topicRepository.findOne(topicId);
        if (topic == null) {
            throw new UmiException(1000, "找不到该帖子");
        }
        Iterable<TopicLike> likeIterable = findLike(user, topicId);
        if (!likeIterable.iterator().hasNext()) {
            TopicLike like = new TopicLike();
            like.setUser(user);
            like.setDeleted(false);
            like.setTopic(topic);
            likeRepository.save(like);
            //
            updateUserTopicLikeNumber(topic);
            //
            topic.setLikeNum(getTopicLikeNum(topic.getId()));
            topicRepository.save(topic);
            //
            System.out.println(!blockUserService.isUserBlockedByOne(topic.getUser(), user.getId()));
            System.out.println(topic.getUser());
            System.out.println(user.getId());
            if (!blockUserService.isUserBlockedByOne(topic.getUser(), user.getId())) {
//                if (!user.getId().equals(topic.getUser().getId())){
                Integer linkType = topic.getTopicType().getCode();
                String content=user.getNickname()+"赞了你的帖子";
                System.out.println("================================"+topic.getUser().getNickname());
                messageService.createMessage(Message.MessageType.LIKE_MESSAGE,user,topic.getUser(),topicId,linkType,content);
//                }
//                messageService.createMessage(Message.MessageType.LIKE_MESSAGE, user, topic.getUser(), topicId, linkType, content:null);
            }
        } else {
            throw new UmiException(1000, "您已经点赞");
        }
    }

    private void updateUserTopicLikeNumber(Topic topic) {
        User author = userRepository.findOne(topic.getUser().getId());
        author.setTopicLikeNum(getUserLikeNum(author.getId()));
        userRepository.save(author);
    }

    @Override
    public Page<Topic> getHomeTopics(User user, Pageable pageable) {
        //
        Collection<Long> followingUserIds = followService.getFollowingUserIds(user.getId());
        Collection<Long> excludeUserIds = getExcludeFollowerIds(user);
        followingUserIds.removeAll(excludeUserIds);
        followingUserIds.add(user.getId());
        //
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopic.user.id.in(followingUserIds));
        builder.and(qTopic.anonymous.isFalse());
        builder.and(qTopic.timed.isTrue());
        builder.and(qTopic.anonymous.isFalse());
        addValidConstraint(qTopic, builder);
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return topicRepository.findAll(builder, pageable);

    }

    private Collection<Long> getExcludeFollowerIds(User user) {
        Collection<Long> excludeUserIds = new HashSet<>();
        excludeUserIds.addAll(blockUserService.getNotSeeItsTopicsUserIds(user.getId()));
        excludeUserIds.addAll(blockUserService.getBlockUserExecutorIds(user.getId()));
        return excludeUserIds;
    }

    private void addValidConstraint(QTopic qTopic, BooleanBuilder builder) {
        builder.and(qTopic.deleted.isFalse());
        builder.and(qTopic.enabled.isTrue());
        builder.and(qTopic.user.enabled.isTrue());
    }

    @Override
    public long getTopicLikeNum(Long topicId) {
        QTopicLike qTopicLike = QTopicLike.topicLike;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopicLike.deleted.isFalse());
        builder.and(qTopicLike.topic.id.eq(topicId));
        return likeRepository.count(builder);
    }

    private long getUserLikeNum(Long userId) {
        QTopicLike qTopicLike = QTopicLike.topicLike;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopicLike.deleted.isFalse());
        builder.and(qTopicLike.user.id.eq(userId));
        return likeRepository.count(builder);
    }

    @Override
    public void unlike(User user, Long topicId) throws UmiException {
        logger.debug(user.getUsername() + " make a unlike to topic with id:" + topicId);
        Iterable<TopicLike> likeIterable = findLike(user, topicId);
        if (!likeIterable.iterator().hasNext()) {
            throw new UmiException(1000, "您没有对该帖子点过赞");
        }
        for (TopicLike like : likeIterable) {
            like.setDeleted(true);
            like.setUpdateTime(new Date());
            likeRepository.save(like);
        }
        Message message=messageService.getMessageByUserAndLink(user,topicId, Message.MessageType.LIKE_MESSAGE);
        message.setDeleted(true);
        message.setUpdateTime(new Date());
        messageRepository.save(message);
        //
        Topic topic = topicRepository.findOne(topicId);
        updateUserTopicLikeNumber(topic);
        topic.setLikeNum(getTopicLikeNum(topic.getId()));
        topicRepository.save(topic);
    }

    @Override
    public Topic getTopic(Long topicId) {
        return topicRepository.findOne(topicId);
    }

    @Override
    public Page<TopicComment> getTopicComments(User user, Long topicId, Pageable pageable) {
        if (pageable.getSort() == null) {
            Sort creationTime = new Sort(Sort.Direction.DESC, "creationTime");
            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), creationTime);
        }
        Topic topic = topicRepository.findOne(topicId);
        QTopicComment qTopicComment = QTopicComment.topicComment;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopicComment.deleted.isFalse());
        builder.and(qTopicComment.topic.id.eq(topicId));
        builder.and(qTopicComment.burned.isFalse());
        if (user != null) {
            Collection<Long> execluedCommentUserIds = blockUserService.getBlockUserExecutorIds(topic.getUser().getId());
            builder.and(new BooleanBuilder(qTopicComment.topic.user.id.ne(user.getId()))
                    .or(new BooleanBuilder(qTopicComment.topic.user.id.eq(user.getId()))
                            .and(qTopicComment.publisher.id.notIn(execluedCommentUserIds))));
        }
        return commentRepository.findAll(builder, pageable);
    }

    @Override
    public void tipOff(User user, Long topicId, String reason) {
        Topic topic = topicRepository.findOne(topicId);
        TopicTipOff tipOff = new TopicTipOff();
        tipOff.setUser(user);
        tipOff.setTopic(topic);
        tipOff.setReason(reason);
        topicTipOffRepository.save(tipOff);
    }

    @Override
    public Page<Topic> getUserPopularTopics(Long userId, Pageable pageable,Boolean anonymous) {
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        addValidConstraint(qTopic, builder);
        builder.and(qTopic.user.id.eq(userId));
        if(anonymous==false){
            builder.and(qTopic.anonymous.isFalse());
        }
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "weight"),
                new Sort.Order(Sort.Direction.DESC, "creationTime"));
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return topicRepository.findAll(builder, pageable);
    }

    @Override
    public Page<Topic> getUserTopics(Long userId, Pageable pageable,Boolean anonymous) {
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopic.user.id.eq(userId));
        if(anonymous==false){
            builder.and(qTopic.anonymous.isFalse());
        }
        builder.and(qTopic.timed.isTrue());
        addValidConstraint(qTopic, builder);
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return topicRepository.findAll(builder, pageable);
    }

    /**
     * 附近用户帖子
     *
     * @param user
     * @param lat
     * @param lng
     * @param distance
     * @param pageable
     * @return
     */
    @Override
    public Page<Topic> getNearByTopics(User user, Double lat, Double lng, Integer distance, Pageable pageable) throws UmiException {
        Collection<Long> nearByUserIds = userService.getNearByUserIds(user.getId(), lat, lng, distance);
        //
        Collection<Long> excludeUserIds = getExcludeFollowerIds(user);
        nearByUserIds.removeAll(excludeUserIds);
        if (!nearByUserIds.isEmpty()) {
            QTopic qTopic = QTopic.topic;
            BooleanBuilder builder = new BooleanBuilder();
            addValidConstraint(qTopic, builder);
            builder.and(qTopic.anonymous.isFalse());
            builder.and(qTopic.user.id.in(nearByUserIds));
            builder.and(qTopic.burnTopic.isFalse());
            builder.and(qTopic.timed.isTrue());
            Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
            return topicRepository.findAll(builder, pageable);
        } else {
            return new PageImpl<>(new ArrayList<Topic>(), pageable, 0);
        }

    }
    /**
     * 附近帖子
     *
     * @param user
     * @param lat
     * @param lng
     * @param distance
     * @param pageable
     * @return
     */
    @Override
    public Map<Long,List<Topic>> getNearTopics(User user, Double lat, Double lng, Integer distance, Pageable pageable) throws UmiException {
        Location[] locations = LatitudeLontitudeUtil.getRectangle4Point(lat,lng, distance);
        System.out.println("Double lat"+locations[2].getLatitude()+"===="+locations[0].getLatitude()+"pageable.getPageNumber()"+pageable.getPageNumber()+"pageable.getPageSize()"+pageable.getPageSize());
        Map<Long,List<Topic>> map=new HashMap<Long,List<Topic>>();
        List<Topic> li=new ArrayList<>();
        li=topicRepository.getNearTopics(locations[2].getLatitude() ,locations[0].getLatitude(),locations[0].getLongitude(),locations[1].getLongitude());

        Long total=topicRepository.getNearTopicCount(locations[2].getLatitude() ,locations[0].getLatitude(),locations[0].getLongitude(),locations[1].getLongitude());
        map.put(total,li);
        return map;
    }
    /**
     * 附近帖子
     *
     * @param user
     * @param lat
     * @param lng
     * @param distance
     * @param pageable
     * @return
     */
    @Override
    public  Page<Topic> getNearTopics2(User user, Double lat, Double lng, Integer distance, Pageable pageable) throws UmiException {
        Location[] locations = LatitudeLontitudeUtil.getRectangle4Point(lat,lng, distance);
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopic.enabled.isTrue());
        builder.and(qTopic.user.id.notIn(user.getId()));
        builder.and(qTopic.burnTopic.isFalse());
        builder.and(qTopic.timed.isTrue());
        builder.and(qTopic.deleted.isFalse());
        builder.and(qTopic.address.lat.between(locations[2].getLatitude(),locations[0].getLatitude()));
        builder.and(qTopic.address.lng.between(locations[0].getLongitude(),locations[1].getLongitude()));
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return topicRepository.findAll(builder, pageable);
    }
    @Override
    public Page<Topic> getChinaTopics(User user, Pageable pageable) {
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        addValidConstraint(qTopic, builder);
        builder.and(qTopic.anonymous.isFalse());
        builder.and(qTopic.user.dialingCode.eq("86"));
        builder.and(qTopic.timed.isTrue());
        Collection<Long> excludeUserIds = getExcludeFollowerIds(user);
        if (!excludeUserIds.isEmpty()) {
            builder.and(qTopic.user.id.notIn(excludeUserIds));
        }
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return topicRepository.findAll(builder, pageable);
    }

    @Override
    public Page<Topic> getOverseaTopics(User user, Pageable pageable) {
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        addValidConstraint(qTopic, builder);
        builder.and(qTopic.user.dialingCode.ne("86"));
        builder.and(qTopic.anonymous.isFalse());
        builder.and(qTopic.timed.isTrue());
        Collection<Long> excludeUserIds = getExcludeFollowerIds(user);
        if (!excludeUserIds.isEmpty()) {
            builder.and(qTopic.user.id.notIn(excludeUserIds));
        }
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return topicRepository.findAll(builder, pageable);
    }

    /**
     * 焚烧帖列表,获取全世界的,不只是关注的
     * @param user
     * @param pageable
     * @return
     */
    @Override
    public Page<Topic> getBurnTopics(User user, Pageable pageable) {
        //
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        Collection<Long> excludeUserIds = getExcludeFollowerIds(user);
        if (!excludeUserIds.isEmpty()) {
            builder.and(qTopic.user.id.notIn(excludeUserIds));
        }
        builder.and(qTopic.burnTopic.isTrue());
        builder.and(qTopic.timed.isTrue());
        addValidConstraint(qTopic, builder);
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return topicRepository.findAll(builder, pageable);
    }

    @Override
    public boolean isUserLiked(User user, Long topicId) {
        QTopicLike qTopicLike = QTopicLike.topicLike;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopicLike.deleted.isFalse());
        builder.and(qTopicLike.user.id.eq(user.getId()));
        builder.and(qTopicLike.topic.id.eq(topicId));
        return likeRepository.count(builder) > 0L;
    }

    @Override
    public Page<Topic> query(User user, String qKey, Pageable pageable) {
        if (pageable.getSort() == null) {
            Sort creationTime = new Sort(Sort.Direction.DESC, "creationTime");
            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), creationTime);
        }
        QTopic qTopic = QTopic.topic;
        String likeStr = "%" + qKey + "%";
        BooleanBuilder builder = new BooleanBuilder();
        addValidConstraint(qTopic, builder);
        //
        Collection<Long> excludeUserIds = getExcludeFollowerIds(user);
        if (!excludeUserIds.isEmpty()) {
            builder.and(qTopic.user.id.notIn(excludeUserIds));
        }
        builder.and(qTopic.anonymous.isFalse());
        builder.and(qTopic.timed.isTrue());
        builder.and(new BooleanBuilder()
                .or(qTopic.description.like(likeStr))
                .or(qTopic.user.nickname.like(likeStr)));
        return topicRepository.findAll(builder, pageable);
    }

    @Override
    public Long getBurnableCommentsNum(Long topicId) {
        QTopicComment qTopicComment = QTopicComment.topicComment;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopicComment.deleted.isFalse());
        builder.and(qTopicComment.topic.id.eq(topicId));
        builder.and(qTopicComment.burnable.isTrue());
        return commentRepository.count(builder);
    }

    private Iterable<TopicLike> findLike(User user, Long topicId) {
        QTopicLike qTopicLike = QTopicLike.topicLike;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopicLike.user.id.eq(user.getId()));
        builder.and(qTopicLike.topic.id.eq(topicId));
        builder.and(qTopicLike.deleted.isFalse());
        return likeRepository.findAll(builder);
    }

    @Override
    public RichTopic getRichTopic(User user, Topic topic, int commentPageSize) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
        Date date =  formatter.parse(topic.getAliveTime());
        RichTopic richTopic = new RichTopic(topic);
        if(null!=topic.getAliveTime()){
            String time=DateUtil.differentDaysByMillisecond(new Date(),date);
            richTopic.setAliveTime(time);
        }
        richTopic.setLikeNum(topic.getLikeNum() == null ? 0 : topic.getLikeNum());
        richTopic.setLiked(isUserLiked(user, topic.getId()));
        //
        richTopic.setCommentsNum(topic.getCommentsNum() == null ? 0 : topic.getCommentsNum());
        richTopic.setBurnableCommentsNum(getBurnableCommentsNum(topic.getId()));
        //
        Pageable commentsPage = new PageRequest(0, commentPageSize, new Sort(Sort.Direction.DESC, "creationTime"));
        Page<TopicComment> comments = getTopicComments(user, topic.getId(), commentsPage);
        for (TopicComment comment : comments) {
            richTopic.getComments().add(new CommentDto(comment));
        }
        Long topicUserId = richTopic.getUser().getId();
        richTopic.setOwner(topicUserId.equals(user.getId()));
        richTopic.setClickNum(topic.getClickNum());
        richTopic.getUser().setFollowing(followService.isFollowing(user.getId(), topicUserId));
        return richTopic;
    }

    @Override
    public Page<Topic> getWorldTopics(User user, Pageable pageable) {
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        addValidConstraint(qTopic, builder);
        builder.and(qTopic.anonymous.isFalse());
        Collection<Long> excludeUserIds = getExcludeFollowerIds(user);
        if (!excludeUserIds.isEmpty()) {
            builder.and(qTopic.user.id.notIn(excludeUserIds));
        }
        Sort sort = new Sort(Sort.Direction.DESC, "creationTime");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return topicRepository.findAll(builder, pageable);
    }

    @Override
    public void setTopicCover(String videoId, String coverUrl) {
        Topic topic = getTopicByVideoId(videoId);
        if (topic != null) {
            logger.debug("set cover url for topic id:" + topic.getId());
            topic.setCover(coverUrl);
            topicRepository.save(topic);
        } else {
            logger.warn("not found topic by video id:" + videoId);
        }
    }

    private Topic getTopicByVideoId(String videoId) {
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopic.content.eq(videoId));
        builder.and(qTopic.topicType.eq(Topic.TopicType.Video));
        return topicRepository.findOne(builder);
    }

    @Override
    public void addPlayUrl(String videoId, String fileUrl) {
        Topic topic = getTopicByVideoId(videoId);
        if (topic != null) {
            logger.debug("set play url for topic id:" + topic.getId());
            if (topic.getPlayUrls() != null) {
                topic.setPlayUrls(topic.getPlayUrls() + "," + fileUrl);
            } else {
                topic.setPlayUrls(fileUrl);
            }
            topicRepository.save(topic);
        } else {
            logger.warn("not found topic by video id:" + videoId);
        }
    }

    @Override
    public void deleteComment(Long userId, Long commentId) throws UmiException {
        TopicComment comment = commentRepository.findOne(commentId);
        if (comment != null) {
            if (comment.getPublisher().getId().equals(userId)) {
                comment.setDeleted(true);
                comment.setUpdateTime(new Date());
                commentRepository.save(comment);
                messageService.deleteCommentMessage(commentId);
                //
                countUserCommentNum(userId);
                Topic topic = topicRepository.findOne(comment.getTopic().getId());
                topic.setCommentsNum(getTopicCommentsNum(topic.getId()));
                topicRepository.save(topic);
            } else {
                throw new UmiException(1000, "不能删除别人发布的评论");
            }
        } else {
            throw new UmiException(1000, "该评论不存在");
        }
    }
    Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);

    @Override
    public void updateTopicClickNum(Long topicId) throws UmiException {
        Topic topic = topicRepository.findOne(topicId);
        topic.setClickNum(topic.getClickNum()+1);
        topicRepository.save(topic);
    }


    @Override
    public Page<Topic> getRandTopics(User user) {
        Collection<Long> list = new  ArrayList<Long>();
        Integer topicSize=(int)topicRepository.count();
        for (int i=0;i<10;i++){
            Random random = new Random();
            list.add((long)random.nextInt(topicSize));
            list.add((long)random.nextInt(topicSize));
            list.add((long)random.nextInt(topicSize));
            list.add((long)random.nextInt(topicSize));
            list.add((long)random.nextInt(topicSize));
        }
//        String[] params = new String[]{"content","playUrls","aliveTime","creationTime","id","description","likeNum","clickNum","commentsNum"};
//        param= params[(int) (Math.random() *9)];
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        addValidConstraint(qTopic, builder);
        builder.and(qTopic.anonymous.isFalse());
        builder.and(qTopic.id.in(list));
        builder.and(qTopic.timed.isTrue());
        Collection<Long> excludeUserIds = getExcludeFollowerIds(user);
        if (!excludeUserIds.isEmpty()) {
            builder.and(qTopic.user.id.notIn(excludeUserIds));
        }
        Pageable   pageable = new PageRequest(0,15);
        return topicRepository.findAll(builder, pageable);
    }

    @Override
    public Page<Topic> getRandBurnTopics(User user, Pageable pageable) {
        if(pageable.getPageNumber()==0){
            String[] params = new String[]{"content","playUrls","aliveTime","creationTime","id","description","likeNum","clickNum","commentsNum"};
            burnParam= params[(int) (Math.random() *9)];
        }
        if(null==burnParam){
            String[] params = new String[]{"content","playUrls","aliveTime","creationTime","id","description","likeNum","clickNum","commentsNum"};
            burnParam= params[(int) (Math.random() *9)];
        }
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        Collection<Long> excludeUserIds = getExcludeFollowerIds(user);
        excludeUserIds.add(user.getId());
        if (!excludeUserIds.isEmpty()) {
            builder.and(qTopic.user.id.notIn(excludeUserIds));
        }
        builder.and(qTopic.timed.isTrue());
        builder.and(qTopic.burnTopic.isTrue());
        addValidConstraint(qTopic, builder);
        Sort.Direction so=null;
        if( (Math.random() *2)==0){so= Sort.Direction.DESC;}
        else {so= Sort.Direction.ASC;}
        Sort sort = new Sort(so,burnParam);
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return topicRepository.findAll(builder, pageable);
    }

    @Override
    public Boolean isDelete(Long topicId) {
        return topicRepository.getDeteled(topicId);
    }

    @Override
    public Page<Topic> getHotTopics(User user,Pageable pageable) {
        QTopic qTopic = QTopic.topic;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTopic.timed.isTrue());
        Collection<Long> excludeUserIds = getExcludeFollowerIds(user);
        excludeUserIds.add(user.getId());
        if (!excludeUserIds.isEmpty()) {
            builder.and(qTopic.user.id.notIn(excludeUserIds));
        }
        addValidConstraint(qTopic, builder);
        Sort sort = new Sort(Sort.Direction.DESC,"clickNum");
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return  topicRepository.findAll(builder, pageable);
    }

//	@Override
//	public  Topic getHomeTopics2(Pageable pageable) {
//		//return topicRepository.findAll(pageable);
//		 //	return topicRepository.findOne((long) 1);
//		return new Topic();
//	}


    /*
    *   不看他的帖子
    *   移除不看他的帖子
    *
    * */

  /*  @Override
    public void notSeeItsTopics(Long executorId, Long blockerId) {
        User user=userRepository.findOne(executorId);
        Topic blackTopic=topicRepository.findOne();
    }

    @Override
    public void removeNotSeeItsTopics(Long executorId, Long blockerId) {

    }*/
}
