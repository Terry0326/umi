package com.ugoodtech.umi.client;

import com.ugoodtech.umi.client.service.TopicService;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.repository.TopicRepository;
import javafx.application.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class ServiceTest {
    @Autowired
    TopicService topicService;

    @Test
    public void insertShop() throws Exception {
        topicService.updateTopicClickNum(159L);
    }
}
