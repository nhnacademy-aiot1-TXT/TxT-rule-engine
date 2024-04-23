package com.nhnacademy.aiot.ruleengine.config.flow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.exception.PayloadParseException;
import com.nhnacademy.aiot.ruleengine.service.redis.OccupancyRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

@Configuration
@RequiredArgsConstructor
public class OccupancyFlowConfig {

    private final ObjectMapper objectMapper;
    private final OccupancyRedisService occupancyRedisService;

    @Bean
    public IntegrationFlow checkOccupancy() {
        return IntegrationFlows.from("occupancyChannel")
                               // influxdb에 저장
                               .channel("influxInputChannel")
                               // string을 payload 객체로 매핑
                               .transform(this::convertStringToPayload)
                               // 최초로 redis와 다른 ocuupancy 값이 들어왔을 때
                               .handle(Payload.class, (payload, headers) -> setTimer(payload))
                               .filter(payload -> occupancyRedisService.hasOccupancyTimer())
                               // redis에 저장
                               .handle(Payload.class, (payload, headers) -> saveOrSetOccupancy(payload))
                               .get();
    }

    private Payload convertStringToPayload(String payload) {
        try {
            return objectMapper.readValue(payload, Payload.class);
        } catch (JsonProcessingException e) {
            throw new PayloadParseException();
        }
    }

    private Payload setTimer(Payload payload) {
        if (!occupancyRedisService.hasOccupancyTimer() &&
                !payload.getValue().equals(occupancyRedisService.getOccupancyStatus())) {
            occupancyRedisService.setOccupancyTimer(payload.getTime());
        }
        return payload;
    }

    private Payload saveOrSetOccupancy(Payload payload) {
        if (payload.getTime() - occupancyRedisService.getOccupancyTimer() <= 600000) {
            // 10분이 지나지 않았다면 redis에 저장
            occupancyRedisService.saveList(payload.getValue());
        } else {
            occupancyRedisService.setOccupancyStatus();
        }

        return payload;
    }
}
