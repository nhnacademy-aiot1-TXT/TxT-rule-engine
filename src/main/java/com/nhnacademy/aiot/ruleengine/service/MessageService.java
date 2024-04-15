package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.dto.SwitchState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
        log.info("message sent: {}", switchState.toString());
        rabbitTemplate.convertAndSend(exchangeName, aircleanerRoutingKey, switchState);
    }

    public void sendLightMessage(SwitchState switchState) {
        log.info("message sent: {}", switchState.toString());
        rabbitTemplate.convertAndSend(exchangeName, lightRoutingKey, switchState);
    }

    public void sendAirconditionerMessage(SwitchState switchState) {
        log.info("message sent: {}", switchState.toString());
        rabbitTemplate.convertAndSend(exchangeName, airconditionerRoutingKey, switchState);
    }


    /**
     * Queue에서 메시지를 구독
     *
     * @param switchState 구독한 메시지를 담고 있는 객체
     */
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessage(SwitchState switchState) {
        log.info("Received message: {}", switchState.toString());
    }
}
