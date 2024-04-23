package com.nhnacademy.aiot.ruleengine.config.flow;

import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.service.SensorService;
import com.nhnacademy.aiot.ruleengine.service.redis.OccupancyRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

@Configuration
@RequiredArgsConstructor
public class OccupancyFlowConfig {

    private final SensorService sensorService;
    private final OccupancyRedisService occupancyRedisService;

    @Bean
    public IntegrationFlow process() {
        return IntegrationFlows.from("occupancyChannel")
                               // string을 payload 객체로 매핑
                               .transform(sensorService::convertStringToPayload)
                               // 최초로 redis와 다른 ocuupancy 값이 들어왔을 때
                               .handle(Payload.class, (payload, headers) -> setTimer(payload))
                               .filter(payload -> occupancyRedisService.hasTimer())
                               // redis에 저장
                               .handle(Payload.class, (payload, headers) -> updateOccupancy(payload))
                               .get();
    }

    private Payload setTimer(Payload payload) {
        if (shouldStartTimer(payload)) {
            occupancyRedisService.setTimer(payload.getTime());
        }
        return payload;
    }

    private Payload updateOccupancy(Payload payload) {
        if (isTimerActive(payload)) {
            // 10분이 지나지 않았다면 redis에 저장
            occupancyRedisService.saveToList(payload.getValue());
        } else {
            occupancyRedisService.setOccupancyStatus();
        }

        return payload;
    }

    private boolean shouldStartTimer(Payload payload) {
        return !occupancyRedisService.hasTimer() &&
                !payload.getValue().equals(occupancyRedisService.getOccupancyStatus());
    }

    private boolean isTimerActive(Payload payload) {
        return payload.getTime() - occupancyRedisService.getTimer() <= 600000;
    }
}
