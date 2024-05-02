package com.nhnacademy.aiot.ruleengine.config.flow;

import com.nhnacademy.aiot.ruleengine.adapter.CommonAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorResponse;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
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
    private final DeviceService deviceService;
    private final OccupancyService occupancyService;

    @Bean
    public IntegrationFlow airCleanerProcess() {
        return IntegrationFlows.from(Constants.AIR_CLEANER_CHANNEL)
                               .filter(payload -> Constants.OCCUPIED.equals(occupancyService.getOccupancyStatus()),
                                       e -> e.discardFlow(airCleanerVacantOffFlow()))
                               .transform(sensorService::convertStringToPayload)
                               .handle(Payload.class, (payload, headers) -> airCleanerService.setTimer(payload))
                               .filter(Payload.class, payload -> !airCleanerService.isTimerActive(payload),
                                       e -> e.discardFlow(flow -> flow.handle(Payload.class, (payload, headers) -> airCleanerService.saveVoc(payload))
                                                                      .nullChannel()))
                               .handle(Payload.class, (payload, headers) -> {
                                   double avg = airCleanerService.getAvg();
                                   DeviceSensorResponse response = commonAdapter.getOnOffValue(Constants.AIRCLEANER_DEVICE_ID, Constants.AIRCLEANER_SENSOR_ID);

                                   if (avg > response.getOnValue() && !deviceService.isAirCleanerPowered()) {
                                       messageService.sendDeviceMessage(Constants.AIRCLEANER, new ValueMessage(true));
                                       deviceService.setAirCleanerPower(true);
                                   }

                                   if (avg < response.getOffValue() && deviceService.isAirCleanerPowered()) {
                                       messageService.sendDeviceMessage(Constants.AIRCLEANER, new ValueMessage(false));
                                       deviceService.setAirCleanerPower(false);
                                   }

                                   airCleanerService.deleteListAndTimer();

                                   return payload;
                               })
                               .nullChannel();
    }

    @Bean
    public IntegrationFlow airCleanerVacantOffFlow() {
        return flow ->
                flow.handle(Payload.class, (payload, headers) -> {
                    if (deviceService.isAirCleanerPowered()) {
                        messageService.sendDeviceMessage(Constants.AIRCLEANER, new ValueMessage(false));
                    }
                    return payload;
                }).nullChannel();
    }
}
