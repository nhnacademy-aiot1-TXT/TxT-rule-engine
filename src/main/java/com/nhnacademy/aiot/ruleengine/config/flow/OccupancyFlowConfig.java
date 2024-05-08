package com.nhnacademy.aiot.ruleengine.config.flow;

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
    public IntegrationFlow occupancyProcess() {
        return IntegrationFlows.from("occupancyChannel")
                // string을 payload 객체로 매핑
                .transform(sensorService::convertStringToPayload)
                // 최초로 redis와 다른 ocuupancy 값이 들어왔을 때
                .handle(Payload.class, (payload, headers) -> occupancyService.setTimer(payload))
                .filter(payload -> occupancyService.hasTimer())
                // redis에 저장
                .handle(Payload.class, (payload, headers) -> occupancyService.updateOccupancy(payload))
                .nullChannel();
    }
}
