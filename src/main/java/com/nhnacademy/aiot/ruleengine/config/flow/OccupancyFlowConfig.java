package com.nhnacademy.aiot.ruleengine.config.flow;

import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.service.OccupancyService;
import com.nhnacademy.aiot.ruleengine.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

@Configuration
@RequiredArgsConstructor
public class OccupancyFlowConfig {

    private final SensorService sensorService;
    private final OccupancyService occupancyService;

    @Bean
    public IntegrationFlow aircleanerOccupancy() {
        return occupancyProcess(Constants.AIRCLEANER);
    }

    @Bean
    public IntegrationFlow lightOccupancy() {
        return occupancyProcess(Constants.LIGHT);
    }

    @Bean
    public IntegrationFlow airconditionerOccupancy() {
        return occupancyProcess(Constants.AIRCONDITIONER);
    }

    private IntegrationFlow occupancyProcess(String devicenName) {
        return IntegrationFlows.from(Constants.OCCUPANCY_CHANNEL)
                               .transform(sensorService::convertStringToPayload)
                               .filter(Payload.class, payload -> occupancyService.shouldStartProcess(payload, devicenName))
                               .handle(Payload.class, (payload, headers) -> occupancyService.setTimer(payload, devicenName))
                               .filter(Payload.class, payload -> !occupancyService.isTimerActive(payload, devicenName),
                                       e -> e.discardFlow(flow -> flow.handle(Payload.class, (payload, headers) -> occupancyService.save(payload, devicenName))
                                                                      .nullChannel()))
                               .handle(Payload.class, (payload, headers) -> occupancyService.updateOccupancy(payload, devicenName))
                               .nullChannel();
    }
}
