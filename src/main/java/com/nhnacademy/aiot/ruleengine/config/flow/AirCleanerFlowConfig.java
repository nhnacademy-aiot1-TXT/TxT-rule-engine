package com.nhnacademy.aiot.ruleengine.config.flow;

import com.nhnacademy.aiot.ruleengine.adapter.CommonAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorResponse;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.service.*;
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
    private final MessageService messageService;
    private final AirCleanerService airCleanerService;
    private final DeviceRedisService deviceRedisService;
    private final OccupancyService occupancyService;

    @Bean
    public IntegrationFlow airCleanerProcess() {
        return IntegrationFlows.from(Constants.AIR_CLEANER_CHANNEL)
                               .filter(payload -> Constants.OCCUPIED.equals(occupancyService.getOccupancyStatus()),
                                       e -> e.discardFlow(airCleanerVacantOffFlow()))
                               .transform(sensorService::convertStringToPayload)
                               .handle(Payload.class, (payload, headers) -> airCleanerService.setTimer(payload))
                               .filter(Payload.class, payload -> !airCleanerService.isTimerActive(payload),
                                       e -> e.discardFlow(flow -> flow.handle(Payload.class, (payload, headers) -> airCleanerService.saveVoc(payload))))
                               .handle(Payload.class, (payload, headers) -> {
                                   float avg = airCleanerService.getAvg();
                                   DeviceSensorResponse response = commonAdapter.getOnOffValue(Constants.AIRCLEANER)
                                                                                .get(0);

                                   if (avg > response.getOnValue() && !deviceRedisService.isAirCleanerPowered()) {
                                       messageService.sendValidateMessage(Constants.AIRCLEANER, Constants.TRUE);
                                   }

                                   if (avg < response.getOffValue() && deviceRedisService.isAirCleanerPowered()) {
                                       messageService.sendValidateMessage(Constants.AIRCLEANER, Constants.FALSE);
                                   }

                                   airCleanerService.deleteListAndTimer();

                                   return null;
                               })
                               .get();
    }

    @Bean
    public IntegrationFlow airCleanerVacantOffFlow() {
        return flow -> {
            if (deviceRedisService.isAirCleanerPowered()) {
                messageService.sendValidateMessage(Constants.AIRCLEANER, Constants.FALSE);
            }
        };
    }
}
