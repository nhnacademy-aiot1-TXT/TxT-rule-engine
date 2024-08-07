package com.nhnacademy.aiot.ruleengine.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RabbitMQConfigTest {
    @Mock
    private ConnectionFactory connectionFactory;

    @Test
    void testRabbitTemplate() {
        RabbitMQConfig rabbitMqConfig = new RabbitMQConfig();
        RabbitTemplate result = rabbitMqConfig.rabbitTemplate(connectionFactory);
        assertNotNull(result);
        assertInstanceOf(Jackson2JsonMessageConverter.class, result.getMessageConverter());
    }
}
