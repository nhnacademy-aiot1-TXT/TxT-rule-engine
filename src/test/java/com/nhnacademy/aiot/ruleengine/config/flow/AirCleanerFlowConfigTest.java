package com.nhnacademy.aiot.ruleengine.config.flow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.adapter.CommonAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorResponse;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
import com.nhnacademy.aiot.ruleengine.service.*;
import lombok.RequiredArgsConstructor;
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
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@EnableIntegration
@SpringJUnitConfig(classes = {AirCleanerFlowConfig.class, SensorService.class, AirCleanerFlowConfigTest.TestConfig.class})
class AirCleanerFlowConfigTest {

    @Autowired
    private IntegrationFlowContext flowContext;
    @Autowired
    private MessageChannel airCleanerChannel;
    @Autowired
    private IntegrationFlow airCleanerProcess;
    @MockBean
    private MessageService messageService;
    @MockBean
    private OccupancyService occupancyService;
    @MockBean
    private AirCleanerService airCleanerService;
    @MockBean
    private DeviceService deviceService;
    @MockBean
    private CommonAdapter commonAdapter;
    private Message<String> message;


    @BeforeEach
    void setUp() {
        if (!flowContext.getRegistry().containsKey("airCleanerProcess")) {
            flowContext.registration(airCleanerProcess).id("airCleanerProcess").register();
        }
        doNothing().when(messageService).sendDeviceMessage(anyString(), any(ValueMessage.class));
        message = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"600\"}");
    }

    @Test
    void vacantStatus() {
        when(occupancyService.getOccupancyStatus()).thenReturn(Constants.VACANT);
        when(deviceService.isAirCleanerPowered()).thenReturn(true);
        ArgumentCaptor<ValueMessage> captor = forClass(ValueMessage.class);

        airCleanerChannel.send(message);

        verify(messageService).sendDeviceMessage(eq(Constants.AIRCLEANER), captor.capture());
        assertFalse((Boolean) captor.getValue().getValue());
    }

    @Test
    void timerActive() {
        when(occupancyService.getOccupancyStatus()).thenReturn(Constants.OCCUPIED);
        when(airCleanerService.setTimer(any(Payload.class))).then(returnsFirstArg());
        when(airCleanerService.isTimerActive(any(Payload.class))).thenReturn(true);
        when(airCleanerService.saveVoc(any(Payload.class))).then(returnsFirstArg());

        airCleanerChannel.send(message);

        verify(airCleanerService).saveVoc(any(Payload.class));
    }

    @Test
    void timerEndPowerOn() {
        when(occupancyService.getOccupancyStatus()).thenReturn(Constants.OCCUPIED);
        when(airCleanerService.setTimer(any(Payload.class))).then(returnsFirstArg());
        when(airCleanerService.isTimerActive(any(Payload.class))).thenReturn(false);
        when(airCleanerService.getAvg()).thenReturn(600D);
        when(commonAdapter.getOnOffValue(anyLong(), anyLong())).thenReturn(new DeviceSensorResponse("airCleaner", 300f, 500f));
        when(deviceService.isAirCleanerPowered()).thenReturn(false);
        doNothing().when(deviceService).setAirCleanerPower(anyBoolean());
        ArgumentCaptor<Boolean> captor = forClass(Boolean.class);
        ArgumentCaptor<ValueMessage> captorValue = forClass(ValueMessage.class);
        doNothing().when(airCleanerService).deleteListAndTimer();

        airCleanerChannel.send(message);

        verify(messageService).sendDeviceMessage(eq(Constants.AIRCLEANER), captorValue.capture());
        verify(deviceService).setAirCleanerPower(captor.capture());
        assertTrue((Boolean) captorValue.getValue().getValue());
        assertTrue(captor.getValue());
    }

    @Test
    void timerEndPowerOff() {
        when(occupancyService.getOccupancyStatus()).thenReturn(Constants.OCCUPIED);
        when(airCleanerService.setTimer(any(Payload.class))).then(returnsFirstArg());
        when(airCleanerService.isTimerActive(any(Payload.class))).thenReturn(false);
        when(airCleanerService.getAvg()).thenReturn(200D);
        when(commonAdapter.getOnOffValue(anyLong(), anyLong())).thenReturn(new DeviceSensorResponse("airCleaner", 300f, 500f));
        when(deviceService.isAirCleanerPowered()).thenReturn(true);
        doNothing().when(deviceService).setAirCleanerPower(anyBoolean());
        ArgumentCaptor<Boolean> captor = forClass(Boolean.class);
        ArgumentCaptor<ValueMessage> captorValue = forClass(ValueMessage.class);
        doNothing().when(airCleanerService).deleteListAndTimer();

        airCleanerChannel.send(message);

        verify(messageService).sendDeviceMessage(eq(Constants.AIRCLEANER), captorValue.capture());
        verify(deviceService).setAirCleanerPower(captor.capture());
        assertFalse((Boolean) captorValue.getValue().getValue());
        assertFalse(captor.getValue());
    }


    @Configuration
    @RequiredArgsConstructor
    static class TestConfig {
        @Bean
        MessageChannel airCleanerChannel() {
            return new DirectChannel();
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
