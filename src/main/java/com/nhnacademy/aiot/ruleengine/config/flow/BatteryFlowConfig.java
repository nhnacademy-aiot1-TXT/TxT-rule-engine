package com.nhnacademy.aiot.ruleengine.config.flow;

import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.message.DetailMessage;
import com.nhnacademy.aiot.ruleengine.service.BatteryLevelService;
import com.nhnacademy.aiot.ruleengine.service.MessageService;
import com.nhnacademy.aiot.ruleengine.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

@Configuration
@RequiredArgsConstructor
public class BatteryFlowConfig {
    private final SensorService sensorService;
    private final MessageService messageService;
    private final BatteryLevelService batteryLevelService;

    @Bean
    public IntegrationFlow batteryLevelProcess() {
        return IntegrationFlows.from(Constants.BATTERY_LEVEL_CHANNEL)
                               .transform(sensorService::convertStringToPayload)
                               .handle(Payload.class, (payload, headers) -> {
                                   String[] topics = sensorService.getTopics(headers);

                                   String place = topics[Constants.PLACE_INDEX];
                                   String deviceId = topics[Constants.DEVICE_INDEX];

                                   if (batteryLevelService.isCriticalLevel(payload) &&
                                           !batteryLevelService.alreadyReportCriticalStatus(deviceId)) {
                                       batteryLevelService.setBatteryStatus(deviceId, Constants.CRITICAL);
                                       messageService.sendSensorMessage(Constants.BATTERY, new DetailMessage(Constants.CRITICAL, place, deviceId));
                                       return payload;
                                   }

                                   if (batteryLevelService.isLowLevel(payload) &&
                                           !batteryLevelService.alreadyReportLowStatus(deviceId)) {
                                       batteryLevelService.setBatteryStatus(deviceId, Constants.LOW);
                                       messageService.sendSensorMessage(Constants.BATTERY, new DetailMessage(Constants.LOW, place, deviceId));
                                       return payload;
                                   }

                                   return payload;
                               })
                               .nullChannel();
    }
}
