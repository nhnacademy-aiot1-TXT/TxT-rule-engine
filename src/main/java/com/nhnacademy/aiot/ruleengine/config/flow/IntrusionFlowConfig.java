package com.nhnacademy.aiot.ruleengine.config.flow;

import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
import com.nhnacademy.aiot.ruleengine.service.IntrusionService;
import com.nhnacademy.aiot.ruleengine.service.MessageService;
import com.nhnacademy.aiot.ruleengine.service.MqttService;
import com.nhnacademy.aiot.ruleengine.service.SensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.MessageChannel;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class IntrusionFlowConfig {

    private final MqttService mqttService;
    private final SensorService sensorService;
    private final MessageService messageService;
    private final IntrusionService intrusionService;
    private final ThreadLocal<String> latestValue = new ThreadLocal<>();

    @Bean
    public MessageChannel intrusionChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer intrusionInbound() {
        return mqttService.createMqttAdapter(Constants.TXT_MQTT, "rule-engine-intrusion", intrusionChannel(),
                                             "milesight/s/nhnacademy/b/gyeongnam/p/class_a/d/vs121/e/occupancy");
    }

    @Bean
    public IntegrationFlow intrusionProcess() {
        return IntegrationFlows.from(Constants.INTRUSION_CHANNEL)
                               .transform(sensorService::convertStringToPayload)
                               .filter(Payload.class, payload -> intrusionService.isAlertTimeActive(payload.getLocalTime()))
                               .filter(Payload.class, payload -> Constants.OCCUPIED.equals(payload.getValue()),
                                       e -> e.discardFlow(flow -> flow.handle(Payload.class, (payload, headers) ->
                                           {
                                               latestValue.set(payload.getValue());
                                               log.info(latestValue.get());
                                               return payload;
                                           }).nullChannel()))
                               .handle(Payload.class, (payload, headers) ->
                                   {
                                       if (Constants.VACANT.equals(latestValue.get())) {
                                           messageService.sendDeviceMessage(new ValueMessage("class_a", "intrusion", true));
                                           latestValue.set(Constants.OCCUPIED);
                                           log.info(latestValue.get());
                                       }
                                       return payload;
                                   }).nullChannel();
    }
}
