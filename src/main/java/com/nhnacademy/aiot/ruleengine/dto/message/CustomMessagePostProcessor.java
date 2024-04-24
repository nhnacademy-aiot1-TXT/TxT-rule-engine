package com.nhnacademy.aiot.ruleengine.dto.message;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

public class CustomMessagePostProcessor implements MessagePostProcessor {
    private final Integer ttl;

    public CustomMessagePostProcessor(final Integer ttl) {
        this.ttl = ttl;
    }

    @Override
    public Message postProcessMessage(final Message message) throws AmqpException {
        message.getMessageProperties().setExpiration(ttl.toString());
        return message;
    }
}
