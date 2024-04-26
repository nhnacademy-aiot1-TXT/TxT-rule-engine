package com.nhnacademy.aiot.ruleengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.dto.message.*;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.exception.PayloadParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
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
    private final PredictMessage predictMessage = new PredictMessage();

    public void sendValidateMessage(String topic, String payload) {
        if (topic.contains("magnet_status")) {
            sendDeviceMessage(new SwitchMessage(payload.contains("open")), airconditionerRoutingKey);
        } else if (topic.contains("occupancy")) {
            sendSensorMessage(new SwitchMessage(payload.contains("occupied")), occupancyRoutingKey);
        } else if (topic.contains("battery_level")) {
            sendSensorMessage(getMessage(topic, payload), batteryRoutingKey);
        } else {
            inputPredictMessage(topic, payload);
            if (isFull()) {
                setTimeValue(payload);
                sendSensorMessage(predictMessage, predictRoutingKey);
                resetIndoorMessage();
            }
        }
    }

    private <T> void sendSensorMessage(T message, String routingKey) {
        rabbitTemplate.convertAndSend(exchangeSensorName, routingKey, message);
    }

    private <T> void sendDeviceMessage(T message, String routingKey) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message, new CustomMessagePostProcessor(0));
    }

    private Message getMessage(String topic, String payload) {
        Result result = getResult(topic, payload);
        return new Message(result.payloadObject.getValue(), result.topics[8], result.topics[6]);
    }

    private void inputPredictMessage(String topic, String payload) {
        if (topic.contains("temperature")) {
            if (topic.contains("outdoor"))
                predictMessage.setOutdoorTemperature(getMessage(topic, payload));
            else
                predictMessage.setIndoorTemperature(getMessage(topic, payload));
        } else if (topic.contains("humidity")) {
            if (topic.contains("outdoor"))
                predictMessage.setOutdoorHumidity(getMessage(topic, payload));
            else
                predictMessage.setIndoorHumidity(getMessage(topic, payload));
        } else
            predictMessage.setTotalPeopleCount(getMessage(topic, payload));
    }

    private boolean isFull() {
        return Objects.nonNull(predictMessage.getIndoorTemperature()) && Objects.nonNull(predictMessage.getIndoorHumidity())
                && Objects.nonNull(predictMessage.getTotalPeopleCount()) && Objects.nonNull(predictMessage.getOutdoorHumidity()) && Objects.nonNull(predictMessage.getOutdoorTemperature());
    }

    private void resetIndoorMessage() {
        predictMessage.setIndoorHumidity(null);
        predictMessage.setIndoorTemperature(null);
        predictMessage.setTotalPeopleCount(null);
    }

    private void setTimeValue(String payload) {
        JSONObject json = new JSONObject(payload);
        predictMessage.setTime(json.getLong("time"));
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
