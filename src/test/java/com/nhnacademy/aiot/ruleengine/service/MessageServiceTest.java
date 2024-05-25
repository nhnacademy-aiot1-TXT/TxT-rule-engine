package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.dto.message.DetailMessage;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.mockito.Mockito.*;

@SpringJUnitConfig(MessageService.class)
class MessageServiceTest {

    @MockBean
    RabbitTemplate rabbitTemplate;
    @Autowired
    MessageService messageService;

    @BeforeEach
    void setUp() {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));
        ReflectionTestUtils.setField(messageService, "exchangeName", "test");
        ReflectionTestUtils.setField(messageService, "exchangeSensorName", "test");
    }

    @Test
    void testSendPredictMessage() {
        Map<String, Object> message = Map.of("test", "value");
        messageService.sendPredictMessage(message);
        messageService.sendPredictMessage(message);

        verify(rabbitTemplate, times(2)).convertAndSend(any(), eq("txt.predict"), any(Map.class));
    }

    @Test
    void testSendDeviceMessage() {
        ValueMessage message = new ValueMessage("test", "test1", "test2");
        messageService.sendDeviceMessage(message);

        verify(rabbitTemplate).convertAndSend(any(), eq("txt.device"), any(ValueMessage.class));
    }

    @Test
    void testSendSensorMessage() {
        DetailMessage message = new DetailMessage(20, "test_place", "test_device");

        messageService.sendSensorMessage("test_measurement", message);

        verify(rabbitTemplate).convertAndSend(anyString(), eq("txt." + "test_measurement"), any(DetailMessage.class));
    }
}
