package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.dto.message.*;
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

    @Value("${rabbitmq.exchange.sensor.name}")
    private String exchangeSensorName;

    private final RabbitTemplate rabbitTemplate;

    public void sendPredictMessage(PredictMessage message) {
        rabbitTemplate.convertAndSend(exchangeSensorName, "txt.predict", message);
    }

    public void sendDeviceMessage(String measurement, ValueMessage message) {
        rabbitTemplate.convertAndSend(exchangeName, "txt." + measurement, message, new CustomMessagePostProcessor(0));
    }
}
