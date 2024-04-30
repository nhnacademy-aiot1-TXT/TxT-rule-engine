package com.nhnacademy.aiot.ruleengine.config;

import com.nhnacademy.aiot.ruleengine.service.InfluxService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.HashMap;

@ExtendWith(MockitoExtension.class)
public class MqttConfigTest {

    @Mock
    private InfluxService influxService;

    @InjectMocks
    MqttConfig mqttConfig;

    @Test
    public void handle_ShouldCallInfluxService() {
        HashMap<String, Object> headersMap = new HashMap<>();
        headersMap.put("mqtt_receivedTopic", "sampleTopic");
        MessageHeaders headers = new MessageHeaders(headersMap);
        Message<String> message = MessageBuilder
                .createMessage("TestPayload", headers);

        mqttConfig.handler().handleMessage(message);
        verify(influxService).save(any(), eq("TestPayload"));
    }

    @Test
    public void testInfluxInputChannelBean() {
        MessageChannel channel = mqttConfig.influxInputChannel();
        assertNotNull(channel);
    }

    @Test
    public void testOccupancyChannelBean() {
        MessageChannel channel = mqttConfig.occupancyChannel();
        assertNotNull(channel);
    }

    @Test
    public void testAirCleanerChannelBean() {
        MessageChannel channel = mqttConfig.airCleanerChannel();
        assertNotNull(channel);
    }

    @Test
    public void testAirConditionerChannelBean() {
        MessageChannel channel = mqttConfig.airConditionerChannel();
        assertNotNull(channel);
    }

    @Test
    public void testTxtSensorInboundBean() {
        MessageProducer producer = mqttConfig.txtSensorInbound();
        assertNotNull(producer);
    }

    @Test
    public void testAcademySensorInboundBean() {
        MessageProducer producer = mqttConfig.academySensorInbound();
        assertNotNull(producer);
    }

    @Test
    public void testOccupancySensorInboundBean() {
        MessageProducer producer = mqttConfig.occupancySensorInbound();
        assertNotNull(producer);
    }

    @Test
    public void testVocSensorInboundBean() {
        MessageProducer producer = mqttConfig.vocSensorInbound();
        assertNotNull(producer);
    }

    @Test
    public void testAirConditionerInboundBean() {
        MessageProducer producer = mqttConfig.airConditionerInbound();
        assertNotNull(producer);
    }

    @Test
    public void testAirConditionerInbound2Bean() {
        MessageProducer producer = mqttConfig.airConditionerInbound2();
        assertNotNull(producer);
    }

}

