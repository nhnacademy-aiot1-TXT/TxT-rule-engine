package com.nhnacademy.aiot.ruleengine.config.flow;

import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
import com.nhnacademy.aiot.ruleengine.service.DeviceService;
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
    private final DeviceService deviceService;
    private final OccupancyService occupancyService;
    private final MessageService messageService;

    @Bean
    public IntegrationFlow lightProcess() {
        return IntegrationFlows.from(Constants.OCCUPANCY_CHANNEL)
                               .filter(p -> deviceService.isAutoMode())
                               .transform(sensorService::convertStringToPayload)
                               .filter(Payload.class, payload -> Constants.OCCUPIED.equals(payload.getValue()) && !deviceService.isLightPowered(),
                                       e -> e.discardFlow(flow -> flow.handle(Payload.class, (payload, headers) -> {
                                           if (Constants.VACANT.equals(occupancyService.getOccupancyStatus(Constants.LIGHT)) && deviceService.isLightPowered()) {
                                               messageService.sendDeviceMessage(Constants.LIGHT, new ValueMessage(false));
                                               deviceService.setLightPower(false);
                                           }
                                           return payload;
                                       }).nullChannel()))
                               .handle(Payload.class, (payload, headers) -> {
                                   messageService.sendDeviceMessage(Constants.LIGHT, new ValueMessage(true));
                                   deviceService.setLightPower(true);
                                   return payload;
                               })
                               .nullChannel();
    }
}
