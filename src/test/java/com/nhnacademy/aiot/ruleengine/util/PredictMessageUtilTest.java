package com.nhnacademy.aiot.ruleengine.util;

import com.nhnacademy.aiot.ruleengine.dto.message.Message;
import com.nhnacademy.aiot.ruleengine.dto.message.PredictMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PredictMessageUtilTest {
    @Test
    void inputFloatMessage() {
        String topic = "data/s/nhnacademy/b/gyeongnam/p/lobby/d/24e124785c389010/e/temperature";
        String payload = "{\"time\":1714117716493,\"value\":23.900000000000002}";

        PredictMessage predictMessage = new PredictMessage();
        Message expectedMessage = (Message) MessageUtil.getMessage(topic, payload);

        PredictMessage result = PredictMessageUtil.inputPredictMessage(topic, payload, predictMessage);

        assertNotNull(result);
        assertEquals(expectedMessage, result.getIndoorTemperature());
    }

    @Test
    void inputIntegerMessage() {
        String topic = "data/s/nhnacademy/b/gyeongnam/p/lobby/d/24e124785c389010/e/total_people_count";
        String payload = "{\"time\":1714117716493,\"value\":10}";

        PredictMessage predictMessage = new PredictMessage();
        Message expectedMessage = (Message) MessageUtil.getMessage(topic, payload);

        PredictMessage result = PredictMessageUtil.inputPredictMessage(topic, payload, predictMessage);

        assertNotNull(result);
        assertEquals(expectedMessage, result.getTotalPeopleCount());
    }
}