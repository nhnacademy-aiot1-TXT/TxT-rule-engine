package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.dto.message.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.nhnacademy.aiot.ruleengine.util.MessageUtil.getMessage;
import static com.nhnacademy.aiot.ruleengine.util.PredictMessageUtil.inputPredictMessage;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {


    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.exchange.sensor.name}")
    private String exchangeSensorName;
    @Value("${rabbitmq.aircleaner.routing.key}")
    private String aircleanerRoutingKey;
    @Value("${rabbitmq.light.routing.key}")
    private String lightRoutingKey;
    @Value("${rabbitmq.airconditioner.routing.key}")
    private String airconditionerRoutingKey;

    @Value("${rabbitmq.occupancy.routing.key}")
    private String occupancyRoutingKey;

    @Value("${rabbitmq.battery.routing.key}")
    private String batteryRoutingKey;

    @Value("${rabbitmq.predict.routing.key}")
    private String predictRoutingKey;


    private final RabbitTemplate rabbitTemplate;
    private PredictMessage predictMessage = new PredictMessage();

    public void sendValidateMessage(String topic, String payload) {
        if (topic.contains("magnet_status")) {
            sendDeviceMessage(new BooleanMessage(payload.contains("open")), airconditionerRoutingKey);
        } else if (topic.contains("occupancy")) {
            sendSensorMessage(new BooleanMessage(payload.contains("occupied")), occupancyRoutingKey);
        } else if (topic.contains("battery_level")) {
            sendSensorMessage(getMessage(topic, payload), batteryRoutingKey);
        } else {
            predictMessage = inputPredictMessage(topic, payload, predictMessage);
            if (isFull()) {
                sendSensorMessage(predictMessage, predictRoutingKey);
                resetIndoorMessage();
            }
        }
    }

    public <T> void sendSensorMessage(T message, String routingKey) {
        rabbitTemplate.convertAndSend(exchangeSensorName, routingKey, message);
    }

    private <T> void sendDeviceMessage(T message, String routingKey) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message, new CustomMessagePostProcessor(0));
    }

    // PredictMessage 관련 메소드
    private boolean isFull() {
        return Objects.nonNull(predictMessage.getIndoorTemperature()) && Objects.nonNull(predictMessage.getIndoorHumidity())
                && Objects.nonNull(predictMessage.getTotalPeopleCount()) && Objects.nonNull(predictMessage.getOutdoorHumidity()) && Objects.nonNull(predictMessage.getOutdoorTemperature());
    }

    private void resetIndoorMessage() {
        predictMessage.setIndoorHumidity(null);
        predictMessage.setIndoorTemperature(null);
        predictMessage.setTotalPeopleCount(null);
    }
}
