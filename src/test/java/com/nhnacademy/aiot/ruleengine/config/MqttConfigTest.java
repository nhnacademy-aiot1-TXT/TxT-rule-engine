package com.nhnacademy.aiot.ruleengine.config;

import com.nhnacademy.aiot.ruleengine.service.InfluxService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MqttConfigTest {

    @Mock
    private InfluxService influxService;
    @InjectMocks
    private MqttConfig config;

    @Test
    void txtSensorInbound() {
        MessageChannel channel = config.txtSensorInputChannel();

        MessageProducer messageProducer = config.txtSensorInbound();

        assertNotNull(messageProducer);
    }

    @Test
    void academySensorInbound() {
        MessageChannel channel = config.academySensorInputChannel();

        MessageProducer messageProducer = config.academySensorInbound();

        assertNotNull(messageProducer);
    }

    @Test
    void handler1() {
        Message<String> message = MessageBuilder.withPayload("test payload")
                                                .setHeader("mqtt_receivedTopic", "test/topic")
                                                .build();

        MessageHandler handler = config.handler1();
        handler.handleMessage(message);

        assertNotNull(handler);
        verify(influxService).saveData("test/topic", "test payload");
    }

    @Test
    void handler2() {
        Message<String> message = MessageBuilder.withPayload("test payload")
                                                .setHeader("mqtt_receivedTopic", "test/topic")
                                                .build();

        MessageHandler handler = config.handler2();
        handler.handleMessage(message);

        assertNotNull(handler);
        verify(influxService).saveData("test/topic", "test payload");
    }
}