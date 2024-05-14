package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.message.DetailMessage;
import com.nhnacademy.aiot.ruleengine.dto.message.PredictMessage;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    public void sendDeviceMessage(String deviceName, ValueMessage message) {
        rabbitTemplate.convertAndSend(exchangeName, "txt." + deviceName, message);
    }

    public void sendSensorMessage(String measurement, DetailMessage message) {
        rabbitTemplate.convertAndSend(exchangeName, "txt." + measurement, message);
    }

    public void injectPredictMessage(Map<String, Object> avg, PredictMessage predictMessage) {
        for (Map.Entry<String, Object> entry : avg.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case Constants.OUTDOOR_TEMPERATURE:
                    predictMessage.setOutdoorTemperature(new ValueMessage(avg.get(key)));
                    break;
                case Constants.OUTDOOR_HUMIDITY:
                    predictMessage.setOutdoorHumidity(new ValueMessage(avg.get(key)));
                    break;
                case Constants.INDOOR_TEMPERATURE:
                    predictMessage.setIndoorTemperature(new ValueMessage(avg.get(key)));
                    break;
                case Constants.INDOOR_HUMIDITY:
                    predictMessage.setIndoorHumidity(new ValueMessage(avg.get(key)));
                    break;
                case Constants.TOTAL_PEOPLE_COUNT:
                    predictMessage.setTotalPeopleCount(new ValueMessage(avg.get(key)));
                    break;
                case Constants.TIME:
                    predictMessage.setTime((Long) avg.get(key));
                    break;
                default:
                    break;
            }
        }
    }
}
