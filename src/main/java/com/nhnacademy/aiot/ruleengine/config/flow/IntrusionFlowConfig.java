package com.nhnacademy.aiot.ruleengine.config.flow;

import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
import com.nhnacademy.aiot.ruleengine.service.IntrusionService;
import com.nhnacademy.aiot.ruleengine.service.MessageService;
import com.nhnacademy.aiot.ruleengine.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

@Configuration
@RequiredArgsConstructor
public class IntrusionFlowConfig {

    private final SensorService sensorService;
    private final MessageService messageService;
    private final IntrusionService intrusionService;

    @Bean
    public IntegrationFlow intrusionProcess() {
        return IntegrationFlows.from(Constants.INTRUSION_CHANNEL)
                               .transform(sensorService::convertStringToPayload)
                               .filter(Payload.class, payload -> intrusionService.isAlertTimeActive(payload.getLocalTime()))
                               .filter(Payload.class, payload -> Constants.OCCUPIED.equals(payload.getValue()))
                               .handle(Payload.class, (payload, headers) -> {
                                   messageService.sendIntrusionMessage(new ValueMessage(true));
                                   return payload;
                               }).nullChannel();
    }
}
