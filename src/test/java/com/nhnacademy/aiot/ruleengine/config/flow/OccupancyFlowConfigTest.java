package com.nhnacademy.aiot.ruleengine.config.flow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
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
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@EnableIntegration
@SpringJUnitConfig(classes = {OccupancyFlowConfig.class, SensorService.class, OccupancyService.class, OccupancyFlowConfigTest.TestConfig.class})
class OccupancyFlowConfigTest {
    @Autowired
    private IntegrationFlowContext flowContext;
    @Autowired
    private MessageChannel occupancyChannel;
    @Autowired
    private OccupancyFlowConfig occupancyFlowConfig;
    @MockBean
    private RedisAdapter redisAdapter;

    @BeforeEach
    void setUp() {
        flowContext.registration(occupancyFlowConfig.occupancyProcess()).id("occupancyProcess").register();
    }

    @Test
    void setOccupied() {
        Message<String> occupiedMsg = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"occupied\"}");
        Message<String> vacantMsg = new GenericMessage<>("{\"time\":1714029200000,\"value\":\"vacant\"}");
        Message<String> occupiedMsg2 = new GenericMessage<>("{\"time\":1714029500000,\"value\":\"occupied\"}");
        Message<String> occupiedMsg3 = new GenericMessage<>("{\"time\":1714029700000,\"value\":\"occupied\"}");
        when(redisAdapter.hasKey(anyString())).thenReturn(false);
        when(redisAdapter.getStringValue(anyString())).thenReturn(Constants.VACANT);
        doNothing().when(redisAdapter).setValue(anyString(), anyLong());
        when(redisAdapter.getLongValue(anyString())).thenReturn(1714029000000L);
        doNothing().when(redisAdapter).saveStringToList(anyString(), anyString());
        doNothing().when(redisAdapter).setValue(anyString(), anyString());
        doNothing().when(redisAdapter).delete(anyString());
        doNothing().when(redisAdapter).delete(anyString());
        ArgumentCaptor<String> captor = forClass(String.class);

        occupancyChannel.send(occupiedMsg);

        when(redisAdapter.hasKey(anyString())).thenReturn(true);

        occupancyChannel.send(vacantMsg);
        occupancyChannel.send(occupiedMsg2);
        occupancyChannel.send(occupiedMsg3);

        verify(redisAdapter).setValue(anyString(), anyLong());
        verify(redisAdapter, times(2)).saveStringToList(anyString(), anyString());
        verify(redisAdapter).setValue(anyString(), captor.capture());
        assertEquals("occupied", captor.getValue());
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
