package com.ugoodtech.umi.manager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UmiManagerApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testHome() throws Exception {

        ResponseEntity<String> entity = this.restTemplate.getForEntity("/home", String.class);
        assertTrue(entity.getStatusCode().equals(HttpStatus.OK));
        assertTrue("Hello World".equals(entity.getBody()));
    }
}