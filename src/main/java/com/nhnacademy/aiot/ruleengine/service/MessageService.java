package com.nhnacademy.aiot.ruleengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.dto.BatteryMessage;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.SwitchState;
import com.nhnacademy.aiot.ruleengine.exception.PayloadParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {
    private final ObjectMapper objectMapper;

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

    @Value("${rabbitmq.battery.routing.key}")
    private String batteryRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    public void sendValidateMessage(String topic, String payload) {
        if (topic.contains("magnet_status")) {
            sendAirconditionerMessage(new SwitchState(payload.contains("open")));
        } else if (topic.contains("occupancy")) {
            sendOccupancyMessage(new SwitchState(payload.contains("occupied")));
        } else if (topic.contains("battery_level")) {
            Result result = getResult(topic, payload);
            sendBatteryMessage(new BatteryMessage(Integer.parseInt(result.payloadObject.getValue()), result.topics[8], result.topics[6]));
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

    private void sendBatteryMessage(BatteryMessage batteryMessage) {
        sendMessage(batteryMessage, batteryRoutingKey);
    }

    private <T> void sendMessage(T message, String routingKey) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
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
