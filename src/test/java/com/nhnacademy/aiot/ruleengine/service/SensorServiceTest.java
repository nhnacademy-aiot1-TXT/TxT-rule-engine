package com.nhnacademy.aiot.ruleengine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.SensorData;
import com.nhnacademy.aiot.ruleengine.exception.PayloadParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(classes = SensorService.class)
class SensorServiceTest {

    @MockBean
    private ObjectMapper objectMapper;
    private SensorService sensorService;

    @BeforeEach
    void setUp() {
        sensorService = new SensorService(objectMapper);
    }

    @Test
    void testBuild() throws Exception {
        String[] topics = "data/s/nhnacademy/b/gyeongnam/p/place/d/device/e/temperature".split("/");
        String payloadStr = "{\"time\":1571885523,\"value\":23.6}";
        Payload payload = new Payload(1571885523L, "23.6");
        Mockito.when(objectMapper.readValue(payloadStr, Payload.class)).thenReturn(payload);
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
    void testConvertStringToPayload() throws Exception {
        String payloadStr = "{\"time\":1571885523,\"value\":23.6}";
        Payload payload = new Payload(1571885523L, "23.6");
        Mockito.when(objectMapper.readValue(payloadStr, Payload.class)).thenReturn(payload);

        Payload result = sensorService.convertStringToPayload(payloadStr);

        assertThat(result.getTime()).isEqualTo(payload.getTime());
        assertThat(result.getValue()).isEqualTo(payload.getValue());
    }

    @Test
    void testConvertStringToPayloadThrowException() throws Exception {
        String payloadStr = "{\"time\":\"invalid\",\"value\":23.6}";
        Mockito.when(objectMapper.readValue(payloadStr, Payload.class))
               .thenThrow(new InvalidFormatException(null, "잘못된 값", null, Long.class));

        assertThatThrownBy(() -> sensorService.convertStringToPayload(payloadStr))
                .isInstanceOf(PayloadParseException.class);
    }

    @Test
    void testParseToFloatValue() {
        Float value = sensorService.parseToFloatValue("23.678");

        assertEquals(23.7f, value);
    }

    @Test
    void testGetTopics() {
        String topicStr = "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/temperature";
        Message<String> message = MessageBuilder.withPayload("dummy")
                                                .setHeader("mqtt_receivedTopic", topicStr)
                                                .build();

        String[] topics = sensorService.getTopics(message.getHeaders());

        assertThat(topics).isEqualTo(topicStr.split("/"));
    }
}
