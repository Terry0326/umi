package com.ugoodtech.umi.client;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ugoodtech.umi.client.dto.CommentContentBody; 
import com.ugoodtech.umi.client.dto.CommentMessage;
import com.ugoodtech.umi.client.service.TopicService;
import com.ugoodtech.umi.core.domain.TopicComment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.io.IOException;

@Component
public class SocketServer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private SocketIOServer server;
    @Value("${ws.hostname}")
    private String hostname;
    @Value("${ws.port}")
    private int port;
    @Autowired
    private TopicService topicService;

    @PostConstruct
    public void init() {
        initServer();
        startServer();
    }

    /**
     * 初始化服务端
     *
     * @return
     */
    private SocketIOServer initServer() {
        Configuration config = new Configuration();
        config.setHostname(hostname);
        config.setPort(port);
        //该处可以用来进行身份验证
        config.setAuthorizationListener(new AuthorizationListener() {
            @Override
            public boolean isAuthorized(HandshakeData data) {
                //http://localhost:8081?username=test&password=test
                //例如果使用上面的链接进行connect，可以使用如下代码获取用户密码信息，本文不做身份验证
//              String username = data.getSingleUrlParam("username");
//              String password = data.getSingleUrlParam("password");
                return true;
            }
        });
        server = new SocketIOServer(config);

        return server;
    }

    /**
     * 启动服务端
     */
    public void startServer() {
        // 添加连接监听
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                logger.info("server 服务端启动成功");
            }
        });
        // 添加断开连接监听
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                logger.info("server 服务端断开连接");
            }
        });
        // 添加事件监听
        server.addEventListener("join", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String str,
                               AckRequest ackRequest)
                    throws Exception {
                logger.info("收到客户端加入消息：" + str);
                server.getBroadcastOperations().sendEvent("joinSuccess", "join success");
            }
        });
        // 添加事件监听
        server.addEventListener("commentMessage", CommentMessage.class, new DataListener<CommentMessage>() {
            @Override
            public void onData(SocketIOClient socketIOClient, CommentMessage message,
                               AckRequest ackRequest)
                    throws Exception {
                logger.info("收到客户端消息：" + message.toString());

            }
        });
        // 启动服务端
        server.start();
    }

    public void sendComment(TopicComment comment) {
        CommentMessage message = new CommentMessage();
        message.setBurnable(comment.isBurnable());
        message.setTopicId(comment.getTopic().getId());
        message.setUserId(comment.getPublisher().getId());
        message.setUserNickname(comment.getPublisher().getNickname());
        ObjectMapper mapper = new ObjectMapper();
        try {
            CommentContentBody body = mapper.readValue(comment.getContent(), CommentContentBody.class);
            message.setContentBody(body);
            String content = mapper.writeValueAsString(message.getContentBody());
            server.getBroadcastOperations().sendEvent("commentMessage", content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止服务端
     */
    public void stopServer() {
        server.stop();
    }
}
