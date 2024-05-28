package com.nhnacademy.aiot.ruleengine.config.flow;

import com.influxdb.exceptions.InfluxException;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.send.MessageSender;
import com.nhnacademy.aiot.ruleengine.service.InfluxService;
import com.nhnacademy.aiot.ruleengine.service.MqttService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@EnableIntegration
@SpringJUnitConfig(classes = {InfluxDbFlowConfig.class, InfluxDbFlowConfigTest.TestConfig.class})
class InfluxDbFlowConfigTest {

    @Autowired
    private MessageChannel influxInputChannel;
    @MockBean
    private InfluxService influxService;
    @MockBean
    private MessageSender messageSender;
    @MockBean
    private MqttService mqttService;
    private Message<String> message;

    @BeforeEach
    void setUp() {
        message = new GenericMessage<>("test", Map.of("header", "testHeader"));
    }

    @Test
    void success() {
        doNothing().when(influxService).save(any(MessageHeaders.class), anyString());

        influxInputChannel.send(message);

        verify(influxService).save(any(MessageHeaders.class), eq("test"));
    }

    @Test
    void fail() {
        doThrow(new InfluxException("test")).when(influxService).save(any(MessageHeaders.class), anyString());

        influxInputChannel.send(message);

        verify(messageSender).send(Constants.INFLUX_DB, Constants.INFLUX_SAVE_ERROR_MESSAGE);
    }


    @Configuration
    @RequiredArgsConstructor
    static class TestConfig {
        @Bean
        MessageChannel influxInputChannel() {
            return new DirectChannel();
        }
    }
}
