package com.nhnacademy.aiot.ruleengine.config.flow;

import com.nhnacademy.aiot.ruleengine.adapter.CommonAdapter;
import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorResponse;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.service.SensorService;
import com.nhnacademy.aiot.ruleengine.service.redis.OccupancyRedisService;
import com.nhnacademy.aiot.ruleengine.service.redis.VocRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

@Configuration
@RequiredArgsConstructor
public class AirCleanerFlowConfig {

    private final CommonAdapter commonAdapter;
    private final SensorService sensorService;
    private final VocRedisService vocRedisService;
    private final OccupancyRedisService occupancyRedisService;

    @Bean
    public IntegrationFlow process() {
        return IntegrationFlows.from("vocChannel")
                               .transform(sensorService::convertStringToPayload)
                               .filter(payload -> "occupied".equals(occupancyRedisService.getOccupancyStatus()),
                                       e -> e.discardFlow(vacantOffFlow()))
                               .handle(Payload.class, (payload, headers) -> {
                                   setTimer(payload);
                                   return updateVoc(payload);
                               })
    }

    @Bean
    public IntegrationFlow vacantOffFlow() {
        return flow -> {

        };
    }


    private Payload setTimer(Payload payload) {
        if (!vocRedisService.hasTimer()) {
            vocRedisService.setTimer(payload.getTime());
        }
        return payload;
    }

    private Payload updateVoc(Payload payload) {
        if (isTimerActive(payload)) {
            vocRedisService.saveToList(payload.getValue());
        } else {
            float avg = vocRedisService.getAvg();
            DeviceSensorResponse response = commonAdapter.getOnOffValue("aircleaner").get(0);

            // on인지 off인지 확인
            // on off 비교
            // 전이랑 다른지메세지 날리기
            // 레디스 지우기
        }
        return payload;
    }

    private boolean isTimerActive(Payload payload) {
        return payload.getTime() - vocRedisService.getTimer() <= 60000;
    }
}
