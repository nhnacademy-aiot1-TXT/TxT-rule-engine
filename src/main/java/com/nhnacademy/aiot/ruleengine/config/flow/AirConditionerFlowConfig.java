package com.nhnacademy.aiot.ruleengine.config.flow;

import com.nhnacademy.aiot.ruleengine.adapter.CommonAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorResponse;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.message.PredictMessage;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
import com.nhnacademy.aiot.ruleengine.service.AirConditionerService;
import com.nhnacademy.aiot.ruleengine.service.DeviceService;
import com.nhnacademy.aiot.ruleengine.service.MessageService;
import com.nhnacademy.aiot.ruleengine.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class AirConditionerFlowConfig {

    private final CommonAdapter commonAdapter;
    private final DeviceService deviceService;
    private final SensorService sensorService;
    private final AirConditionerService airConditionerService;
    private final MessageService messageService;

    @Bean
    public IntegrationFlow autoMode() {
        return IntegrationFlows.from("airConditionerChannel")
                               .filter(p -> deviceService.isAirConditionerAutoMode(),
                                       e -> e.discardChannel(manualModeChannel()))
                               .transform(sensorService::convertStringToPayload)
                               .handle(Payload.class, (payload, headers) -> {
                                   airConditionerService.deleteListAndTimer();
                                   return payload;
                               })
                               .handle(Payload.class, (payload, headers) -> airConditionerService.setTimer(Constants.AUTO_AIRCONDITIONER, payload))
                               .filter(Payload.class, payload -> airConditionerService.isTimerActive(Constants.AUTO_AIRCONDITIONER, payload),
                                       e -> e.discardFlow(flow -> flow.handle(Payload.class, (payload, headers) -> {
                                           Map<String, Object> avg = airConditionerService.getAvgForAutoMode();

                                           PredictMessage predictMessage = new PredictMessage();
                                           messageService.injectPredictMessage(avg, predictMessage);
                                           messageService.sendPredictMessage(predictMessage);

                                           airConditionerService.deleteForAutoMode();
                                           return payload;
                                       }).nullChannel()))
                               .handle(Payload.class, (payload, headers) -> airConditionerService.saveForAutoMode(headers, payload))
                               .nullChannel();
    }

    @Bean
    public MessageChannel manualModeChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow manualMode() {
        return IntegrationFlows.from(manualModeChannel())
                               .filter(Message.class, airConditionerService::isIndoorTempMsg)
                               .transform(sensorService::convertStringToPayload)
                               .handle(Payload.class, (payload, headers) -> {
                                   airConditionerService.deleteForAutoMode();
                                   return payload;
                               })
                               .handle(Payload.class, (payload, headers) -> airConditionerService.setTimer(Constants.AIRCONDITIONER, payload))
                               .filter(Payload.class, payload -> !airConditionerService.isTimerActive(Constants.AIRCONDITIONER, payload),
                                       e -> e.discardFlow(flow -> flow.handle(Payload.class, (payload, headers) -> airConditionerService.saveTemperature(payload))
                                                                      .nullChannel()))
                               .handle(Payload.class, (payload, headers) -> {
                                   double avg = airConditionerService.getAvg(Constants.TEMPERATURE);
                                   DeviceSensorResponse response = commonAdapter.getOnOffValue(Constants.AIRCONDITIONER_DEVICE_ID, Constants.AIRCONDITIONER_SENSOR_ID);

                    if (avg > response.getOnValue() && !deviceService.isAirConditionerPowered()) {
                        messageService.sendDeviceMessage(Constants.AIRCONDITIONER, new ValueMessage(true));
                        deviceService.setAirConditionerPower(true);
                    }

                                   if (avg < response.getOffValue() && deviceService.isAirConditionerPowered()) {
                                       messageService.sendDeviceMessage(Constants.AIRCONDITIONER, new ValueMessage(false));
                                       deviceService.setAirConditionerPower(false);
                                   }

                                   airConditionerService.deleteListAndTimer();
                                   return payload;
                               })
                               .nullChannel();
    }
}
