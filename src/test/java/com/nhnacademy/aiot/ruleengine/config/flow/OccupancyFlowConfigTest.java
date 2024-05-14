package com.nhnacademy.aiot.ruleengine.config.flow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.service.OccupancyService;
import com.nhnacademy.aiot.ruleengine.service.SensorService;
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
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@EnableIntegration
@SpringJUnitConfig(classes = {OccupancyFlowConfig.class, SensorService.class, OccupancyFlowConfigTest.TestConfig.class})
class OccupancyFlowConfigTest {
    @Autowired
    private MessageChannel occupancyChannel;
    @MockBean
    private OccupancyService occupancyService;
    private Message<String> message;

    @BeforeEach
    void setUp() {
        message = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"occupied\"}");
        when(occupancyService.shouldStartProcess(any(Payload.class), anyString())).thenReturn(true);
        when(occupancyService.setTimer(any(Payload.class), anyString())).then(returnsFirstArg());
        when(occupancyService.save(any(Payload.class), anyString())).then(returnsFirstArg());
        when(occupancyService.updateOccupancy(any(Payload.class), anyString())).then(returnsFirstArg());
    }

    @Test
    void updateOccupancy() {
        when(occupancyService.isTimerActive(any(Payload.class), anyString())).thenReturn(false);

        occupancyChannel.send(message);

        verify(occupancyService).updateOccupancy(any(Payload.class), anyString());
    }

    @Test
    void saveOccupancySave() {
        when(occupancyService.isTimerActive(any(Payload.class), anyString())).thenReturn(true);

        occupancyChannel.send(message);

        verify(occupancyService).save(any(Payload.class), anyString());
    }

    @Configuration
    static class TestConfig {
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
