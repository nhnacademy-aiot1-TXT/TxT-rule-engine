package com.nhnacademy.aiot.ruleengine.config.flow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
import com.nhnacademy.aiot.ruleengine.service.DeviceService;
import com.nhnacademy.aiot.ruleengine.service.MessageService;
import com.nhnacademy.aiot.ruleengine.service.OccupancyService;
import com.nhnacademy.aiot.ruleengine.service.SensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@EnableIntegration
@SpringJUnitConfig(classes = {LightFlowConfig.class, DeviceService.class, SensorService.class, OccupancyService.class, LightFlowConfigTest.Testconfig.class})
class LightFlowConfigTest {

    @Autowired
    private IntegrationFlowContext flowContext;
    @Autowired
    private MessageChannel occupancyChannel;
    @Autowired
    private IntegrationFlow lightProcess;
    @MockBean
    private MessageService messageService;
    @MockBean
    private RedisAdapter redisAdapter;

    @BeforeEach
    void setUp() {
        if (!flowContext.getRegistry().containsKey("lightProcess")) {
            flowContext.registration(lightProcess).id("lightProcess").register();
        }
        doNothing().when(messageService).sendDeviceMessage(anyString(), any(ValueMessage.class));
        doNothing().when(redisAdapter).setDevicePower(anyString(), anyBoolean());
    }

    @Test
    void sendOnMessageWhenOccupiedLightOff() {
        Message<String> message = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"occupied\"}");
        when(redisAdapter.getStatus(anyString())).thenReturn(Constants.VACANT);
        when(redisAdapter.isDevicePowered(Constants.LIGHT)).thenReturn(false);
        ArgumentCaptor<ValueMessage> captor = forClass(ValueMessage.class);

        occupancyChannel.send(message);

        verify(messageService, times(1)).sendDeviceMessage(eq(Constants.LIGHT), captor.capture());
        assertTrue((Boolean) captor.getValue().getValue());
        verify(redisAdapter, times(1)).setDevicePower(eq(Constants.LIGHT), eq(true));
        verify(redisAdapter, never()).getStatus(anyString());
        verify(redisAdapter, never()).setDevicePower(eq(Constants.LIGHT), eq(false));
    }

    @Test
    void sendOffMessageWhenVacantLightOn() {
        Message<String> message = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"vacant\"}");
        when(redisAdapter.getStatus(anyString())).thenReturn(Constants.VACANT);
        when(redisAdapter.isDevicePowered(Constants.LIGHT)).thenReturn(true);
        ArgumentCaptor<ValueMessage> captor = forClass(ValueMessage.class);

        occupancyChannel.send(message);

        verify(messageService, times(1)).sendDeviceMessage(eq(Constants.LIGHT), captor.capture());
        assertFalse((Boolean) captor.getValue().getValue());
        verify(redisAdapter, times(1)).setDevicePower(eq(Constants.LIGHT), eq(false));
        verify(redisAdapter, never()).setDevicePower(eq(Constants.LIGHT), eq(true));
    }

    @Test
    void doNotSendAnyMessage() {
        Message<String> message = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"occupied\"}");
        when(redisAdapter.getStatus(anyString())).thenReturn(Constants.OCCUPIED);
        when(redisAdapter.isDevicePowered(Constants.LIGHT)).thenReturn(true);

        occupancyChannel.send(message);

        verify(messageService, never()).sendDeviceMessage(anyString(), any(ValueMessage.class));
        verify(redisAdapter, never()).setDevicePower(anyString(), anyBoolean());
    }

    @Configuration
    static class Testconfig {
        @Bean
        MessageChannel occupancyChannel() {
            return new DirectChannel();
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
