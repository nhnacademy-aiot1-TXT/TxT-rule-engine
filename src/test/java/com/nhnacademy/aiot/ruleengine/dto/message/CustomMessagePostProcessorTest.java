package com.nhnacademy.aiot.ruleengine.dto.message;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomMessagePostProcessorTest {

    @Test
    void postProcessMessageSetsTtl() {
        int ttl = 123;
        CustomMessagePostProcessor postProcessor = new CustomMessagePostProcessor(ttl);
        Message message = new Message("test".getBytes(), new MessageProperties());

        Message processedMessage = postProcessor.postProcessMessage(message);

        assertEquals(ttl, Integer.parseInt(processedMessage.getMessageProperties().getExpiration()));
    }
}