package com.nhnacademy.aiot.ruleengine.config.flow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.adapter.CommonAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorResponse;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.message.PredictMessage;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
import com.nhnacademy.aiot.ruleengine.service.*;
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
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@EnableIntegration
@SuppressWarnings("unchecked")
@SpringJUnitConfig(classes = {AirConditionerFlowConfig.class, SensorService.class, AirConditionerFlowConfigTest.TestConfig.class})
class AirConditionerFlowConfigTest {

    @Autowired
    private IntegrationFlowContext flowContext;
    @Autowired
    private MessageChannel airConditionerChannel;
    @Autowired
    private IntegrationFlow autoMode;
    @MockBean
    private AirConditionerService airConditionerService;
    @MockBean
    private MessageService messageService;
    @MockBean
    private DeviceService deviceService;
    @MockBean
    private CommonAdapter commonAdapter;
    @MockBean
    private OccupancyService occupancyService;
    private Message<String> message;
    private Payload payload;

    @BeforeEach
    void setUp() {
        if (!flowContext.getRegistry().containsKey("autoMode")) {
            flowContext.registration(autoMode).id("autoMode").register();
        }
        doNothing().when(messageService).sendDeviceMessage(anyString(), any(ValueMessage.class));
        message = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"23.5\"}");
        payload = mock(Payload.class);
    }

    @Test
    void autoModeTimerEnd() {
        when(deviceService.isAutoMode()).thenReturn(true);
        when(airConditionerService.isTimerActive(anyString(), any(Payload.class))).thenReturn(false);
        doNothing().when(airConditionerService).deleteListAndTimer();
        when(airConditionerService.setTimer(anyString(), any(Payload.class))).thenReturn(payload);
        when(airConditionerService.getAvgForAutoMode()).thenReturn(mock(HashMap.class));
        doNothing().when(messageService).injectPredictMessage(any(Map.class), any(PredictMessage.class));
        doNothing().when(airConditionerService).deleteForAutoMode();

        airConditionerChannel.send(message);

        verify(messageService).injectPredictMessage(any(Map.class), any(PredictMessage.class));
        verify(messageService).sendPredictMessage(any(PredictMessage.class));
        verify(airConditionerService).deleteForAutoMode();
        verify(airConditionerService, never()).saveForAutoMode(any(MessageHeaders.class), any(Payload.class));
    }

    @Test
    void autoModeTimerActive() {
        when(deviceService.isAutoMode()).thenReturn(true);
        when(airConditionerService.isTimerActive(anyString(), any(Payload.class))).thenReturn(true);
        doNothing().when(airConditionerService).deleteListAndTimer();
        when(airConditionerService.setTimer(anyString(), any(Payload.class))).thenReturn(payload);

        airConditionerChannel.send(message);

        verify(airConditionerService).saveForAutoMode(any(MessageHeaders.class), any(Payload.class));
    }

    @Test
    void manualModeTimerActive() {
        when(deviceService.isAutoMode()).thenReturn(false);
        when(airConditionerService.isIndoorTempMsg(any(Message.class))).thenReturn(true);
        when(airConditionerService.setTimer(anyString(), any(Payload.class))).thenReturn(payload);
        when(airConditionerService.isTimerActive(anyString(), any(Payload.class))).thenReturn(true);
        when(airConditionerService.saveTemperature(any(Payload.class))).thenReturn(payload);
        when(occupancyService.getOccupancyStatus(Constants.AIRCONDITIONER)).thenReturn(Constants.OCCUPIED);

        airConditionerChannel.send(message);

        verify(airConditionerService).saveTemperature(any(Payload.class));
    }

    @Test
    void manualModePowerOn() {
        when(deviceService.isAutoMode()).thenReturn(false);
        when(airConditionerService.isIndoorTempMsg(any(Message.class))).thenReturn(true);
        when(airConditionerService.setTimer(anyString(), any(Payload.class))).thenReturn(payload);
        when(airConditionerService.isTimerActive(anyString(), any(Payload.class))).thenReturn(false);
        when(airConditionerService.getAvg(Constants.TEMPERATURE)).thenReturn(30D);
        when(commonAdapter.getSensorByDeviceAndSensor(anyLong(), anyLong())).thenReturn(new DeviceSensorResponse("airconditioner", 27f, 18f));
        when(deviceService.isAirConditionerPowered()).thenReturn(false);
        when(occupancyService.getOccupancyStatus(Constants.AIRCONDITIONER)).thenReturn(Constants.OCCUPIED);
        ArgumentCaptor<Boolean> captor = forClass(Boolean.class);
        ArgumentCaptor<ValueMessage> captorValue = forClass(ValueMessage.class);

        airConditionerChannel.send(message);

        verify(messageService).sendDeviceMessage(eq(Constants.AIRCONDITIONER), captorValue.capture());
        verify(deviceService).setAirConditionerPower(captor.capture());
        assertTrue((Boolean) captorValue.getValue().getValue());
        assertTrue(captor.getValue());
        verify(airConditionerService).deleteListAndTimer();
    }

    @Test
    void manualModePowerOff() {
        when(deviceService.isAutoMode()).thenReturn(false);
        when(airConditionerService.isIndoorTempMsg(any(Message.class))).thenReturn(true);
        when(airConditionerService.setTimer(anyString(), any(Payload.class))).thenReturn(payload);
        when(airConditionerService.isTimerActive(anyString(), any(Payload.class))).thenReturn(false);
        when(airConditionerService.getAvg(Constants.TEMPERATURE)).thenReturn(16D);
        when(commonAdapter.getSensorByDeviceAndSensor(anyLong(), anyLong())).thenReturn(new DeviceSensorResponse("airconditioner", 27f, 18f));
        when(deviceService.isAirConditionerPowered()).thenReturn(true);
        when(occupancyService.getOccupancyStatus(Constants.AIRCONDITIONER)).thenReturn(Constants.OCCUPIED);
        ArgumentCaptor<Boolean> captor = forClass(Boolean.class);
        ArgumentCaptor<ValueMessage> captorValue = forClass(ValueMessage.class);

        airConditionerChannel.send(message);

        verify(messageService).sendDeviceMessage(eq(Constants.AIRCONDITIONER), captorValue.capture());
        verify(deviceService).setAirConditionerPower(captor.capture());
        assertFalse((Boolean) captorValue.getValue().getValue());
        assertFalse(captor.getValue());
        verify(airConditionerService).deleteListAndTimer();
    }


    @Configuration
    static class TestConfig {
        @Bean
        MessageChannel airConditionerChannel() {
            return new DirectChannel();
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
