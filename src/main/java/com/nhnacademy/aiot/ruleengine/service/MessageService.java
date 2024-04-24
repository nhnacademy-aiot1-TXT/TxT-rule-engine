package com.nhnacademy.aiot.ruleengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.dto.message.*;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.exception.PayloadParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {
    private final ObjectMapper objectMapper;

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
            sendDeviceMessage(new SwitchState(payload.contains("open")), airconditionerRoutingKey);
        } else if (topic.contains("occupancy")) {
            sendSensorMessage(new SwitchState(payload.contains("occupied")), occupancyRoutingKey);
        } else if (topic.contains("battery_level")) {
            sendSensorMessage(handleMessageWithIntegerResult(topic, payload), batteryRoutingKey);
        } else {
            inputPredictMessage(topic, payload);
            if (Objects.nonNull(predictMessage.getTemperatureMessage()) && Objects.nonNull(predictMessage.getHumidityMessage()) && Objects.nonNull(predictMessage.getTotalPeopleCountMessage())) {
                sendPredictMessage(predictMessage);
                predictMessage = new PredictMessage();
            }
        }
    }

    private void sendPredictMessage(PredictMessage predictMessage) {
        rabbitTemplate.convertAndSend(exchangeSensorName, predictRoutingKey, predictMessage);
    }

    private <T> void sendSensorMessage(T message, String routingKey) {
        rabbitTemplate.convertAndSend(exchangeSensorName, routingKey, message);
    }

    private <T> void sendDeviceMessage(T message, String routingKey) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message, new CustomMessagePostProcessor(2000));
    }

    private IntegerMessage handleMessageWithIntegerResult(String topic, String payload) {
        Result result = getResult(topic, payload);
        return new IntegerMessage(Integer.parseInt(result.payloadObject.getValue()), result.topics[8], result.topics[6]);
    }

    private FloatMessage handleMessageWithFloatResult(String topic, String payload) {
        Result result = getResult(topic, payload);
        return new FloatMessage(Float.parseFloat(result.payloadObject.getValue()), result.topics[8], result.topics[6]);
    }

    private void inputPredictMessage(String topic, String payload) {
        if (topic.contains("temperature"))
            predictMessage.setTemperatureMessage(handleMessageWithFloatResult(topic, payload));
        else if (topic.contains("humidity"))
            predictMessage.setHumidityMessage(handleMessageWithFloatResult(topic, payload));
        else
            predictMessage.setTotalPeopleCountMessage(handleMessageWithIntegerResult(topic, payload));
    }

    @NotNull
    private Result getResult(String topic, String payload) {
        String[] topics = topic.split("/");
        Payload payloadObject;
        try {
            payloadObject = objectMapper.readValue(payload, Payload.class);
        } catch (JsonProcessingException e) {
            throw new PayloadParseException();
        }
        return new Result(topics, payloadObject);
    }

    private static class Result {
        public final String[] topics;
        public final Payload payloadObject;

        public Result(String[] topics, Payload payloadObject) {
            this.topics = topics;
            this.payloadObject = payloadObject;
        }
    }
}
