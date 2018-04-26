package com.ugoodtech.umi.manager.dto;

import com.ugoodtech.umi.core.domain.Message;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.repository.MessageRepository;
import com.ugoodtech.umi.manager.service.PushService;
import com.ugoodtech.umi.manager.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Kotone
 * Date: 2017/8/17
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("ALL")
public class MessageTask extends TimerTask {

    private UserService userService;

    private String countries;

    private Message savedMessage;

    private MessageRepository messageRepository;

    private PushService pushService;

    @SuppressWarnings("AlibabaAvoidUseTimer")
    private Timer timer;



    @SuppressWarnings("AlibabaAvoidUseTimer")
    public MessageTask(UserService userService, String countries, Message savedMessage, MessageRepository messageRepository, PushService pushService, Timer timer){
        this.userService=userService;
        this.countries=countries;
        this.savedMessage=savedMessage;
        this.messageRepository=messageRepository;
        this.pushService=pushService;
        this.timer=timer;
    }

    @Override
    public void run() {
        Page<User> clientUsers = userService.queryClientUser(null, null, countries, null, new PageRequest(0, 10000));
        Map<String, String> extra = new HashMap<>();
        extra.put("type", savedMessage.getType().getCode() + "");
        boolean timerCancel=false;
        for(User user:clientUsers){
            if(messageRepository.findOne(savedMessage.getId()).isExpired()){
                timer.cancel();
                timerCancel=true;
            }else{
                pushService.sendNotification(user, savedMessage.getTitle(), savedMessage.getContent(), extra);
            }
        }
        if(!timerCancel){
            savedMessage.setRead(true);
            messageRepository.save(savedMessage);
            timer.cancel();

        }
    }
}
