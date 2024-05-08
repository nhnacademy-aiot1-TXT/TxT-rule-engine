package com.nhnacademy.aiot.ruleengine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.SensorData;
import com.nhnacademy.aiot.ruleengine.exception.PayloadParseException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class SensorServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Test
    public void testBuild() throws Exception {
        String[] topics = "data/s/nhnacademy/b/gyeongnam/p/place/d/device/e/temperature".split("/");
        String payloadStr = "{\"time\":1571885523,\"value\":23.6}";
        Payload payload = new Payload(1571885523L, "23.6");

        Mockito.when(objectMapper.readValue(payloadStr, Payload.class)).thenReturn(payload);

        SensorService sensorService = new SensorService(objectMapper);

        Map<String, Object> headers = new HashMap<>();
        headers.put("mqtt_receivedTopic", String.join("/", topics));
        SensorData result = sensorService.build(new MessageHeaders(headers), payloadStr);

        assertThat(result.getPlace()).isEqualTo(topics[6]);
        assertThat(result.getTopic()).isEqualTo(String.join("/", topics));
        assertThat(result.getDevice()).isEqualTo(topics[8]);
        assertThat(result.getValue()).isEqualTo(payload.getValue());
        assertThat(result.getTime()).isEqualTo(payload.getTime());
    }

    @Test
    public void testConvertStringToPayload() throws Exception {
        String payloadStr = "{\"time\":1571885523,\"value\":23.6}";
        Payload payload = new Payload(1571885523L, "23.6");

        Mockito.when(objectMapper.readValue(payloadStr, Payload.class)).thenReturn(payload);

        SensorService sensorService = new SensorService(objectMapper);

        Payload result = sensorService.convertStringToPayload(payloadStr);

        assertThat(result.getTime()).isEqualTo(payload.getTime());
        assertThat(result.getValue()).isEqualTo(payload.getValue());
    }

    @Test
    public void testConvertStringToPayloadThrowException() throws Exception {
        String payloadStr = "{\"time\":\"invalid\",\"value\":23.6}";

        Mockito.when(objectMapper.readValue(payloadStr, Payload.class))
                .thenThrow(new InvalidFormatException(null, "잘못된 값", null, Long.class));

        SensorService sensorService = new SensorService(objectMapper);

        assertThatThrownBy(() -> sensorService.convertStringToPayload(payloadStr))
                .isInstanceOf(PayloadParseException.class);
    }

    @Test
    public void testParseToFloatValue() {
        SensorService sensorService = new SensorService(objectMapper);

        Float value = sensorService.parseToFloatValue("23.678");

        assertThat(value).isEqualTo(23.7f);
    }

    @Test
    public void testGetTopics() {
        String topicStr = "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/temperature";
        SensorService sensorService = new SensorService(objectMapper);

        Message<String> message = MessageBuilder.withPayload("dummy")
                .setHeader("mqtt_receivedTopic", topicStr)
                .build();
        String[] topics = sensorService.getTopics(message.getHeaders());

        assertThat(topics).isEqualTo(topicStr.split("/"));
    }
}