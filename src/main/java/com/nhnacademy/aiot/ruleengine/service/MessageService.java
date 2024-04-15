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

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    /**
     * Queue로 메시지를 발행
     *
     * @param switchState 발행할 메시지의 DTO 객체
     */
    public void sendMessage(SwitchState  switchState) {
        log.info("message sent: {}", switchState.toString());
        rabbitTemplate.convertAndSend(exchangeName, routingKey, switchState);
    }

    /**
     * Queue에서 메시지를 구독
     *
     * @param switchState 구독한 메시지를 담고 있는 객체
     */
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void reciveMessage(SwitchState switchState) {
        log.info("Received message: {}", switchState.toString());
    }
}
