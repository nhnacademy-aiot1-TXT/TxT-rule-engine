package com.nhnacademy.aiot.ruleengine.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.message.Message;
import com.nhnacademy.aiot.ruleengine.exception.PayloadParseException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class MessageUtil {

    public static Message getMessage(String topic, String payload) {
        Result result = getResult(topic, payload);
        return new Message(result.payloadObject.getValue(), result.topics[8], result.topics[6]);
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
