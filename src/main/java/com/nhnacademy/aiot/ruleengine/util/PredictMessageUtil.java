package com.nhnacademy.aiot.ruleengine.util;

import com.nhnacademy.aiot.ruleengine.dto.message.Message;
import com.nhnacademy.aiot.ruleengine.dto.message.PredictMessage;
import org.json.JSONObject;

import static com.nhnacademy.aiot.ruleengine.util.MessageUtil.getMessage;

public class PredictMessageUtil {


    public static PredictMessage inputPredictMessage(String topic, String payload, PredictMessage predictMessage) {
        if (topic.contains("temperature")) {
            if (topic.contains("outdoor"))
                predictMessage.setOutdoorTemperature((Message)getMessage(topic, payload));
            else
                predictMessage.setIndoorTemperature((Message)getMessage(topic, payload));
        } else if (topic.contains("humidity")) {
            if (topic.contains("outdoor"))
                predictMessage.setOutdoorHumidity((Message)getMessage(topic, payload));
            else
                predictMessage.setIndoorHumidity((Message)getMessage(topic, payload));
        } else
            predictMessage.setTotalPeopleCount((Message)getMessage(topic, payload));
        JSONObject json = new JSONObject(payload);
        predictMessage.setTime(json.getLong("time"));
        return predictMessage;
    }
}
