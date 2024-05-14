package com.nhnacademy.aiot.ruleengine.config.flow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.message.DetailMessage;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
import com.nhnacademy.aiot.ruleengine.service.BatteryLevelService;
import com.nhnacademy.aiot.ruleengine.service.MessageService;
import com.nhnacademy.aiot.ruleengine.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@EnableIntegration
@SpringJUnitConfig(classes = {BatteryFlowConfig.class, SensorService.class, BatteryFlowConfigTest.TestConfig.class})
class BatteryFlowConfigTest {

    @Autowired
    private IntegrationFlowContext flowContext;
    @Autowired
    private MessageChannel batteryLevelChannel;

    @MockBean
    private BatteryLevelService batteryLevelService;
    @MockBean
    private MessageService messageService;
    @Captor
    private ArgumentCaptor<DetailMessage> detailMessageCaptor;

    private final Map<String, Object> headersMap = new HashMap<>();
    private Message<String> message;

    @Autowired
    private IntegrationFlow batteryLevelProcess;

    @BeforeEach
    void setUp() throws Exception {
        if (!flowContext.getRegistry().containsKey("batteryLevelProcess")) {
            flowContext.registration(batteryLevelProcess).id("batteryLevelProcess").register();
        }
        headersMap.put("mqtt_receivedTopic", "milesight/s/nhnacademy/b/gyeongnam/p/class_a/d/vs121/e/occupancy");
        doNothing().when(messageService).sendSensorMessage(anyString(), any(DetailMessage.class));

        ArgumentCaptor<ValueMessage> captor = forClass(ValueMessage.class);

    }


    @Test
    void batteryCriticalLevelProcess() {
        message = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"5\"}", new MessageHeaders(headersMap));
        when(batteryLevelService.isCriticalLevel(any())).thenReturn(true);
        when(batteryLevelService.alreadyReportCriticalStatus(anyString())).thenReturn(false);

        batteryLevelChannel.send(message);

        verify(messageService).sendSensorMessage(eq(Constants.BATTERY), detailMessageCaptor.capture());
        DetailMessage capturedMessage = detailMessageCaptor.getValue();

        assertEquals(Constants.CRITICAL, capturedMessage.getValue());
    }

    @Test
    void batteryLowLevelProcess() {
        message = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"20\"}", new MessageHeaders(headersMap));

        when(batteryLevelService.isLowLevel(any())).thenReturn(true);
        when(batteryLevelService.alreadyReportLowStatus(anyString())).thenReturn(false);

        batteryLevelChannel.send(message);

        verify(messageService).sendSensorMessage(eq(Constants.BATTERY), detailMessageCaptor.capture());
        DetailMessage capturedMessage = detailMessageCaptor.getValue();
        
        assertEquals(Constants.LOW, capturedMessage.getValue());
    }

    @Configuration
    @RequiredArgsConstructor
    static class TestConfig {
        @Bean
        MessageChannel batteryLevelChannel() {
            return new DirectChannel();
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}