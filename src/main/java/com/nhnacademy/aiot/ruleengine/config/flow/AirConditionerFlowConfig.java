package com.nhnacademy.aiot.ruleengine.config.flow;

import com.nhnacademy.aiot.ruleengine.adapter.CommonAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorResponse;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.message.PredictMessage;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
import com.nhnacademy.aiot.ruleengine.service.AirConditionerService;
import com.nhnacademy.aiot.ruleengine.service.DeviceRedisService;
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
    private final DeviceRedisService deviceRedisService;
    private final SensorService sensorService;
    private final AirConditionerService airConditionerService;
    private final MessageService messageService;

    @Bean
    public IntegrationFlow checkAutoMode() {
        return IntegrationFlows.from("airConditionerChannel")
                               .filter(p -> deviceRedisService.isAirConditionerAutoMode(),
                                       e -> e.discardChannel(airConditionerProcessChannel()))
                               .transform(sensorService::convertStringToPayload)
                               .handle(Payload.class, (payload, headers) -> airConditionerService.setTimer(Constants.AUTO_AIRCONDITIONER, payload))
                               .filter(Payload.class, payload -> airConditionerService.isTimerActive(Constants.AUTO_AIRCONDITIONER, payload),
                                       e -> e.discardFlow(flow -> {
                                           Map<String, Float> avg = airConditionerService.getAvgForAutoMode();

                                           PredictMessage predictMessage = new PredictMessage();
                                           messageService.injectPredictMessage(avg, predictMessage);
                                           messageService.sendPredictMessage(predictMessage);

                                           airConditionerService.deleteForAutoMode();
                                       }))
                               .handle(Payload.class, (payload, headers) -> airConditionerService.saveForAutoMode(headers, payload))
                               .get();
    }

    @Bean
    public MessageChannel airConditionerProcessChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow airConditionerProcess() {
        // 수동모드일때
        return IntegrationFlows.from(airConditionerProcessChannel())
                               .filter(Message.class, airConditionerService::isIndoorTempMsg)
                               .transform(sensorService::convertStringToPayload)
                               .handle(Payload.class, (payload, headers) -> airConditionerService.setTimer(Constants.AIRCONDITIONER, payload))
                               .filter(Payload.class, payload -> !airConditionerService.isTimerActive(Constants.AIRCONDITIONER, payload),
                                       e -> e.discardFlow(flow -> flow.handle(Payload.class, (payload, headers) -> airConditionerService.saveTemperature(payload))))
                               .handle(Payload.class, (payload, headers) -> {
                                   float avg = airConditionerService.getAvg(Constants.TEMPERATURE);
                                   DeviceSensorResponse response = commonAdapter.getOnOffValue(Constants.AIRCONDITIONER)
                                                                                .get(0);

                                   if (avg > response.getOnValue() && !deviceRedisService.isAirConditionerPowered()) {
                                       messageService.sendDeviceMessage(Constants.AIRCONDITIONER, new ValueMessage(true));
                                   }

                                   if (avg < response.getOffValue() && deviceRedisService.isAirCleanerPowered()) {
                                       messageService.sendDeviceMessage(Constants.AIRCONDITIONER, new ValueMessage(false));
                                   }

                                   airConditionerService.deleteListAndTimer();
                                   return null;
                               }).get();
    }
}
