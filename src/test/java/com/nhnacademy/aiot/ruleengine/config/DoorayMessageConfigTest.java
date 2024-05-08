package com.nhnacademy.aiot.ruleengine.config;

import com.nhn.dooray.client.DoorayHookSender;
import com.nhnacademy.aiot.ruleengine.send.MessageSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"message.dooray.hook-url=test"}) //Mock your property here
public class DoorayMessageConfigTest {

    @Autowired
    private DoorayHookSender doorayHookSender;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void restTemplateTest() {
        RestTemplate rt = restTemplate;
        assertThat(rt).isNotNull();
    }

    @Test
    public void doorayHookSenderTest() {
        assertThat(doorayHookSender).isNotNull();
    }

    @Test
    public void messageSenderTest() {
        assertThat(messageSender).isNotNull();
    }

}