package com.nhnacademy.aiot.ruleengine.config.flow;

import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.message.DetailMessage;
import com.nhnacademy.aiot.ruleengine.service.BatteryLevelService;
import com.nhnacademy.aiot.ruleengine.service.MessageService;
import com.nhnacademy.aiot.ruleengine.service.SensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatteryFlowConfig {
    private final SensorService sensorService;
    private final MessageService messageService;
    private final BatteryLevelService batteryLevelService;
    public static final int LOW_LEVEL = 20;
    public static final int CRITICAL_LEVEL = 10;


    @Bean
    public IntegrationFlow batteryLevelProcess() {
        return IntegrationFlows.from(Constants.BATTERY_LEVEL_CHANNEL)
                               .transform(sensorService::convertStringToPayload)
                               .handle(Payload.class, (payload, headers) -> {
                                   int batteryLevel = Integer.parseInt(payload.getValue());
                                   String[] topics = sensorService.getTopics(headers);

                                   String place = topics[Constants.PLACE_INDEX];
                                   String deviceId = topics[Constants.DEVICE_INDEX];

                                   String currentStatus = batteryLevelService.getBatteryStatus(deviceId);

                                   if (batteryLevel <= CRITICAL_LEVEL && !Constants.CRITICAL.equals(currentStatus)) {
                                       batteryLevelService.setBatteryStatus(deviceId, Constants.CRITICAL);
                                       messageService.sendSensorMessage(Constants.BATTERY, new DetailMessage(Constants.CRITICAL, place, deviceId));
                                   } else if (batteryLevel <= LOW_LEVEL && batteryLevel > CRITICAL_LEVEL && !Constants.LOW.equals(currentStatus)) {
                                       batteryLevelService.setBatteryStatus(deviceId, Constants.LOW);
                                       messageService.sendSensorMessage(Constants.BATTERY, new DetailMessage(Constants.LOW, place, deviceId));
                                   }
                                   return payload;
                               })
                               .nullChannel();
    }
}
