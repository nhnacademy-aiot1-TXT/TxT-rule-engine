package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.domain.SwitchState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${rabbitmq.aircleaner.routing.key}")
    private String aircleanerRoutingKey;
    @Value("${rabbitmq.light.routing.key}")
    private String lightRoutingKey;
    @Value("${rabbitmq.airconditioner.routing.key}")
    private String airconditionerRoutingKey;

    @Value("${rabbitmq.occupancy.routing.key}")
    private String occupancyRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    public void sendValidateMessage(String topic, String payload) {
        if (topic.contains("magnet_status")) {
            sendAirconditionerMessage(new SwitchState(payload.contains("open")));
        } else if (topic.contains("occupancy")) {
            sendOccupancyMessage(new SwitchState(payload.contains("occupied")));
        }
    }

    private void sendOccupancyMessage(SwitchState switchState) {
        sendMessage(switchState, occupancyRoutingKey);
    }

    private void sendAircleanerMessage(SwitchState switchState) {
        sendMessage(switchState, aircleanerRoutingKey);
    }

    private void sendLightMessage(SwitchState switchState) {
        sendMessage(switchState, lightRoutingKey);
    }

    private void sendAirconditionerMessage(SwitchState switchState) {
        sendMessage(switchState, airconditionerRoutingKey);
    }

    private void sendMessage(SwitchState switchState, String routingKey) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, switchState);
    }

}
