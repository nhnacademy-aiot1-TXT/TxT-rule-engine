package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.dto.message.DetailMessage;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.exchange.sensor.name}")
    private String exchangeSensorName;

    private final RabbitTemplate rabbitTemplate;

    public void sendPredictMessage(Map<String, Object> message) {
        rabbitTemplate.convertAndSend(exchangeSensorName, "txt.predict", message);
    }

    public void sendDeviceMessage(ValueMessage message) {
        rabbitTemplate.convertAndSend(exchangeName, "txt." + message.getDeviceName(), message);
    }

    public void sendSensorMessage(String measurement, DetailMessage message) {
        rabbitTemplate.convertAndSend(exchangeSensorName, "txt." + measurement, message);
    }
}
