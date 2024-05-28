package com.nhnacademy.aiot.ruleengine.config.flow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.service.OccupancyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@EnableIntegration
@SpringJUnitConfig(classes = {OccupancyFlowConfigTest.TestConfig.class})
class OccupancyFlowConfigTest {

    @Autowired
    private IntegrationFlowContext flowContext;
    @Autowired
    private IntegrationFlow occupancyProcess;
    @MockBean
    private static OccupancyService occupancyService;

    @Test
    void test() {
        flowContext.registration(occupancyProcess).id("intrusionProcess").register();
        doNothing().when(occupancyService).updateAll();

        await().atMost(6, TimeUnit.SECONDS)
               .untilAsserted(() -> verify(occupancyService, atLeastOnce()).updateAll());
    }


    @Configuration
    static class TestConfig {
        @Bean
        public IntegrationFlow occupancyProcess() {
            return IntegrationFlows.from(() -> new GenericMessage<>("trigger"),
                                         c -> c.poller(Pollers.fixedRate(2000)))
                                   .handle((payload, headers) ->
                                               {
                                                   occupancyService.updateAll();
                                                   return null;
                                               }).nullChannel();
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
