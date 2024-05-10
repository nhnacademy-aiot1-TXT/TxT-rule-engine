package com.nhnacademy.aiot.ruleengine.config.flow;

import com.fasterxml.jackson.databind.ObjectMapper;
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
@SpringJUnitConfig(classes = {LightFlowConfig.class, SensorService.class, LightFlowConfigTest.Testconfig.class})
class LightFlowConfigTest {

    @Autowired
    private MessageChannel occupancyChannel;
    @MockBean
    private MessageService messageService;
    @MockBean
    private DeviceService deviceService;
    @MockBean
    private OccupancyService occupancyService;

    @BeforeEach
    void setUp() {
        doNothing().when(messageService).sendDeviceMessage(anyString(), any(ValueMessage.class));
        doNothing().when(deviceService).setLightPower(anyBoolean());
        when(deviceService.isAutoMode()).thenReturn(true);
    }

    @Test
    void sendOnMessageWhenOccupiedLightOff() {
        Message<String> message = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"occupied\"}");
        when(occupancyService.getOccupancyStatus(Constants.LIGHT)).thenReturn(Constants.OCCUPIED);
        when(deviceService.isLightPowered()).thenReturn(false);
        ArgumentCaptor<ValueMessage> captor = forClass(ValueMessage.class);

        occupancyChannel.send(message);

        verify(messageService).sendDeviceMessage(eq(Constants.LIGHT), captor.capture());
        assertTrue((Boolean) captor.getValue().getValue());
        verify(deviceService).setLightPower(true);
        verify(occupancyService, never()).getOccupancyStatus(Constants.LIGHT);
        verify(deviceService, never()).setLightPower(false);
    }

    @Test
    void sendOffMessageWhenVacantLightOn() {
        Message<String> message = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"vacant\"}");
        when(occupancyService.getOccupancyStatus(Constants.LIGHT)).thenReturn(Constants.VACANT);
        when(deviceService.isLightPowered()).thenReturn(true);
        ArgumentCaptor<ValueMessage> captor = forClass(ValueMessage.class);

        occupancyChannel.send(message);

        verify(messageService).sendDeviceMessage(eq(Constants.LIGHT), captor.capture());
        assertFalse((Boolean) captor.getValue().getValue());
        verify(deviceService).setLightPower(false);
        verify(deviceService, never()).setLightPower(true);
    }

    @Test
    void doNotSendAnyMessage() {
        Message<String> message = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"occupied\"}");
        when(occupancyService.getOccupancyStatus(Constants.LIGHT)).thenReturn(Constants.OCCUPIED);
        when(deviceService.isLightPowered()).thenReturn(true);

        occupancyChannel.send(message);

        verify(messageService, never()).sendDeviceMessage(anyString(), any(ValueMessage.class));
        verify(deviceService, never()).setLightPower(anyBoolean());
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
