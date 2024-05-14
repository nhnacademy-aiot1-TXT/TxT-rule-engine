package com.nhnacademy.aiot.ruleengine.service;


import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = AirConditionerService.class)
class AirConditionerServiceTest {

    @MockBean
    private RedisAdapter redisAdapter;
    @MockBean
    private SensorService sensorService;
    private AirConditionerService airConditionerService;

    @BeforeEach
    void setUp() {
        airConditionerService = new AirConditionerService(redisAdapter, sensorService);
    }

    @Test
    void testSetTimer() {
        Payload payload = new Payload(1713406102466L, "25");
        Mockito.when(redisAdapter.hasKey(Constants.AIRCONDITIONER + Constants.TIMER)).thenReturn(false);

        Payload result = airConditionerService.setTimer(Constants.AIRCONDITIONER, payload);

        assertEquals(payload, result);
        Mockito.verify(redisAdapter).setValue(Constants.AIRCONDITIONER + Constants.TIMER, payload.getTime());
    }

    @Test
    void testIsIndoorTempMsg() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("mqtt_receivedTopic", "test/class_a/temperature");
        Message<String> message = new GenericMessage<>("Test message", headers);

        boolean result = airConditionerService.isIndoorTempMsg(message);

        assertTrue(result);
    }

    @Test
    void testIsTimerActive() {
        Payload payload = new Payload(1713406102466L, "25");

        boolean result = airConditionerService.isTimerActive(Constants.AIRCONDITIONER, payload);

        assertFalse(result);
    }

    @Test
    void testSaveTemperature() {
        Payload payload = new Payload(1713406102466L, "12.5");

        Payload result = airConditionerService.saveTemperature(payload);

        assertEquals(payload, result);
        Mockito.verify(redisAdapter).saveFloatToList(Constants.TEMPERATURE, payload.getValue());
    }

    @Test
    void testDeleteForAutoMode() {
        airConditionerService.deleteForAutoMode();

        Mockito.verify(redisAdapter).deleteListWithPrefix("airconditioner:");
        Mockito.verify(redisAdapter).delete(Constants.AUTO_AIRCONDITIONER + Constants.TIMER);
    }

    @Test
    void testDeleteListAndTimer() {
        airConditionerService.deleteListAndTimer();

        Mockito.verify(redisAdapter).delete(Constants.TEMPERATURE);
        Mockito.verify(redisAdapter).delete(Constants.AIRCONDITIONER + Constants.TIMER);
    }
}
