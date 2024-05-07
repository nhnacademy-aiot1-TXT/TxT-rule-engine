package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.dto.message.*;
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

    public void sendDeviceMessage(String measurement, ValueMessage message) {
        rabbitTemplate.convertAndSend(exchangeName, "txt." + measurement, message);
    }

    public void injectPredictMessage(Map<String, Object> avg, PredictMessage predictMessage) {
        for (Map.Entry<String, Object> entry : avg.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "outdoorTemperature":
                    predictMessage.setOutdoorTemperature(new ValueMessage(avg.get(key)));
                    break;
                case "outdoorHumidity":
                    predictMessage.setOutdoorHumidity(new ValueMessage(avg.get(key)));
                    break;
                case "indoorTemperature":
                    predictMessage.setIndoorTemperature(new ValueMessage(avg.get(key)));
                    break;
                case "indoorHumidity":
                    predictMessage.setIndoorHumidity(new ValueMessage(avg.get(key)));
                    break;
                case "totalPeopleCount":
                    predictMessage.setTotalPeopleCount(new ValueMessage(avg.get(key)));
                    break;
                case "time":
                    predictMessage.setTime((Long) avg.get(key));
                    break;
                default:
                    break;
            }
        }
    }
}
