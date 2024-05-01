package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.dto.message.PredictMessage;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    RabbitTemplate rabbitTemplate;

    @InjectMocks
    MessageService messageService;

    @Test
    public void testSendPredictMessage() {
        PredictMessage message = new PredictMessage();
        messageService.sendPredictMessage(message);

        verify(rabbitTemplate, times(1)).convertAndSend(any(), eq("txt.predict"), any(PredictMessage.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"airconditioner", "aircleaner", "light"})
    public void testSendDeviceMessage(String measurement) {
        ValueMessage message = new ValueMessage(true);
        messageService.sendDeviceMessage(measurement, message);

        verify(rabbitTemplate, times(1)).convertAndSend(any(), eq("txt." + measurement), any(ValueMessage.class));
    }
}