package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.dto.SwitchState;
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

    private final RabbitTemplate rabbitTemplate;

    public void sendAircleanerMessage(SwitchState switchState) {
        sendMessage(switchState, aircleanerRoutingKey);
    }

    public void sendLightMessage(SwitchState switchState) {
        sendMessage(switchState, lightRoutingKey);
    }

    public void sendAirconditionerMessage(SwitchState switchState) {
        sendMessage(switchState, airconditionerRoutingKey);
    }

    private void sendMessage(SwitchState switchState, String routingKey) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, switchState);
    }

}
