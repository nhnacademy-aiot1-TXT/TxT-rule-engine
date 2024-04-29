package com.nhnacademy.aiot.ruleengine.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.message.DetailMessage;
import com.nhnacademy.aiot.ruleengine.dto.message.Message;
import com.nhnacademy.aiot.ruleengine.exception.PayloadParseException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MessageUtil {
    public static final Map<String, String> deviceMap = new HashMap<>() {{
        put("magnet_status", "txt.airconditioner");
        put("aircleaner", "txt.aircleaner");
        put("light", "txt.light");
    }};

    public static final Map<String, String[]> stringMap = new HashMap<>() {{
        put("occupancy", new String[]{"occupied", "txt.occupancy"});
    }};

    public static final Map<String, String> numberMap = new HashMap<>() {{
        put("battery_level", "txt.battery");
    }};

    public static Message getMessage(String topic, String payload) {
        Result result = getResult(topic, payload);
        return new Message(result.payloadObject.getValue());
    }

    public static DetailMessage getDetailedMessage(String topic, String payload) {
        Result result = getResult(topic, payload);
        return new DetailMessage(result.payloadObject.getValue(),result.topics[6], result.topics[8]);
    }

    @NotNull
    private static Result getResult(String topic, String payload) {
        ObjectMapper objectMapper = new ObjectMapper();

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
