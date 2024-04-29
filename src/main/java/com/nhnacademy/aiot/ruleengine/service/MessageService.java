package com.nhnacademy.aiot.ruleengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.dto.IntegerMessage;
import com.nhnacademy.aiot.ruleengine.dto.FloatMessage;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.SwitchState;
import com.nhnacademy.aiot.ruleengine.exception.PayloadParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

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

    @Value("${rabbitmq.temperature.routing.key}")
    private String temperatureRoutingKey;

    @Value("${rabbitmq.humidity.routing.key}")
    private String humidityRoutingKey;

    @Value("${rabbitmq.totalPeopleCount.routing.key}")
    private String totalPeopleCountRoutingKey;


    private final RabbitTemplate rabbitTemplate;

    public void sendValidateMessage(String topic, String payload) {
        if (topic.contains("magnet_status")) {
            sendSwitchStateMessage(payload.contains("open"), this::sendAirconditionerMessage);
        } else if (topic.contains("occupancy")) {
            sendSwitchStateMessage(payload.contains("occupied"), this::sendOccupancyMessage);
        } else if (topic.contains("battery_level") || topic.contains("total_people_count")) {
            handleMessageWithIntegerResult(topic, payload);
        } else if (topic.contains("temperature") || topic.contains("humidity")) {
            handleMessageWithFloatResult(topic, payload);
        } else if (topic.contains("aircleaner")) {
            sendSwitchStateMessage(Boolean.parseBoolean(payload), this::sendAircleanerMessage);
        }
    }

    private void sendAircleanerMessage(SwitchState switchState) {
        sendDeviceControlMessage(switchState, aircleanerRoutingKey);
    }

    private void sendLightMessage(SwitchState switchState) {
        sendDeviceControlMessage(switchState, lightRoutingKey);
    }

    private void sendAirconditionerMessage(SwitchState switchState) {
        sendDeviceControlMessage(switchState, airconditionerRoutingKey);
    }

    // on/off 제어 데이터
    // -------------------------------------------------------------------------------------------------------------------------------------
    // 센서 데이터

    private void sendBatteryMessage(IntegerMessage batteryMessage) {
        sendSensorDataMessage(batteryMessage, batteryRoutingKey);
    }

    private void sendTotalPeopleCountMessage(IntegerMessage totalPeopleCountMessage) {
        sendSensorDataMessage(totalPeopleCountMessage, totalPeopleCountRoutingKey);
    }

    private void sendTemperatureMessage(FloatMessage temperatureMessage) {
        sendSensorDataMessage(temperatureMessage, temperatureRoutingKey);
    }

    private void sendHumidityMessage(FloatMessage humidityMessage) {
        sendSensorDataMessage(humidityMessage, humidityRoutingKey);
    }

    private void sendOccupancyMessage(SwitchState switchState) {
        sendSensorDataMessage(switchState, occupancyRoutingKey);
    }


    private <T> void sendSensorDataMessage(T message, String routingKey) {
        rabbitTemplate.convertAndSend(exchangeSensorName, routingKey, message);
    }

    private <T> void sendDeviceControlMessage(T message, String routingKey) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    }

    private void sendSwitchStateMessage(boolean state, Consumer<SwitchState> switchStateSender) {
        switchStateSender.accept(new SwitchState(state));
    }

    private void handleMessageWithIntegerResult(String topic, String payload) {
        Result result = getResult(topic, payload);
        IntegerMessage message = new IntegerMessage(Integer.parseInt(result.payloadObject.getValue()), result.topics[8], result.topics[6]);

        if (topic.contains("battery_level")) {
            sendBatteryMessage(message);
        } else if (topic.contains("total_people_count")) {
            sendTotalPeopleCountMessage(message);
        }
    }

    private void handleMessageWithFloatResult(String topic, String payload) {
        Result result = getResult(topic, payload);
        FloatMessage message = new FloatMessage(Float.parseFloat(result.payloadObject.getValue()), result.topics[8], result.topics[6]);

        if (topic.contains("temperature")) {
            sendTemperatureMessage(message);
        } else if (topic.contains("humidity")) {
            sendHumidityMessage(message);
        }
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
