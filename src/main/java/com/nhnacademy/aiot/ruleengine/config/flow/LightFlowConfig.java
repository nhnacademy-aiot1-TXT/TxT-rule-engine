package com.nhnacademy.aiot.ruleengine.config.flow;

import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.service.DeviceRedisService;
import com.nhnacademy.aiot.ruleengine.service.MessageService;
import com.nhnacademy.aiot.ruleengine.service.OccupancyService;
import com.nhnacademy.aiot.ruleengine.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

@Configuration
@RequiredArgsConstructor
public class LightFlowConfig {

    private final SensorService sensorService;
    private final DeviceRedisService deviceService;
    private final OccupancyService occupancyService;
    private final MessageService messageService;

    @Bean
    public IntegrationFlow lightProcess() {
        return IntegrationFlows.from("occupancyChannel")
                               .transform(sensorService::convertStringToPayload)
                               .filter(Payload.class, payload -> Constants.OCCUPIED.equals(payload.getValue()) && !deviceService.isLightPowered(),
                                       e -> e.discardFlow(flow -> messageService.sendValidateMessage(Constants.LIGHT, Constants.TRUE)))
                               .handle(Payload.class, (payload, headers) -> {
                                   if (Constants.VACANT.equals(occupancyService.getOccupancyStatus()) && deviceService.isLightPowered()) {
                                       messageService.sendValidateMessage(Constants.LIGHT, Constants.FALSE);
                                   }
                                   return null;
                               })
                               .get();
    }
}
